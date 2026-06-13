# TV2 Implementation Plan

## Mục tiêu
Hoàn thiện chức năng Video & Interactions cho TV2, bao gồm:
- Xem video
- Like / Dislike
- Subscribe / Unsubscribe
- Undo hành động tương tác
- Minh họa State pattern cho trạng thái trình phát
- Minh họa Command pattern cho các thao tác tương tác
- Minh họa Proxy pattern cho auth check

## Những phần đã triển khai

### 1. Controller
Đã hoàn thiện `VideoController` với các endpoint chính:
- `GET /api/videos/{videoId}`
- `POST /api/videos/{videoId}/view`
- `POST /api/videos/{videoId}/like`
- `POST /api/videos/{videoId}/dislike`
- `POST /api/channels/{channelId}/subscribe`
- `POST /api/channels/{channelId}/unsubscribe`
- `POST /api/interactions/undo`
- `POST /api/videos/{videoId}/play`
- `POST /api/videos/{videoId}/pause`
- `POST /api/videos/{videoId}/seek`
- `POST /api/videos/{videoId}/end`

### 2. Service và Domain
- `VideoService` mở rộng:
  - `recordView`
  - `likeVideo`
  - `unlikeVideo`
  - `dislikeVideo`
  - `undislikeVideo`
- `VideoEntity` thêm các mutator thay đổi:
  - `incrementViewCount`
  - `incrementLikeCount`
  - `decrementLikeCount`
  - `incrementDislikeCount`
  - `decrementDislikeCount`
- Event publish:
  - `VideoViewedEvent`
  - `LikeChangedEvent`
  - `SubscriptionChangedEvent`

### 3. Pattern implementation
- Command:
  - `LikeCommand`, `DislikeCommand`, `SubscribeCommand`, `UnsubscribeCommand`
  - `CommandHistory` là component có thể undo
- Proxy:
  - `IInteractionService` mở rộng thêm các hành vi undo / subscribe / unsubscribe
  - `InteractionServiceProxy` kiểm tra auth qua `SessionManager.requireLogin()`
  - `RealInteractionService` thực thi hành vi thực tế và phát event
- State:
  - Hoàn thành logic các trạng thái player:
    - `IdleState`
    - `LoadingState`
    - `PlayingState`
    - `PausedState`
    - `EndedState`

### 4. Kiểm thử
- Thêm/ mở rộng test cho:
  - `VideoServiceTest`
  - `VideoControllerTest`
- Kết quả: `./mvnw.cmd -q test` chạy thành công

## Kết quả hiện tại
- TV2 đã có flow cơ bản cho video và interaction
- Patterns Command / Proxy / State đã được tích hợp vào luồng
- Actions like/dislike/subscribe/unsubscribe hiện có thể thực thi và undo (về mặt command)
- Response API trả về trạng thái và count cần thiết

## Đề xuất bước tiếp theo
1. Hoàn thiện `undo` cho subscribe/unsubscribe trong controller và command
2. Thêm event handler demo hoặc log subscriber changes
3. Bổ sung endpoint lấy trạng thái player hiện tại
4. Kiểm tra thực tế với `SessionManager` khi chưa login và behavior của proxy
5. Mở rộng `CommentController` để tiếp tục TV3
