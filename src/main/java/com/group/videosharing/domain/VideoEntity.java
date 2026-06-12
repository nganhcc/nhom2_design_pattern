package com.group.videosharing.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * VideoEntity — bất biến sau khi tạo qua Builder.
 * TV4 phụ trách: Builder pattern.
 */
@Entity
@Table(name = "videos")
@Getter
@NoArgsConstructor
public class VideoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String thumbnailUrl;

    @Column(nullable = false)
    private String visibility; // "public" | "private"

    @Column(nullable = false)
    private String uploaderId;

    private String videoUrl;

    private int durationSeconds;

    @Column(nullable = false)
    private String category = "General";

    private long viewCount = 0;
    private long likeCount = 0;
    private long dislikeCount = 0;

    @Column(nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    // ── Builder ──────────────────────────────────────────────────────────────

    public static class Builder {
        private String title;
        private String description;
        private String thumbnailUrl;
        private String visibility;
        private String uploaderId;
        private String videoUrl;
        private int durationSeconds;
        private String category = "General";

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setThumbnail(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public Builder setVisibility(String visibility) {
            this.visibility = visibility;
            return this;
        }

        public Builder setUploaderId(String uploaderId) {
            this.uploaderId = uploaderId;
            return this;
        }

        public Builder setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
            return this;
        }

        public Builder setDurationSeconds(int durationSeconds) {
            this.durationSeconds = durationSeconds;
            return this;
        }

        public Builder setCategory(String category) {
            this.category = category;
            return this;
        }

        public VideoEntity build() {
            // Validate tập trung tại đây
            if (title == null || title.isBlank())
                throw new IllegalArgumentException("Title không được trống");
            if (title.length() > 100)
                throw new IllegalArgumentException("Title tối đa 100 ký tự");
            if (visibility == null || (!visibility.equals("public") && !visibility.equals("private")))
                throw new IllegalArgumentException("Visibility phải là 'public' hoặc 'private'");
            if (uploaderId == null || uploaderId.isBlank())
                throw new IllegalArgumentException("UploaderId là bắt buộc");

            VideoEntity v = new VideoEntity();
            v.title = this.title;
            v.description = this.description;
            v.thumbnailUrl = this.thumbnailUrl;
            v.visibility = this.visibility;
            v.uploaderId = this.uploaderId;
            v.videoUrl = this.videoUrl;
            v.durationSeconds = this.durationSeconds;
            v.category = this.category;
            return v;
        }
    }
}
