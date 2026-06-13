package com.group.videosharing.service;

import com.group.videosharing.domain.VideoEntity;
import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.patterns.behavioral.observer.LikeChangedEvent;
import com.group.videosharing.patterns.behavioral.observer.VideoViewedEvent;
import com.group.videosharing.patterns.structural.facade.IVideoService;
import com.group.videosharing.repository.VideoRepository;
import com.group.videosharing.patterns.creational.singleton.EventBus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class VideoService implements IVideoService {

    private static final String PUBLIC_VISIBILITY = "public";

    private final VideoRepository videoRepository;
    private final VideoMapper videoMapper;

    public VideoService(VideoRepository videoRepository, VideoMapper videoMapper) {
        this.videoRepository = videoRepository;
        this.videoMapper = videoMapper;
    }

    @Override
    public List<VideoDto> getVideosByChannel(String channelId) {
        validateId(channelId, "channelId");
        return videoRepository
                .findByUploaderIdAndVisibilityOrderByUploadedAtDesc(channelId, PUBLIC_VISIBILITY)
                .stream()
                .map(videoMapper::toDto)
                .toList();
    }

    @Override
    public VideoDto getVideoById(String videoId) {
        validateId(videoId, "videoId");
        return videoRepository.findById(videoId)
                .map(videoMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("Video not found: " + videoId));
    }

    @Override
    public List<VideoDto> getAllPublicVideos() {
        return videoRepository
                .findByVisibilityOrderByUploadedAtDesc(PUBLIC_VISIBILITY)
                .stream()
                .map(videoMapper::toDto)
                .toList();
    }

    private void validateId(String id, String fieldName) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }
}
