# Interactive Timetable Grid Blueprint & Conflict Visualization States

**Author:** Bhavishya Raghav (Backend & Frontend Lead, Master Data & UI)  
**Project:** SKIT-CSE-2023-2027-05 / KronosTT  
**Date:** August 30, 2026  
**Status:** Completed  
**Sprint:** Sprint 2 — Master Data & UI Design  

---

## 1. Overview & Purpose

The Interactive Timetable Grid is the core visual centerpiece of the KronosTT application. It presents multi-dimensional scheduling allocations across Days and Time Periods. It supports three distinct viewing perspectives:
1. **Section / Batch View:** Student cohort weekly schedule.
2. **Faculty / Teacher View:** Instructor weekly teaching and consultation timetable.
3. **Room / Lab View:** Spatial facility occupancy and utilization matrix.

---

## 2. Desktop Timetable Matrix Wireframe (1440px Viewport)

```
+-------------------------------------------------------------------------------------------------------------------+
|  [Logo] KronosTT | CSE Department        [Search timetable...]                    (🔔 3) [Bhavishya Raghav (Admin) v]|
+-------------------------------------------------------------------------------------------------------------------+
|  [Dashboard | Master Data v | Timetable Engine | Department Timetables (Active) | Reports | Settings]              |
+-------------------------------------------------------------------------------------------------------------------+
|                                                                                                                   |
|  Department Timetables > Fall 2026 (Semester V - Sec A)                     [🖨 Print / PDF] [📤 Export CSV] [⚙ Mode] |
|                                                                                                                   |
|  +-------------------------------------------------------------------------------------------------------------+  |
|  | VIEW CONTROLS & PERSPECTIVE SELECTOR                                                                        |  |
|  | Perspective: [• Section View]  [  Faculty View ]  [  Room View   ]                                          |  |
|  | Select Section: [ B.Tech CSE - 5th Sem (Sec A) v ]   Status: [✔ PUBLISHED & LOCKED]                        |  |
|  +-------------------------------------------------------------------------------------------------------------+  |
|                                                                                                                   |
|  +-------------------------------------------------------------------------------------------------------------+  |
|  | Day / Period | P1 (08:00) | P2 (08:50) | P3 (09:40) | [Break] | P4 (10:45) | P5 (11:35) | [Lunch] | P6 (13:05) |  |
|  | -------------+------------+------------+------------+---------+------------+------------+---------+------------|  |
|  | MONDAY       | 5CS4-01 OS | 5CS4-02 CD | 5CS4-03 DB |  15m    | 5CS4-04 CN | 5CS4-05 SE |  40m    | [  FREE  ] |  |
|  |              | LH-201     | LH-201     | LH-201     |  Recess | LH-201     | LH-201     |  Break  |            |  |
|  |              | Dr. Priya  | Prof. Rksh | Dr. Neha   |         | Er. Amit   | Er. Sunita |         |            |  |
|  | -------------+------------+------------+------------+---------+------------+------------+---------+------------|  |
|  | TUESDAY      | 5CS4-02 CD | 5CS4-01 OS | 5CS4-04 CN |         | [======== 5CS4-21 OS LAB ========] | 5CS4-03 DB |  |
|  |              | LH-201     | LH-201     | LH-201     |         | Batch A1: Lab-01 (Dr. Priya Verma) | LH-201     |  |
|  |              | Prof. Rksh | Dr. Priya  | Er. Amit   |         | Batch A2: Lab-02 (Er. Rahul Shrma) | Dr. Neha   |  |
|  | -------------+------------+------------+------------+---------+------------+------------+---------+------------|  |
|  | WEDNESDAY    | 5CS4-03 DB | 5CS4-05 SE | 5CS4-01 OS |         | 5CS4-02 CD | [  FREE  ] |         | 5CS4-04 CN |  |
|  |              | LH-201     | LH-201     | LH-201     |         | LH-201     |            |         | LH-201     |  |
|  | -------------+------------+------------+------------+---------+------------+------------+---------+------------|  |
|  | THURSDAY     | 5CS4-04 CN | 5CS4-03 DB | 5CS4-02 CD |         | [======== 5CS4-22 DBMS LAB =======] | [  FREE  ] |  |
|  |              | LH-201     | LH-201     | LH-201     |         | Batch A1: Lab-02 (Dr. Neha Agrawal)|            |  |
|  |              | Er. Amit   | Dr. Neha   | Prof. Rksh |         | Batch A2: Lab-01 (Er. Pooja Meena) |            |  |
|  | -------------+------------+------------+------------+---------+------------+------------+---------+------------|  |
|  | FRIDAY       | 5CS4-01 OS | 5CS4-04 CN | 5CS4-05 SE |         | 5CS4-03 DB | 5CS4-02 CD |         | Mentorship |  |
|  | -------------+------------+------------+------------+---------+------------+------------+---------+------------|  |
|  | SATURDAY     | Remedial   | Library    | Sports/CCA |         | Seminar    | Project Wrk|         | [  FREE  ] |  |
|  +-------------------------------------------------------------------------------------------------------------+  |
|                                                                                                                   |
+-------------------------------------------------------------------------------------------------------------------+
```

---

## 3. Cell Component & Conflict State Visual Styling

```
+-------------------------------------------------------------------+
|  [NORMAL THEORY SLOT]            [LAB 2-PERIOD SPAN SLOT]         |
|  +--------------------------+    +------------------------------+ |
|  | 5CS4-01 (OS)             |    | 5CS4-21 OS Practical Lab     | |
|  | LH-201 • Capacity: 65    |    | Batch A1 -> Lab-01 (Dr. Priya| |
|  | Dr. Priya Verma          |    | Batch A2 -> Lab-02 (Er. Rahul| |
|  +--------------------------+    +------------------------------+ |
|  Border: Slate-200               Border: Emerald-300              |
|  Bg: White / Slate-50 hover      Bg: Emerald-50/50                |
|                                                                   |
|  [HARD CONFLICT WARNING]         [SOFT PENALTY WARNING]           |
|  +--------------------------+    +------------------------------+ |
|  | ⚠️ ROOM CLASH DETECTED   |    | 🟡 3RD CONSECUTIVE LECTURE   | |
|  | LH-201 double-booked:    |    | Dr. Priya Verma exceeds      | |
|  | 5CS4-01 AND 7CS3-02      |    | continuous teaching guideline| |
|  +--------------------------+    +------------------------------+ |
|  Border: Rose-500, Text: Rose-700 Border: Amber-400, Bg: Amber-50  |
|  Animation: Pulse 2s             Badge: Soft Alert                |
+-------------------------------------------------------------------+
```

---

## 4. Interaction Features
- **Hover Micro-card:** Hovering over any slot highlights the same faculty's other sessions across the week in a soft indigo tint.
- **Empty Slot Click:** Clicking any `[FREE]` slot opens a slide-over modal enabling manual session assignment or scheduling an ad-hoc tutorial/seminar.
- **Lock Pin Indicator:** Admin can click the "Pin" icon on any slot card to fix its day and period permanently, preventing future solver runs from moving it.
