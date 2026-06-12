package com.group.videosharing.patterns.behavioral.observer;
public record LikeChangedEvent(String videoId, long newCount) {}
