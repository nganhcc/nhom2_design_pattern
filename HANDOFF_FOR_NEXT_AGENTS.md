# Handoff Cho Coding Agents Tiếp Theo

## 1. Project Summary

Dự án là backend Spring Boot cho ứng dụng chia sẻ video dạng Mini YouTube, phục vụ môn Design Patterns.

Tech stack hiện tại:

- Java 21
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA
- PostgreSQL theo `application.properties`
- Redis dependency có trong `pom.xml`, nhưng hiện chưa có flow thật dùng Redis
- Maven wrapper: `./mvnw`

Mục tiêu kiến trúc:

- Minh họa 13 design patterns trong một ứng dụng chia sẻ video nhỏ.
- Mỗi thành viên phụ trách một nhóm chức năng/pattern.
- Code hiện tại ưu tiên demo backend, pattern rõ ràng, test pass, chưa phải production-ready YouTube clone.

Trạng thái hiện tại:

- Phần TV4 đã hoàn thành.
- `./mvnw test` pass lần gần nhất với `47 tests`, `0 failures`, `0 errors`.
- Các controller của TV1/TV2/TV3 vẫn còn cần hoàn thiện.

## 2. Completed TV4 Implementation

TV4 phụ trách Channel & Core Infrastructure. Các phần sau đã được implement và có test:

### Singleton + Observer

Files chính:

- `src/main/java/com/group/videosharing/patterns/creational/singleton/EventBus.java`
- `src/main/java/com/group/videosharing/patterns/creational/singleton/SessionManager.java`
- `src/main/java/com/group/videosharing/patterns/behavioral/observer/VideoUploadedEvent.java`
- Các event khác trong `patterns/behavioral/observer`

Đã có:

- `EventBus.getInstance()`
- `subscribe`
- `unsubscribe`
- `publish`
- `clearAllHandlers`
- `SessionManager.getInstance()`
- `login`
- `logout`
- `isLoggedIn`
- `getCurrentUser`
- `getCurrentUserId`
- `requireLogin`

### Facade Cho Trang Kênh

Files chính:

- `src/main/java/com/group/videosharing/patterns/structural/facade/ChannelFacade.java`
- `src/main/java/com/group/videosharing/controller/ChannelController.java`
- `src/main/java/com/group/videosharing/dto/ChannelPageViewModel.java`

Đã có:

- `ChannelFacade.getChannelPage(channelId, viewerId)`
- Gom dữ liệu từ:
  - `IUserService`
  - `IVideoService`
  - `ISubscriptionService`
- Controller endpoint:
  - `GET /api/channels/{channelId}?viewerId=...`

### Builder Cho Upload Video

Files chính:

- `src/main/java/com/group/videosharing/domain/VideoEntity.java`
- `src/main/java/com/group/videosharing/dto/UploadVideoRequest.java`
- `src/main/java/com/group/videosharing/service/UploadService.java`
- `src/main/java/com/group/videosharing/controller/UploadController.java`
- `src/main/java/com/group/videosharing/service/VideoMapper.java`

Đã có:

- Upload metadata video qua JSON.
- Tạo entity bằng `VideoEntity.Builder`.
- Lưu qua `VideoRepository`.
- Publish `VideoUploadedEvent`.
- Trả về `VideoDto`.

Controller endpoint:

- `POST /api/upload`

### Repository + Service Nền

Files chính:

- `src/main/java/com/group/videosharing/repository/UserRepository.java`
- `src/main/java/com/group/videosharing/repository/VideoRepository.java`
- `src/main/java/com/group/videosharing/service/UserService.java`
- `src/main/java/com/group/videosharing/service/VideoService.java`
- `src/main/java/com/group/videosharing/service/SubscriptionService.java`

Đã có:

- JPA repository cho user/video.
- Service implementation cho interface facade.
- `SubscriptionService` dùng in-memory map để demo subscription.

### Demo Data Seeder

File chính:

- `src/main/java/com/group/videosharing/config/DemoDataSeeder.java`

Đã có:

- Seed 2 user/channel demo.
- Seed 5 video public demo.
- Seed subscription in-memory hai chiều.
- Idempotent với user theo `username`.
- Không seed thêm video nếu `videoRepository.count() > 0`.

## 3. Important Files

Entry point:

- `src/main/java/com/group/videosharing/VideosharingApplication.java`

Domain:

- `src/main/java/com/group/videosharing/domain/UserEntity.java`
- `src/main/java/com/group/videosharing/domain/VideoEntity.java`
- `src/main/java/com/group/videosharing/domain/CommentEntity.java`

DTO:

- `src/main/java/com/group/videosharing/dto/UserDto.java`
- `src/main/java/com/group/videosharing/dto/VideoDto.java`
- `src/main/java/com/group/videosharing/dto/ChannelPageViewModel.java`
- `src/main/java/com/group/videosharing/dto/UploadVideoRequest.java`
- `src/main/java/com/group/videosharing/dto/CommentDto.java`
- `src/main/java/com/group/videosharing/dto/CommentRequest.java`
- `src/main/java/com/group/videosharing/dto/ValidationResult.java`

Controllers:

- `src/main/java/com/group/videosharing/controller/ChannelController.java`
- `src/main/java/com/group/videosharing/controller/UploadController.java`
- `src/main/java/com/group/videosharing/controller/SearchController.java`
- `src/main/java/com/group/videosharing/controller/VideoController.java`
- `src/main/java/com/group/videosharing/controller/CommentController.java`

Pattern packages:

- `src/main/java/com/group/videosharing/patterns/creational`
- `src/main/java/com/group/videosharing/patterns/structural`
- `src/main/java/com/group/videosharing/patterns/behavioral`

Tests:

- `src/test/java/com/group/videosharing/config/DemoDataSeederTest.java`
- `src/test/java/com/group/videosharing/controller/ChannelControllerTest.java`
- `src/test/java/com/group/videosharing/controller/UploadControllerTest.java`
- `src/test/java/com/group/videosharing/service`
- `src/test/java/com/group/videosharing/patterns`

## 4. Current APIs

### Get Channel Page

Endpoint:

```http
GET /api/channels/{channelId}?viewerId={viewerId}
```

Example:

```http
GET /api/channels/user-1?viewerId=user-2
```

Success response:

```json
{
  "channelInfo": {
    "id": "user-1",
    "username": "demo_creator",
    "email": "demo.creator@example.com",
    "avatarUrl": "https://example.com/avatars/demo-creator.png",
    "channelId": "user-1"
  },
  "videos": [
    {
      "id": "video-1",
      "title": "Design Patterns trong Mini YouTube",
      "description": "Demo cách áp dụng Facade, Builder, Observer và Singleton.",
      "uploaderId": "user-1",
      "channelId": "user-1",
      "visibility": "public",
      "viewCount": 0,
      "likeCount": 0,
      "duration": 480,
      "category": "Education",
      "thumbnailUrl": "https://example.com/thumbs/design-patterns.jpg",
      "createdAt": "2026-06-12T10:30"
    }
  ],
  "subscribed": true,
  "subscriberCount": 1
}
```

Error responses:

```http
400 Bad Request
channelId must not be blank
```

```http
404 Not Found
User not found: {channelId}
```

### Upload Video Metadata

Endpoint:

```http
POST /api/upload
Content-Type: application/json
```

Request:

```json
{
  "title": "Upload Video bằng Builder Pattern",
  "description": "Tạo metadata video bằng fluent builder.",
  "thumbnailUrl": "https://example.com/thumbs/upload-builder.jpg",
  "visibility": "public",
  "uploaderId": "user-1",
  "videoUrl": "https://example.com/videos/upload-builder.mp4",
  "durationSeconds": 420,
  "category": "Design Patterns"
}
```

Required fields:

- `title`
- `visibility`
- `uploaderId`

Accepted visibility values:

- `public`
- `private`

Success response:

```http
201 Created
```

```json
{
  "id": "generated-video-id",
  "title": "Upload Video bằng Builder Pattern",
  "description": "Tạo metadata video bằng fluent builder.",
  "uploaderId": "user-1",
  "channelId": "user-1",
  "visibility": "public",
  "viewCount": 0,
  "likeCount": 0,
  "duration": 420,
  "category": "Design Patterns",
  "thumbnailUrl": "https://example.com/thumbs/upload-builder.jpg",
  "createdAt": "2026-06-12T10:30"
}
```

Error response:

```http
400 Bad Request
Title không được trống
```

Other possible builder validation messages:

- `Title tối đa 100 ký tự`
- `Visibility phải là 'public' hoặc 'private'`
- `UploaderId là bắt buộc`
- `request must not be null`

## 5. Test Status

Command:

```bash
./mvnw test
```

Last known result:

```text
Tests run: 47, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Important testing constraint:

- Do not use Mockito unless you first fix the environment/JVM agent issue.
- Mockito inline self-attach failed earlier on this machine.
- Existing tests use manual fake classes or Java dynamic proxy for repositories.
- Follow that pattern for new tests.

## 6. Architecture Conventions

Use these conventions unless the user explicitly changes the design:

- `channelId == UserEntity.id`
- `VideoEntity.uploaderId == channelId`
- `VideoDto.channelId = VideoEntity.uploaderId`
- `UserDto.channelId = UserEntity.id`
- Channel page only lists `public` videos.
- Upload currently accepts JSON metadata only, not `MultipartFile`.
- Subscription is in-memory via `SubscriptionService`.
- Auth/session is in-memory via `SessionManager`.
- Eventing is in-memory via `EventBus`.
- Pattern implementation should remain visible and easy to explain for coursework.

Design style:

- Keep business logic in service/facade classes.
- Controllers should stay thin.
- Keep DTO mapping in mapper/helper classes when reused.
- Add focused unit tests for each pattern/feature.
- Avoid broad refactors unrelated to the current member's scope.

## 7. Constraints And Known Limitations

Known limitations:

- No real login/JWT.
- No persistent subscription table.
- No actual video file upload/storage.
- No frontend implementation in this repo.
- Redis is configured as dependency but not meaningfully used yet.
- PostgreSQL must exist locally if running the full app normally.
- `SearchController`, `VideoController`, and `CommentController` still need implementation.

Important caution:

- There is a separate demo class at `patterns/creational/builder/VideoEntity.java`.
- Real app flow should use `domain.VideoEntity`, not the pattern-demo duplicate.
- `VideoUploadedEvent` already uses `domain.VideoEntity`.

## 8. Next Implementation Plan For TV1

TV1 scope:

- Trang chủ / đề xuất
- Tìm kiếm & lọc
- Patterns:
  - Strategy
  - Decorator
  - Template Method

Recommended implementation:

- Complete `SearchController`.
- Add a search/home service if needed, preferably under `service`.
- Use existing strategy classes in `patterns/behavioral/strategy`.
- Use existing decorator classes in `patterns/structural/decorator`.
- Use existing template classes in `patterns/behavioral/template`.
- Source data can initially come from `VideoRepository` or `VideoService`.

Suggested APIs:

```http
GET /api/search?q={keyword}&sort=relevance|date|views&category={category}&minDuration={seconds}&maxDuration={seconds}
```

```http
GET /api/home?viewerId={viewerId}
```

Acceptance criteria:

- Search by keyword returns matching public videos.
- `sort=relevance`, `sort=date`, and `sort=views` select different `SearchStrategy`.
- Category/duration/date filters are composed through decorators.
- Home endpoint returns a basic recommendation list.
- Template Method classes are used in the flow, not only left as unused examples.
- Add tests for strategy switching, decorator filtering, and controller success response.
- `./mvnw test` remains green.

## 9. Next Implementation Plan For TV2

TV2 scope:

- Xem video
- Like / dislike
- Subscribe / unsubscribe
- Patterns:
  - State
  - Command
  - Proxy

Recommended implementation:

- Complete `VideoController`.
- Use `VideoPlayerContext` and state classes for player state demo.
- Use command classes for like/dislike/subscribe/unsubscribe.
- Use `InteractionServiceProxy` for auth check through `SessionManager.requireLogin()`.
- Connect subscribe/unsubscribe to current `SubscriptionService`.
- Publish relevant events via `EventBus`:
  - `VideoViewedEvent`
  - `LikeChangedEvent`
  - `SubscriptionChangedEvent`

Suggested APIs:

```http
GET /api/videos/{videoId}
POST /api/videos/{videoId}/view
POST /api/videos/{videoId}/like
POST /api/videos/{videoId}/dislike
POST /api/channels/{channelId}/subscribe
POST /api/channels/{channelId}/unsubscribe
POST /api/interactions/undo
```

Acceptance criteria:

- Video lookup returns `VideoDto`.
- Like/dislike actions require login via proxy.
- Subscribe/unsubscribe updates `SubscriptionService`.
- CommandHistory can undo at least the latest like or subscribe action.
- State pattern demo can transition through core player states.
- Events are published for view/like/subscription actions.
- Add tests for unauthenticated interaction, command execute/undo, event publish, and state transition.
- `./mvnw test` remains green.

## 10. Next Implementation Plan For TV3

TV3 scope:

- Comment system
- Patterns:
  - Composite
  - Iterator
  - Chain of Responsibility

Recommended implementation:

- Complete `CommentController`.
- Add `CommentRepository extends JpaRepository<CommentEntity, String>`.
- Add `CommentService`.
- Use existing composite classes for building/rendering comment tree.
- Use iterator classes for flat/threaded views.
- Use Chain of Responsibility validators before saving comments:
  - auth check
  - empty content
  - max length
  - spam filter
- Publish `CommentAddedEvent` after successful comment creation.

Suggested APIs:

```http
GET /api/comments/video/{videoId}?view=flat|threaded
POST /api/comments
DELETE /api/comments/{commentId}
```

Suggested request for create comment:

```json
{
  "videoId": "video-1",
  "authorId": "user-1",
  "text": "Bài này dễ hiểu",
  "parentId": null
}
```

Acceptance criteria:

- Add top-level comment.
- Add reply comment with `parentId`.
- Validate comment request through CoR before saving.
- Return flat or threaded comment list based on query param.
- Composite/Iterator classes are integrated in service flow.
- Delete comment by id.
- Publish `CommentAddedEvent` after successful add.
- Add tests for validation failure, top-level comment, reply comment, flat/threaded ordering, and event publish.
- `./mvnw test` remains green.

## 11. Suggested Work Order For Next Agents

Recommended order:

1. TV2 subscribe/unsubscribe integration, because it improves channel page demo immediately.
2. TV1 search endpoint, because seeded videos already support search/filter demos.
3. TV3 comment service/controller, because comment has the most data-shaping logic.
4. Optional final integration pass: ensure demo flow covers all 13 patterns.

Demo flow target:

1. Open channel page.
2. Search videos with sort/filter.
3. View a video.
4. Like and undo.
5. Subscribe and see channel subscriber count update.
6. Add comment and render comment tree.
7. Upload video and observe `VideoUploadedEvent`.

## 12. Final Reminder

Keep the implementation simple and explainable. This project is for a Design Patterns course, so pattern visibility matters more than production-grade infrastructure. Do not hide all pattern behavior behind generic abstractions that make the demo harder to present.
