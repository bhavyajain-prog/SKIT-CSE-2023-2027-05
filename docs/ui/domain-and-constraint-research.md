# Academic Timetable Problem Domain & Constraint Workflow Research

**Author:** Bhavishya Raghav (Backend & Frontend Lead, Master Data & UI)  
**Project:** SKIT-CSE-2023-2027-05 / KronosTT  
**Date:** August 11, 2026  
**Status:** Completed  
**Sprint:** Sprint 2 — Master Data & UI Design  

---

## 1. Executive Summary & Problem Domain

University course timetabling is a notoriously NP-hard combinatorial optimization problem characterized by multi-dimensional constraints across time slots, spatial facilities (classrooms and laboratories), human resources (faculty members), and pedagogical cohorts (student sections and lab batches). 

At the **Swami Keshvanand Institute of Technology, Management & Gramothan (SKIT), Jaipur**, academic scheduling in the Department of Computer Science & Engineering involves coordinating:
- Over **40 full-time faculty members** with varying specialization areas and seniority.
- Over **720 students** across 6 sections (A through F) subdivided into 12 laboratory batches.
- **18 specialized lecture halls** and **8 high-performance computing laboratories**.
- **6 operational teaching days** per week (Monday to Saturday) divided into **7 daily 50-minute periods** (08:00 AM – 02:30 PM).

Historically, the manual compilation of timetables consumed 3 to 4 weeks of dedicated faculty effort prior to each semester, resulted in recurring faculty double-bookings, inefficient room utilization, and excessive lecture fragmentation for students. This research establishes the formal constraint taxonomy and interaction workflow model to govern the design of KronosTT's user interface and master data schemas.

---

## 2. Formal Constraint Taxonomy

Timetabling constraints within KronosTT are mathematically categorized into **Hard Constraints** (inviolable physical and logical truths) and **Soft Constraints** (pedagogical and ergonomic quality metrics).

```
+-----------------------------------------------------------------------------------+
|                            KRONOSTT CONSTRAINT TAXONOMY                           |
+-----------------------------------------------------------------------------------+
|                                                                                   |
|  [HARD CONSTRAINTS - Strict Feasibility]                                          |
|  1. Faculty Clashing: A teacher cannot teach >1 session in any slot.              |
|  2. Room Collisions: A room cannot host >1 session concurrently.                  |
|  3. Cohort Overlap: A student batch cannot attend >1 session concurrently.       |
|  4. Facility Compatibility: Lab sessions must be scheduled in lab-type rooms.     |
|  5. Room Capacity: Room capacity must be >= batch enrolled student count.         |
|  6. Credit Fulfillment: Exactly target weekly hours for each subject must be met.|
|                                                                                   |
|  [SOFT CONSTRAINTS - Pedagogical Quality]                                         |
|  1. Daily Load Balancing: Max 4-5 theory lectures per batch per day.              |
|  2. Faculty Idle Time Minimization: Avoid isolated 1-2 period gaps for teachers.  |
|  3. Block Scheduling: Lab sessions must be scheduled as 2 continuous periods.    |
|  4. Preferred Shift / Time Slots: Morning slots prioritized for core subjects.   |
|  5. Inter-Building Transit: Minimize faculty room changes across distinct blocks. |
|                                                                                   |
+-----------------------------------------------------------------------------------+
```

### 2.1 Hard Constraints Formulation

1. **Teacher Uniqueness**:
   $$\sum_{s \in S(t, d, p)} x_s \le 1 \quad \forall t \in \text{Teachers}, d \in \text{Days}, p \in \text{Periods}$$
   Where $S(t, d, p)$ represents all sessions assigned to teacher $t$ on day $d$ in period $p$.

2. **Room Exclusivity**:
   $$\sum_{s \in S(r, d, p)} x_s \le 1 \quad \forall r \in \text{Rooms}, d \in \text{Days}, p \in \text{Periods}$$

3. **Student Batch Exclusivity**:
   $$\sum_{s \in S(b, d, p)} x_s \le 1 \quad \forall b \in \text{Batches}, d \in \text{Days}, p \in \text{Periods}$$

4. **Room Capacity & Type Validation**:
   $$\text{Capacity}(r) \ge \text{BatchSize}(b) \quad \text{and} \quad \text{RoomType}(r) = \text{SubjectType}(sub)$$

---

## 3. Current Institutional Workflow Analysis

The existing scheduling workflow was documented through observational shadowing of the SKIT CSE Timetable Committee:

```
[Phase 1: Curriculum Intake]
   |--> Collect subject credit matrix (L-T-P splits) from RTU curriculum.
   |--> Collect list of eligible faculty from Head of Department (HOD).
   |
[Phase 2: Master Data Collation (Excel Spreadsheets)]
   |--> Manual entry of room numbers (e.g., 201, 202, Lab-1, Lab-2).
   |--> Manual tracking of room capacities and audio-visual equipment.
   |--> Disconnected Google Sheets for faculty subject preferences.
   |
[Phase 3: Manual Scheduling Drafting]
   |--> Drafting on whiteboard / oversized grid sheets.
   |--> Human error: Faculty assigned to Room 204 while simultaneously in Lab-3.
   |--> High friction when adjusting for faculty sabbatical or leave requests.
   |
[Phase 4: Publication & Post-Hoc Patching]
   |--> PDF export from Excel distributed via WhatsApp groups.
   |--> Frequent room clash discoveries during Week 1 of academic semester.
```

---

## 4. Key UX Findings & System Requirements

1. **Immediate Visual Conflict Feedback**:
   Administrators cannot wait for an overnight solver run to find out that Room 302 has a capacity of 30 but was assigned to Section A (60 students). Master data forms must validate room capacities proactively during data entry.

2. **Hierarchical Master Data Architecture**:
   Rooms must not be flat strings; they must belong to explicit physical hierarchies: `Campus -> Building Block -> Floor -> Room Number` with tagged capabilities (`LECTURE_HALL`, `COMPUTER_LAB`, `HARDWARE_LAB`, `SEMINAR_HALL`).

3. **Batch Splitting Affordances**:
   Theory classes are attended by the entire Section (e.g., CSE-3A, 60 students), whereas practical laboratory sessions are split into sub-batches (A1, A2, 30 students each) running simultaneously in distinct physical labs. The UI must support this parent-child cohort structure natively.

---

## 5. Next Steps for Sprint 2
- Conduct competitive benchmarking against UniTime, ASC Timetables, and FET.
- Formulate stakeholder personas and end-to-end user journey maps.
- Draft low-fidelity wireframes for the Room Management and Administrative Dashboard.
