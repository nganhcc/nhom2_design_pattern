package com.group.videosharing.repository;

import com.group.videosharing.domain.VideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<VideoEntity, String> {
    List<VideoEntity> findByUploaderIdAndVisibilityOrderByUploadedAtDesc(String uploaderId, String visibility);
}
