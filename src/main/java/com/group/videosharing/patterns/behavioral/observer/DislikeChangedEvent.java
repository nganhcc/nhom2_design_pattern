package com.group.videosharing.patterns.behavioral.observer;

public record DislikeChangedEvent(String videoId, long newCount) {}
