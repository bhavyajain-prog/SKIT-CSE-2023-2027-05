# Wireframes & Navigation Architecture — KronosTT

**Track:** UI + Data (Bhavishya Raghav)  
**Sprint:** Master Data & UI Design (Form-2: Research and UI/UX design)  
**Date:** September 2026

---

## 1. Navigation Architecture

The KronosTT user interface uses a responsive sidebar navigation layout with persistent top bar indicators:

- **Sidebar (Collapsible):**
  - 📊 Dashboard
  - 🏫 Master Data
    - Subjects Management
    - Classrooms & Labs
    - Faculty Directory
    - Student Batches
  - ⚙️ Timetable Generation
  - 📅 Timetable Views (Batch / Teacher / Room)
  - 📈 Audit & Activity Logs
- **Top Bar:** Current Semester indicator, Active Profile, System Health pulse, Quick Export.

---

## 2. Wireframe Specifications

### 2.1 Master Data: Room Management Wireframe
```
+--------------------------------------------------------------------------+
|  Classrooms & Laboratories                               [ + Add Room ]  |
+--------------------------------------------------------------------------+
|  Search: [ Search rooms... ]    Filter: [ All Types v ]   Sort: [ Name v ]|
+--------------------------------------------------------------------------+
|  ID  | Room Name | Capacity | Type    | Fixed Batch | Status  | Actions  |
|------+-----------+----------+---------+-------------+---------+----------|
|  1   | LT-101    | 80       | Lecture | CSE-A       | Active  | [Edit]   |
|  2   | CS-Lab-1  | 35       | Lab     | None        | Active  | [Edit]   |
|  3   | LT-102    | 75       | Lecture | None        | Active  | [Edit]   |
+--------------------------------------------------------------------------+
|  Showing 1-10 of 28 rooms                             < [1] 2 3 >        |
+--------------------------------------------------------------------------+
```

### 2.2 Timetable Matrix Grid Wireframe
```
+----------------------------------------------------------------------------------------------------+
|  Batch: [ 5-CSE-A v ]  |  Semester: 5th  |  Export: [ PDF v ] [ Excel ]  |  [ Filter Conflicts ]  |
+----------------------------------------------------------------------------------------------------+
| Day / Slot | 08:00 - 08:55 | 08:55 - 09:50 | 09:50 - 10:45 | 11:00 - 11:55 | 11:55 - 12:50 | ...  |
|------------+---------------+---------------+---------------+---------------+---------------+------|
| Monday     | CS501 (LT101) | CS502 (LT101) | CS503 (LT101) |    BREAK      | CS504 (LT101) | ...  |
|            | Dr. Sharma    | Prof. Gupta   | Dr. Verma     |               | Prof. Singh   |      |
|------------+---------------+---------------+---------------+---------------+---------------+------|
| Tuesday    | CS503 (LT101) | CS501 (LT101) | CS505 (LT101) |    BREAK      | ---- LAB ---- | ...  |
|            | Dr. Verma     | Dr. Sharma    | Dr. Meena     |               | CS506 (Lab-2) |      |
|------------+---------------+---------------+---------------+---------------+---------------+------|
| ...        | ...           | ...           | ...           | ...           | ...           | ...  |
+----------------------------------------------------------------------------------------------------+
```

---

## 3. Responsive Breakpoint Standards
- Mobile (`< 640px`): Single-column day cards with accordion slot expansion.
- Tablet (`640px - 1024px`): Horizontally scrollable grid with frozen Day/Time headers.
- Desktop (`>= 1024px`): Full multi-column weekly grid view with interactive drag-to-adjust capability.
