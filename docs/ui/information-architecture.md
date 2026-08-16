# KronosTT Information Architecture & Navigation Taxonomy

**Author:** Bhavishya Raghav (Backend & Frontend Lead, Master Data & UI)  
**Project:** SKIT-CSE-2023-2027-05 / KronosTT  
**Date:** August 16, 2026  
**Status:** Completed  
**Sprint:** Sprint 2 — Master Data & UI Design  

---

## 1. Information Architecture Overview

The Information Architecture (IA) defines the structural taxonomy, content hierarchy, and navigational pathways of the KronosTT platform. Designed specifically for academic institutions with multi-tiered administrative hierarchies, the IA minimizes interaction depth (maximum 3 clicks to any operational screen) while enforcing clear boundaries between Master Data Management, Timetable Generation, and Review/Approval.

```
+-----------------------------------------------------------------------------------------+
|                                KRONOSTT SITEMAP & IA TREE                               |
+-----------------------------------------------------------------------------------------+
|                                                                                         |
|  [Top-Level Navigation Bar]                                                             |
|  ├── Dashboard (Overview KPIs, active schedules, conflict alerts)                       |
|  ├── Master Data (Entities driving scheduling constraints)                              |
|  │   ├── Rooms & Infrastructure                                                         |
|  │   │   ├── Room Directory (Filter by Building, Floor, Type, Capacity)                |
|  │   │   ├── Add / Edit Room Modal Drawer                                               |
|  │   │   └── Bulk CSV Ingestion & Validation                                            |
|  │   ├── Subjects & Curriculum                                                          |
|  │   │   ├── Course Catalog (Code, Name, Department, L-T-P split)                       |
|  │   │   └── Curriculum Matrix (Subject to Semester mapping)                            |
|  │   ├── Faculty Management                                                             |
|  │   │   ├── Faculty Directory (Designation, Max Hours, Department)                    |
|  │   │   └── Availability Matrix (Preferred slots & leave settings)                     |
|  │   └── Student Batches                                                                |
|  │       ├── Section Roster (e.g. 5A, 5B, 5C)                                           |
|  │       └── Lab Sub-Batches (e.g. A1, A2, B1, B2)                                      |
|  ├── Timetable Engine (Scheduling Operations)                                           |
|  │   ├── Generator Console (Constraint weight sliders, solver timeout, run trigger)    |
|  │   ├── Generation History (Past solver runs, objective scores, execution logs)        |
|  │   └── Review & Tweak Workspace (Interactive Drag-and-drop grid)                      |
|  ├── Department Timetables (Published Views)                                            |
|  │   ├── Class / Section View                                                           |
|  │   ├── Teacher View                                                                   |
|  │   └── Room / Lab View                                                                |
|  └── Settings & RBAC                                                                    |
|      ├── Academic Calendar (Terms, working days, daily period slots)                    |
|      └── User Management (Role assignment: Admin, Coordinator, Faculty, Viewer)         |
|                                                                                         |
+-----------------------------------------------------------------------------------------+
```

---

## 2. Navigation Taxonomy & Routing Schema

The front-end client utilizes React Router 6.x with strict URL route definitions mapped to REST API back-end resource collections:

| Application Route | Associated Resource | Primary Roles | Key UI Layout Pattern |
|---|---|---|---|
| `/dashboard` | `/api/v1/analytics/overview` | Admin, Coordinator, HOD | Metric KPI Cards + Split Action Grid |
| `/master/rooms` | `/api/v1/rooms` | Admin, Coordinator | Sortable Data Table + Filter Drawer |
| `/master/rooms/:id` | `/api/v1/rooms/{id}` | Admin, Coordinator | Room Profile + Weekly Occupancy Grid |
| `/master/subjects` | `/api/v1/subjects` | Admin, Coordinator | Grouped Card Catalog by Semester |
| `/master/faculty` | `/api/v1/teachers` | Admin, Coordinator | Staff Table + Workload Badges |
| `/master/batches` | `/api/v1/batches` | Admin, Coordinator | Nested Tree List (Section -> Sub-Batches) |
| `/engine/generate` | `/api/v1/timetable/generate`| Coordinator, HOD | Parameter Wizard + Live Execution Log |
| `/timetable/review/:id`| `/api/v1/timetable/{id}` | Coordinator, HOD | Full-Screen Drag-and-Drop Matrix |
| `/timetable/view` | `/api/v1/timetable/active` | All Users, Students | Multi-Tab Schedule Grid + Export Bar |

---

## 3. Master Data Entity Relationships

```
+----------------+          +-------------------+          +-------------------+
|   DEPARTMENT   | 1 ---- * |      TEACHER      | 1 ---- * |  SUBJECT_TEACHER  |
+----------------+          +-------------------+          +-------------------+
        | 1                           | 1                            | *
        |                             |                              |
        *                             *                              |
+----------------+          +-------------------+                    |
|     BATCH      | 1 ---- * |      SESSION      | * -----------------+
+----------------+          +-------------------+
        |                             | *
        |                             |
        *                             | 
+----------------+                    |
|   SUB_BATCH    |                    |
+----------------+                    *
                            +-------------------+          +-------------------+
                            | SCHEDULED_SESSION | * ---- 1 |       ROOM        |
                            +-------------------+          +-------------------+
```

---

## 4. Design Guidelines for Master Data Screens
1. **Predictable Action Bars**: Every master data screen must feature a consistent top action toolbar containing Search input, Status filter, Sort order, and primary `Add Entity` action button.
2. **Context Preservation**: Editing an existing entity must occur within a right-aligned sliding flyout panel or modal, preventing user loss of table scroll position.
3. **Empty States**: If a data view contains zero entries, an actionable empty state with an SVG illustration and explicit "Add your first Room / Subject" CTA must be rendered.
