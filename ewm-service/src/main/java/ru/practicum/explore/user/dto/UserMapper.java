package ru.practicum.explore.user.dto;

import org.mapstruct.Mapper;
import ru.practicum.explore.user.model.User;

@Mapper
public interface UserMapper {
    User mapUserDtoInToUser(UserDtoIn userDtoIn);

    UserDtoOut mapUserToUserDtoOut(User user);

    UserShortDtoOut mapUserToUserShortDtoOut(User userD);
}
