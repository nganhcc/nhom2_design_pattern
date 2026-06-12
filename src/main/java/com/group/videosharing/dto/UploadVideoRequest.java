package com.group.videosharing.dto;

import lombok.Data;

@Data
public class UploadVideoRequest {
    private String title;
    private String description;
    private String thumbnailUrl;
    private String visibility;
    private String uploaderId;
    private String videoUrl;
    private Integer durationSeconds;
    private String category;
}
