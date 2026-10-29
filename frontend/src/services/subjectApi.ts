import { env } from '../config/env';
import type { SubjectDto, PageResponse } from '../types/subject';

const API_BASE_URL = `${env.apiBaseUrl}/v1/subjects`;

export async function getAllSubjects(page = 0, size = 20): Promise<PageResponse<SubjectDto>> {
  const res = await fetch(`${API_BASE_URL}?page=${page}&size=${size}&sort=subjectCode,asc`);
  if (!res.ok) {
    throw new Error(`Failed to fetch subjects: ${res.statusText}`);
  }
  return res.json();
}

export async function getAllSubjectsUnpaged(): Promise<SubjectDto[]> {
  const res = await fetch(`${API_BASE_URL}/all`);
  if (!res.ok) {
    throw new Error(`Failed to fetch all subjects: ${res.statusText}`);
  }
  return res.json();
}

export async function getSubjectById(id: number): Promise<SubjectDto> {
  const res = await fetch(`${API_BASE_URL}/${id}`);
  if (!res.ok) {
    throw new Error(`Failed to fetch subject ${id}: ${res.statusText}`);
  }
  return res.json();
}

export async function getSubjectByCode(code: string): Promise<SubjectDto> {
  const res = await fetch(`${API_BASE_URL}/by-code/${encodeURIComponent(code)}`);
  if (!res.ok) {
    throw new Error(`Failed to fetch subject with code ${code}: ${res.statusText}`);
  }
  return res.json();
}

export async function createSubject(subject: SubjectDto): Promise<SubjectDto> {
  const res = await fetch(API_BASE_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(subject),
  });
  if (!res.ok) {
    const error = await res.json().catch(() => ({ message: res.statusText }));
    throw new Error(error.message || `Failed to create subject`);
  }
  return res.json();
}

export async function updateSubject(id: number, subject: SubjectDto): Promise<SubjectDto> {
  const res = await fetch(`${API_BASE_URL}/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(subject),
  });
  if (!res.ok) {
    const error = await res.json().catch(() => ({ message: res.statusText }));
    throw new Error(error.message || `Failed to update subject`);
  }
  return res.json();
}

export async function deleteSubject(id: number): Promise<void> {
  const res = await fetch(`${API_BASE_URL}/${id}`, {
    method: 'DELETE',
  });
  if (!res.ok) {
    const error = await res.json().catch(() => ({ message: res.statusText }));
    throw new Error(error.message || `Failed to delete subject`);
  }
}
