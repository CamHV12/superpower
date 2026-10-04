import { api } from '../../services/api';

export interface AuditLog {
  id: string;
  actorUserId: string | null;
  actorEmail: string | null;
  action: string;
  path: string;
  statusCode: number;
  durationMs: number;
  ipAddress: string | null;
  userAgent: string | null;
  createdAt: string;
}

interface AuditPage {
  content: AuditLog[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export const auditService = {
  async list(page = 0, size = 20) {
    const response = await api.get<AuditPage>('/audit-logs', { params: { page, size } });
    return response.data;
  },
};