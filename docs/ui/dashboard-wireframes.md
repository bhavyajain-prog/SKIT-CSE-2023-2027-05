# Administrative Overview Dashboard Wireframes & Layout Specifications

**Author:** Bhavishya Raghav (Backend & Frontend Lead, Master Data & UI)  
**Project:** SKIT-CSE-2023-2027-05 / KronosTT  
**Date:** August 18, 2026  
**Status:** Completed  
**Sprint:** Sprint 2 — Master Data & UI Design  

---

## 1. Overview & Purpose

The Administrative Overview Dashboard serves as the central command cockpit for Department Heads and Timetable Coordinators. It provides immediate situational awareness regarding infrastructure readiness, curriculum allocation completion, solver run status, and active timetable health metrics.

---

## 2. Desktop Dashboard Wireframe Blueprint (1440px Viewport)

```
+-------------------------------------------------------------------------------------------------------------------+
|  [Logo] KronosTT | CSE Department        [Search rooms, faculty, codes...]       (🔔 3) [Dr. Neha Agarwal (HOD) v] |
+-------------------------------------------------------------------------------------------------------------------+
|  [Nav: Dashboard (Active) | Master Data v | Timetable Engine | Department Timetables | Reports | Settings]        |
+-------------------------------------------------------------------------------------------------------------------+
|                                                                                                                   |
|  Dashboard Overview — Fall Semester 2026                                      [+ Add Room] [⚡ Quick Generate]    |
|                                                                                                                   |
|  +--------------------+  +--------------------+  +--------------------+  +--------------------+                   |
|  | TOTAL ROOMS        |  | ACTIVE FACULTY     |  | SUBJECT HOURS MET  |  | TIMETABLE STATUS   |                   |
|  | 26 Rooms           |  | 42 Teachers        |  | 98.4% (362/368 hrs)|  | OPTIMIZED (0 Clash)|                   |
|  | 18 Class | 8 Labs  |  | Avg 14.2 hrs/wk    |  | 6 hrs pending      |  | Last Run: 2 hrs ago|                   |
|  | [View Directory ->]|  | [Manage Staff ->]  |  | [View Matrix ->]   |  | [Open Review Grid] |                   |
|  +--------------------+  +--------------------+  +--------------------+  +--------------------+                   |
|                                                                                                                   |
|  +-------------------------------------------------------------+  +--------------------------------------------+  |
|  | CAMPUS INFRASTRUCTURE UTILIZATION (ROOM OCCUPANCY HEATMAP)  |  | TIMETABLE GENERATION WORKFLOW              |  |
|  | Filter: [All Buildings v] [Mon-Sat v]                       |  | Current Term: B.Tech Sem V & VII           |  |
|  |                                                             |  |                                            |  |
|  | Room     P1(08:00)  P2(08:50)  P3(09:40)  P4(10:45)  P5(11:35) |  | [✔] 1. Master Data Validation Passed       |  |
|  | ----------------------------------------------------------- |  |     - 26 Rooms configured (1,140 seats)    |  |
|  | LH-201   [ 5A-OS ]  [ 5A-OS ]  [ 7B-AI ]  [  FREE ]  [ 5B-CN ] |  |     - 42 Faculty workloads compliant       |  |
|  | LH-202   [ 7A-CD ]  [ 7A-CD ]  [ 5C-DBMS] [ 5C-DBMS] [  FREE ] |  | [✔] 2. Constraint Model Formulation Ready  |  |
|  | Lab-01   [--- 5A1 OS Lab ----] [--- 7B1 ML Lab ----] [  FREE ] |  | [✔] 3. CP-SAT Solver Execution Complete    |  |
|  | Lab-02   [--- 5B2 CN Lab ----] [  FREE    ] [--- 5C1 DB Lab --]|  |     - Objective Score: 9,840 (0 hard drop) |  |
|  | Lab-03   [  FREE ]  [--- 7A2 IoT Lab ---]  [  FREE ]  [ 7A-EE ] |  | [ ] 4. Formal HOD Approval Required        |  |
|  |                                                             |  |                                            |  |
|  | [View Full 26-Room Utilization Grid]                        |  | [ Reject & Re-solve ]   [ Approve & Publish] |  |
|  +-------------------------------------------------------------+  +--------------------------------------------+  |
|                                                                                                                   |
|  +-------------------------------------------------------------------------------------------------------------+  |
|  | RECENT SYSTEM NOTIFICATIONS & AUDIT LOGS                                                    [View Full Log] |  |
|  | • 10:14 AM - Room 204 capacity updated from 55 to 65 by Bhavishya Raghav (Admin)                           |  |
|  | • 09:30 AM - Timetable solver executed: Fall 2026 v2.4 (Execution time: 14.2s, 0 hard conflicts)           |  |
|  | • Yesterday - Automated Form-3 Progress Report archived by github-actions[bot]                              |  |
|  +-------------------------------------------------------------------------------------------------------------+  |
|                                                                                                                   |
+-------------------------------------------------------------------------------------------------------------------+
```

---

## 3. Component Specification & Behavioral Logic

### 3.1 Stat Card Widget (`StatCard.tsx`)
- Displays metric label (uppercase, tracking-wider, text-slate-500).
- Displays primary numeric value (font-semibold, text-slate-900, text-3xl).
- Displays auxiliary sub-metric or secondary badge (e.g., emerald pill badge for compliance, amber for pending hours).
- Provides hover elevation effect with link navigation to respective module.

### 3.2 Occupancy Heatmap Widget (`RoomHeatmap.tsx`)
- Color-coded slot blocks:
  - Slate-100: Free period (Available).
  - Indigo-50 / Indigo-700: Scheduled theory session.
  - Emerald-50 / Emerald-700: Scheduled lab session (spans 2 contiguous columns).
  - Rose-100 / Rose-700: Conflict or overflow warning.
- Clicking any occupied block opens the `SessionDetailsPopover` displaying Subject, Faculty, Batch, and enrolled count.

### 3.3 HOD Approval Callout (`ApprovalWorkflowCard.tsx`)
- Conditionally rendered if a generated timetable is in `DRAFT_PENDING_APPROVAL` status.
- Primary CTA triggers confirmation modal verifying academic calendar dates and syllabus compliance.
