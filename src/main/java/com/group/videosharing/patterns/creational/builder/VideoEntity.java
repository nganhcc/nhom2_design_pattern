package com.group.videosharing.patterns.creational.builder;

/**
 * Builder — Pattern 11
 * Bất biến sau build(). Validate tập trung tại build().
 */
public final class VideoEntity {
    private final String title;
    private final String description;
    private final String thumbnailUrl;
    private final String visibility;
    private final String uploaderId;

    private VideoEntity(Builder b) {
        this.title        = b.title;
        this.description  = b.description;
        this.thumbnailUrl = b.thumbnailUrl;
        this.visibility   = b.visibility;
        this.uploaderId   = b.uploaderId;
    }

    public String getTitle()        { return title; }
    public String getDescription()  { return description; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public String getVisibility()   { return visibility; }
    public String getUploaderId()   { return uploaderId; }

    public static class Builder {
        private String title;
        private String description;
        private String thumbnailUrl;
        private String visibility;
        private String uploaderId;

        public Builder setTitle(String title)           { this.title        = title;        return this; }
        public Builder setDescription(String desc)      { this.description  = desc;         return this; }
        public Builder setThumbnail(String url)         { this.thumbnailUrl = url;          return this; }
        public Builder setVisibility(String visibility) { this.visibility   = visibility;   return this; }
        public Builder setUploaderId(String uploaderId) { this.uploaderId   = uploaderId;   return this; }

        public VideoEntity build() {
            if (title == null || title.isBlank())
                throw new IllegalArgumentException("Title không được trống");
            if (title.length() > 100)
                throw new IllegalArgumentException("Title tối đa 100 ký tự");
            if (visibility == null || visibility.isBlank())
                throw new IllegalArgumentException("Visibility là bắt buộc (public/private)");
            return new VideoEntity(this);
        }
    }
}
