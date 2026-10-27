export type SubjectType = 'LECTURE' | 'LAB';

export interface SubjectDto {
  id?: number;
  subjectCode: string;
  subjectName: string;
  weight: number;
  slotDuration: number;
  subjectType: SubjectType;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
