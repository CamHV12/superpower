export type ProjectStatus = 'DRAFT' | 'ACTIVE' | 'ON_HOLD' | 'COMPLETED' | 'CANCELLED';
export type ProjectPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';

export interface Employee {
  id: string;
  fullName: string;
  email: string;
  phone?: string | null;
  active: boolean;
}

export interface Project {
  id: string;
  code: string;
  name: string;
  description?: string | null;
  managerId: string;
  managerName: string;
  status: ProjectStatus;
  priority: ProjectPriority;
  startDate: string;
  endDate: string;
  budget: number;
  progress: number;
  createdAt: string;
  updatedAt: string;
}

export interface Task {
  id: string;
  projectId: string;
  code: string;
  title: string;
  description?: string | null;
  assigneeId: string;
  assigneeName: string;
  status: 'TODO' | 'IN_PROGRESS' | 'REVIEW' | 'DONE' | 'CANCELLED';
  priority: ProjectPriority;
  startDate: string;
  dueDate: string;
  estimatedHours: number;
  actualHours: number;
  progress: number;
  createdAt: string;
  updatedAt: string;
}

export interface ProjectMember {
  id: string;
  projectId: string;
  employeeId: string;
  employeeName: string;
  role: string;
  joinedAt: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface ProjectFilters {
  page?: number;
  size?: number;
  status?: ProjectStatus;
  priority?: ProjectPriority;
  managerId?: string;
  keyword?: string;
}
