# Room Management REST API Specifications & Contracts — KronosTT

**Track:** UI + Data (Bhavishya Raghav)  
**Sprint:** Master Data & UI Design (Form-2: CRUD operations on rooms)  
**Date:** October 2026

---

## 1. Overview & Endpoints

The Room Management module exposes full REST CRUD operations under `/api/v1/rooms`:

| Method | Path | Request Body | Response Status | Purpose |
|---|---|---|---|---|
| `GET` | `/api/v1/rooms` | — | `200 OK` | Paginated listing with sorting (`name`, `capacity`) |
| `GET` | `/api/v1/rooms/{id}` | — | `200 OK` / `404 Not Found` | Fetch single room by primary key |
| `POST` | `/api/v1/rooms` | `RoomDto` | `201 Created` / `400 Bad Request` / `409 Conflict` | Create new classroom or laboratory |
| `PUT` | `/api/v1/rooms/{id}` | `RoomDto` | `200 OK` / `400 Bad Request` / `404 Not Found` / `409 Conflict` | Update existing room attributes |
| `DELETE` | `/api/v1/rooms/{id}` | — | `204 No Content` / `404 Not Found` / `409 Conflict` | Delete room (blocked if referenced) |

---

## 2. Validation & Business Rules

1. **Uniqueness:** Room name is case-insensitively unique across the entire institution.
2. **Capacity Constraints:** Must be an integer greater than or equal to 1.
3. **Foreign Key Integrity:** Deletion is rejected with `409 Conflict` if the room is assigned in active timetable sessions (`sessions.preferred_room_id`) or scheduled timetable slots (`scheduled_sessions.room_id`).
4. **Audit Trail:** All create, update, and delete actions trigger `AuditEntityListener` writing to the central `audit_log` table.
