package com.group.videosharing.dto;
import lombok.Data;
@Data
public class CommentRequest {
    private String videoId;
    private String text;
    private String parentId;
    private boolean loggedIn;
}
