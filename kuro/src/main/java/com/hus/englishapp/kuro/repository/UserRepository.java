package com.hus.englishapp.kuro.repository;

import com.hus.englishapp.kuro.model.User;
import com.hus.englishapp.kuro.model.dto.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    //    User findByUsername(String username);
    @Query(value = "select count(u.id) from User u where u.username = :username or u.email = :email")
    int checkAccountAvailable(String username, String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByResetToken(String resetToken);

    Optional<User> findByRefreshToken(String oldRefreshToken);
}
