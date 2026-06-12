package com.group.videosharing.patterns.behavioral.observer;

/** Observer — Pattern 12 */
public interface IEventHandler<T> {
    void handle(T event);
}
