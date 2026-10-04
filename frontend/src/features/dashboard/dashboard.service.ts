import { api } from '../../services/api';

export interface DashboardOverview {
  totalEmployees: number;
  activeEmployees: number;
  totalProjects: number;
  activeProjects: number;
  totalCustomers: number;
  activeCustomers: number;
  totalTasks: number;
  overdueTasks: number;
}

export const dashboardService = {
  async overview() {
    const response = await api.get<DashboardOverview>('/dashboard/overview');
    return response.data;
  },
};
