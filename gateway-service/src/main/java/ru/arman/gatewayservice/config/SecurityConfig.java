package ru.arman.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http,
                                                      ServerOAuth2AuthorizationRequestResolver resolver) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers("/error").permitAll()

                        .pathMatchers(HttpMethod.GET, "/api/v1/user-service/user/**").permitAll()
                        .pathMatchers("/api/v1/user-service/user/register").permitAll()
                        .pathMatchers("/api/v1/user-service/user/assign-role/**").hasAnyRole("MANAGER", "ADMIN")
                        .pathMatchers("/api/v1/user-service/user/remove-role/**").hasAnyRole("MANAGER", "ADMIN")

                        .pathMatchers(HttpMethod.GET, "/api/v1/comment-service/comments/**").permitAll()

                        .pathMatchers(HttpMethod.GET, "/api/v1/post-service/**").permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .oauth2Login(login -> login.authorizationRequestResolver(resolver))
                .build();
    }

    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        return new JwtRoleConverter();
    }

    private static class JwtRoleConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {
        @Override
        public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
            List<String> roles = (List<String>) jwt.getClaimAsMap("realm_access")
                    .getOrDefault("roles", Collections.emptyList());

            // Convert roles to GrantedAuthority objects, filtering only those starting with 'ROLE_'
            Collection<GrantedAuthority> authorities = roles.stream()
                    .filter(role -> role.startsWith("ROLE_"))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            // Add existing authorities from the JWT, if any
            authorities.addAll(new JwtGrantedAuthoritiesConverter().convert(jwt));

            // Create and return a JwtAuthenticationToken with the roles and authorities
            return Mono.just(new JwtAuthenticationToken(jwt, authorities));
        }
    }

    @Bean
    public ReactiveOAuth2UserService<OidcUserRequest, OidcUser> reactiveOAuth2UserService() {
        var oidcUserService = new OidcReactiveOAuth2UserService();

        return userRequest -> oidcUserService.loadUser(userRequest)
                .map(oidcUser -> {
//                    var roles = oidcUser.getClaimAsStringList("blog-spring-sec-roles");
                    var roles = (List<String>) oidcUser.getClaimAsMap("realm_access")
                            .getOrDefault("roles", Collections.emptyList());
                    var authorities = Stream.concat(
                            oidcUser.getAuthorities().stream(),
                            roles.stream()
                                    .filter(role -> role.startsWith("ROLE_"))
                                    .map(SimpleGrantedAuthority::new)
                                    .map(GrantedAuthority.class::cast)
                    ).toList();

                    return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
                });
    }

    @Bean
    public ServerOAuth2AuthorizationRequestResolver pkceResolver(ReactiveClientRegistrationRepository repo) {
        var resolver = new DefaultServerOAuth2AuthorizationRequestResolver(repo);
        resolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());
        return resolver;
    }
}
