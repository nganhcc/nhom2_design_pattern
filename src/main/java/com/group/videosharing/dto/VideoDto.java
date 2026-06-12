package com.group.videosharing.dto;
import lombok.Data;
@Data
public class VideoDto {
    private String id;
    private String title;
    private String description;
    private String uploaderId;
    private String channelId;
    private String visibility;
    private long   viewCount;
    private long   likeCount;
    private int    duration;
    private String category;
    private String thumbnailUrl;
    private String createdAt;
}
