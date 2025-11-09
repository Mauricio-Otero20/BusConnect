package com.example.busconnect.services.mappers;
import com.example.busconnect.api.dto.UserDtos.*;
import com.example.busconnect.domine.entities.User;
import com.example.busconnect.domine.entities.enums.UserStatus;
import org.mapstruct.*;


@Mapper(componentModel = "spring", imports = {UserStatus.class})
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(UserCreateRequest dto);

    @Mapping(target = "username", source = "username")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "status", source = "status")
    void updateEntity(UserUpdateRequest dto, @MappingTarget User user);

    @Mapping(target = "role", source = "role")
    @Mapping(target = "status", source = "status")
    UserResponse toResponse(User entity);
}