# Curriculum Credit Split & Subject Domain Architecture

**Author:** Bhavishya Raghav (Backend & Frontend Lead, Master Data & UI)  
**Project:** SKIT-CSE-2023-2027-05 / KronosTT  
**Date:** October 19, 2026  
**Status:** Completed  
**Sprint:** Sprint 2 — Master Data & UI Design  

---

## 1. Domain Modeling Overview

In university academic administration under the Rajasthan Technical University (RTU) system, courses follow a structured **Lecture-Tutorial-Practical (L-T-P)** credit split:
- **Lecture (L):** Theoretical instructional periods held in lecture halls for the entire student section (e.g. 60 students in CSE-5A).
- **Tutorial (T):** Problem-solving and interactive discussion periods, typically held in standard classrooms.
- **Practical (P):** Laboratory sessions requiring computer workstations or hardware benches, held in 2-period contiguous blocks for divided sub-batches (e.g., A1 and A2 with 30 students each).

```
+-----------------------------------------------------------------------------------+
|                         SUBJECT DECOMPOSITION ARCHITECTURE                        |
+-----------------------------------------------------------------------------------+
|                                                                                   |
|  [RTU Curriculum Specification]                                                   |
|  Course: 5CS4-01 Operating Systems (3-0-2 Split)                                  |
|                                                                                   |
|                   │                                                               |
|                   ▼                                                               |
|  [KronosTT Subject Model]                                                         |
|  ├── Theory Subject: 5CS4-01 (Weight=3, Duration=1, Type=LECTURE)                 |
|  └── Lab Subject: 5CS4-21 OS Lab (Weight=2, Duration=2, Type=LAB)                 |
|                                                                                   |
|                   │                                                               |
|                   ▼                                                               |
|  [Session Generator Output]                                                       |
|  ├── Session 1: 5CS4-01 Theory (1 period, Section 5A, Room: LECTURE_HALL)         |
|  ├── Session 2: 5CS4-01 Theory (1 period, Section 5A, Room: LECTURE_HALL)         |
|  ├── Session 3: 5CS4-01 Theory (1 period, Section 5A, Room: LECTURE_HALL)         |
|  ├── Session 4: 5CS4-21 Lab (2 continuous periods, Batch A1, Room: COMPUTER_LAB)  |
|  └── Session 5: 5CS4-21 Lab (2 continuous periods, Batch A2, Room: COMPUTER_LAB)  |
|                                                                                   |
+-----------------------------------------------------------------------------------+
```

---

## 2. Mathematical Session Generation Rules

For any subject offering $S$ with weekly credit weight $W(S)$ and slot duration $D(S)$:
1. **Total Weekly Periods:**
   $$N_{\text{sessions}}(S) = \frac{W(S)}{D(S)}$$
   For a standard 3-credit theory lecture with $D(S)=1$, $N_{\text{sessions}} = 3$.
   For a 2-credit practical lab with $D(S)=2$, $N_{\text{sessions}} = 1$ (spanning 2 consecutive timetable periods).

2. **Room Type Compatibility Constraint:**
   $$\text{FacilityType}(R) = \begin{cases} \text{LECTURE\_HALL}, & \text{if } \text{Type}(S) = \text{LECTURE} \\ \text{COMPUTER\_LAB} \lor \text{HARDWARE\_LAB}, & \text{if } \text{Type}(S) = \text{LAB} \end{cases}$$

3. **Sub-Batch Lab Synchronization:**
   When lab sessions are scheduled for sub-batches $A_1$ and $A_2$:
   $$\text{Slot}(A_1, S_{\text{lab}}) = \text{Slot}(A_2, S_{\text{lab}})$$
   $$\text{Room}(A_1, S_{\text{lab}}) \neq \text{Room}(A_2, S_{\text{lab}})$$
   Both batches execute their practical work simultaneously in distinct physical laboratories.

---

## 3. Database Schema Mapping & Foreign Key Integrity

```
+--------------------+            +------------------------+
|      SUBJECTS      | 1 ───────* |        SESSIONS        |
+--------------------+            +------------------------+
| PK  id             |            | PK  id                 |
|     subject_code   |            | FK  subject_id         | ──> subjects.id (RESTRICT)
|     subject_name   |            | FK  batch_id           | ──> batches.id
|     weight         |            | FK  preferred_room_id  | ──> rooms.id
|     slot_duration  |            |     session_type       |
|     subject_type   |            +------------------------+
+--------------------+
```

### Deletion Protection Policy
When a deletion request is issued for subject $S$:
1. The service checks `sessionRepository.existsBySubjectId(S.id)`.
2. If sessions exist, the system blocks deletion and raises `ConflictException` (HTTP 409).
3. The administrator is required to remove or reassign curriculum offerings prior to entity retirement.
