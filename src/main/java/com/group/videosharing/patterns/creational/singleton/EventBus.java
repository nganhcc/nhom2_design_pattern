package com.group.videosharing.patterns.creational.singleton;

import com.group.videosharing.patterns.behavioral.observer.IEventHandler;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Singleton — Pattern 13
 * Observer hub — Pattern 12
 */
public class EventBus {

    private static volatile EventBus instance;
    private final Map<Class<?>, List<IEventHandler<?>>> handlers = new ConcurrentHashMap<>();

    private EventBus() {}

    public static EventBus getInstance() {
        if (instance == null) {
            synchronized (EventBus.class) {
                if (instance == null) instance = new EventBus();
            }
        }
        return instance;
    }

    public <T> void subscribe(Class<T> eventType, IEventHandler<T> handler) {
        Objects.requireNonNull(eventType, "eventType must not be null");
        Objects.requireNonNull(handler, "handler must not be null");
        handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(handler);
    }

    public <T> void unsubscribe(Class<T> eventType, IEventHandler<T> handler) {
        Objects.requireNonNull(eventType, "eventType must not be null");
        Objects.requireNonNull(handler, "handler must not be null");

        List<IEventHandler<?>> list = handlers.get(eventType);
        if (list != null) {
            list.remove(handler);
            if (list.isEmpty()) {
                handlers.remove(eventType);
            }
        }
    }

    public void clearAllHandlers() {
        handlers.clear();
    }

    @SuppressWarnings("unchecked")
    public <T> void publish(T event) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }

        List<IEventHandler<?>> list = handlers.get(event.getClass());
        if (list != null) list.forEach(h -> ((IEventHandler<T>) h).handle(event));
    }
}
