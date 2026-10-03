import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { vi } from 'vitest';
import { ProjectsPage } from './ProjectsPage';

const list = vi.fn();
const listEmployees = vi.fn();
const create = vi.fn();

vi.mock('./projects.service', () => ({
  projectsService: {
    list,
    listEmployees,
    create,
  },
}));

describe('ProjectsPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    list.mockResolvedValue({
      content: [{
        id: 'p1',
        code: 'PRJ-001',
        name: 'Enterprise Dashboard',
        description: 'Quản lý doanh nghiệp',
        managerId: 'e1',
        managerName: 'Nguyễn Văn A',
        status: 'ACTIVE',
        priority: 'HIGH',
        startDate: '2026-10-01',
        endDate: '2026-12-31',
        budget: 100000000,
        progress: 35,
        createdAt: '2026-10-01T00:00:00Z',
        updatedAt: '2026-10-01T00:00:00Z',
      }],
      totalElements: 1,
      totalPages: 1,
      size: 10,
      number: 0,
      first: true,
      last: true,
    });
    listEmployees.mockResolvedValue({
      content: [{ id: 'e1', fullName: 'Nguyễn Văn A', email: 'a@example.com', active: true }],
      totalElements: 1,
      totalPages: 1,
      size: 100,
      number: 0,
      first: true,
      last: true,
    });
  });

  it('loads real project data', async () => {
    render(<MemoryRouter><ProjectsPage /></MemoryRouter>);

    expect(screen.getByRole('heading', { name: 'Dự án' })).toBeInTheDocument();
    await waitFor(() => expect(screen.getByText('Enterprise Dashboard')).toBeInTheDocument());
    expect(screen.getByText('Nguyễn Văn A')).toBeInTheDocument();
    expect(list).toHaveBeenCalled();
    expect(listEmployees).toHaveBeenCalled();
  });

  it('shows create project form when requested', async () => {
    render(<MemoryRouter><ProjectsPage /></MemoryRouter>);

    await waitFor(() => screen.getByText('Enterprise Dashboard'));
    screen.getByRole('button', { name: 'Tạo dự án' }).click();

    expect(screen.getByText('Mã dự án')).toBeInTheDocument();
    expect(screen.getByText('Tên dự án')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Lưu dự án' })).toBeInTheDocument();
  });
});
