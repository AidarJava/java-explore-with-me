package ru.practicum.explore.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explore.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "SELECT * FROM users AS u WHERE id IN :ids ORDER BY u.id OFFSET :from LIMIT :size", nativeQuery = true)
    List<User> findUsersByIdsWithLimit(@Param("ids") Integer[] ids, @Param("from") Integer from, @Param("size") Integer size);

    @Query(value = "SELECT * FROM users AS u WHERE id IN ORDER BY u.id :ids OFFSET :from", nativeQuery = true)
    List<User> findUsersByIdsWithFrom(@Param("ids") Integer[] ids, @Param("from") Integer from);

    @Query(value = "SELECT * FROM users AS u ORDER BY u.id OFFSET :from LIMIT :size", nativeQuery = true)
    List<User> findUsersWithLimit(@Param("from") Integer from, @Param("size") Integer size);

    @Query(value = "SELECT * FROM users AS u ORDER BY u.id OFFSET :from", nativeQuery = true)
    List<User> findUsersWithFrom(@Param("from") Integer from);

    User findByEmail(String string);
}
