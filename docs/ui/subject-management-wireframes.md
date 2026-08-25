# Subject & Curriculum Management Wireframe Specifications

**Author:** Bhavishya Raghav (Backend & Frontend Lead, Master Data & UI)  
**Project:** SKIT-CSE-2023-2027-05 / KronosTT  
**Date:** August 25, 2026  
**Status:** Completed  
**Sprint:** Sprint 2 — Master Data & UI Design  

---

## 1. Overview & Purpose

Curriculum specifications dictate the instructional demand placed on the scheduling engine. Each subject is defined by its course code, title, department, target semester, and credit breakdown: Lecture (L), Tutorial (T), and Practical/Laboratory (P) periods per week. This document outlines the user interface blueprints and interaction patterns for configuring course offerings and allocating teaching staff.

---

## 2. Desktop Subject Directory Wireframe (1440px Viewport)

```
+-------------------------------------------------------------------------------------------------------------------+
|  [Logo] KronosTT | CSE Department        [Search courses, codes, semesters...]    (🔔 3) [Bhavishya Raghav (Admin) v]|
+-------------------------------------------------------------------------------------------------------------------+
|  [Dashboard | Master Data v (Subjects Active) | Timetable Engine | Department Timetables | Reports | Settings]     |
+-------------------------------------------------------------------------------------------------------------------+
|                                                                                                                   |
|  Master Data > Subjects & Curriculum Offerings                                [📥 Import CSV] [+ Add New Subject]  |
|                                                                                                                   |
|  +-------------------------------------------------------------------------------------------------------------+  |
|  | FILTER TOOLBAR                                                                                              |  |
|  | Semester: [Semester V v]  Department: [CSE v]  Type: [All Types v]  Search: [🔍 Code or Title...         ]  |  |
|  | Quick Filter: ( ) All Semesters   (•) 3rd Year (Sem V)   ( ) 4th Year (Sem VII)   ( ) Electives Only        |  |
|  +-------------------------------------------------------------------------------------------------------------+  |
|                                                                                                                   |
|  +-------------------------------------------------------------------------------------------------------------+  |
|  | SUBJECT CATALOG TABLE                                                                  Showing 1-8 of 18    |  |
|  | Course Code | Subject Title                | Sem | L-T-P  | Total Hrs | Type   | Assigned Faculty    | Actions|  |
|  | ----------------------------------------------------------------------------------------------------------- |  |
|  | 5CS4-01     | Operating Systems            | V   | 3-0-2  | 5 hrs/wk  | THEORY | Dr. Priya Verma     | [✏][🗑] |  |
|  | 5CS4-02     | Compiler Design              | V   | 3-1-0  | 4 hrs/wk  | THEORY | Prof. Rajesh Sharma | [✏][🗑] |  |
|  | 5CS4-03     | Database Management Systems  | V   | 3-0-2  | 5 hrs/wk  | THEORY | Dr. Neha Agarwal    | [✏][🗑] |  |
|  | 5CS4-04     | Computer Networks            | V   | 3-0-2  | 5 hrs/wk  | THEORY | Er. Amit Mathur     | [✏][🗑] |  |
|  | 5CS4-05     | Software Engineering         | V   | 3-0-0  | 3 hrs/wk  | THEORY | Er. Sunita Jain     | [✏][🗑] |  |
|  | 5CS4-21     | Operating Systems Lab        | V   | 0-0-4  | 4 hrs/wk  | LAB    | Dr. Priya Verma (A1)| [✏][🗑] |  |
|  | 5CS4-22     | DBMS Lab                     | V   | 0-0-4  | 4 hrs/wk  | LAB    | Dr. Neha Agarwal(A2)| [✏][🗑] |  |
|  +-------------------------------------------------------------------------------------------------------------+  |
|                                                                                                                   |
+-------------------------------------------------------------------------------------------------------------------+
```

---

## 3. "Add / Edit Subject" Form Modal Blueprint (600px Width)

```
+---------------------------------------------------------------+
|  Configure Subject Offering                               [X] |
|  Enter RTU curriculum course specifications and credit load.  |
+---------------------------------------------------------------+
|                                                               |
|  Course Identification                                        |
|  Course Code *                        Department *            |
|  [ 5CS4-01                          ] [ Computer Science  v ] |
|  Official university code                                     |
|                                                               |
|  Subject Title *                                              |
|  [ Operating Systems                                        ] |
|                                                               |
|  Curriculum Credit Split & Weekly Hours                       |
|  Target Semester *                    Subject Category *      |
|  [ Semester V (3rd Year)        v ]   [ Core Theory & Lab v ] |
|                                                               |
|  Lecture (L)       Tutorial (T)       Practical / Lab (P)     |
|  [ 3 ] hrs/week    [ 0 ] hrs/week     [ 2 ] hrs/week (1 block)|
|                                                               |
|  Facility Requirement                                         |
|  Required Facility Type *                                     |
|  (•) Standard Classroom (LH)   ( ) Computer Lab   ( ) HW Lab  |
|                                                               |
|  Faculty Allocation                                           |
|  Lead Faculty In-Charge *                                     |
|  [ Dr. Priya Verma (Assistant Professor)                  v ] |
|                                                               |
|  Co-Faculty / Lab Assistants (Multi-Select)                   |
|  [ Er. Rahul Sharma x ] [ Er. Pooja Meena x ]                 |
|                                                               |
+---------------------------------------------------------------+
|  [ Cancel ]                                 [ Save Subject ✔ ]|
+---------------------------------------------------------------+
```

---

## 4. Constraint Generation Rules
- **L-T-P to Session Generation Mapping:**
  - `L=3`: Generates 3 distinct 1-hour `Session` records with `session_type = 'LECTURE'`, tagged for the whole section (e.g., CSE-5A).
  - `T=1`: Generates 1 1-hour `Session` record with `session_type = 'TUTORIAL'`.
  - `P=2`: Generates 1 continuous 2-hour `Session` record with `session_type = 'PRACTICAL'` for each sub-batch (A1 in Lab-1, A2 in Lab-2).
- This automatic session decomposition enables the constraint solver to place sessions without manual hour counting.
