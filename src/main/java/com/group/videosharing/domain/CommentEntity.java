package com.group.videosharing.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String videoId;

    @Column(nullable = false)
    private String authorId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String parentId; // null = top-level comment

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
