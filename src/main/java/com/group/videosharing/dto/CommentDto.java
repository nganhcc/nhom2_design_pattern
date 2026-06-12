package com.group.videosharing.dto;
import lombok.Data;
import java.util.List;
@Data
public class CommentDto {
    private String id;
    private String videoId;
    private String authorId;
    private String text;
    private String parentId;
    private String createdAt;
    private List<CommentDto> replies;
}
