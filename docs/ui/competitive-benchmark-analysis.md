# Competitive Benchmark & UX Evaluation of Academic Scheduling Platforms

**Author:** Bhavishya Raghav (Backend & Frontend Lead, Master Data & UI)  
**Project:** SKIT-CSE-2023-2027-05 / KronosTT  
**Date:** August 12, 2026  
**Status:** Completed  
**Sprint:** Sprint 2 — Master Data & UI Design  

---

## 1. Overview & Objective

To ensure that **KronosTT** delivers a modern, intuitive, and high-efficiency user experience, an empirical competitive analysis was performed evaluating four established academic timetabling platforms:
1. **UniTime** (Open-source comprehensive university scheduling system)
2. **aSc TimeTables** (Commercial desktop scheduling software)
3. **FET** (Free Evolutionary Timetabling software)
4. **TimeTable Plus** (Proprietary higher-education scheduling SaaS)

The evaluation benchmarks user onboarding speed, master data management efficiency, visual timetable clarity, manual conflict resolution ergonomics, and responsive accessibility.

---

## 2. Competitive Comparison Matrix

| Evaluation Dimension | UniTime | aSc TimeTables | FET | TimeTable Plus | KronosTT (Target Architecture) |
|---|---|---|---|---|---|
| **Architecture** | Java / GWT Web Client | Legacy Windows Desktop C++ | C++ Qt Desktop Application | Cloud SaaS (React / Node) | Modern Monorepo (Spring Boot + React/TS) |
| **Room Data Management** | Complex multi-page forms, high cognitive load | Desktop dialog with tabs, rigid table schema | Text-heavy XML dialogs, steep learning curve | Clean card grid, limited bulk editing | Responsive Data Table + Flyout Drawer + CSV import |
| **Conflict Visualization** | Text warning list after solver execution | Red diagonal stripes across cells, visual clutter | Static console log of broken constraints | Modal alert dialog, disoriented context | Real-time inline collision badges + ghost slot indicators |
| **Manual Override UX** | Form-based drop-down reassignments | Drag-and-drop with pin/lock, desktop-only | Manual constraint editing and re-solving | Click-to-swap panel with 2-second lag | Micro-animated drag-to-move with collision snapping |
| **Mobile / Tablet Usability** | Non-responsive, broken on viewport < 1024px | None (Desktop only) | None (Desktop only) | Moderate (Fluid container, tiny grid text) | Full responsive breakpoints with mobile agenda card stack |
| **Master Data Validation** | Server-side validation upon save attempt | In-memory validation on modal close | XML parser schema validation on import | Client-side reactive validation | Reactive Bean Validation + Instant OpenAPI Schema Feedback |

---

## 3. Deep Dive: Competitor Bottlenecks & UX Pitfalls

### 3.1 UniTime (University Timetabling System)
- **Strengths**: Comprehensive constraint engine (CPSolver), handles complex course catalog hierarchies and room equipment requirements.
- **Critical Flaws**:
  - GWT-based user interface designed in 2008; excessively dense navigation trees.
  - Room editing requires traversing 4 distinct sub-menus (`Instructional Offerings -> Rooms -> Features -> Preferences`).
  - No visual affordance for physical room layout or building proximity.
  - Incomplete feedback during manual session movement, requiring page reload to verify conflict state.

### 3.2 aSc TimeTables
- **Strengths**: Widely adopted in Indian school/college environments; intuitive card-based layout during automated runs; drag-and-drop manual adjustment.
- **Critical Flaws**:
  - Requires native Windows installation; zero multi-user concurrent editing capabilities.
  - Binary file storage (`.roz`) creates synchronization nightmares among department committee members.
  - Poor room capacity handling: allows scheduling 70 students into a 40-seat room without warning if room capacity constraint is toggled soft.

### 3.3 FET (Free Evolutionary Timetabling)
- **Strengths**: High solver speed using heuristic genetic algorithms; completely free and open-source.
- **Critical Flaws**:
  - User interface uses antiquated Qt widgets that are impenetrable to non-technical academic staff.
  - Visual output consists of raw HTML tables without interactive filtering, dragging, or search.
  - Zero support for modern RBAC (Dean, HOD, Faculty, Student roles).

---

## 4. Architectural Directives for KronosTT

From this benchmark analysis, the following UX and UI design mandates are established for KronosTT:

```
+-----------------------------------------------------------------------------------+
|                        KRONOSTT DESIGN & WORKFLOW MANDATES                        |
+-----------------------------------------------------------------------------------+
| 1. Master Data Primacy:                                                           |
|    Room & subject setup must be completed in under 5 minutes through combined     |
|    batch CSV import and quick-add slide-over drawers.                             |
|                                                                                   |
| 2. Zero-Loss Manual Overrides:                                                    |
|    When an administrator moves a timetable slot, the UI must highlight all valid  |
|    alternate drop zones and give clear visual indicators for hard collisions.     |
|                                                                                   |
| 3. Tri-Perspective Visual Grid:                                                   |
|    Provide instant toggle between:                                                |
|    (a) Section/Batch View (Student perspective)                                   |
|    (b) Teacher View (Faculty workload & free-period perspective)                  |
|    (c) Room/Lab View (Campus infrastructure utilization perspective)              |
|                                                                                   |
| 4. Cloud Collaborative & Role-Governed:                                           |
|    HOD reviews and approves; Coordinators configure; Faculty view & request swaps.|
+-----------------------------------------------------------------------------------+
```

---

## 5. Summary
The competitive analysis demonstrates a major opportunity for KronosTT: combining the algorithmic rigor of constraint solvers (Google OR-Tools) with a consumer-grade, accessible React/Tailwind web experience.
