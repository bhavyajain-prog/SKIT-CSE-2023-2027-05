# Accessible Modal Dialog & Form Input Specifications — KronosTT

**Track:** UI + Data (Bhavishya Raghav)  
**Sprint:** Master Data & UI Design (Form-2: Research and UI/UX design)  
**Date:** September 2026

---

## 1. Overview & Accessibility Standards

Master data CRUD operations (Rooms, Subjects, Faculty) use modal dialogs to maintain context without full-page reloads. All dialogs conform to WAI-ARIA 1.2 modal dialog design patterns.

---

## 2. Room Creation / Edit Modal Specification

### 2.1 Visual Blueprint
```
+--------------------------------------------------------------+
|  Add New Classroom / Lab                            [ X ]    |
+--------------------------------------------------------------+
|                                                              |
|  Room Name / Identifier *                                    |
|  [ LT-101                                                  ] |
|  Must be unique across the campus.                           |
|                                                              |
|  Seating Capacity *                                          |
|  [ 80                                                      ] |
|  Minimum 1 student capacity.                                 |
|                                                              |
|  Permanently Assigned Batch (Optional)                       |
|  [ 5-CSE-A (Strength: 60)                                v ] |
|  Assigning a batch reserves this room as their home hall.    |
|                                                              |
|  ----------------------------------------------------------- |
|  [ Cancel ]                                   [ Save Room ]  |
+--------------------------------------------------------------+
```

---

## 3. Form Validation States & Field Error Schema

In alignment with `GlobalExceptionHandler` and `ApiError`:

1. **Required Fields:** Name and Capacity cannot be empty.
2. **Real-time Client Validation:**
   - On blur: Verify name length (`1..255`) and capacity (`>= 1`).
   - If empty: Display red helper message `"Room name must not be blank"`.
3. **Server Error Mapping:**
   - If backend returns `409 Conflict`, highlight Room Name with red border and message `"Room with name 'LT-101' already exists"`.
   - If backend returns `400 Bad Request` with field error array, map each error to its corresponding form field.

---

## 4. Keyboard Navigation & ARIA Attributes

- Container: `role="dialog" aria-modal="true" aria-labelledby="modal-title"`.
- Focus Management: Focus automatically moves to the first interactive field (`Room Name`) upon opening.
- Escape Key: Closes modal dialog and restores focus to the trigger button (`+ Add Room`).
- Tab Navigation: Tab cycles strictly within modal elements (Focus Trap).
