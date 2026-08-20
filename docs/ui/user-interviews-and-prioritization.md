# User Interview Synthesis & Feature Prioritization Matrix

**Author:** Bhavishya Raghav (Backend & Frontend Lead, Master Data & UI)  
**Project:** SKIT-CSE-2023-2027-05 / KronosTT  
**Date:** August 20, 2026  
**Status:** Completed  
**Sprint:** Sprint 2 — Master Data & UI Design  

---

## 1. Research Overview & Methodology

To ground the software requirements in real institutional needs, semi-structured interviews were conducted with 12 faculty members, 3 academic timetable coordinators, and 2 department heads across the Engineering departments of SKIT Jaipur.

```
+-----------------------------------------------------------------------------------+
|                            INTERVIEWEE SAMPLE BREAKDOWN                           |
+-----------------------------------------------------------------------------------+
| • 2 Department Heads (CSE & IT)                                                   |
| • 3 Timetable Committee Members (5+ years manual scheduling experience)           |
| • 5 Senior Faculty / Associate Professors (Teaching >12 hours/week)               |
| • 4 Assistant Professors & Lab Coordinators (Managing practical batch splits)     |
+-----------------------------------------------------------------------------------+
```

---

## 2. Key Synthesis & Interview Insights

### Finding 1: Room Allocation Bottlenecks
- *Quote (Timetable Committee Convener):* "The hardest part isn't placing the lectures—it's finding available rooms with working projectors and enough capacity for combined sections."
- *Takeaway:* The Room Management interface must track not only capacity but room equipment tags (Projector, Smart Board, AC, Workstation Count).

### Finding 2: Lab Batch Fragmentation
- *Quote (Lab In-Charge):* "In CSE 3rd year, we have 60 students per section split into Batch 1 and Batch 2. Both batches need computer labs simultaneously during practical periods. If the system puts Batch 1 on Tuesday and Batch 2 on Thursday, syllabus synchronization breaks down."
- *Takeaway:* The scheduling constraint model and UI must support synchronized parallel batch allocation.

### Finding 3: Faculty Fatigue & Continuous Teaching
- *Quote (Assistant Professor):* "Teaching 3 hours continuously in the afternoon heat without a 10-minute break destroys lecture quality."
- *Takeaway:* Enforce a soft constraint: Maximum 2 consecutive lecture periods for any faculty member.

---

## 3. Kano Feature Prioritization Model

To balance sprint scope within our academic development timeline (August 2026 – March 2027), features were categorized using the Kano model:

```
+-----------------------------------------------------------------------------------+
|                        KANO FEATURE PRIORITIZATION MATRIX                         |
+-----------------------------------------------------------------------------------+
|                                                                                   |
|  [MUST-HAVES - Baseline Functionality]                                            |
|  • Room Management CRUD with capacity & building tracking                         |
|  • Zero hard conflict scheduling (Faculty clash, Room clash, Batch clash)         |
|  • Section & Room master data CSV bulk import                                     |
|  • Student Section & Faculty weekly timetable views                               |
|                                                                                   |
|  [PERFORMANCE FEATURES - Linear Satisfaction]                                     |
|  • Drag-and-drop manual schedule tweaks with live conflict detection              |
|  • Fast solver execution (<30 seconds for 700+ sessions)                          |
|  • Departmental room occupancy heatmap                                            |
|  • Standard Form-3 Weekly Progress Report PDF generation                          |
|                                                                                   |
|  [DELIGHTERS - Competitive Differentiators]                                       |
|  • Instant Room Relocation alert notification                                      |
|  • Mobile responsive agenda view for students and faculty                         |
|  • Multi-criteria optimization penalty score visualization                        |
|                                                                                   |
+-----------------------------------------------------------------------------------+
```

---

## 4. Sprint 2 & 3 Roadmap Alignment

1. **Sprint 2 (Current):** Master Data & UI Design
   - Finalize Room Management CRUD schemas and REST controllers.
   - Implement Database Indexing to support sub-millisecond query performance on slot lookups.
   - Build UI token design system (Tailwind CSS, fonts, card components).

2. **Sprint 3:** Scheduling Engine Integration
   - Feed master data into Google OR-Tools CP-SAT solver.
   - Enforce hard room capacity and lab synchronization constraints.
