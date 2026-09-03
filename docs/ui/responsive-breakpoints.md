# Responsive Breakpoint Specifications & Mobile Adaptation Guidelines

**Author:** Bhavishya Raghav (Backend & Frontend Lead, Master Data & UI)  
**Project:** SKIT-CSE-2023-2027-05 / KronosTT  
**Date:** September 03, 2026  
**Status:** Completed  
**Sprint:** Sprint 2 — Master Data & UI Design  

---

## 1. Overview & Breakpoint Philosophy

Academic timetables are inherently broad, multi-column data structures that pose significant challenges on smaller screens. KronosTT utilizes a mobile-first responsive strategy, adapting layouts fluidly across four core device breakpoints while guaranteeing that students and faculty can access clear daily schedules on standard smartphones.

---

## 2. Breakpoint Architecture & Media Query Scale

```
+-----------------------------------------------------------------------------------+
|                        KRONOSTT RESPONSIVE BREAKPOINT SCALE                       |
+-----------------------------------------------------------------------------------+
| Breakpoint | Viewport Min | Target Hardware          | Grid Presentation Strategy |
|------------+--------------+--------------------------+----------------------------|
|  xs / sm   | < 768px      | Mobile Smartphones       | Stacked Day Accordion Card |
|  md        | 768px        | Tablets (Portrait)       | 3-Day Mini-Grid + Swiper   |
|  lg        | 1024px       | Tablets / Laptops        | 6-Day Grid with Scroll-Lock|
|  xl / 2xl  | >= 1280px    | Large Monitors & Desktops| Full 6x7 Matrix + Inspector|
+-----------------------------------------------------------------------------------+
```

---

## 3. Mobile Adaptation Strategy (< 768px)

On mobile devices, attempting to render a 6-day x 7-period grid results in illegible, microscopic text. Instead, KronosTT automatically transforms the matrix into a **Day-by-Day Agenda View**:

```
+---------------------------------------------------+
|  [Logo] KronosTT                 (🔔) [Profile v] |
+---------------------------------------------------+
|  Fall 2026 — B.Tech CSE (Sec A)                   |
|                                                   |
|  DAY SELECTOR PILL TABS                           |
|  [Mon] (• Tue) [Wed] [Thu] [Fri] [Sat]            |
|                                                   |
|  Tuesday, October 14, 2026                        |
|                                                   |
|  +---------------------------------------------+  |
|  | 08:00 - 08:50 AM (Period 1)                 |  |
|  | 5CS4-02 Compiler Design                     |  |
|  | 📍 LH-201 • 👨‍🏫 Prof. Rajesh Sharma           |  |
|  +---------------------------------------------+  |
|                                                   |
|  +---------------------------------------------+  |
|  | 08:50 - 09:40 AM (Period 2)                 |  |
|  | 5CS4-01 Operating Systems                   |  |
|  | 📍 LH-201 • 👩‍🏫 Dr. Priya Verma              |  |
|  +---------------------------------------------+  |
|                                                   |
|  +---------------------------------------------+  |
|  | 10:45 - 12:25 PM (Period 4 & 5 - 2 Hrs)     |  |
|  | 5CS4-21 OS Practical Laboratory             |  |
|  | • Batch A1: Lab-01 (Dr. Priya Verma)        |  |
|  | • Batch A2: Lab-02 (Er. Rahul Sharma)       |  |
|  +---------------------------------------------+  |
|                                                   |
|  [📥 Download Full Week PDF]                      |
+---------------------------------------------------+
```

---

## 4. Tablet Adaptation Strategy (768px – 1023px)

- In tablet landscape mode, the grid renders Monday through Wednesday with a floating bottom pagination swiper to slide into Thursday through Saturday.
- Time period column widths are pinned at minimum `110px` to maintain text legibility.
- Left-most Day label column is styled with `position: sticky; left: 0; z-index: 20` with a subtle dropshadow.

---

## 5. CSS Grid Implementation Tokens
```css
/* Responsive Grid Wrapper */
.timetable-viewport {
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

@media (min-width: 1024px) {
  .timetable-grid {
    display: grid;
    grid-template-columns: 120px repeat(7, minmax(130px, 1fr));
    gap: 0.5rem;
  }
}
```
