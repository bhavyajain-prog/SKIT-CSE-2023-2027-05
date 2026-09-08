# User Workflow Analysis & Persona Requirements — KronosTT

**Track:** UI + Data (Bhavishya Raghav)  
**Sprint:** Master Data & UI Design (Form-2: Research and UI/UX design)  
**Date:** September 2026

---

## 1. Overview & Objectives

KronosTT is designed to replace manual, error-prone academic timetable generation with an AI/constraint-optimized scheduling system. This document outlines the primary user personas, workflow journeys, and screen interactions identified during the initial research phase.

---

## 2. Key User Personas

### 2.1 Academic Coordinator (Primary Operator)
- **Goal:** Input semester master data (faculty, rooms, subjects, batches), trigger timetable generation, review conflict diagnostics, and publish finalized schedules.
- **Pain Points:** High cognitive load balancing overlapping room and faculty availability; manual rescheduling takes days when conflicts occur.
- **Workflow:**
  1. Login via role-based authentication.
  2. Master data management: verify/update subjects, faculty availability, and classroom capacity.
  3. Pre-generation validation: verify all batches have assigned subjects and credit allocations.
  4. Initiate automated scheduling job.
  5. Inspect timetable grid, verify soft constraint adherence, and approve/publish.

### 2.2 Department Faculty Member (Viewer / Constraint Contributor)
- **Goal:** View personal weekly teaching timetable, submit preferred slots / unavailable windows, and download schedule.
- **Workflow:**
  1. Access faculty portal.
  2. View weekly lecture/lab commitments across assigned batches.
  3. Export personal schedule to PDF/calendar.

### 2.3 Student / Batch Representative
- **Goal:** View clean, responsive batch timetable with room numbers, subject codes, and instructor names.
- **Workflow:**
  1. Select Department, Semester, and Batch/Section.
  2. View real-time weekly class schedule.

---

## 3. High-Priority Screen Hierarchy

```
[Login / SSO]
      │
      ▼
[Coordinator Dashboard]
      ├── Master Data Management
      │     ├── Subjects (/subjects)
      │     ├── Classrooms & Labs (/rooms)
      │     ├── Faculty Profiles (/faculty)
      │     └── Student Batches (/batches)
      ├── Timetable Engine Operations
      │     ├── Pre-generation Validation Overview
      │     ├── Generate Timetable (/generate)
      │     └── Status & Progress Tracker
      └── Timetable Views & Management
            ├── Batch View
            ├── Faculty View
            ├── Room View
            └── Conflict Resolver & Manual Adjustment
```

---

## 4. Usability & Interaction Requirements

1. **Information Density:** Timetable matrix must clearly display subject code, teacher abbreviation, room number, and session type (Lecture vs Lab) without visual clutter.
2. **Instant Feedback:** Master data forms must validate uniqueness (room name, subject code, email) inline.
3. **Accessibility:** Comply with WCAG 2.1 AA standards for high contrast schedule cells and keyboard navigability.
