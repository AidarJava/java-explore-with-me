package ru.practicum.explore.user;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.exception.ConflictException;
import ru.practicum.explore.exception.NotFoundException;
import ru.practicum.explore.user.dto.UserDtoIn;
import ru.practicum.explore.user.dto.UserDtoOut;
import ru.practicum.explore.user.dto.UserMapper;
import ru.practicum.explore.user.dto.UserShortDtoOut;
import ru.practicum.explore.user.model.User;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Override
    public List<UserDtoOut> getUsers(Integer[] ids, Integer from, Integer size) {
        if (ids == null) {
            return userRepository.findUsersWithLimit(from, size).stream().map(mapper::mapUserToUserDtoOut).toList();
        } else {
            return userRepository.findUsersByIdsWithLimit(ids, from, size).stream().map(mapper::mapUserToUserDtoOut).toList();
        }
    }

    @Transactional
    @Override
    public UserDtoOut addUser(UserDtoIn userDtoIn) {
        if (userRepository.findByEmail(userDtoIn.getEmail()) != null) {
            throw new ConflictException("Email пользователя должно быть уникальным!");
        }
        return mapper.mapUserToUserDtoOut(userRepository.save(mapper.mapUserDtoInToUser(userDtoIn)));
    }

    @Transactional
    @Override
    public ResponseEntity<Void> deleteUser(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

    @Override
    public UserShortDtoOut getUser(Integer userId) {
        return mapper.mapUserToUserShortDtoOut(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found")));
    }
}