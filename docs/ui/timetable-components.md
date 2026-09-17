# Reusable Timetable Slot Component Specifications

**Track:** UI + Data (Bhavishya Raghav)  
**Sprint:** Master Data & UI Design (Form-2: Research and UI/UX design)  
**Date:** September 2026

---

## 1. Component Architecture: `TimetableSlotCard`

The `TimetableSlotCard` is the atomic visual building block of the weekly matrix grid. It encapsulates slot duration, room allocation, instructor metadata, and collision states.

---

## 2. Component Interface Definition (TypeScript)

```typescript
export interface TimetableSlotProps {
  id: string;
  dayOfWeek: 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY';
  startSlot: number;
  durationSlots: number;
  subjectCode: string;
  subjectName: string;
  teacherName: string;
  teacherInitials: string;
  roomName: string;
  roomCapacity: number;
  batchName: string;
  batchStrength: number;
  sessionType: 'LECTURE' | 'LAB' | 'TUTORIAL';
  isConflict?: boolean;
  conflictDetails?: string;
  isLocked?: boolean;
  onSlotClick?: (slotId: string) => void;
  onSlotMove?: (sourceSlotId: string, targetDay: string, targetSlot: number) => void;
}
```

---

## 3. Visual States & Micro-Interactions

### 3.1 Normal State (Theory Lecture)
- Single 55-minute slot.
- Header: Bold subject code (`CS501`) on top-left, room pill (`LT-101`) on top-right.
- Body: Instructor abbreviation (`Dr. R. Mehta`), session icon.
- Hover: Elevates with `translateY(-1px)` and subtle shadow.

### 3.2 Laboratory Session State (Multi-Slot Span)
- Spans 2 consecutive slots (`grid-column: span 2`).
- Accent color: Cyan-500 border with cyan-50 background.
- Displays multi-faculty badges (e.g. `Lead: Prof. Sharma`, `Co: Prof. Meena`).

### 3.3 Conflict / Collision Alert State
- Flashing amber/red dashed border.
- Warning badge `[! Conflict]` in top right corner.
- Tooltip displays exact clash: e.g. `"Room LT-101 double-booked with 5-CSE-B"`.

---

## 4. Accessibility Implementation
- Keyboard navigation: Arrow keys navigate between adjacent slots in the matrix.
- `aria-label`: `"Monday Slot 1, 08:00 to 08:55, Computer Networks in Lecture Hall 101 with Dr. Mehta"`.
- Focus ring: High-contrast blue outline on active slot focus.
