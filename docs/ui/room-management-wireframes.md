# Room & Infrastructure Management Wireframe Specifications

**Author:** Bhavishya Raghav (Backend & Frontend Lead, Master Data & UI)  
**Project:** SKIT-CSE-2023-2027-05 / KronosTT  
**Date:** August 23, 2026  
**Status:** Completed  
**Sprint:** Sprint 2 — Master Data & UI Design  

---

## 1. Overview & Purpose

Classrooms and laboratories represent the foundational physical infrastructure of academic scheduling. The Room Management interface enables administrators to define, configure, inspect, and update all campus instructional facilities. This document provides the layout blueprints, form specifications, input validation constraints, and modal interaction models for the Room module.

---

## 2. Desktop Room Directory Wireframe (1440px Viewport)

```
+-------------------------------------------------------------------------------------------------------------------+
|  [Logo] KronosTT | CSE Department        [Search rooms, buildings, capacity...]   (🔔 3) [Bhavishya Raghav (Admin) v]|
+-------------------------------------------------------------------------------------------------------------------+
|  [Dashboard | Master Data v (Rooms Active) | Timetable Engine | Department Timetables | Reports | Settings]        |
+-------------------------------------------------------------------------------------------------------------------+
|                                                                                                                   |
|  Master Data > Rooms & Instructional Spaces                                   [📥 Import CSV] [+ Add New Room]     |
|                                                                                                                   |
|  +-------------------------------------------------------------------------------------------------------------+  |
|  | FILTER TOOLBAR                                                                                              |  |
|  | Search: [🔍 Room number or name...       ]  Building: [All Buildings v]  Type: [All Types v]  Floor: [All v]|  |
|  | Quick Filter: (•) All Rooms (26)   ( ) Lecture Halls (18)   ( ) Computer Labs (6)   ( ) Hardware Labs (2)   |  |
|  +-------------------------------------------------------------------------------------------------------------+  |
|                                                                                                                   |
|  +-------------------------------------------------------------------------------------------------------------+  |
|  | ROOM DIRECTORY TABLE                                                                   Showing 1-10 of 26   |  |
|  | [ ] Room Name/Code  | Building Block | Floor | Capacity | Type          | Equipment Tags       | Actions   |  |
|  | ----------------------------------------------------------------------------------------------------------- |  |
|  | [ ] LH-201          | Academic Block | 2nd   | 65 seats | LECTURE_HALL  | [Projector][AC][Mic] | [✏][📊][🗑]|  |
|  | [ ] LH-202          | Academic Block | 2nd   | 65 seats | LECTURE_HALL  | [Projector][AC]      | [✏][📊][🗑]|  |
|  | [ ] LH-203          | Academic Block | 2nd   | 70 seats | LECTURE_HALL  | [Projector][Smart]   | [✏][📊][🗑]|  |
|  | [ ] LH-301          | Academic Block | 3rd   | 60 seats | LECTURE_HALL  | [Projector]          | [✏][📊][🗑]|  |
|  | [ ] Lab-01 (AI/ML)  | CS Block       | 1st   | 35 seats | COMPUTER_LAB  | [35 PCs][GPU][LAN]   | [✏][📊][🗑]|  |
|  | [ ] Lab-02 (Systems)| CS Block       | 1st   | 35 seats | COMPUTER_LAB  | [35 PCs][Linux][LAN] | [✏][📊][🗑]|  |
|  | [ ] Lab-03 (Networks| CS Block       | 2nd   | 30 seats | COMPUTER_LAB  | [30 PCs][Cisco Racks]| [✏][📊][🗑]|  |
|  | [ ] Seminar Hall-A  | Central Block  | Ground| 120 seats| AUDITORIUM    | [AV Suite][Stage][AC]| [✏][📊][🗑]|  |
|  +-------------------------------------------------------------------------------------------------------------+  |
|  | [Bulk Actions: Delete Selected | Export Filtered]                     [< Previous] [Page 1 of 3] [Next >]   |  |
|  +-------------------------------------------------------------------------------------------------------------+  |
|                                                                                                                   |
+-------------------------------------------------------------------------------------------------------------------+
```

---

## 3. "Add / Edit Room" Slide-Over Drawer Blueprint (560px Width)

```
+---------------------------------------------------------------+
|  Add New Room / Instructional Facility                    [X] |
|  Define spatial properties and scheduling constraints.        |
+---------------------------------------------------------------+
|                                                               |
|  Room Identification                                          |
|  Room Name / Number *                                         |
|  [ LH-204                                                   ] |
|  e.g., LH-204, Computer Lab 3, Faraday Seminar Hall           |
|                                                               |
|  Building Block *                     Floor Level *           |
|  [ Academic Block B           v ]     [ 2nd Floor         v ] |
|                                                               |
|  Capacity & Infrastructure Type                               |
|  Student Seating Capacity *           Facility Type *         |
|  [ 65                       ] seats   [ Lecture Hall      v ] |
|  Min: 1 seat, Max: 500 seats          Options: Lecture, Lab,  |
|                                       Seminar, Workshop       |
|                                                               |
|  Equipment & Capabilities (Multi-Select Tags)                 |
|  [✔] Ceiling Projector    [✔] Air Conditioned   [✔] Mic/Audio |
|  [ ] Smart Interactive Board  [ ] Desktop Workstations        |
|                                                               |
|  Availability & Status                                        |
|  Operational Status: (•) Active / Schedulable                 |
|                      ( ) Under Maintenance (Exclude)          |
|                                                               |
|  Notes / Maintenance Comments                                 |
|  [ Audio system serviced on Aug 15. All mics operational.   ] |
|                                                               |
+---------------------------------------------------------------+
|  [ Cancel ]                                  [ Save Room ✔ ]  |
+---------------------------------------------------------------+
```

---

## 4. Field Validation Constraints (Mapped to Spring Boot Bean Validation)

| Field Name | Type | Form Validation Rules | Back-End DTO Annotation | Error Feedback Message |
|---|---|---|---|---|
| `name` | String | Required, trimmed, 1–50 characters, unique per building | `@NotBlank`, `@Size(max=50)` | "Room name is required and cannot exceed 50 characters." |
| `capacity` | Integer | Required, integer, minimum 1, maximum 500 | `@NotNull`, `@Min(1)`, `@Max(500)` | "Capacity must be a positive integer between 1 and 500." |
| `building` | String | Optional, max 50 characters | `@Size(max=50)` | "Building name cannot exceed 50 characters." |
| `type` | String / Enum | Required, must match `RoomType` enum | `@NotNull` | "Valid room type selection is required." |

---

## 5. Deletion Protection Logic
- **Critical Business Rule:** A room cannot be deleted if it is referenced in an active or draft schedule.
- When delete is triggered:
  - System invokes `scheduledSessionRepository.existsByRoomId(id)`.
  - If references exist, a `ConflictException` (HTTP 409) is returned with the error: *"Cannot delete room: Currently referenced by active scheduled sessions."*
  - The UI displays an amber warning dialog instructing the user to reassign sessions before room removal.
