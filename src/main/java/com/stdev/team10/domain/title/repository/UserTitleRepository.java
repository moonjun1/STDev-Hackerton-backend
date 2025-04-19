package com.stdev.team10.domain.title.repository;

import com.stdev.team10.domain.title.entity.TitleEntity;
import com.stdev.team10.domain.title.entity.UserTitleEntity;
import com.stdev.team10.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserTitleRepository extends JpaRepository<UserTitleEntity, Long> {
    List<UserTitleEntity> findByUser(UserEntity user);

    Optional<UserTitleEntity> findByUserAndTitle(UserEntity user, TitleEntity title);

    Optional<UserTitleEntity> findByUserAndIsActiveTrue(UserEntity user);

    @Query("SELECT COUNT(ut) > 0 FROM UserTitleEntity ut WHERE ut.user = :user AND ut.title = :title")
    boolean existsByUserAndTitle(@Param("user") UserEntity user, @Param("title") TitleEntity title);
}