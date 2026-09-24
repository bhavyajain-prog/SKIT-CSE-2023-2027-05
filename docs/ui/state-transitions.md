# End-to-End Screen Interaction Flows & State Transitions — KronosTT

**Track:** UI + Data (Bhavishya Raghav)  
**Sprint:** Master Data & UI Design (Form-2: Research and UI/UX design)  
**Date:** September 2026

---

## 1. Overview

This document specifies the state machines and interaction flows governing user operations across timetable generation, review, and master data modifications.

---

## 2. Timetable Generation State Machine

```
   [ IDLE / DRAFT ]
          │
          │ User clicks "Generate Timetable"
          ▼
   [ VALIDATING INPUTS ] ───(Errors found)───► [ VALIDATION FAILED ]
          │                                          │
          │ (Master data valid)                      │ User corrects data
          ▼                                          ▼
   [ SCHEDULING RUNNING ] <──────────────────────────┘
          │
          ├── (Heuristic Placement)
          ├── (CSP Constraint Solver)
          ▼
   [ GENERATION COMPLETE ]
          │
          ├── [ Conflicts Detected ] ──► [ Conflict Resolver Screen ]
          └── [ Feasible Schedule ]  ──► [ Review & Approval Screen ]
                                                │
                                                ├── Approve ──► [ PUBLISHED ]
                                                └── Adjust  ──► [ MANUAL LOCK/MOVE ]
```

---

## 3. Master Data Mutation Flow

1. **Optimistic UI Updates:**
   - On deleting a room, show confirmation dialog: `"Are you sure you want to delete room LT-101? This cannot be undone."`
   - If server returns `409 Conflict` (room is active in scheduled sessions), rollback deletion and display an alert banner: `"Cannot delete room because it is assigned in active timetable sessions."`
2. **Batch Assignment Workflow:**
   - Selecting a `fixedBatchId` displays the batch's current strength and warns if `batch.strength > room.capacity`.
