# Drag-and-Drop Manual Adjustment Interaction Specifications

**Author:** Bhavishya Raghav (Backend & Frontend Lead, Master Data & UI)  
**Project:** SKIT-CSE-2023-2027-05 / KronosTT  
**Date:** September 01, 2026  
**Status:** Completed  
**Sprint:** Sprint 2 — Master Data & UI Design  

---

## 1. Overview & Purpose

While the Google OR-Tools CP-SAT solver generates optimal schedules automatically, real-world academic departments frequently encounter sudden operational changes (e.g., emergency faculty medical leave, classroom renovation, guest lecture visits). To accommodate these without recalculating the entire departmental schedule, KronosTT provides a fine-grained, drag-and-drop manual adjustment workspace with real-time collision feedback.

---

## 2. Interaction State Machine

```
                   [IDLE: Timetable Cell Selected]
                                 |
                        (User MouseDown / TouchHold)
                                 v
                     [DRAGGING: Card Lifted]
                                 |
             +-------------------+-------------------+
             |                                       |
     (Hover over Free Cell)                  (Hover over Occupied Cell)
             v                                       v
    [VALID DROP TARGET]                     [SWAP CANDIDATE TARGET]
   - Emerald dash border                   - Indigo outline
   - Feasibility check: PASS               - Feasibility check: PASS/FAIL
             |                                       |
             +-------------------+-------------------+
                                 |
                        (User MouseUp / Drop)
                                 v
               [COMMIT TRANSACTION / API REASSIGN]
             /                                   \
  (Backend 200 OK)                          (Backend 409 Conflict)
         v                                             v
  [FLASH SUCCESS GREEN]                     [SNAP BACK TO SOURCE]
  - Update local cache                      - Display error toast
  - Persist move in DB                      - Revert UI position
```

---

## 3. Visual Affordance Specifications

```
+---------------------------------------------------------------------------------------------------+
|  DRAG IN PROGRESS MOCKUP                                                                          |
+---------------------------------------------------------------------------------------------------+
|                                                                                                   |
|  Source Slot (Ghost)                  Target 1: Free & Feasible       Target 2: Room Collision     |
|  +--------------------------+         +--------------------------+    +--------------------------+|
|  | 5CS4-01 (OS)             |         | 🟢 DROP TO MOVE          |    | 🔴 CANNOT DROP           ||
|  | LH-201 (Opacity: 40%)    |  ====>  | Free Period              |    | LH-201 already occupied  ||
|  | Dr. Priya Verma          |         | Zero conflicts detected  |    | by 7CS3-02 (AI)          ||
|  +--------------------------+         +--------------------------+    +--------------------------+|
|  Border: Dashed Slate-300             Border: Dashed Emerald-500      Border: Dashed Rose-500     |
|  Background: Slate-100                Background: Emerald-50          Background: Rose-50         |
|                                                                                                   |
+---------------------------------------------------------------------------------------------------+
```

---

## 4. Front-End Technical Implementation (HTML5 Drag API / React-DnD)

1. **Draggable Item Interface:**
   ```typescript
   export interface DragSessionItem {
     sessionId: number;
     subjectCode: string;
     teacherId: number;
     roomId: number;
     originalDay: string;
     originalPeriod: number;
     durationPeriods: number; // 1 for theory, 2 for practical lab
   }
   ```

2. **Real-Time Validation Endpoint (`POST /api/v1/timetable/validate-move`):**
   - Payload:
     ```json
     {
       "sessionId": 104,
       "targetDay": "WEDNESDAY",
       "targetPeriod": 4,
       "targetRoomId": 12
     }
     ```
   - Response:
     ```json
     {
       "valid": true,
       "conflicts": [],
       "softPenalties": [
         "Faculty Dr. Priya Verma will have 3 consecutive teaching periods."
       ]
     }
     ```

3. **Snap-Back Animation on Rejection:**
   - If the move is invalid or aborted by the user, the dragged item smoothly returns to its origin using CSS transition: `transform 0.25s cubic-bezier(0.2, 0, 0, 1)`.
