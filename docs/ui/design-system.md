# Design System Tokens & Component Specifications — KronosTT

**Track:** UI + Data (Bhavishya Raghav)  
**Sprint:** Master Data & UI Design (Form-2: Research and UI/UX design)  
**Date:** September 2026

---

## 1. Design Tokens & Styling Principles

Built on **Tailwind CSS** with a professional, clean academic aesthetic:

### 1.1 Color Palette
- **Primary Brand:** Slate / Navy (`#0F172A`, `#1E293B`, `#2563EB`)
- **Backgrounds:** Clean Canvas (`#F8FAFC`, `#FFFFFF`)
- **Status Colors:**
  - Success / Approved: Emerald (`#10B981`, bg: `#ECFDF5`)
  - Warning / Soft Constraint Violation: Amber (`#F59E0B`, bg: `#FFFBEB`)
  - Error / Hard Conflict: Rose (`#EF4444`, bg: `#FEF2F2`)
  - Info / Lecture: Indigo (`#6366F1`, bg: `#EEF2FF`)
  - Lab Session: Cyan (`#06B6D4`, bg: `#ECFEFF`)

### 1.2 Typography
- Font Family: Inter, system-ui, -apple-system, sans-serif
- Hierarchy:
  - Page Titles: `text-2xl font-bold tracking-tight text-slate-900`
  - Section Headings: `text-lg font-semibold text-slate-800`
  - Table / Data Cells: `text-sm font-normal text-slate-700`
  - Meta & Badges: `text-xs font-medium uppercase tracking-wider`

---

## 2. Reusable Component Blueprint

1. **`DataTable`**: Standardized sorting, pagination controls, search input, and empty state rendering.
2. **`SlotTile`**: Renders timetable session details with interactive hover popover showing full subject title, instructor, room capacity, and batch strength.
3. **`StatusBadge`**: Pill-shaped status indicator for master data records and validation health.
4. **`ModalDialog`**: Accessible backdrop, trap focus, escape-to-close for CRUD operations.
