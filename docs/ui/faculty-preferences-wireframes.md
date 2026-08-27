# Faculty Workload & Availability Preference Interface Blueprints

**Author:** Bhavishya Raghav (Backend & Frontend Lead, Master Data & UI)  
**Project:** SKIT-CSE-2023-2027-05 / KronosTT  
**Date:** August 27, 2026  
**Status:** Completed  
**Sprint:** Sprint 2 — Master Data & UI Design  

---

## 1. Overview & Purpose

Faculty availability matrices and workload policies provide the human constraints for timetable generation. To prevent faculty burnout and satisfy institutional AICTE/RTU norms, the system must enforce maximum weekly teaching loads, restrict consecutive teaching periods, and accommodate approved faculty research or administrative day-release preferences.

---

## 2. Desktop Faculty Workload Roster Wireframe (1440px Viewport)

```
+-------------------------------------------------------------------------------------------------------------------+
|  [Logo] KronosTT | CSE Department        [Search faculty, designation...]         (🔔 3) [Bhavishya Raghav (Admin) v]|
+-------------------------------------------------------------------------------------------------------------------+
|  [Dashboard | Master Data v (Faculty Active) | Timetable Engine | Department Timetables | Reports | Settings]      |
+-------------------------------------------------------------------------------------------------------------------+
|                                                                                                                   |
|  Master Data > Faculty & Teaching Staff                                      [⚙ Workload Rules] [+ Add Faculty]   |
|                                                                                                                   |
|  +-------------------------------------------------------------------------------------------------------------+  |
|  | FACULTY WORKLOAD OVERVIEW & COMPLIANCE                                                                      |  |
|  | Total Teaching Staff: 42 Faculty Members | Department Target: 588 Weekly Teaching Hours                     |  |
|  | [||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||........] 94.2% Load Allocated                |  |
|  +-------------------------------------------------------------------------------------------------------------+  |
|                                                                                                                   |
|  +-------------------------------------------------------------------------------------------------------------+  |
|  | FACULTY DIRECTORY & TEACHING HOURS                                                     Showing 1-7 of 42    |  |
|  | Teacher Name       | Designation       | Target Load | Assigned Load | Max Consecutive | Prefs Set | Actions|  |
|  | ----------------------------------------------------------------------------------------------------------- |  |
|  | Dr. Neha Agarwal   | Professor & HOD   | 8 hrs/wk    | 8 hrs (100%)  | 2 periods       | [✔ Config] | [✏][🗓] |  |
|  | Prof. Rajesh Sharma| Associate Prof    | 14 hrs/wk   | 14 hrs (100%) | 2 periods       | [✔ Config] | [✏][🗓] |  |
|  | Dr. Priya Verma    | Assistant Prof    | 16 hrs/wk   | 15 hrs (94%)  | 2 periods       | [✔ Config] | [✏][🗓] |  |
|  | Er. Amit Mathur    | Assistant Prof    | 16 hrs/wk   | 16 hrs (100%) | 2 periods       | [✔ Config] | [✏][🗓] |  |
|  | Er. Sunita Jain    | Assistant Prof    | 16 hrs/wk   | 14 hrs (88%)  | 2 periods       | [✔ Config] | [✏][🗓] |  |
|  | Er. Vikas Tiwari   | Assistant Prof    | 16 hrs/wk   | 16 hrs (100%) | 2 periods       | [✔ Config] | [✏][🗓] |  |
|  +-------------------------------------------------------------------------------------------------------------+  |
|                                                                                                                   |
+-------------------------------------------------------------------------------------------------------------------+
```

---

## 3. Weekly Availability & Preference Grid Modal (720px Width)

```
+-----------------------------------------------------------------------+
|  Faculty Availability Matrix: Dr. Priya Verma                     [X] |
|  Designation: Assistant Professor | Department: Computer Science      |
+-----------------------------------------------------------------------+
|  Click cell to cycle: [🟩 Available]  [🟨 Preferred]  [🟥 Blocked]    |
|                                                                       |
|  Time Slot       Mon       Tue       Wed       Thu       Fri       Sat|
|  -------------------------------------------------------------------  |
|  08:00 - 08:50   [  🟩  ]  [  🟨  ]  [  🟩  ]  [  🟩  ]  [  🟨  ]  [  🟩  ]|
|  08:50 - 09:40   [  🟩  ]  [  🟨  ]  [  🟩  ]  [  🟩  ]  [  🟨  ]  [  🟩  ]|
|  09:40 - 10:30   [  🟩  ]  [  🟩  ]  [  🟩  ]  [  🟩  ]  [  🟩  ]  [  🟩  ]|
|  10:30 - 10:45   --- SHORT TEA RECESS (NO SESSIONS SCHEDULED) ---     |
|  10:45 - 11:35   [  🟩  ]  [  🟩  ]  [  🟩  ]  [  🟩  ]  [  🟩  ]  [  🟩  ]|
|  11:35 - 12:25   [  🟩  ]  [  🟩  ]  [  🟩  ]  [  🟩  ]  [  🟩  ]  [  🟩  ]|
|  12:25 - 01:05   --- LUNCH BREAK (CAMPUS-WIDE) ---                    |
|  01:05 - 01:55   [  🟩  ]  [  🟩  ]  [  🟥  ]  [  🟩  ]  [  🟩  ]  [  🟥  ]|
|  01:55 - 02:45   [  🟩  ]  [  🟩  ]  [  🟥  ]  [  🟩  ]  [  🟩  ]  [  🟥  ]|
|                                                                       |
|  Note: Wed & Sat afternoons blocked for Ph.D. Research Supervision.   |
|                                                                       |
|  Teaching Constraint Overrides                                        |
|  Max Daily Hours: [ 4 ] hrs   |   Max Consecutive Hours: [ 2 ] hrs    |
+-----------------------------------------------------------------------+
|  [ Reset to Default ]                          [ Save Preferences ✔ ] |
+-----------------------------------------------------------------------+
```

---

## 4. Constraint Mathematical Translation
- **Blocked Slots:** Formulate strict constraint $x_s = 0$ for any session $s \in S(t, d, p)$ where $(d, p) \in \text{Blocked}(t)$.
- **Preferred Slots:** Add objective reward $+10$ in the CP-SAT solver objective function when session $s$ is scheduled in a cell where $(d, p) \in \text{Preferred}(t)$.
- **Max Consecutive Hours:** For any 3 contiguous periods $p, p+1, p+2$, enforce $\sum_{k=p}^{p+2} x_{s(t, d, k)} \le 2$.
