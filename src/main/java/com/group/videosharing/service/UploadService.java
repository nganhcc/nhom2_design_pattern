package com.group.videosharing.service;

import com.group.videosharing.domain.VideoEntity;
import com.group.videosharing.dto.UploadVideoRequest;
import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.patterns.behavioral.observer.VideoUploadedEvent;
import com.group.videosharing.patterns.creational.singleton.EventBus;
import com.group.videosharing.repository.VideoRepository;
import org.springframework.stereotype.Service;

@Service
public class UploadService {

    private final VideoRepository videoRepository;
    private final VideoMapper videoMapper;

    public UploadService(VideoRepository videoRepository, VideoMapper videoMapper) {
        this.videoRepository = videoRepository;
        this.videoMapper = videoMapper;
    }

    public VideoDto upload(UploadVideoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }

        VideoEntity.Builder builder = new VideoEntity.Builder()
                .setTitle(request.getTitle())
                .setVisibility(request.getVisibility())
                .setUploaderId(request.getUploaderId());

        if (hasText(request.getDescription())) {
            builder.setDescription(request.getDescription());
        }
        if (hasText(request.getThumbnailUrl())) {
            builder.setThumbnail(request.getThumbnailUrl());
        }
        if (hasText(request.getVideoUrl())) {
            builder.setVideoUrl(request.getVideoUrl());
        }
        if (request.getDurationSeconds() != null) {
            builder.setDurationSeconds(request.getDurationSeconds());
        }
        if (hasText(request.getCategory())) {
            builder.setCategory(request.getCategory());
        }

        VideoEntity savedVideo = videoRepository.save(builder.build());
        EventBus.getInstance().publish(new VideoUploadedEvent(savedVideo));
        return videoMapper.toDto(savedVideo);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
