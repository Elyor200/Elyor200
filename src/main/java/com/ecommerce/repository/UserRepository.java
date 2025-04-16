package com.ecommerce.repository;

import com.ecommerce.model.User;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByChatId(Long chatId);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Long countByIsVerified(boolean Verified);
}
