# Enterprise Management Dashboard — Design Specification

**Date:** 2026-10-03  
**Status:** Approved  
**Repository:** CamHV12/superpower  
**Target implementation stack:** React + Vite + TypeScript + Tailwind CSS + shadcn/ui + Lucide React + Recharts; Spring Boot + Java 17+ + Spring Security/JWT + Spring Data JPA; PostgreSQL.

## 1. Product goal

Build a modern enterprise management web application that gives business administrators and department users one place to monitor operations and manage employees, projects, finances, customers, and reports.

The first implementation should be production-oriented in structure, but it may use realistic Vietnamese mock data on the frontend until the REST backend is connected.

## 2. User roles

- **ADMIN:** full system access, users/roles/permissions, all business modules.
- **MANAGER:** employees, projects, customers, reports and operational dashboards.
- **HR:** employees, departments, positions and attendance.
- **ACCOUNTANT:** revenue, expenses, invoices, transactions and financial reports.
- **EMPLOYEE:** personal information, assigned projects and assigned tasks.

Authorization must be enforced by the backend. Frontend route/element guards are for UX only.

## 3. Functional modules

### Dashboard
- KPI cards: revenue, expenses, profit, employee count.
- Revenue vs expense trend.
- Profit trend.
- Project status distribution.
- Employee distribution.
- Customer growth.
- Recent activity feed.
- Notifications.
- Date range and basic filtering.

### Employee management
- Employee list with search, sorting, filtering and pagination.
- Employee detail.
- Create/update/delete employee.
- Department and position.
- Employment status.
- Attendance overview.

### Project management
- Project list.
- Project detail.
- Project status and progress.
- Project members.
- Tasks with assignee, priority, status and deadline.
- Search/filter/sort.
- Empty, loading and error states.

### Finance
- Financial overview.
- Revenue.
- Expenses.
- Invoices.
- Transactions.
- Status and date filters.
- Financial KPI and charts.

### Customer management
- Customer list.
- Customer detail.
- Contact information.
- Customer projects.
- Customer activity/history.
- Search, filtering and pagination.

### Reports
- Revenue report.
- Expense report.
- Employee report.
- Project report.
- Financial report.
- Date filters and export-ready presentation.

### Cross-cutting
- Authentication.
- JWT authorization.
- Role-based access control.
- Notifications.
- Audit log.
- Global search-ready architecture.
- Light/Dark mode.
- Responsive desktop/tablet/mobile layouts.

## 4. Frontend architecture

Feature-oriented structure:

```
frontend/
  src/
    assets/
    components/
      ui/
      layout/
      charts/
      common/
    features/
      auth/
      dashboard/
      employees/
      projects/
      finance/
      customers/
      reports/
    layouts/
      DashboardLayout.tsx
      AuthLayout.tsx
    routes/
      AppRoutes.tsx
    services/
      api.ts
      auth.service.ts
      employee.service.ts
      project.service.ts
      finance.service.ts
      customer.service.ts
    stores/
      auth.store.ts
      ui.store.ts
    hooks/
    types/
    utils/
    mock/
    App.tsx
    main.tsx
```

Components should be small and composable. Domain-specific behavior belongs in feature folders, not generic UI components.

## 5. Backend architecture

Domain-oriented Spring Boot packages:

```
backend/
  src/main/java/com/company/enterprise/
    common/
      exception/
      response/
      validation/
      audit/
    config/
    security/
    auth/
    dashboard/
    employee/
    project/
    finance/
    customer/
    report/
```

Each domain follows:

```
Controller -> Service -> Repository -> Entity
```

DTOs are exposed through API contracts instead of returning JPA entities directly.

## 6. Core database model

Initial entities/tables:

- users
- roles
- permissions
- user_roles
- role_permissions
- employees
- departments
- positions
- attendance
- customers
- customer_contacts
- projects
- project_members
- tasks
- invoices
- transactions
- expenses
- reports
- notifications
- audit_logs

Core relationships:

- User ↔ Employee
- Department → Employees
- Position → Employees
- Customer → Projects
- Project ↔ Employees through project_members
- Project → Tasks
- Project → Invoices
- Invoice → Transactions
- User → Notifications
- User → Audit logs

All mutable business entities should have appropriate created/updated timestamps and explicit status fields.

## 7. API conventions

Base path:

`/api/v1`

Initial resources:

- `/auth`
- `/dashboard`
- `/employees`
- `/departments`
- `/positions`
- `/attendance`
- `/projects`
- `/tasks`
- `/customers`
- `/customer-contacts`
- `/finance`
- `/invoices`
- `/transactions`
- `/expenses`
- `/reports`
- `/notifications`

CRUD APIs should use consistent HTTP semantics and a consistent error response format.

## 8. Authentication and security

- Spring Security.
- JWT access token.
- Passwords hashed with a secure password encoder.
- Stateless API authentication.
- Backend role checks.
- CORS configured for the frontend origin.
- Validation at API boundaries.
- No secrets committed to source control.
- Environment-specific configuration.

## 9. UI/UX specification

Visual direction: **Light + Dark Mode**.

- Default light theme.
- User-controlled dark theme.
- Primary color: blue.
- Accent color: orange.
- Neutral gray surfaces/borders.
- Moderate border radius.
- Clear typography and spacing.
- Lucide icons.
- Recharts for analytics.
- Subtle micro-animations.
- Mobile-first responsive behavior.
- Sidebar becomes a drawer on narrow screens.
- Tables become horizontally scrollable or responsive cards where appropriate.

Dashboard shell:

```
Sidebar | Header
        | Page content
```

Header includes search-ready area, notifications, theme switcher and current-user menu.

## 10. Data and states

The frontend must support:

- Loading skeletons.
- Empty states.
- API error states with retry.
- Search with no results.
- Form validation.
- Delete confirmation.
- Permission denied state.
- Pagination.
- Sorting.
- Filtering.
- Disabled/submitting states.

Mock data should use realistic Vietnamese names, companies, projects, invoices, dates and currency values. Mock data must be isolated so it can later be replaced by service/API calls.

## 11. Testing strategy

Follow RED → GREEN → REFACTOR.

Frontend:
- Unit tests for important utilities and state behavior.
- Component tests for critical interactions.
- Route/permission behavior tests where practical.

Backend:
- Unit tests for services.
- Controller/API tests for important endpoints.
- Repository tests only where custom queries justify them.
- Security/authorization tests for protected resources.

Production code should not be written before a relevant failing test for behavior under development.

## 12. Implementation sequence

1. Repository/project baseline and development branch.
2. Frontend scaffold and design system.
3. Application shell: sidebar, header, theme, routing.
4. Dashboard with realistic mock data and charts.
5. Authentication UI and backend security foundation.
6. Employee module.
7. Project/task module.
8. Customer module.
9. Finance module.
10. Reports.
11. Notifications/audit foundation.
12. API integration and replacement of mock services.
13. Validation, responsive review, error/empty/loading states.
14. Test suite and code review.
15. Final integration and documentation.

## 13. Non-goals for the first implementation

- Payroll processing.
- Full accounting compliance.
- Real payment gateway integration.
- Real-time collaborative editing.
- Complex BI/data warehouse infrastructure.
- Native mobile application.

These can be added later without changing the core domain boundaries.

## 14. Acceptance criteria

The implementation is considered complete for the first release when:

- All six business modules have navigable pages.
- Dashboard renders meaningful KPI and chart data.
- Role-based navigation is represented in the UI.
- Protected backend endpoints enforce authorization.
- CRUD flows exist for core management resources.
- Loading, empty, error and validation states are implemented.
- Light/Dark mode works across the application.
- Desktop, tablet and mobile layouts are usable.
- Frontend and backend have automated tests for critical behavior.
- No TODO/placeholder implementation remains in completed features.
- API contracts and local development setup are documented.
