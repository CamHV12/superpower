# Enterprise Management Dashboard — Implementation Plan

**Date:** 2026-10-03  
**Status:** Ready for implementation  
**Design:** `docs/superpowers/specs/2026-10-03-enterprise-dashboard-design.md`  
**Branch:** `feat/enterprise-dashboard-design`

## 1. Mục tiêu thực thi

Triển khai hệ thống Dashboard quản lý doanh nghiệp theo thiết kế đã được duyệt, sử dụng:

- Frontend: React + Vite + TypeScript + Tailwind CSS + shadcn/ui + Lucide React + Recharts.
- Backend: Spring Boot + Java 17+ + Spring Security + JWT + Spring Data JPA.
- Database: PostgreSQL.
- Testing: Vitest/React Testing Library ở frontend; JUnit 5 + Spring Boot Test ở backend.
- API base path: `/api/v1`.

Nguyên tắc thực thi: RED → GREEN → REFACTOR; mỗi behavior quan trọng phải có test thất bại trước khi viết production code.

## 2. Quy ước chung

### 2.1 Naming

- Java: package/class/method/field bằng English, theo Java convention.
- TypeScript/React: component/type/function/file name bằng English.
- API resource dùng plural nouns: `/employees`, `/projects`, `/customers`.
- Database table dùng snake_case.
- UI text và mock data dùng tiếng Việt.
- Không trả JPA Entity trực tiếp từ Controller; dùng Request/Response DTO.

### 2.2 API response

Thống nhất response envelope:

```json
{
  "success": true,
  "message": "Success",
  "data": {},
  "timestamp": "2026-10-03T10:00:00Z"
}
```

Error:

```json
{
  "success": false,
  "message": "Validation failed",
  "data": null,
  "timestamp": "2026-10-03T10:00:00Z",
  "errors": []
}
```

Danh sách dùng pagination chuẩn với `page`, `size`, `sort`.

### 2.3 Security

- Password hash bằng BCrypt.
- JWT access token.
- Stateless SecurityContext.
- Backend là nguồn kiểm soát authorization.
- Frontend route guard chỉ phục vụ UX.
- Secret/config nhạy cảm lấy từ environment variables.
- CORS chỉ cho phép frontend origin đã cấu hình.

## 3. Phase 0 — Baseline và tooling

### Task 0.1 — Xác nhận repository baseline

Files/paths cần kiểm tra hoặc tạo:

- `README.md`
- `.gitignore`
- `.editorconfig`
- `.env.example`
- `frontend/`
- `backend/`

Acceptance:

- Có cấu trúc frontend/backend tách biệt.
- Có tài liệu local development.
- Không commit secret thật.

### Task 0.2 — Frontend toolchain

Tạo:

- `frontend/package.json`
- `frontend/tsconfig.json`
- `frontend/vite.config.ts`
- `frontend/index.html`
- `frontend/src/main.tsx`
- `frontend/src/App.tsx`
- `frontend/src/index.css`

Dependencies chính:

- react
- react-dom
- react-router-dom
- tailwindcss
- lucide-react
- recharts
- zustand
- axios
- zod
- react-hook-form

Testing:

- vitest
- jsdom
- @testing-library/react
- @testing-library/jest-dom
- @testing-library/user-event

### Task 0.3 — Backend toolchain

Tạo:

- `backend/pom.xml`
- `backend/src/main/java/com/company/enterprise/EnterpriseApplication.java`
- `backend/src/main/resources/application.yml`
- `backend/src/test/resources/application-test.yml`

Dependencies chính:

- spring-boot-starter-web
- spring-boot-starter-validation
- spring-boot-starter-security
- spring-boot-starter-data-jpa
- PostgreSQL driver
- JWT library
- springdoc-openapi
- spring-boot-starter-test

## 4. Phase 1 — Frontend design system và application shell

### Task 1.1 — TDD cho theme store

Test trước:

- default theme là light.
- toggle chuyển light ↔ dark.
- reload giữ preference.
- hệ thống không tạo trạng thái theme không hợp lệ.

Files:

- `frontend/src/stores/ui.store.ts`
- `frontend/src/stores/ui.store.test.ts`

### Task 1.2 — Design tokens và global styles

Files:

- `frontend/src/index.css`
- `frontend/tailwind.config.*` nếu phiên bản Tailwind yêu cầu.
- `frontend/components.json` nếu dùng shadcn/ui CLI.

Thiết lập:

- blue primary.
- orange accent.
- neutral background/border.
- light/dark variables.
- focus-visible.
- reduced-motion friendly behavior.

### Task 1.3 — Generic UI primitives

Tạo các component nhỏ:

- `components/ui/button.tsx`
- `components/ui/card.tsx`
- `components/ui/input.tsx`
- `components/ui/badge.tsx`
- `components/ui/table.tsx`
- `components/ui/dialog.tsx`
- `components/ui/dropdown-menu.tsx`
- `components/ui/select.tsx`
- `components/ui/skeleton.tsx`
- `components/ui/alert.tsx`
- `components/ui/empty-state.tsx`

Không đặt business logic trong `components/ui`.

### Task 1.4 — Layout và navigation

Files:

- `frontend/src/layouts/DashboardLayout.tsx`
- `frontend/src/layouts/AuthLayout.tsx`
- `frontend/src/components/layout/Sidebar.tsx`
- `frontend/src/components/layout/Header.tsx`
- `frontend/src/components/layout/MobileSidebar.tsx`
- `frontend/src/components/layout/UserMenu.tsx`
- `frontend/src/components/layout/ThemeToggle.tsx`

Behavior:

- desktop sidebar cố định.
- mobile sidebar thành drawer.
- active route rõ ràng.
- header có search-ready area, notification entry, theme switcher và user menu.

### Task 1.5 — Routing

Files:

- `frontend/src/routes/AppRoutes.tsx`
- `frontend/src/routes/ProtectedRoute.tsx`
- `frontend/src/routes/RoleRoute.tsx`

Routes ban đầu:

- `/login`
- `/`
- `/employees`
- `/projects`
- `/customers`
- `/finance`
- `/reports`

TDD:

- anonymous user bị chuyển về login.
- authenticated user vào dashboard.
- role không có quyền không truy cập được protected route.

## 5. Phase 2 — Dashboard với mock data

### Task 2.1 — Dashboard domain types

Files:

- `frontend/src/features/dashboard/types.ts`
- `frontend/src/features/dashboard/dashboard.types.test.ts`

Models:

- DashboardKpi
- RevenueExpensePoint
- ProfitPoint
- ProjectStatusDistribution
- EmployeeDistribution
- CustomerGrowthPoint
- RecentActivity
- DashboardNotification

### Task 2.2 — Mock service

Files:

- `frontend/src/mock/dashboard.mock.ts`
- `frontend/src/services/dashboard.service.ts`

Service interface phải độc lập với component để sau này thay bằng API thật.

### Task 2.3 — KPI cards

Files:

- `frontend/src/features/dashboard/components/KpiCard.tsx`
- `frontend/src/features/dashboard/components/KpiGrid.tsx`

KPI:

- Doanh thu.
- Chi phí.
- Lợi nhuận.
- Nhân sự.

### Task 2.4 — Charts

Files:

- `frontend/src/features/dashboard/components/RevenueExpenseChart.tsx`
- `frontend/src/features/dashboard/components/ProfitChart.tsx`
- `frontend/src/features/dashboard/components/ProjectStatusChart.tsx`
- `frontend/src/features/dashboard/components/EmployeeDistributionChart.tsx`
- `frontend/src/features/dashboard/components/CustomerGrowthChart.tsx`

Dùng Recharts, responsive container và accessible labels/tooltips.

### Task 2.5 — Activity/notification widgets

Files:

- `frontend/src/features/dashboard/components/RecentActivity.tsx`
- `frontend/src/features/dashboard/components/NotificationPanel.tsx`

### Task 2.6 — Dashboard page states

Files:

- `frontend/src/features/dashboard/DashboardPage.tsx`
- `frontend/src/features/dashboard/DashboardPage.test.tsx`

Phải có:

- loading skeleton.
- empty.
- error + retry.
- normal state.

## 6. Phase 3 — Authentication và backend security foundation

### Task 3.1 — Backend security tests trước

Tạo test cases cho:

- login thành công.
- password sai.
- token thiếu.
- token hết hạn/không hợp lệ.
- role đúng được truy cập.
- role sai bị từ chối.

### Task 3.2 — Auth domain

Package:

`backend/src/main/java/com/company/enterprise/auth/`

Files chính:

- `AuthController.java`
- `AuthService.java`
- `AuthRepository.java` nếu cần custom query.
- `User.java`
- `Role.java`
- `Permission.java`
- `UserRepository.java`
- `AuthRequest.java`
- `AuthResponse.java`
- `CurrentUserResponse.java`

### Task 3.3 — Security package

Package:

`backend/src/main/java/com/company/enterprise/security/`

Files:

- `SecurityConfig.java`
- `JwtAuthenticationFilter.java`
- `JwtService.java`
- `CustomUserDetailsService.java`
- `AuthenticatedUser.java`
- `SecurityExceptionHandler.java`

### Task 3.4 — Frontend auth

Files:

- `frontend/src/features/auth/LoginPage.tsx`
- `frontend/src/features/auth/LoginPage.test.tsx`
- `frontend/src/stores/auth.store.ts`
- `frontend/src/services/auth.service.ts`
- `frontend/src/types/auth.ts`

Behavior:

- login form validation.
- submitting state.
- invalid credential error.
- successful redirect.
- logout.
- persisted auth state without storing secrets insecurely beyond agreed MVP mechanism.

## 7. Phase 4 — Database foundation

### Task 4.1 — Migration strategy

Use Flyway.

Files:

- `backend/src/main/resources/db/migration/V1__create_security_tables.sql`
- `V2__create_employee_tables.sql`
- `V3__create_customer_project_tables.sql`
- `V4__create_finance_tables.sql`
- `V5__create_report_notification_audit_tables.sql`
- `V6__seed_roles_and_permissions.sql`

Database constraints:

- primary keys.
- foreign keys.
- unique constraints.
- useful indexes.
- status columns with validated values.
- created_at/updated_at timestamps.

## 8. Phase 5 — Employee module

### Backend

Package:

`backend/src/main/java/com/company/enterprise/employee/`

Implement:

- Department entity/repository/service/controller.
- Position entity/repository/service/controller.
- Employee entity/repository/service/controller.
- Attendance entity/repository/service/controller.
- Request/response DTOs.
- Search/filter/pagination.
- validation.
- authorization.

Tests first:

- employee creation.
- employee update.
- duplicate/invalid data.
- pagination/filter.
- permission restrictions.

### Frontend

Files:

- `features/employees/EmployeesPage.tsx`
- `features/employees/EmployeeDetailPage.tsx`
- `features/employees/components/EmployeeTable.tsx`
- `features/employees/components/EmployeeForm.tsx`
- `features/employees/components/EmployeeFilters.tsx`
- `features/employees/components/AttendanceSummary.tsx`
- `features/employees/employees.service.ts`
- `features/employees/employees.types.ts`

States:

- loading.
- empty.
- no search results.
- error/retry.
- create/edit validation.
- delete confirmation.
- permission denied.

## 9. Phase 6 — Project and task module

### Backend

Package:

`backend/src/main/java/com/company/enterprise/project/`

Implement:

- Project.
- ProjectMember.
- Task.
- task status/priority.
- project progress.
- assignment.
- deadline filtering.

Tests:

- create/update project.
- member assignment.
- task assignment.
- progress calculation.
- access control.

### Frontend

Files:

- `features/projects/ProjectsPage.tsx`
- `features/projects/ProjectDetailPage.tsx`
- `features/projects/components/ProjectTable.tsx`
- `features/projects/components/ProjectForm.tsx`
- `features/projects/components/ProjectProgress.tsx`
- `features/projects/components/TaskBoard.tsx`
- `features/projects/components/TaskForm.tsx`

## 10. Phase 7 — Customer module

### Backend

Package:

`backend/src/main/java/com/company/enterprise/customer/`

Implement:

- Customer.
- CustomerContact.
- customer project relationship.
- activity/history.

Tests:

- CRUD.
- search/filter.
- contact validation.
- customer/project access.

### Frontend

Files:

- `features/customers/CustomersPage.tsx`
- `features/customers/CustomerDetailPage.tsx`
- `features/customers/components/CustomerTable.tsx`
- `features/customers/components/CustomerForm.tsx`
- `features/customers/components/CustomerContacts.tsx`
- `features/customers/components/CustomerProjects.tsx`

## 11. Phase 8 — Finance module

### Backend

Package:

`backend/src/main/java/com/company/enterprise/finance/`

Implement:

- Invoice.
- Transaction.
- Expense.
- finance overview queries.
- revenue/expense summaries.
- date/status filtering.

Tests:

- invoice lifecycle.
- transaction creation.
- expense validation.
- summary calculations.
- accountant-only authorization.

### Frontend

Files:

- `features/finance/FinancePage.tsx`
- `features/finance/InvoicesPage.tsx`
- `features/finance/TransactionsPage.tsx`
- `features/finance/ExpensesPage.tsx`
- finance components/forms/tables/charts.

Currency:

- VND formatting.
- consistent date formatting.
- explicit status badges.

## 12. Phase 9 — Reports

### Backend

Package:

`backend/src/main/java/com/company/enterprise/report/`

Implement report queries for:

- revenue.
- expense.
- employees.
- projects.
- finance.

Tests:

- date-range filtering.
- aggregation correctness.
- authorization.

### Frontend

Files:

- `features/reports/ReportsPage.tsx`
- `features/reports/components/ReportFilters.tsx`
- `features/reports/components/RevenueReport.tsx`
- `features/reports/components/ExpenseReport.tsx`
- `features/reports/components/EmployeeReport.tsx`
- `features/reports/components/ProjectReport.tsx`
- `features/reports/components/FinancialReport.tsx`

Export-ready structure should not couple the UI to a particular export library.

## 13. Phase 10 — Notifications và audit

### Backend

Packages:

- `common/audit/`
- notification domain.

Implement:

- Notification entity.
- unread/read state.
- AuditLog entity.
- audit events for important mutations.
- actor, action, resource, resource_id, timestamp.

Tests:

- notification read state.
- audit event persistence.
- protected access.

### Frontend

Implement:

- header notification indicator.
- notification panel.
- read/unread behavior.
- audit-ready UI where required.

## 14. Phase 11 — API integration

### Frontend infrastructure

Files:

- `src/services/api.ts`
- `src/services/api-error.ts`
- `src/hooks/useApiQuery.ts` if needed.
- domain service modules.

Responsibilities:

- base URL.
- auth header.
- response normalization.
- 401 handling.
- common error mapping.
- cancellation where useful.

Replace mock services one domain at a time:

1. auth
2. dashboard
3. employees
4. projects
5. customers
6. finance
7. reports
8. notifications

Mocks remain available only for isolated UI tests/demo mode if needed.

## 15. Phase 12 — Cross-cutting UX hardening

Review every page for:

- responsive breakpoints.
- keyboard navigation.
- focus states.
- semantic labels.
- disabled/submitting states.
- optimistic behavior only where safe.
- loading skeleton.
- empty state.
- API error + retry.
- no-results state.
- destructive confirmation.
- permission denied.
- long text overflow.
- table overflow on mobile.
- chart responsiveness.
- light/dark contrast.

Use subtle animation only where it improves feedback.

## 16. Phase 13 — Testing and quality gates

### Frontend

Run:

- unit tests.
- component tests.
- route/role tests.
- build/typecheck.
- lint.

Critical test areas:

- auth.
- protected routes.
- role visibility.
- dashboard state transitions.
- CRUD forms.
- filters/pagination.
- theme persistence.

### Backend

Run:

- unit tests.
- controller/API tests.
- security tests.
- repository tests for custom queries.
- application context test.
- build.

Critical test areas:

- JWT.
- RBAC.
- validation.
- CRUD services.
- aggregation queries.
- error mapping.

No feature is considered complete if its critical behavior has no automated test.

## 17. Phase 14 — Code review checklist

Review against:

- approved design spec.
- clean architecture boundaries.
- no duplicated business logic.
- no Entity exposure.
- no secret in repository.
- no TODO/placeholder in completed features.
- consistent error format.
- consistent pagination.
- authorization on backend.
- frontend UX guards.
- mobile usability.
- accessible interactive controls.
- test coverage for critical behavior.
- no dead code.
- no unused dependency.

## 18. Phase 15 — Documentation and local setup

Update:

- root `README.md`.
- `frontend/README.md` if useful.
- `backend/README.md` if useful.
- `.env.example`.

Document:

1. Prerequisites.
2. PostgreSQL setup.
3. Environment variables.
4. Migration.
5. Backend run command.
6. Frontend run command.
7. Test commands.
8. Default development roles/users only if seeded safely.
9. API documentation URL.
10. Troubleshooting common startup issues.

## 19. Suggested commit sequence

Use small, reviewable commits:

1. `chore: establish frontend and backend baseline`
2. `test: add theme and routing behavior tests`
3. `feat: build dashboard application shell`
4. `feat: add dashboard mock experience`
5. `test: add authentication and authorization tests`
6. `feat: implement jwt security foundation`
7. `feat: implement employee management`
8. `feat: implement project and task management`
9. `feat: implement customer management`
10. `feat: implement finance management`
11. `feat: implement reports`
12. `feat: implement notifications and audit`
13. `feat: integrate frontend with backend api`
14. `test: complete cross-module regression coverage`
15. `docs: document local development and api contracts`

## 20. Definition of Done

Một phase chỉ hoàn thành khi:

- Test liên quan đã được viết trước production code.
- Test chuyển RED → GREEN.
- Code đã refactor.
- Build/typecheck/lint tương ứng pass.
- UI có loading/empty/error/validation khi phù hợp.
- Backend có authorization đúng role.
- API không trả Entity trực tiếp.
- Không có secret hard-code.
- Không có TODO/placeholder cho behavior đã cam kết.
- Thay đổi được commit độc lập, dễ review.

Toàn bộ release đạt Done khi đáp ứng acceptance criteria trong design specification và frontend/backend có thể chạy local từ tài liệu README mà không cần thao tác không được ghi nhận.
