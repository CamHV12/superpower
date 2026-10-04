import { api } from '../../services/api';

export interface NotificationItem {
  id: string;
  type: string;
  title: string;
  message: string;
  read: boolean;
  createdAt: string;
  readAt: string | null;
}

interface NotificationPage {
  content: NotificationItem[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export const notificationService = {
  async list(unreadOnly = false, page = 0, size = 10) {
    const response = await api.get<NotificationPage>('/notifications', {
      params: { unreadOnly, page, size },
    });
    return response.data;
  },

  async unreadCount() {
    const response = await api.get<number>('/notifications/unread-count');
    return response.data;
  },

  async markRead(id: string) {
    await api.patch('/notifications/' + id + '/read');
  },

  async markAllRead() {
    await api.patch('/notifications/read-all');
  },
};