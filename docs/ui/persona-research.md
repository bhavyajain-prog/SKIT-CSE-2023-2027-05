# Comprehensive User Persona Research & Academic Scheduling Requirements

**Track:** UI + Data (Bhavishya Raghav)  
**Sprint:** Master Data & UI Design (Form-2: Research and UI/UX design)  
**Start Date:** 10 August 2026  
**Evaluation Window:** August–September 2026

---

## 1. Executive Summary

Academic timetable generation in institutional environments presents multi-dimensional constraint-satisfaction challenges. This research document analyzes user personas, operational pain points, and user journey maps to formulate foundational user requirements for the KronosTT platform.

---

## 2. Target User Personas

### 2.1 Persona A: Dr. Rajesh Mehta — Head of Academic Scheduling
- **Demographics:** Senior Faculty & Academic Coordinator, 15+ years experience.
- **Responsibilities:**
  - Manages semester scheduling for 1,200+ students across 8 semesters and 24 batches.
  - Balances 65 faculty members, 40 lecture halls, and 14 specialized computer labs.
  - Resolves faculty leave requests, guest lecture slots, and room maintenance conflicts.
- **Core Frustrations:**
  - Manual spreadsheet-based scheduling requires 3 weeks per semester.
  - Minor changes (e.g. faculty unavailable on Tuesdays) trigger cascading timetable rewrites.
  - Inadequate visibility into classroom capacity versus batch strength utilization.
- **Key System Needs:**
  - Automated constraint-satisfaction engine with conflict diagnostics.
  - Real-time visual heatmaps of room and faculty occupancy.
  - Audit trails for regulatory compliance and AICTE accreditation.

### 2.2 Persona B: Prof. Sunita Verma — Assistant Professor (CSE)
- **Demographics:** Department Faculty, teaching 3 courses across 4 sections.
- **Responsibilities:**
  - Conducts lectures, laboratory sessions, and tutorial hours.
  - Needs uninterrupted research hours and specific preparation time slots.
- **Core Frustrations:**
  - Scattered 1-hour slots across the day with 3-hour gaps.
  - Back-to-back classes in distant buildings on multi-block campuses.
  - Lack of timely notifications when class schedules change.
- **Key System Needs:**
  - Personal calendar view accessible via web and mobile.
  - Ability to submit preferred teaching windows and blacklisted slots.
  - One-click PDF and iCal schedule synchronization.

### 2.3 Persona C: Rohan Sharma — Class Representative (5th Sem CSE-A)
- **Demographics:** Undergraduate student, managing communication for 60 peers.
- **Responsibilities:**
  - Relays timetable notices and room changes to peers.
- **Core Frustrations:**
  - Confusing PDFs with missing room allocations.
  - Unannounced room shifts leading to missed attendance.
- **Key System Needs:**
  - Clean, color-coded weekly schedule showing course title, room, and professor.
  - Fast search and filtering by batch, section, and semester.

---

## 3. Workflow Journey Mapping

```
+-----------------------------------------------------------------------------------+
|                            COORDINATOR JOURNEY MAP                                 |
+-----------------------------------------------------------------------------------+
| Stage 1: Ingestion      | Stage 2: Verification  | Stage 3: Optimization | Stage 4: Review    |
|-------------------------+------------------------+-----------------------+--------------------|
| - Bulk import faculty   | - Check credit splits  | - Launch engine       | - Inspect heatmaps |
| - Configure rooms & labs| - Verify room capacity | - Heuristic placement | - Manual drag-move |
| - Assign batch subjects | - Detect early clashes | - Constraint solver   | - Final approval   |
+-----------------------------------------------------------------------------------+
```

---

## 4. UI/UX Functional Requirements Matrix

| ID | Module | User Story | Acceptance Criteria | Priority |
|---|---|---|---|---|
| REQ-01 | Auth | Role-based login | JWT authentication with ADMIN, COORDINATOR, and FACULTY roles | P0 |
| REQ-02 | Rooms | Room CRUD interface | Validates name uniqueness, capacity >= 1, and fixed batch binding | P0 |
| REQ-03 | Subjects | Subject registry | Tracks lecture vs lab credits, weekly duration, and subject codes | P0 |
| REQ-04 | Grid View | Multi-view timetable | Toggle between Batch, Faculty, and Classroom schedule matrices | P0 |
| REQ-05 | Diagnostics | Conflict highlight | Visual badges for overlapping slots or capacity mismatches | P1 |
| REQ-06 | Export | PDF / Excel export | Generates formatted print-ready weekly schedules | P1 |
