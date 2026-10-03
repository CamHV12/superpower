---
name: "Fruit Shop Microservices - Quy Trình Thực Hiện"
description: "Hướng dẫn áp dụng Superpowers skills để xây dựng dự án Quản lý Bán Trái Cây"
type: "implementation-guide"
version: "1.0"
---

# 🚀 QUY TRÌNH THỰC HIỆN DỰ ÁN

## 📍 Điểm Bắt Đầu: Brainstorming Skill

Khi bạn yêu cầu: **"Tạo project web Quản lý bán trái cây theo mô hình microservices"**

### **Agent sẽ làm:**

```
1. Agent đọc fruit-shop-microservices.prompt.md
   ↓
2. Agent lấy danh sách câu hỏi từ Phase 2 & hỏi từng câu
   - Domain business: Thêm feature gì?
   - Người dùng: Admin / Khách hàng / Nhân viên?
   - Quy mô: Bao nhiêu người dùng?
   - Tích hợp: Payment, Email, SMS?
   - MVP vs Full?
   ↓
3. Sau khi lấy đủ thông tin, agent:
   - Ghi lại requirement
   - Đề xuất 2-3 approach (Monolith vs Microservices vs Modular)
   - Recommend Full Microservices
   - Trình bày design (diagram, flow, tech stack)
   ↓
4. Agent yêu cầu bạn approve design
   ↓
5. Agent lưu design vào: docs/superpowers/specs/2026-04-12-fruit-shop-design.md
   ↓
6. ✅ Brainstorming hoàn thành → Next: Writing Plans
```

---

## 📍 Bước 2: Writing Plans Skill

**Khi design được approve:**

### **Agent sẽ:**

```
1. Đọc design document
   ↓
2. Phân chia microservices thành các tasks độc lập:
   
   TASK 1: Setup Infrastructure
   - Docker Compose for PostgreSQL, RabbitMQ
   - Network setup
   - Environment variables
   
   TASK 2: API Gateway Service
   - Spring Cloud Gateway setup
   - Routing configuration
   - Authentication middleware
   
   TASK 3: Product Service
   - JPA entities & repositories
   - REST API endpoints (CRUD)
   - Inventory management
   - Unit tests
   - Integration tests
   
   TASK 4: Customer Service
   - Customer entity & auth
   - REST API endpoints
   - Loyalty points system
   - Unit & integration tests
   
   TASK 5: Order Service
   - Order & OrderItem entities
   - REST API endpoints (create, update, cancel)
   - Call Product Service (Feign client)
   - Event publishing (OrderCreated)
   - Unit & integration tests
   
   TASK 6: Payment Service
   - Payment entity
   - REST API endpoints
   - Stripe/Payment gateway integration
   - Event publishing (PaymentCompleted)
   - Unit & integration tests
   
   TASK 7: Notification Service
   - Email service integration
   - Event listener (subscribe to OrderCreated)
   - Email template
   - Unit tests
   
   TASK 8: React Frontend - Product Management
   - Components: ProductList, ProductDetail
   - API service for Product
   - Redux store setup
   - Unit tests
   
   TASK 9: React Frontend - Order Management
   - Components: OrderForm, OrderStatus
   - API service for Order
   - State management
   - Unit tests
   
   TASK 10: React Frontend - Customer Profile
   - Components: Profile, OrderHistory
   - API service for Customer
   - Authentication flow
   - Integration tests
   
   TASK 11: End-to-End Testing
   - Full workflow testing
   - Performance testing
   - Load testing
   
   TASK 12: Deployment & Documentation
   - Docker image building
   - Kubernetes manifests (optional)
   - API documentation (Swagger)
   - Deployment guide
   - README

   ↓
3. Mỗi TASK được viết chi tiết với:
   - [ ] RED: Write failing test
   - [ ] GREEN: Implement code
   - [ ] REFACTOR: Clean up
   - [ ] VERIFY: Test & commit
   ↓
4. Agent lưu vào: docs/superpowers/plans/2026-04-12-fruit-shop-implementation.md
   ↓
5. ✅ Writing Plans hoàn thành → Next: Subagent-Driven Development
```

---

## 📍 Bước 3: Subagent-Driven Development Skill

**Khi implementation plan được approve:**

### **Agent sẽ:**

```
1. Tạo worktree mới (using-git-worktrees skill)
   git worktree add .worktrees/fruit-shop develop
   ↓
2. Cho mỗi TASK trong plan:
   ┌─────────────────────────────────────────┐
   │ TASK X: [Tên Task]                      │
   └─────────────────────────────────────────┘
   
   a) Dispatch Implementer Subagent
      - Subagent đọc TASK detail
      - Ask clarifying questions (nếu cần)
      - Implement code step-by-step
      - Write tests (RED-GREEN-REFACTOR)
      - Commit
      ↓
   b) Dispatch Spec Reviewer Subagent
      - Review: Code có match spec không?
      - Check: Functional requirements?
      - Approve hoặc: Request changes
      ↓
   c) Dispatch Code Quality Reviewer Subagent
      - Review: Code quality, style
      - Check: Best practices?
      - Check: Error handling?
      - Approve hoặc: Request changes
      ↓
   d) Mark TASK as completed
   
   ↓ (Lặp lại cho task tiếp theo)
   
3. Khi tất cả TASKS xong:
   Dispatch Final Code Reviewer (entire implementation)
   ↓
4. ✅ All tasks verified → Next: Finishing a Development Branch
```

---

## 📍 Bước 4: Finishing a Development Branch Skill

**Khi toàn bộ code xong:**

### **Agent sẽ:**

```
1. Run ALL tests:
   - Backend unit tests (mvn test)
   - Backend integration tests (mvn verify)
   - Frontend tests (npm test)
   - End-to-end tests
   ↓
2. Check code coverage (>= 80% requirement)
   ↓
3. Run linters & formatters:
   - Backend: Checkstyle, SpotBugs
   - Frontend: ESLint, Prettier
   ↓
4. Verify documentation:
   - API docs (Swagger)
   - Architecture docs
   - Deployment guide
   ↓
5. Create Docker images:
   docker build -t fruit-shop-api-gateway:latest ./api-gateway
   docker build -t fruit-shop-product-service:latest ./product-service
   ... (tất cả services)
   ↓
6. Test Docker Compose:
   docker-compose up -d
   Test endpoints
   docker-compose down
   ↓
7. Prepare PR / Merge:
   - Show diff
   - Get human approval
   - Merge to develop/main
   ↓
8. ✅ Project ready for deployment!
```

---

## 🔑 KEY PRINCIPLES DURING EXECUTION

### **1️⃣ TDD - Test-Driven Development**
```
EVERY feature:
  RED: Write failing test
  GREEN: Implement code (minimal)
  REFACTOR: Clean up
  VERIFY: Test passes, commit
  
❌ NEVER: Write code before test
```

### **2️⃣ Systematic Debugging**
```
WHEN bug appears:
  1. Find ROOT CAUSE (không fix symptoms)
  2. Viết test để reproduce bug
  3. Fix code tối thiểu
  4. Verify test passes
```

### **3️⃣ Verification Before Completion**
```
NEVER claim success mà chưa:
  1. RUN the verification command
  2. READ full output
  3. CONFIRM result matches claim
  
❌ "Should work" = lying
✅ "[Run command] → Result shows success" = verification
```

### **4️⃣ Code Review Reception**
```
WHEN feedback received:
  1. Understand requirement
  2. Verify against reality
  3. Ask if unclear
  4. Implement one-by-one
  5. Test each change
```

### **5️⃣ Git Workflow**
```
- Create feature branch per task
- Commit frequently (after each step)
- Descriptive commit messages
- Review before merge
- Tests pass before merge
```

---

## 📊 EXPECTED TIMELINE & DELIVERABLES

### **Phase 1: Brainstorming (1-2 hours)**
- [ ] Finalize requirements & constraints
- [ ] Design document (docs/superpowers/specs/)
- [ ] Tech stack confirmed
- [ ] Architecture diagram agreed

### **Phase 2: Planning (1-2 hours)**
- [ ] Task list defined
- [ ] File structure designed
- [ ] Implementation plan (docs/superpowers/plans/)
- [ ] Testing strategy documented

### **Phase 3: Implementation (5-7 days)**
**Backend Services (3-4 days):**
- [ ] Infrastructure setup (Docker, databases)
- [ ] API Gateway
- [ ] Product Service (fully tested)
- [ ] Customer Service (fully tested)
- [ ] Order Service (fully tested, event-driven)
- [ ] Payment Service (fully tested)
- [ ] Notification Service (fully tested)

**Frontend (1-2 days):**
- [ ] React project setup
- [ ] Redux/Context setup
- [ ] Components for Products, Orders, Customers
- [ ] API integration
- [ ] Tests

**Testing & Deployment (1 day):**
- [ ] End-to-end testing
- [ ] Performance testing
- [ ] Docker image building
- [ ] Docker Compose setup
- [ ] Documentation

### **Phase 4: Finishing (2-4 hours)**
- [ ] All tests passing (80%+ coverage)
- [ ] Linters clean
- [ ] Documentation complete
- [ ] PR ready for review

**TOTAL: ~1 week of focused work**

---

## 🛠️ COMMANDS TO REMEMBER

### **Backend (Java/Spring):**
```bash
# Create services
mvn archetype:generate -DgroupId=com.fruitshop -DartifactId=product-service

# Build & test
mvn clean install
mvn test
mvn verify (integration tests)

# Docker
docker build -t fruit-shop-product-service:latest .
docker run -d -p 8081:8081 fruit-shop-product-service:latest

# Docker Compose
docker-compose up -d
docker-compose logs -f product-service
docker-compose down
```

### **Frontend (React):**
```bash
# Create React app
npx create-react-app fruit-shop-frontend

# Start dev server
npm start

# Run tests
npm test

# Build for production
npm run build

# Docker
docker build -t fruit-shop-frontend:latest .
docker run -d -p 3000:3000 fruit-shop-frontend:latest
```

### **Database:**
```bash
# Connect PostgreSQL
psql -U postgres -d fruit_shop_products_db

# Common queries
SELECT * FROM products;
SELECT * FROM orders;
SELECT * FROM customers;
```

### **Git:**
```bash
# Worktree
git worktree add .worktrees/fruit-shop develop
git worktree list
git worktree remove .worktrees/fruit-shop

# Commits
git add .
git commit -m "feat: Product service CRUD endpoints"
git push origin feature/product-service
git pull origin develop
```

---

## 📝 TESTING STRATEGY

### **Unit Tests (TDD - RED-GREEN-REFACTOR)**
```java
// Example: ProductService unit test
@Test
void testCreateProduct_Success() {
    // GIVEN
    CreateProductRequest request = new CreateProductRequest("Apple", 5.99M, 100);
    
    // WHEN
    ProductDTO result = productService.createProduct(request);
    
    // THEN
    verify(productRepository).save(any(Product.class));
    assertEquals("Apple", result.getName());
}

@Test
void testCreateProduct_InvalidPrice_ThrowsException() {
    // GIVEN
    CreateProductRequest request = new CreateProductRequest("Apple", -5.99M, 100);
    
    // WHEN & THEN
    assertThrows(InvalidPriceException.class, () -> productService.createProduct(request));
}
```

### **Integration Tests**
```java
// Test API endpoint
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerIntegrationTest {
    @Test
    void testGetAllProducts_ReturnsOK() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List> response = restTemplate.getForEntity("http://localhost:8081/api/products", List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
```

### **Frontend Tests (React)**
```javascript
// Example: ProductList component test
describe('ProductList Component', () => {
    it('renders product list', () => {
        const mockProducts = [{id: 1, name: 'Apple', price: 5.99}];
        render(<ProductList products={mockProducts} />);
        
        expect(screen.getByText('Apple')).toBeInTheDocument();
    });
});
```

---

## 🎯 ACCEPTANCE CRITERIA - PROJECT COMPLETE

- [ ] ✅ All 7 microservices running
- [ ] ✅ API Gateway routing correctly
- [ ] ✅ Service discovery working (Eureka)
- [ ] ✅ Event-driven async communication (RabbitMQ)
- [ ] ✅ All REST APIs documented (Swagger)
- [ ] ✅ Frontend fully functional
- [ ] ✅ All tests passing (unit + integration + e2e)
- [ ] ✅ Code coverage >= 80%
- [ ] ✅ Docker Compose local setup verified
- [ ] ✅ Documentation complete (Architecture, API, Deploy guide)
- [ ] ✅ Code review passed
- [ ] ✅ Ready for production deployment

---

## 🚨 RED FLAGS - STOP & ASK FOR HELP

**STOP working immediately when:**
- [ ] Test fails consistently → Don't skip, debug systematically
- [ ] Service can't communicate → Check messaging, networking
- [ ] Database query slow → Optimize or ask partner
- [ ] Design unclear → Ask for clarification
- [ ] Blocker not in plan → Stop, don't improvise
- [ ] Tired/stressed → Take break, don't force through

**ALWAYS ask your human partner before:**
- [ ] Changing architecture
- [ ] Using new libraries
- [ ] Skipping tests
- [ ] Committing untested code
- [ ] Deploying to production

---

## 📚 REFERENCED SKILLS (Link Flow)

```
SKILL FLOW FOR THIS PROJECT:
│
├─→ brainstorming: Clarify requirements & design
│   │
│   └─→ writing-plans: Break design into implementation tasks
│       │
│       └─→ subagent-driven-development: Execute tasks with 2-stage review
│           │
│           ├─→ test-driven-development: Write tests first
│           ├─→ systematic-debugging: Debug issues properly
│           ├─→ verification-before-completion: Verify everything works
│           └─→ receiving-code-review: Handle feedback
│
└─→ finishing-a-development-branch: Final verification & deployment
    │
    ├─→ using-git-worktrees: Manage branches safely
    └─→ verification-before-completion: Confirm ready to merge

```

---

## 💡 TIPS FOR SUCCESS

1. **Start Small:** MVP first (Product + Order + Customer), add Payment/Notification later
2. **Test Everything:** TDD from the beginning saves massive rework
3. **Commit Often:** Small, logical commits make debugging easier
4. **Document As You Go:** Don't leave docs to the end
5. **Use Postman/Insomnia:** Test APIs manually before frontend
6. **Check Logs:** Services should log important events
7. **Monitor:** Keep eye on Docker containers, database performance
8. **Review Code:** Before merge, ensure quality standards
9. **Ask Questions:** Ambiguity leads to wrong implementation
10. **Celebrate:** Microservices are complex, celebrate each milestone! 🎉

---

**🎯 READY TO START? Begin with Brainstorming Skill! Agent uses fruit-shop-microservices.prompt.md as reference.**

