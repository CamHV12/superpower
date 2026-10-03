import { api } from '../../services/api';
import type { CustomerPage } from '../customers/customers.types';
import type {
  Employee,
  PageResponse,
  Project,
  ProjectFilters,
  ProjectMember,
  Task,
} from './projects.types';

const cleanParams = (params: Record<string, unknown>) =>
  Object.fromEntries(Object.entries(params).filter(([, value]) => value !== undefined && value !== ''));

export const projectsService = {
  async list(filters: ProjectFilters = {}) {
    const response = await api.get<PageResponse<Project>>('/projects', {
      params: cleanParams({ page: 0, size: 10, ...filters }),
    });
    return response.data;
  },

  async get(id: string) {
    const response = await api.get<Project>(`/projects/${id}`);
    return response.data;
  },

  async create(payload: {
    code: string;
    name: string;
    description?: string;
    managerId: string;
    customerId?: string;
    startDate: string;
    endDate: string;
    budget: number;
    priority: string;
  }) {
    const response = await api.post<Project>('/projects', payload);
    return response.data;
  },

  async update(id: string, payload: {
    name: string;
    description?: string;
    managerId: string;
    customerId?: string;
    startDate: string;
    endDate: string;
    budget: number;
    status: string;
    priority: string;
    progress: number;
  }) {
    const response = await api.put<Project>(`/projects/${id}`, payload);
    return response.data;
  },

  async updateProgress(id: string, progress: number) {
    const response = await api.patch<Project>(`/projects/${id}/progress`, null, {
      params: { progress },
    });
    return response.data;
  },

  async listMembers(projectId: string) {
    const response = await api.get<ProjectMember[]>(`/projects/${projectId}/members`);
    return response.data;
  },

  async addMember(projectId: string, employeeId: string, role: string) {
    const response = await api.post<ProjectMember>(`/projects/${projectId}/members`, {
      employeeId,
      role,
    });
    return response.data;
  },

  async removeMember(projectId: string, employeeId: string) {
    await api.delete(`/projects/${projectId}/members/${employeeId}`);
  },

  async listTasks(projectId: string, params: {
    page?: number;
    size?: number;
    status?: Task['status'];
    priority?: Task['priority'];
    assigneeId?: string;
    keyword?: string;
  } = {}) {
    const response = await api.get<PageResponse<Task>>(`/projects/${projectId}/tasks`, {
      params: cleanParams({ page: 0, size: 50, ...params }),
    });
    return response.data;
  },

  async createTask(projectId: string, payload: {
    code: string;
    title: string;
    description?: string;
    assigneeId: string;
    startDate: string;
    dueDate: string;
    estimatedHours: number;
    priority: Task['priority'];
  }) {
    const response = await api.post<Task>(`/projects/${projectId}/tasks`, payload);
    return response.data;
  },

  async updateTaskProgress(projectId: string, taskId: string, progress: number) {
    const response = await api.patch<Task>(
      `/projects/${projectId}/tasks/${taskId}/progress`,
      null,
      { params: { progress } },
    );
    return response.data;
  },

  async listCustomers(size = 100) {
    const response = await api.get<CustomerPage>('/customers', { params: { page: 0, size, active: true } });
    return response.data;
  },

  async listEmployees(size = 100) {
    const response = await api.get<PageResponse<Employee>>('/employees', {
      params: { page: 0, size },
    });
    return response.data;
  },
};
