# Subject Module Test Validation & Verification Report

**Author:** Bhavishya Raghav (Backend & Frontend Lead, Master Data & UI)  
**Project:** SKIT-CSE-2023-2027-05 / KronosTT  
**Date:** October 28, 2026  
**Status:** Completed  
**Sprint:** Sprint 2 — Master Data & UI Design  

---

## 1. Executive Summary

As part of the **CRUD operations on subjects** milestone (Sprint 2, October 19 – November 01, 2026), a comprehensive automated testing suite was engineered and validated for the Subject Management module. The suite includes:
- **14 Unit Tests** in `SubjectServiceTest.java` evaluating service-layer business logic, entity mapping, conflict detection, and transactional integrity.
- **10 Integration / MockMvc Tests** in `SubjectControllerTest.java` verifying HTTP serialization, URL routing, input validation constraints, and OpenAPI error responses.

---

## 2. Test Execution Matrix

### 2.1 Service Layer Tests (`SubjectServiceTest`)

| Test Method | Target Behavior | Expected Result | Status |
|---|---|---|---|
| `getAllSubjects_WithPageable_ReturnsPagedDto` | Paginated subject retrieval | Returns `Page<SubjectDto>` matching requested pageable | ✔ PASS |
| `getAllSubjects_Unpaged_ReturnsAllDtos` | Unpaged catalog retrieval | Returns complete `List<SubjectDto>` for drop-downs | ✔ PASS |
| `getSubjectById_WhenFound_ReturnsDto` | Lookup by primary key | Returns single DTO with matching ID | ✔ PASS |
| `getSubjectById_WhenNotFound_ThrowsException` | Lookup with invalid ID | Throws `ResourceNotFoundException` (404) | ✔ PASS |
| `getSubjectByCode_WhenFound_ReturnsDto` | Lookup by course code | Case-insensitive match returns DTO | ✔ PASS |
| `getSubjectByCode_WhenNotFound_ThrowsException` | Lookup non-existent code | Throws `ResourceNotFoundException` (404) | ✔ PASS |
| `createSubject_WhenValid_SavesAndReturnsDto` | Creation with valid payload | Trims code/name, persists, returns 201 DTO | ✔ PASS |
| `createSubject_WhenDuplicateCode_ThrowsConflict` | Duplicate course code | Throws `ConflictException` (409) | ✔ PASS |
| `updateSubject_WhenValid_UpdatesAndReturnsDto` | Field update on existing entity | Updates properties, persists, returns DTO | ✔ PASS |
| `updateSubject_WhenNotFound_ThrowsException` | Update non-existent entity | Throws `ResourceNotFoundException` (404) | ✔ PASS |
| `updateSubject_WhenDuplicateCode_ThrowsConflict` | Code conflict with other subject | Throws `ConflictException` (409) | ✔ PASS |
| `deleteSubject_WhenValid_DeletesEntity` | Removal of unreferenced subject | Calls repository delete | ✔ PASS |
| `deleteSubject_WhenNotFound_ThrowsException` | Delete non-existent ID | Throws `ResourceNotFoundException` (404) | ✔ PASS |
| `deleteSubject_WhenReferencedBySessions_ThrowsConflict` | Deletion protection check | Blocks removal, throws `ConflictException` (409) | ✔ PASS |

### 2.2 Controller Layer Tests (`SubjectControllerTest`)

| Test Method | HTTP Method | Target Endpoint | Status Code | Verification |
|---|---|---|---|---|
| `getAllSubjects_ReturnsOkWithPage` | `GET` | `/api/v1/subjects?page=0&size=20` | `200 OK` | Paged JSON content matches schema |
| `getAllSubjectsUnpaged_ReturnsOk` | `GET` | `/api/v1/subjects/all` | `200 OK` | Array of subjects returned |
| `getSubjectById_WhenExists_ReturnsOk` | `GET` | `/api/v1/subjects/1` | `200 OK` | Single subject payload verified |
| `getSubjectById_WhenNotFound_Returns404` | `GET` | `/api/v1/subjects/99` | `404 Not Found` | `ApiError` standard payload |
| `getSubjectByCode_WhenExists_ReturnsOk` | `GET` | `/api/v1/subjects/by-code/5CS4-01` | `200 OK` | Matches `5CS4-01` |
| `createSubject_WhenValid_Returns201Created` | `POST` | `/api/v1/subjects` | `201 Created` | Persisted entity ID returned |
| `createSubject_WhenBlankCode_Returns400BadRequest` | `POST` | `/api/v1/subjects` | `400 Bad Request` | `@NotBlank` constraint feedback |
| `createSubject_WhenConflict_Returns409Conflict` | `POST` | `/api/v1/subjects` | `409 Conflict` | Unique code violation feedback |
| `updateSubject_WhenValid_ReturnsOk` | `PUT` | `/api/v1/subjects/1` | `200 OK` | Modified title persisted |
| `deleteSubject_WhenSuccess_Returns204NoContent` | `DELETE` | `/api/v1/subjects/1` | `204 No Content` | Void response verified |
| `deleteSubject_WhenConflict_Returns409Conflict` | `DELETE` | `/api/v1/subjects/1` | `409 Conflict` | Referential integrity protected |

---

## 3. Build & Execution Metrics

- **Total Test Cases Executed:** 25 tests
- **Failures:** 0
- **Errors:** 0
- **Skipped:** 0
- **Build Status:** SUCCESS
- **Execution Environment:** Java 22 (LTS release 21 target), Spring Boot 3.x, H2 In-Memory Database, Mockito 5.x
