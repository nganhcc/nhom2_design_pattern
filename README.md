# Tài Liệu Dự Án Mini YouTube - Design Patterns

## 1. Tổng Quan

Dự án là backend Spring Boot cho một nền tảng chia sẻ video quy mô nhỏ, mô phỏng các chức năng cốt lõi của YouTube ở mức demo:

- Trang chủ và gợi ý video.
- Tìm kiếm, sắp xếp, lọc video.
- Xem video, tăng lượt xem.
- Like, dislike, subscribe, unsubscribe, undo thao tác.
- Trang kênh.
- Upload metadata video.
- Bình luận, trả lời bình luận, xem bình luận dạng phẳng hoặc dạng cây.

Mục tiêu chính của dự án là minh họa cách áp dụng các Design Patterns trong một bài toán backend thực tế, không phải xây dựng một hệ thống production hoàn chỉnh.

## 2. Công Nghệ Sử Dụng

- Java 21
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Redis dependency có trong project, hiện dùng ở mức cấu hình/demo
- Maven Wrapper
- Docker Compose cho PostgreSQL và Redis

## 3. Cách Chạy Dự Án

### 3.1. Chạy database

```bash
docker compose up -d
```

`docker-compose.yml` sẽ bật:

- PostgreSQL tại port `5432`
- Redis tại port `6379`

Thông tin PostgreSQL mặc định:

```text
Database: videosharing
Username: postgres
Password: postgres
```

### 3.2. Chạy backend

```bash
./mvnw spring-boot:run
```

Server mặc định chạy tại:

```text
http://localhost:8080
```

### 3.3. Chạy test

```bash
./mvnw test
```

Kết quả kiểm tra gần nhất:

```text
Tests run: 109, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### 3.4. Chạy frontend

Frontend React + Vite nằm trong thư mục `frontend/`.

Cài dependencies lần đầu:

```bash
cd frontend
npm install
```

Chạy frontend dev server:

```bash
npm run dev
```

Frontend chạy tại:

```text
http://localhost:5173
```

Vite đã cấu hình proxy `/api` sang backend:

```text
http://localhost:8080
```

Build frontend:

```bash
npm run build
```

## 4. Cấu Trúc Chức Năng Theo Thành Viên

### TV1 - Trang Chủ, Tìm Kiếm Và Lọc

Chức năng:

- `GET /api/home`
- `GET /api/search`
- Gợi ý video cho guest/user.
- Tìm kiếm theo keyword.
- Sắp xếp theo relevance, ngày upload, lượt xem.
- Lọc theo category, duration, date range.

Design Patterns:

- Strategy
- Decorator
- Template Method

Các lớp chính:

- `SearchController`
- `SearchService`
- `SearchContext`
- `RelevanceSearchStrategy`
- `DateSearchStrategy`
- `ViewCountSearchStrategy`
- `CategoryFilterDecorator`
- `DurationFilterDecorator`
- `DateRangeFilterDecorator`
- `HomePageLoader`
- `SearchResultLoader`

### TV2 - Xem Video Và Tương Tác

Chức năng:

- Xem thông tin video.
- Tăng view count.
- Like, unlike qua undo.
- Dislike, undislike qua undo.
- Subscribe, unsubscribe.
- Undo thao tác gần nhất.
- Demo trạng thái player: play, pause, seek, end.
- Demo login/logout để test proxy auth.

Design Patterns:

- State
- Command
- Proxy

Các lớp chính:

- `VideoController`
- `AuthController`
- `VideoService`
- `CommandHistory`
- `LikeCommand`
- `DislikeCommand`
- `SubscribeCommand`
- `UnsubscribeCommand`
- `VideoPlayerContext`
- `InteractionServiceProxy`
- `RealInteractionService`

### TV3 - Comment System

Chức năng:

- Thêm comment top-level.
- Thêm reply bằng `parentId`.
- Lấy comment dạng `flat`.
- Lấy comment dạng `threaded`.
- Xóa comment và toàn bộ reply con.
- Validate comment trước khi lưu.
- Publish event sau khi thêm comment.

Design Patterns:

- Chain of Responsibility
- Composite
- Iterator

Các lớp chính:

- `CommentController`
- `CommentService`
- `CommentRepository`
- `CommentHandler`
- `AuthCheckHandler`
- `VideoIdRequiredHandler`
- `EmptyContentHandler`
- `MaxLengthHandler`
- `SpamFilterHandler`
- `Comment`
- `CommentThread`
- `CommentCollection`
- `FlatCommentIterator`
- `DepthFirstCommentIterator`

### TV4 - Channel, Upload Và Core Infrastructure

Chức năng:

- Trang kênh.
- Upload metadata video.
- Seed dữ liệu demo.
- Quản lý session demo.
- Event bus demo.

Design Patterns:

- Builder
- Facade
- Singleton
- Observer

Các lớp chính:

- `ChannelController`
- `UploadController`
- `ChannelFacade`
- `UploadService`
- `VideoEntity.Builder`
- `SessionManager`
- `EventBus`
- `DemoDataSeeder`

## 5. Danh Sách Design Patterns

| Nhóm | Pattern | Áp dụng trong dự án |
| --- | --- | --- |
| Behavioral | Strategy | Chọn chiến lược search/sort/recommendation |
| Structural | Decorator | Gắn thêm filter cho search result |
| Behavioral | Template Method | Chuẩn hóa flow load page/search/home |
| Behavioral | State | Trạng thái video player |
| Behavioral | Command | Like/dislike/subscribe/unsubscribe và undo |
| Structural | Proxy | Kiểm tra đăng nhập trước interaction |
| Structural | Composite | Cây comment và reply |
| Behavioral | Iterator | Duyệt comment dạng flat/threaded |
| Behavioral | Chain of Responsibility | Validate comment |
| Structural | Facade | Gom dữ liệu trang kênh |
| Creational | Builder | Tạo `VideoEntity` khi upload |
| Behavioral | Observer | EventBus và các event của hệ thống |
| Creational | Singleton | `SessionManager`, `EventBus` |

## 6. API Documentation

### 6.1. Auth Demo API

Login:

```bash
curl -X POST "http://localhost:8080/api/auth/login?userId=user-1"
```

Get current user:

```bash
curl "http://localhost:8080/api/auth/me"
```

Logout:

```bash
curl -X POST "http://localhost:8080/api/auth/logout"
```

### 6.2. Home Và Search

Home guest:

```bash
curl "http://localhost:8080/api/home"
```

Home user:

```bash
curl "http://localhost:8080/api/home?viewerId=user-1"
```

Search cơ bản:

```bash
curl "http://localhost:8080/api/search?q=builder&sort=relevance"
```

Search có filter:

```bash
curl "http://localhost:8080/api/search?q=pattern&sort=views&category=Design%20Patterns&minDuration=100&maxDuration=500"
```

Search theo date range:

```bash
curl "http://localhost:8080/api/search?sort=date&from=2026-06-12T00:00&to=2026-06-14T23:59"
```

### 6.3. Channel API

Lấy trang kênh:

```bash
curl "http://localhost:8080/api/channels/{channelId}"
```

Lấy trang kênh với viewer:

```bash
curl "http://localhost:8080/api/channels/{channelId}?viewerId={viewerId}"
```

Ghi chú: `channelId` hiện tương đương `UserEntity.id` hoặc `VideoDto.uploaderId`.

### 6.4. Upload API

Upload metadata video:

```bash
curl -X POST "http://localhost:8080/api/upload" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Upload Video bằng Builder Pattern",
    "description": "Tạo metadata video bằng fluent builder.",
    "thumbnailUrl": "https://example.com/thumbs/upload-builder.jpg",
    "visibility": "public",
    "uploaderId": "{uploaderId}",
    "videoUrl": "https://example.com/videos/upload-builder.mp4",
    "durationSeconds": 420,
    "category": "Design Patterns"
  }'
```

Validation chính:

- `title` không được trống.
- `title` tối đa 100 ký tự.
- `visibility` phải là `public` hoặc `private`.
- `uploaderId` là bắt buộc.

### 6.5. Video Và Interaction API

Lấy video:

```bash
curl "http://localhost:8080/api/videos/{videoId}"
```

Tăng view:

```bash
curl -X POST "http://localhost:8080/api/videos/{videoId}/view"
```

Like video:

```bash
curl -X POST "http://localhost:8080/api/videos/{videoId}/like"
```

Dislike video:

```bash
curl -X POST "http://localhost:8080/api/videos/{videoId}/dislike"
```

Subscribe kênh:

```bash
curl -X POST "http://localhost:8080/api/channels/{channelId}/subscribe"
```

Unsubscribe kênh:

```bash
curl -X POST "http://localhost:8080/api/channels/{channelId}/unsubscribe"
```

Undo thao tác interaction gần nhất:

```bash
curl -X POST "http://localhost:8080/api/interactions/undo"
```

Ghi chú: `like`, `dislike`, `subscribe`, `unsubscribe`, `undo` cần login trước qua `/api/auth/login`.

### 6.6. Video Player State API

```bash
curl -X POST "http://localhost:8080/api/videos/{videoId}/play"
curl -X POST "http://localhost:8080/api/videos/{videoId}/pause"
curl -X POST "http://localhost:8080/api/videos/{videoId}/seek?timeMs=30000"
curl -X POST "http://localhost:8080/api/videos/{videoId}/end"
```

Các state demo:

- `IdleState`
- `LoadingState`
- `PlayingState`
- `PausedState`
- `EndedState`

### 6.7. Comment API

Login trước khi thêm comment:

```bash
curl -X POST "http://localhost:8080/api/auth/login?userId=user-1"
```

Thêm top-level comment:

```bash
curl -X POST "http://localhost:8080/api/comments" \
  -H "Content-Type: application/json" \
  -d '{
    "videoId": "{videoId}",
    "text": "Bài này dễ hiểu",
    "parentId": null
  }'
```

Thêm reply:

```bash
curl -X POST "http://localhost:8080/api/comments" \
  -H "Content-Type: application/json" \
  -d '{
    "videoId": "{videoId}",
    "text": "Mình đồng ý",
    "parentId": "{commentId}"
  }'
```

Lấy comment dạng threaded:

```bash
curl "http://localhost:8080/api/comments/video/{videoId}?view=threaded"
```

Lấy comment dạng flat:

```bash
curl "http://localhost:8080/api/comments/video/{videoId}?view=flat"
```

Xóa comment:

```bash
curl -X DELETE "http://localhost:8080/api/comments/{commentId}"
```

Khi xóa parent comment, hệ thống xóa cả các reply con để tránh comment mồ côi.

## 7. Demo Flow Gợi Ý

1. Chạy database và backend.
2. Gọi `GET /api/home` để lấy `videoId` và `channelId`.
3. Gọi `GET /api/search` để demo TV1.
4. Gọi `GET /api/channels/{channelId}` để demo TV4 Facade.
5. Login bằng `/api/auth/login`.
6. Like/dislike/subscribe rồi undo để demo TV2.
7. Thêm comment/reply và gọi `view=flat|threaded` để demo TV3.
8. Upload metadata video để demo Builder.

## 8. Testing

Dự án có unit/controller tests cho các module chính:

- Auth/session/event bus.
- Upload service/controller.
- Channel facade/controller.
- Search service/controller và strategy/decorator.
- Video interaction/controller, command, proxy, state.
- Comment service/controller, CoR, iterator.

Chạy toàn bộ test:

```bash
./mvnw test
```

Kết quả gần nhất:

```text
Tests run: 109, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 9. Giới Hạn Hiện Tại

Các giới hạn này phù hợp với scope demo môn Design Patterns:

- Chưa có JWT hoặc hệ thống auth production.
- `SessionManager` là singleton in-memory.
- `SubscriptionService` là in-memory, chưa lưu DB.
- Upload hiện chỉ nhận metadata JSON, chưa upload file thật.
- Redis mới có dependency/cấu hình, chưa có flow nghiệp vụ quan trọng.
- Player state là API demo, chưa có frontend/video player thật.
- Comment không validate `videoId` có tồn tại trong `VideoRepository`, chỉ validate không trống.

## 10. Ghi Chú Khi Test Bằng Dữ Liệu Thật

`DemoDataSeeder` tạo user/video với UUID tự sinh. Vì vậy không nên hard-code `user-1` hoặc `video-1` khi test trên database thật.

Cách lấy id thật:

```bash
curl "http://localhost:8080/api/home"
```

Từ response, lấy:

- `id` làm `videoId`
- `uploaderId` hoặc `channelId` làm `channelId`

Sau đó dùng các id này để test các API còn lại.
