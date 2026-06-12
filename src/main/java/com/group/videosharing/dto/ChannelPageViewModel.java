package com.group.videosharing.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
@Data
@AllArgsConstructor
public class ChannelPageViewModel {
    private UserDto        channelInfo;
    private List<VideoDto> videos;
    private boolean        isSubscribed;
    private long           subscriberCount;
}
