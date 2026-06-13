package com.group.videosharing.controller;

import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.patterns.behavioral.command.CommandHistory;
import com.group.videosharing.patterns.behavioral.command.DislikeCommand;
import com.group.videosharing.patterns.behavioral.command.ICommand;
import com.group.videosharing.patterns.behavioral.command.LikeCommand;
import com.group.videosharing.patterns.behavioral.command.SubscribeCommand;
import com.group.videosharing.patterns.behavioral.command.UnsubscribeCommand;
import com.group.videosharing.patterns.behavioral.state.VideoPlayerContext;
import com.group.videosharing.patterns.structural.proxy.IInteractionService;
import com.group.videosharing.service.SubscriptionService;
import com.group.videosharing.service.VideoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * TV2 — Video & Interactions, State, Command, Proxy
 */
@RestController
@RequestMapping("/api")
public class VideoController {

    private final VideoService videoService;
    private final IInteractionService interactionService;
    private final SubscriptionService subscriptionService;
    private final CommandHistory commandHistory;
    private final VideoPlayerContext playerContext = new VideoPlayerContext();

    public VideoController(VideoService videoService,
                           IInteractionService interactionService,
                           SubscriptionService subscriptionService,
                           CommandHistory commandHistory) {
        this.videoService = videoService;
        this.interactionService = interactionService;
        this.subscriptionService = subscriptionService;
        this.commandHistory = commandHistory;
    }

    @GetMapping("/videos/{videoId}")
    public ResponseEntity<?> getVideo(@PathVariable String videoId) {
        try {
            VideoDto result = videoService.getVideoById(videoId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/videos/{videoId}/view")
    public ResponseEntity<?> viewVideo(@PathVariable String videoId) {
        try {
            VideoDto result = videoService.recordView(videoId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/videos/{videoId}/like")
    public ResponseEntity<?> likeVideo(@PathVariable String videoId) {
        return executeInteraction(new LikeCommand(interactionService, videoId), videoId);
    }

    @PostMapping("/videos/{videoId}/dislike")
    public ResponseEntity<?> dislikeVideo(@PathVariable String videoId) {
        return executeInteraction(new DislikeCommand(interactionService, videoId), videoId);
    }

    @PostMapping("/channels/{channelId}/subscribe")
    public ResponseEntity<?> subscribeChannel(@PathVariable String channelId) {
        ResponseEntity<?> response = executeInteraction(new SubscribeCommand(interactionService, channelId));
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(Map.of(
                    "channelId", channelId,
                    "subscribed", true,
                    "subscriberCount", subscriptionService.getSubscriberCount(channelId)
            ));
        }
        return response;
    }

    @PostMapping("/channels/{channelId}/unsubscribe")
    public ResponseEntity<?> unsubscribeChannel(@PathVariable String channelId) {
        ResponseEntity<?> response = executeInteraction(new UnsubscribeCommand(interactionService, channelId));
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(Map.of(
                    "channelId", channelId,
                    "subscribed", false,
                    "subscriberCount", subscriptionService.getSubscriberCount(channelId)
            ));
        }
        return response;
    }

    @PostMapping("/interactions/undo")
    public ResponseEntity<?> undoInteraction() {
        try {
            commandHistory.undo();
            return ResponseEntity.ok(Map.of("status", "undone"));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }

    @PostMapping("/videos/{videoId}/play")
    public ResponseEntity<?> playVideo(@PathVariable String videoId) {
        playerContext.play();
        return ResponseEntity.ok(Map.of("state", playerContext.getState().getClass().getSimpleName()));
    }

    @PostMapping("/videos/{videoId}/pause")
    public ResponseEntity<?> pauseVideo(@PathVariable String videoId) {
        playerContext.pause();
        return ResponseEntity.ok(Map.of("state", playerContext.getState().getClass().getSimpleName()));
    }

    @PostMapping("/videos/{videoId}/seek")
    public ResponseEntity<?> seekVideo(@PathVariable String videoId, @RequestParam long timeMs) {
        playerContext.seek(timeMs);
        return ResponseEntity.ok(Map.of("state", playerContext.getState().getClass().getSimpleName()));
    }

    @PostMapping("/videos/{videoId}/end")
    public ResponseEntity<?> endVideo(@PathVariable String videoId) {
        playerContext.end();
        return ResponseEntity.ok(Map.of("state", playerContext.getState().getClass().getSimpleName()));
    }

    private ResponseEntity<?> executeInteraction(ICommand command) {
        try {
            commandHistory.push(command);
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    private ResponseEntity<?> executeInteraction(ICommand command, String videoId) {
        try {
            commandHistory.push(command);
            return ResponseEntity.ok(videoService.getVideoById(videoId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
