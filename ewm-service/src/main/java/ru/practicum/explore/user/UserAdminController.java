package ru.practicum.explore.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.user.dto.UserDtoIn;
import ru.practicum.explore.user.dto.UserDtoOut;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UserAdminController {
    private final UserService userService;

    @GetMapping
    public List<UserDtoOut> getUsers(@RequestParam(name = "ids", required = false) Integer[] ids,
                                     @RequestParam(name = "from", defaultValue = "0") Integer from,
                                     @RequestParam(name = "size", required = false) Integer size) {
        log.info("GET/ Проверка параметров запроса метода getUsers, ids - {}, from - {}, size - {}", ids, from, size);
        return userService.getUsers(ids, from, size);
    }

    @PostMapping
    public UserDtoOut addUser(@Valid @RequestBody UserDtoIn userDtoIn) {
        log.info("POST/ Проверка параметров запроса метода addUser, userDtoIn - {}", userDtoIn);
        return userService.addUser(userDtoIn);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "userId") Integer userId) {
        log.info("DELETE/ Проверка параметров запроса метода deleteUser, userId - {}", userId);
        return userService.deleteUser(userId);
    }
}
