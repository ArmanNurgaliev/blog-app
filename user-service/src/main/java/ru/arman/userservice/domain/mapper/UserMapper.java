package ru.arman.userservice.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.arman.userservice.domain.dto.UserDto;
import ru.arman.userservice.domain.dto.UserDtoResponse;
import ru.arman.userservice.domain.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User userDtoToUser(UserDto userDto);
    UserDtoResponse userToUserResponseDto(User user);
}
