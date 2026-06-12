package com.group.videosharing.controller;

import com.group.videosharing.dto.ChannelPageViewModel;
import com.group.videosharing.patterns.structural.facade.ChannelFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

/**
 * TV4 — ChannelFacade
 */
@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelFacade channelFacade;

    public ChannelController(ChannelFacade channelFacade) {
        this.channelFacade = channelFacade;
    }

    @GetMapping("/{channelId}")
    public ResponseEntity<?> getChannelPage(@PathVariable String channelId,
                                            @RequestParam(required = false) String viewerId) {
        try {
            ChannelPageViewModel viewModel = channelFacade.getChannelPage(channelId, viewerId);
            return ResponseEntity.ok(viewModel);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

}
