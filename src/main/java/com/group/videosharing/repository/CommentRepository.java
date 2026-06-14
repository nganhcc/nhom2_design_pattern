package com.group.videosharing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group.videosharing.domain.CommentEntity;

public interface CommentRepository
        extends JpaRepository<CommentEntity, String> {

    List<CommentEntity> findByVideoId(String videoId);
    List<CommentEntity> findByParentId(String parentId);
}
