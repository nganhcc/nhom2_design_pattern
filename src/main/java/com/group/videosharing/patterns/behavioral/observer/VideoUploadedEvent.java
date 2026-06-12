package com.group.videosharing.patterns.behavioral.observer;

import com.group.videosharing.domain.VideoEntity;

public record VideoUploadedEvent(VideoEntity video) {}
