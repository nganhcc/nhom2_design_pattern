package com.group.videosharing.service;

import com.group.videosharing.domain.VideoEntity;
import com.group.videosharing.dto.VideoDto;
import org.springframework.stereotype.Component;

@Component
public class VideoMapper {

    public VideoDto toDto(VideoEntity video) {
        VideoDto dto = new VideoDto();
        dto.setId(video.getId());
        dto.setTitle(video.getTitle());
        dto.setDescription(video.getDescription());
        dto.setUploaderId(video.getUploaderId());
        dto.setChannelId(video.getUploaderId());
        dto.setVisibility(video.getVisibility());
        dto.setViewCount(video.getViewCount());
        dto.setLikeCount(video.getLikeCount());
        dto.setDuration(video.getDurationSeconds());
        dto.setCategory(video.getCategory());
        dto.setThumbnailUrl(video.getThumbnailUrl());
        dto.setCreatedAt(video.getUploadedAt() != null ? video.getUploadedAt().toString() : null);
        return dto;
    }
}
