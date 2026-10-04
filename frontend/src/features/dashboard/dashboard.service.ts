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

export interface DashboardEmployeeWorkload {
  employeeId: string;
  employeeName: string;
  openTasks: number;
  overdueTasks: number;
  estimatedHours: number;
  actualHours: number;
}

export interface DashboardCustomerKpi {
  customerId: string;
  customerName: string;
  projectCount: number;
  activeProjects: number;
  projectBudget: number;
}

export interface DashboardOperational {
  projectStatuses: DashboardStatusCount[];
  taskStatuses: DashboardStatusCount[];
  employeeWorkloads: DashboardEmployeeWorkload[];
  customerKpis: DashboardCustomerKpi[];
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
