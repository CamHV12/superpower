# 🍎 Fruit Shop Microservices - CÁC LỆNH RUN AGENT

## 📌 Các Lệnh Cụ Thể Để Chỉ Đạo Agent

---

## 🎯 **LỆNH LEVEL 1: KHỞI ĐẦU BRAINSTORMING**

### **Command 1.1: Bắt Đầu Dự Án (Basic)**

Dán lệnh này **NGAY LẦN ĐẦU** khi bạn muốn tạo dự án:

```
Tạo một project web Quản lý bán trái cây theo mô hình microservices. 

Tech stack:
- Backend: Java (Spring Boot, Spring Cloud)
- Frontend: React
- Database: PostgreSQL
- Message Queue: RabbitMQ
- Container: Docker

Hãy bắt đầu từ brainstorming skill. 
Hỏi tôi 5 câu hỏi cần thiết (một lần một câu) để làm rõ yêu cầu trước khi setting architecture.
```

**Khi nào dùng:** Lần đầu tiên
**Kết quả:** Agent sẽ hỏi 5 câu hỏi brainstorming

---

### **Command 1.2: Bắt Đầu - Chi Tiết (Full)**

Nếu bạn muốn chi tiết hơn, dùng command này:

```
Yêu cầu: Xây dựng project web Quản lý Bán Trái Cây - Microservices Architecture

Context:
- Đây là dự án portfolio để học microservices
- Team size: 1 người (tôi)
- Timeline: 1 tuần
- Scope: MVP + Basic features

Tech Stack (Fixed):
- Backend: Java 17+ (Spring Boot 3.x + Spring Cloud)
- Frontend: React 18.x + Redux
- Database: PostgreSQL (1 database per service)
- Message Queue: RabbitMQ
- API Gateway: Spring Cloud Gateway
- Container: Docker + Docker Compose

Core Features (MVP):
1. Quản lý sản phẩm (trái cây) - CRUD, inventory
2. Quản lý khách hàng - Đăng ký, profile
3. Quản lý đơn hàng - Tạo, update status, cancel
4. Xử lý thanh toán - Mock/Stripe
5. Gửi email - Order confirmation

Architecture Pattern:
- Full Microservices (7 services)
- Event-driven communication (RabbitMQ)
- Synchronous: REST + Feign client
- Service Discovery: Manual (hoặc Eureka)

Hãy dùng brainstorming skill. Mặc dù scope đã clear, hãy vẫn hỏi tôi:
1. Có thêm feature nào?
2. Có payment gateway thực tế?
3. Có SMS/Push notification?
4. Có admin dashboard?
5. Có scaling/monitoring requirement?

Sau đó trình bày design & lưu vào docs/superpowers/specs/
```

**Khi nào dùng:** Khi bạn đã có context rõ ràng
**Kết quả:** Agent sẽ vẫn brainstorm nhưng có context tốt hơn

---

## 🏗️ **LỆNH LEVEL 2: APPROVE DESIGN & NEXT STEPS**

### **Command 2.1: Design OK, Tiếp Tục Plan**

Khi agent trình bày design & hỏi bạn approve:

```
Design looking good! Tôi approve architecture này:
- 7 microservices
- RabbitMQ async communication
- PostgreSQL per service
- Spring Cloud Gateway
- Docker Compose

Hãy proceed ngay với writing-plans skill.
Chia design này thành implementation plan chi tiết:
- 12 tasks (bite-sized)
- Mỗi task 2-5 phút
- TDD approach (test first)
- Frequent commits

Lưu vào docs/superpowers/plans/
```

**Khi nào dùng:** Khi design được approve
**Kết quả:** Agent viết implementation plan

---

### **Command 2.2: Approve + Request Changes**

Nếu bạn muốn sửa gì đó:

```
Design gần OK, nhưng tôi muốn thay đổi:

1. Thêm Redis caching cho Product Service
2. Thay Eureka bằng service discovery manual đơn giản
3. Frontend: Dùng Tailwind thay Material-UI
4. Testing: Jest + React Testing Library (không Cypress)

Vui lòng update design document & trình bày lại những thay đổi này.
```

**Khi nào dùng:** Khi muốn sửa design
**Kết quả:** Agent update design

---

## 🚀 **LỆNH LEVEL 3: BẮT ĐẦU IMPLEMENTATION**

### **Command 3.1: Start Implementation**

Khi plan được approve, dùng lệnh này:

```
Implementation plan looks great! Ready to proceed.

Hãy dùng subagent-driven-development skill:
1. Create git worktree (.worktrees/fruit-shop)
2. Dispatch implementer subagent cho Task 1
3. Implementer implements, tests, commits
4. Dispatch spec reviewer subagent
5. Dispatch quality reviewer subagent
6. Mark task complete
7. Lặp lại cho task tiếp theo

Remember:
- TDD: Test first, code after
- Verify before claiming success
- Commit frequently
- If blocked: Stop & ask for help

Hãy bắt đầu với Task 1: Infrastructure Setup
```

**Khi nào dùng:** Khi plan được approve
**Kết quả:** Agent thực hiện tasks từ plan

---

### **Command 3.2: Resume Implementation (Sau Break)**

Nếu bạn dừng & muốn tiếp tục:

```
Tôi đã dừng implementation. Hãy resume từ nơi dừng.

Status:
- Completed: Task 1 (Infrastructure Setup), Task 2 (API Gateway)
- In Progress: Task 3 (Product Service)
- Remaining: Task 4-12

Hãy tiếp tục Task 3 từ bước [nêu bước nếu biết] hoặc từ đầu Task 3.
```

**Khi nào dùng:** Khi resume sau break
**Kết quả:** Agent tiếp tục task tiếp theo

---

## ✅ **LỆNH LEVEL 4: FOCUS TASKS**

### **Command 4.1: Skip to Specific Task**

Nếu bạn muốn focus task nào đó:

```
Bây giờ focus vào Task 5: Order Service (chỉ task này, bỏ qua task khác).

Requirements:
- Create Order & OrderItem entities
- REST API endpoints (create, update, cancel)
- Call Product Service (Feign client) để check inventory
- Publish OrderCreated event to RabbitMQ
- Unit tests (mock Product Service)
- Integration tests

Hãy implement từ RED (test) → GREEN → REFACTOR

Stop khi Task 5 được approved bởi spec & quality reviewers.
```

**Khi nào dùng:** Khi muốn focus 1 task
**Kết quả:** Agent implement chỉ 1 task

---

### **Command 4.2: Fix Issue in Task**

Nếu task có issue:

```
Task 3 có issue:
- Product Service POST /api/products không return correct DTO
- Inventory update không check min stock

Hãy dùng systematic-debugging skill:
1. Find root cause (không fix symptoms)
2. Write test to reproduce bug
3. Fix minimal code
4. Verify test passes
5. Commit

Sau đó resubmit Task 3 cho spec & quality review.
```

**Khi nào dùng:** Khi task fail test/review
**Kết quả:** Agent debug & fix

---

## 🔍 **LỆNH LEVEL 5: CODE REVIEW & FEEDBACK**

### **Command 5.1: Provide Code Review Feedback**

Khi agent submit code & bạn review:

```
Task 5 (Order Service) code review feedback:

✅ Good:
- Tests are comprehensive
- TDD approach followed
- Commits are logical

❌ Need to fix:
1. OrderService.java:45 - InvalidQuantityException should include quantity in message
2. OrderDTO mapping - Missing orderStatus field (should be OrderStatus enum)
3. OrderController.POST - Missing @Valid annotation for request body
4. Database migration - Missing index on orders.customer_id
5. Tests - Missing negative test for order with 0 quantity

Please implement these fixes one-by-one & re-submit for review.
```

**Khi nào dùng:** Khi bạn review code
**Kết quả:** Agent fix issues & resubmit

---

### **Command 5.2: Approve Task**

Khi code review hoàn tất:

```
Task 5 (Order Service) ✅ APPROVED!

Excellent work:
- All tests passing
- Code quality good
- API follows REST conventions
- Documentation clear

Hãy mark Task 5 as completed & move to Task 6 (Payment Service).
```

**Khi nào dùng:** Khi task passed review
**Kết quả:** Agent mark task complete & next task

---

## 🚨 **LỆNH LEVEL 6: TROUBLESHOOTING**

### **Command 6.1: Debug Test Failure**

Khi test fail:

```
Task 3 test failure:

Test: ProductServiceTest.testCreateProduct_InsufficientStock()
Error: Expected StockInsufficientException but got IllegalArgumentException

Hãy:
1. Read error message carefully
2. Reproduce bug consistently (run test 3 times)
3. Check recent changes (git diff)
4. Add logging/debugging
5. Find ROOT CAUSE
6. Fix code (not test)
7. Verify test passes

REMEMBER: No symptom fixes! Find root cause first.
```

**Khi nào dùng:** Khi test fail
**Kết quả:** Agent systematic debugging

---

### **Command 6.2: Build/Deployment Issue**

Khi Build/Docker fail:

```
docker-compose up failed:

Error: 
  product-service: Connection refused to postgres:5432
  order-service: Cannot resolve api-gateway hostname

Debug steps:
1. Check docker-compose.yml networks & links
2. Check environment variables in .env
3. Verify all services defined in compose file
4. Test connectivity: docker exec order-service ping api-gateway
5. Check logs: docker-compose logs product-service
6. Find root cause

Hãy fix & retest docker-compose.
```

**Khi nào dùng:** Khi infra/deployment fail
**Kết quả:** Agent debug infrastructure

---

### **Command 6.3: Ask Clarification**

Khi requirements unclear:

```
Question about Task 7 (Notification Service):

❓ Email template - should include order details?
❓ Should notification service retry if email fails?
❓ How many times retry?
❓ Should we log failed emails to database?
❓ Timeout before considered failed?

Hãy clarify trước khi implement Task 7.
```

**Khi nào dùng:** Khi không clear requirement
**Kết quả:** Agent ask/clarify

---

## 🎯 **LỆNH LEVEL 7: FINISHING & DEPLOYMENT**

### **Command 7.1: Final Verification**

Khi tất cả task xong:

```
All 12 tasks completed! Hãy perform final verification:

1. ✅ Run all tests (backend):
   mvn clean verify -DskipITs=false

2. ✅ Run all tests (frontend):
   npm test -- --coverage

3. ✅ Check coverage:
   - Backend: >= 80%
   - Frontend: >= 75%

4. ✅ Lint checks:
   - Backend: mvn checkstyle:check spotbugs:check
   - Frontend: npm run lint

5. ✅ Build Docker images:
   docker-compose build

6. ✅ Start & test:
   docker-compose up -d
   [test all endpoints]
   docker-compose down

7. ✅ Git cleanup:
   git log --oneline (check commits)

Report verification results trước khi proceed.
```

**Khi nào dùng:** Khi tất cả tasks finished
**Kết quả:** Final verification report

---

### **Command 7.2: Finish Development Branch**

Khi verification passed:

```
Verification passed! ✅

Hãy dùng finishing-a-development-branch skill:
1. Merge worktree branch to develop
2. Create deployment-ready build
3. Generate final documentation
4. Create release notes

Sau đó project sẽ sẵn sàng cho production deployment.
```

**Khi nào dùng:** Khi final verification pass
**Kết quả:** Project ready to deploy

---

## 📋 **LỆNH QUICK REFERENCE**

### **Workflow Timeline**

```
1️⃣ START
   └─ Command 1.1 / 1.2 (Brainstorming)
   
2️⃣ DESIGN
   └─ Command 2.1 / 2.2 (Approve design)
   
3️⃣ PLANNING
   └─ Command 3.1 (Writing-plans)
   
4️⃣ IMPLEMENTATION  
   ├─ Command 3.1 (Start implementation)
   ├─ Command 4.1 / 4.2 (Focus/Fix tasks)
   ├─ Command 5.1 / 5.2 (Code review)
   └─ Command 6.1 / 6.2 (Troubleshooting)
   
5️⃣ FINISHING
   ├─ Command 7.1 (Final verification)
   └─ Command 7.2 (Finish & deploy)
```

---

## 🎓 **CHEATSHEET - LỆNH THƯỜNG DÙNG**

### **Khi bạn muốn...**

| Muốn... | Dùng Command... | Kết Quả |
|---------|---|---|
| Bắt đầu dự án | 1.1 hoặc 1.2 | Agent brainstorm & hỏi 5 câu |
| Approve design | 2.1 | Agent viết implementation plan |
| Sửa design | 2.2 | Agent update design |
| Bắt đầu code | 3.1 | Agent implement tasks |
| Resume implementation | 3.2 | Agent tiếp tục từ task được dừng |
| Focus 1 task | 4.1 | Agent implement chỉ task đó |
| Fix task issue | 4.2 | Agent systematic debug & fix |
| Review code | 5.1 | Agent fix feedback |
| Approve code | 5.2 | Agent move to next task |
| Debug test fail | 6.1 | Agent find root cause & fix |
| Fix deployment | 6.2 | Agent debug infrastructure |
| Ask clarification | 6.3 | Agent clarify requirement |
| Final check | 7.1 | Agent verify all tests/docs |
| Deploy ready | 7.2 | Agent finish & ready to deploy |

---

## 💡 **TIPS KHI RUN COMMANDS**

### **✅ DO:**
```
✅ Copy-paste lệnh đầy đủ
✅ Rõ ràng & cụ thể
✅ Include context nếu cần
✅ Wait for agent to respond fully
✅ Review output trước yêu cầu next step
```

### **❌ DON'T:**
```
❌ Lệnh quá mơ hồ ("fix it")
❌ Multiple requests in 1 message
❌ Change requirement mid-task
❌ Skip verification steps
❌ Interrupt agent mid-work
```

---

## 📞 **COMMON SCENARIOS**

### **Scenario 1: Fresh Start**
```
→ Command 1.1 (brainstorm)
→ Command 2.1 (approve design)
→ Command 3.1 (start implementation)
→ Tasks 1-12 (use Command 4.1/5.1/5.2)
→ Command 7.1 (final verification)
→ Command 7.2 (finish)
```

### **Scenario 2: Mid-Project Issue**
```
→ Command 6.1 (debug issue)
→ Command 4.2 (fix task)
→ Command 5.1 (review feedback)
→ Command 5.2 (approve & continue)
```

### **Scenario 3: Change Requirements**
```
→ Command 2.2 (request design change)
→ [Agent updates design]
→ Command 2.1 (approve new design)
→ [Agent re-plans affected tasks]
→ Command 3.1 (resume from changed task)
```

### **Scenario 4: Deploy Ready**
```
→ Command 7.1 (final verification)
→ [Check all test results]
→ Command 7.2 (finish branch & deploy)
```

---

## 🎯 **EXAMPLE: FULL CONVERSATION**

### **You:**
```
Tạo một project web Quản lý bán trái cây theo mô hình microservices. 

Tech stack:
- Backend: Java (Spring Boot, Spring Cloud)
- Frontend: React
- Database: PostgreSQL
- Message Queue: RabbitMQ

Hãy bắt đầu từ brainstorming skill.
```

### **Agent:**
```
[Reads fruit-shop-microservices.prompt.md]

Tôi sẽ bắt đầu brainstorming. Câu hỏi #1:

❓ Domain Business - Có thêm feature nào không? (VD: Refund, Return, Loyalty, ...)
```

### **You:**
```
Feature thêm:
- Loyalty points (mỗi order +10 points, 100 points = voucher $10)
- Voucher/Discount codes
- Order returns (được return 7 ngày)
- Staff dashboard (view orders, update status)

Có set bác từ design proposal không thay đổi tech stack & microservices pattern.
```

### **Agent:**
```
Got it! Câu hỏi #2:

❓ Scale - Bao nhiêu người dùng đồng thời? Bao nhiêu orders/ngày?
```

### **You:**
```
MVP phase: ~100 concurrent users, ~50 orders/ngày
Không cần optimize lúc này, chỉ cần code structure good & testable.
```

### **Agent:**
```
[Continue with Q3, Q4, Q5...]
[Present design with loyalty, returns, staff dashboard added]

Design proposal:
- 7 services (+ Loyalty Service, Return Service riêng)
- OK để approve không?
```

### **You:**
```
Design looking good! Tôi approve. Hãy proceed với writing-plans.
```

### **Agent:**
```
[Writes implementation plan with 15 tasks (added loyalty, returns, staff tasks)]

Plan ready. Approve?
```

### **You:**
```
Perfect! Hãy start implementation ngay. Task 1 first.
```

### **Agent:**
```
[Dispatch implementer subagent cho Task 1: Infrastructure Setup]
[After implement] → [Dispatch spec reviewer] → [Dispatch quality reviewer]

Task 1 ✅ COMPLETED

[Move to Task 2...]
```

[...tasks 2-15...]

### **You:**
```
[After all tasks] Final verification time!

Command 7.1
```

### **Agent:**
```
[Run all tests, coverage, lint, docker build, etc.]

All tests passing! ✅ Coverage: 85%
Linter clean! ✅
Docker build successful! ✅
All endpoints tested! ✅

Ready for production deployment.
```

### **You:**
```
Command 7.2
```

### **Agent:**
```
[Merge develop, create release]

Project complete & ready! 🎉
```

---

## 📝 **NOTES**

- **Copy-paste is your friend:** Dùng lệnh này, không cần modify gì
- **Be specific:** Càng detail command, càng tốt kết quả
- **Read response fully:** Trước khi send next command
- **Don't rush:** Mỗi phase có purpose, không bỏ qua
- **Save commands:** Nếu muốn reuse later

---

**🚀 READY? Pick a command above & send to your agent!**
