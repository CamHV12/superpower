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

export interface DashboardStatusCount {
  status: string;
  count: number;
}

export interface DashboardOperational {
  projectStatuses: DashboardStatusCount[];
  taskStatuses: DashboardStatusCount[];
}

export const dashboardService = {
  async overview() {
    const response = await api.get<DashboardOverview>('/dashboard/overview');
    return response.data;
  },

  async operational() {
    const response = await api.get<DashboardOperational>('/dashboard/operational');
    return response.data;
  },
};
