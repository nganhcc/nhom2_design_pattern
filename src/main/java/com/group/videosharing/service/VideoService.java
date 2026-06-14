package com.group.videosharing.service;

import com.group.videosharing.domain.VideoEntity;
import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.patterns.behavioral.observer.DislikeChangedEvent;
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

    public VideoDto recordView(String videoId) {
        VideoEntity video = fetchVideo(videoId);
        video.incrementViewCount();
        videoRepository.save(video);
        EventBus.getInstance().publish(new VideoViewedEvent(videoId));
        return videoMapper.toDto(video);
    }

    public VideoDto likeVideo(String videoId) {
        VideoEntity video = fetchVideo(videoId);
        video.incrementLikeCount();
        videoRepository.save(video);
        EventBus.getInstance().publish(new LikeChangedEvent(videoId, video.getLikeCount()));
        return videoMapper.toDto(video);
    }

    public VideoDto unlikeVideo(String videoId) {
        VideoEntity video = fetchVideo(videoId);
        video.decrementLikeCount();
        videoRepository.save(video);
        EventBus.getInstance().publish(new LikeChangedEvent(videoId, video.getLikeCount()));
        return videoMapper.toDto(video);
    }

    public VideoDto dislikeVideo(String videoId) {
        VideoEntity video = fetchVideo(videoId);
        video.incrementDislikeCount();
        videoRepository.save(video);
        EventBus.getInstance().publish(new DislikeChangedEvent(videoId, video.getDislikeCount()));
        return videoMapper.toDto(video);
    }

    public VideoDto undislikeVideo(String videoId) {
        VideoEntity video = fetchVideo(videoId);
        video.decrementDislikeCount();
        videoRepository.save(video);
        EventBus.getInstance().publish(new DislikeChangedEvent(videoId, video.getDislikeCount()));
        return videoMapper.toDto(video);
    }

    private VideoEntity fetchVideo(String videoId) {
        validateId(videoId, "videoId");
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new NoSuchElementException("Video not found: " + videoId));
    }

    private void validateId(String id, String fieldName) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }
}
