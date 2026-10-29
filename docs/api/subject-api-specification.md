# Subject Management REST API Specification

**Author:** Bhavishya Raghav (Backend & Frontend Lead, Master Data & UI)  
**Project:** SKIT-CSE-2023-2027-05 / KronosTT  
**Date:** October 17, 2026  
**Status:** Completed  
**Sprint:** Sprint 2 — Master Data & UI Design  

---

## 1. Overview & Resource Definition

The Subject Management module manages academic course offerings, curriculum constraints, and instructional parameters. Each subject represents a teachable academic unit defined by university course codes, instructional delivery types (Theory Lecture vs. Practical Lab), weekly credit weights, and period slot durations.

### Base Resource URI
```http
/api/v1/subjects
```

---

## 2. Data Transfer Object Schema (`SubjectDto`)

```json
{
  "id": 1,
  "subjectCode": "5CS4-01",
  "subjectName": "Operating Systems",
  "weight": 3,
  "slotDuration": 1,
  "subjectType": "LECTURE"
}
```

### Field Constraints & Validation Rules

| Property | Type | Constraints | Description |
|---|---|---|---|
| `id` | Long | Read-only | Auto-generated primary key identifier |
| `subjectCode` | String | `@NotBlank`, `@Size(max = 50)`, `@Pattern(regexp = "^[A-Za-z0-9_-]+$")` | Unique university curriculum course code (e.g. `5CS4-01`, `5CS4-21`) |
| `subjectName` | String | `@NotBlank`, `@Size(max = 255)` | Full official descriptive course title |
| `weight` | Integer | `@NotNull`, `@Min(1)`, `@Max(10)` | Weekly credit weight / number of weekly periods |
| `slotDuration` | int | `@Min(1)`, `@Max(4)` | Consecutive periods per session (1 for theory, 2 for practical labs) |
| `subjectType` | Enum | `@NotNull`, `[LECTURE, LAB]` | Pedagogical delivery mode mapping to classroom vs lab room constraints |

---

## 3. REST API Endpoints Specification

### 3.1 List Subjects (Paginated)
- **Method:** `GET`
- **Path:** `/api/v1/subjects`
- **Query Parameters:**
  - `page` (int, default: 0)
  - `size` (int, default: 20)
  - `sort` (string, default: `subjectCode,asc`)
  - `type` (optional string, filter by `LECTURE` or `LAB`)
- **Responses:**
  - `200 OK`: Returns Spring `Page<SubjectDto>`

### 3.2 List All Subjects (Unpaged)
- **Method:** `GET`
- **Path:** `/api/v1/subjects/all`
- **Responses:**
  - `200 OK`: Returns `List<SubjectDto>`

### 3.3 Get Subject by ID
- **Method:** `GET`
- **Path:** `/api/v1/subjects/{id}`
- **Responses:**
  - `200 OK`: Subject details found and returned
  - `404 Not Found`: `{"status": 404, "message": "Subject not found with id: {id}"}`

### 3.4 Get Subject by Code
- **Method:** `GET`
- **Path:** `/api/v1/subjects/by-code/{code}`
- **Responses:**
  - `200 OK`: Subject details matching code
  - `404 Not Found`: No subject with specified code

### 3.5 Create Subject
- **Method:** `POST`
- **Path:** `/api/v1/subjects`
- **Request Body:** `SubjectDto` (id omitted)
- **Responses:**
  - `201 Created`: Returns created `SubjectDto` with generated `id`
  - `400 Bad Request`: Validation failure on payload
  - `409 Conflict`: Subject code already exists (`ConflictException`)

### 3.6 Update Subject
- **Method:** `PUT`
- **Path:** `/api/v1/subjects/{id}`
- **Request Body:** `SubjectDto`
- **Responses:**
  - `200 OK`: Returns updated `SubjectDto`
  - `400 Bad Request`: Validation failure on payload
  - `404 Not Found`: Subject does not exist
  - `409 Conflict`: Subject code conflicts with another subject

### 3.7 Delete Subject
- **Method:** `DELETE`
- **Path:** `/api/v1/subjects/{id}`
- **Responses:**
  - `204 No Content`: Successfully deleted
  - `404 Not Found`: Subject does not exist
  - `409 Conflict`: Cannot delete subject referenced by active sessions or batch curricula

---

## 4. Error Response Format (`ApiError`)

```json
{
  "timestamp": "2026-10-17T14:30:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Subject with code '5CS4-01' already exists",
  "path": "/api/v1/subjects"
}
```
