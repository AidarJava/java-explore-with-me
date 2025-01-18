package ru.practicum.explore.user;

import org.springframework.http.ResponseEntity;
import ru.practicum.explore.user.dto.UserDtoIn;
import ru.practicum.explore.user.dto.UserDtoOut;
import ru.practicum.explore.user.dto.UserShortDtoOut;

import java.util.List;

public interface UserService {

    List<UserDtoOut> getUsers(Integer[] ids, Integer from, Integer size);

    UserDtoOut addUser(UserDtoIn userDtoIn);

    ResponseEntity<Void> deleteUser(Integer userId);

    UserShortDtoOut getUser(Integer userId);
}
