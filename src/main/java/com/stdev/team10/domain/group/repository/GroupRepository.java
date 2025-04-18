package com.stdev.team10.domain.group.repository;

import com.stdev.team10.domain.group.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {
    boolean existsByGroupName(String groupName);
    GroupEntity findByGroupName(String groupName);
    List<GroupEntity> findByGroupNameContaining(String groupName);
}
