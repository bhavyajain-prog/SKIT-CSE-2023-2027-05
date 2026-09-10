# Responsive Wireframe Blueprints for Multi-Tier Scheduling Views

**Track:** UI + Data (Bhavishya Raghav)  
**Sprint:** Master Data & UI Design (Form-2: Research and UI/UX design)  
**Date:** September 2026

---

## 1. Blueprint Architecture

This document provides visual ASCII wireframe specifications for the three primary views of the KronosTT interface:
1. **Administrative Master Data Dashboard**
2. **Weekly Matrix Timetable View**
3. **Room Allocation & Capacity Manager**

---

## 2. Screen Specifications

### 2.1 Administrative Master Data Dashboard
```
========================================================================================================
 KRONOSTT | AI Academic Scheduling        [ Academic Year: 2026-27 v ]          [ Profile: Admin v ]
========================================================================================================
 [Dashboard] [Classrooms] [Subjects] [Faculty] [Batches] [Generate Timetable] [Audit Logs]
--------------------------------------------------------------------------------------------------------
 OVERVIEW METRICS:
 +--------------------+  +--------------------+  +--------------------+  +--------------------+
 | Total Rooms:    42 |  | Total Subjects: 68 |  | Active Faculty: 54 |  | Student Batches: 18|
 | Lecture Halls:  28 |  | Theory:         44 |  | Avg Load: 14 hrs/wk|  | Avg Strength:   60 |
 | Laboratories:   14 |  | Practical Labs: 24 |  | Max Load: 18 hrs/wk|  | Total Students: 1080
 +--------------------+  +--------------------+  +--------------------+  +--------------------+

 SCHEDULING HEALTH & READINESS:
 [==============================================================>-------] 88% Data Complete
 - Subjects without assigned Faculty: 0 (PASSED)
 - Batches with unmapped core subjects: 0 (PASSED)
 - Rooms with capacity lower than assigned batch: 1 (WARNING: LT-204 capacity 50 < Batch 3-CSE-B 60)
 
 [ + Create New Room ]   [ + Add Subject ]   [ + Register Faculty ]   [ >> Proceed to Generation ]
========================================================================================================
```

### 2.2 Weekly Matrix Timetable View
```
========================================================================================================
 VIEW: [ By Batch v ] [ Batch: 5-CSE-A v ]  |  [ Filter Conflict Only: [ ] ]  |  [ Export PDF ] [ Excel ]
========================================================================================================
 SLOT / TIME | 08:00 - 08:55 | 08:55 - 09:50 | 09:50 - 10:45 | 11:00 - 11:55 | 11:55 - 12:50 | 12:50 - 01:45
-------------+---------------+---------------+---------------+---------------+---------------+--------------
 MONDAY      | CS501 [LT101] | CS502 [LT101] | CS503 [LT101] |  SHORT BREAK  | CS504 [LT101] | CS505 [LT101]
             | Dr. Mehta     | Prof. Sharma  | Dr. Verma     |               | Prof. Singh   | Dr. Gupta
-------------+---------------+---------------+---------------+---------------+---------------+--------------
 TUESDAY     | CS503 [LT101] | CS501 [LT101] | CS505 [LT101] |  SHORT BREAK  | ======= CS506 LAB ========
             | Dr. Verma     | Dr. Mehta     | Dr. Gupta     |               | CS-Lab-2 (Prof. Sharma/Meena)
-------------+---------------+---------------+---------------+---------------+------------------------------
 WEDNESDAY   | CS504 [LT101] | CS502 [LT101] | CS501 [LT101] |  SHORT BREAK  | CS503 [LT101] | CS502 [LT101]
             | Prof. Singh   | Prof. Sharma  | Dr. Mehta     |               | Dr. Verma     | Prof. Sharma
-------------+---------------+---------------+---------------+---------------+---------------+--------------
 THURSDAY    | CS502 [LT101] | CS504 [LT101] | CS503 [LT101] |  SHORT BREAK  | CS505 [LT101] | CS501 [LT101]
             | Prof. Sharma  | Prof. Singh   | Dr. Verma     |               | Dr. Gupta     | Dr. Mehta
-------------+---------------+---------------+---------------+---------------+---------------+--------------
 FRIDAY      | CS505 [LT101] | CS501 [LT101] | CS502 [LT101] |  SHORT BREAK  | ======= CS507 LAB ========
             | Dr. Gupta     | Dr. Mehta     | Prof. Sharma  |               | CS-Lab-1 (Dr. Verma/Singh)
========================================================================================================
 [Legend: [■] Theory Lecture  [■] Laboratory  [■] Break  [!] Overlap Conflict Alert]
```

### 2.3 Room Allocation & Capacity Manager
```
========================================================================================================
 CLASSROOM & LAB MANAGEMENT                                            [ + Add Classroom / Lab ]
========================================================================================================
 Search: [ Search room name...     ]    Type: [ All Types v ]    Capacity Range: [ 30 ] to [ 150 ]
--------------------------------------------------------------------------------------------------------
 ID   Room Name   Capacity   Building / Floor   Fixed Assigned Batch   Current Utilization   Actions
--------------------------------------------------------------------------------------------------------
 01   LT-101      80         Block-A / 1st      5-CSE-A                85% (34 hrs/wk)       [Edit] [Delete]
 02   LT-102      75         Block-A / 1st      5-CSE-B                80% (32 hrs/wk)       [Edit] [Delete]
 03   LT-103      60         Block-A / 1st      None (Dynamic)         65% (26 hrs/wk)       [Edit] [Delete]
 04   CS-Lab-1    35         Block-B / Ground   None (Specialized)     90% (36 hrs/wk)       [Edit] [Delete]
 05   CS-Lab-2    35         Block-B / Ground   None (Specialized)     85% (34 hrs/wk)       [Edit] [Delete]
--------------------------------------------------------------------------------------------------------
 Showing 1 - 5 of 42 rooms                                              Page: < [1] 2 3 4 >
========================================================================================================
```
