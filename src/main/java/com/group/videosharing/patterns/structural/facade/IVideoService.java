package com.group.videosharing.patterns.structural.facade;

import com.group.videosharing.dto.VideoDto;
import java.util.List;

public interface IVideoService {
    List<VideoDto> getVideosByChannel(String channelId);
    VideoDto       getVideoById(String videoId);
    List<VideoDto> getAllPublicVideos();
}
