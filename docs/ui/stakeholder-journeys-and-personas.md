# Stakeholder User Personas & End-to-End Journey Maps

**Author:** Bhavishya Raghav (Backend & Frontend Lead, Master Data & UI)  
**Project:** SKIT-CSE-2023-2027-05 / KronosTT  
**Date:** August 13, 2026  
**Status:** Completed  
**Sprint:** Sprint 2 — Master Data & UI Design  

---

## 1. Overview & Purpose

To guarantee user adoption and ensure high system utility across all academic ranks at SKIT Jaipur, four primary stakeholder personas were developed. Each persona represents an archetypal user archetype with distinct technological comfort levels, authorization boundaries, and core operational goals.

---

## 2. Stakeholder User Personas

### Persona 1: Prof. Rajesh Sharma — Timetable Coordinator (Primary User)
- **Role:** Associate Professor & Convener, Department Timetable Committee
- **Demographics:** Age 44 | Tech-literate | Uses Google Sheets & Excel daily
- **Core Goals:**
  - Complete semester timetable generation in under 2 days without manual calculation.
  - Manage master data (40+ faculty, 26 rooms/labs, 30+ course codes) with zero data corruption.
  - Adjust for sudden room maintenance or faculty leaves with minimal ripple-effect disruptions.
- **Pain Points:**
  - "Last semester we had two professors scheduled in Room 204 at 10:00 AM on Tuesdays. The confusion lasted two weeks."
  - "When a professor takes maternity or medical leave, finding replacement slots manually takes hours."
- **KronosTT Requirement:**
  - Rapid room & subject data entry, bulk CSV ingestion, single-click constraint solving, and instant visual clash detection.

---

### Persona 2: Dr. Neha Agarwal — Head of Department (HOD)
- **Role:** Professor & Head, Department of Computer Science & Engineering
- **Demographics:** Age 52 | Uses iPad and Laptop | High-level administrative focus
- **Core Goals:**
  - Verify equitable distribution of teaching loads (14–16 hours/week for Asst. Professors, 8–10 for Professors).
  - Ensure zero unallocated syllabus hours before submitting to the Academic Council.
  - Formal one-click approval / rejection workflow for proposed draft schedules.
- **Pain Points:**
  - "I receive static PDF printouts that take hours to audit for faculty overtime or compliance."
- **KronosTT Requirement:**
  - Department analytics dashboard with workload distribution graphs, room utilization heatmaps, and formal "Approve & Publish" workflow controls.

---

### Persona 3: Dr. Priya Verma — Faculty Member
- **Role:** Assistant Professor (Specialization: Operating Systems & Networks)
- **Demographics:** Age 31 | Mobile-first user | Accesses college portal on phone
- **Core Goals:**
  - Access personal weekly teaching schedule on mobile device with zero zoom-in friction.
  - View designated classroom and lab locations with clear lab assistant details.
  - Avoid consecutive 4-hour lecture blocks without break.
- **Pain Points:**
  - "Searching for my name on an 8-column wide college PDF on my smartphone is frustrating."
- **KronosTT Requirement:**
  - Responsive Teacher View filterable by faculty name, printable personal schedule, and timetable change notifications.

---

### Persona 4: Rohan Gupta — Student Representative
- **Role:** Class Representative, 3rd Year B.Tech CSE (Section A)
- **Demographics:** Age 20 | Mobile smartphone user
- **Core Goals:**
  - Immediate notification when a classroom is relocated (e.g., Room 301 to Room 208).
  - Clear distinction between Section-wide theory lectures and Sub-batch practical lab sessions.
- **KronosTT Requirement:**
  - Clean student section timetable view with lab batch split badges and responsive cards.

---

## 3. End-to-End User Journey Maps

```
+------------------------------------------------------------------------------------------------+
|                         TIMETABLE COORDINATOR JOURNEY: SEMESTER SETUP                          |
+------------------------------------------------------------------------------------------------+
| Stage 1: Master Setup      Stage 2: Engine Solve      Stage 3: Review/Tweak   Stage 4: Publish |
|                                                                                                |
| [1. Ingest Rooms & Labs]   [3. Run OR-Tools Engine]   [5. Visual Audit Grid]  [7. HOD Approval]|
| - Define Capacities        - Set Time Budget (60s)    - Inspect Room Clashes  - Sign-off lock  |
| - Tag Projector/Lab types  - Enforce Max Daily Hours  - Drag-to-swap slots    - Distribute PDF |
|                                                                                                |
| [2. Ingest Subjects/Staff] [4. Inspect Objective]     [6. Resolve Warnings]   [8. Live Status] |
| - L-T-P Split per Course   - View Soft Score Penalty  - Adjust Room Overflows - Student Mobile |
+------------------------------------------------------------------------------------------------+
```

### Detailed Journey Steps:
1. **Curriculum Master Data Ingestion**:
   - Coordinator navigates to `Master Data -> Rooms`, reviews existing 26 rooms, adds new AI/ML Lab with capacity 35.
   - Navigates to `Master Data -> Subjects`, confirms CSE-501 (OS) has 3 Lectures + 2 Lab periods.
2. **Automated Timetable Generation**:
   - Navigates to `Timetable Engine`, selects Semester 5 (Fall 2026), clicks `Generate Timetable`.
   - Engine executes in background; progress bar updates; returns conflict-free schedule in 14 seconds.
3. **Manual Conflict Review & Tweak**:
   - Coordinator switches to `Room Utilization View`, notices Room 204 is idle on Thursday afternoon while Lab 2 is overbooked.
   - Drags elective session from Lab 2 to Lab 4; system performs real-time feasibility check and confirms zero clashes.
4. **HOD Approval & Distribution**:
   - HOD logs in, reviews the Department Summary, verifies teaching loads, clicks `Approve Draft`.
   - System updates schedule state to `PUBLISHED`; generates official PDF formatted per SKIT guidelines.

---

## 4. Architectural Impact on Sprint 2 & 3
- Room Management must feature strict validation rules: capacity >= 1, non-blank name, unique room number per building.
- Navigation header must feature rapid role switching and intuitive breadcrumb tracking.
- Timetable grid components must support instant drag previews and visual conflict cues.
