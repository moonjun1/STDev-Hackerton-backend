package com.stdev.team10.domain.user.repository;


import com.stdev.team10.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByUserName(String username);
    UserEntity findByUserName(String username);

}