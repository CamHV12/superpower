# 🍎 Fruit Shop Microservices - QUICKSTART GUIDE

## 📌 Tệp Prompt Đã Được Tạo

Tôi đã tạo **2 file prompt chính** cho dự án của bạn:

### **File 1: `fruit-shop-microservices.prompt.md`**
- 📋 Tổng quan dự án
- 🏗️ Kiến trúc microservices chi tiết
- ❓ Các câu hỏi brainstorming
- 💡 3 cách tiếp cận (Monolith vs Microservices vs Modular)
- 📐 Design proposal (system diagram, data flow)
- ✅ Success criteria

**Để dùng:** Khi bạn yêu cầu agent tạo project mới, agent sẽ đọc file này

### **File 2: `fruit-shop-microservices-IMPLEMENTATION-GUIDE.md`**
- 🚀 Quy trình thực hiện step-by-step
- 🔑 Cách áp dụng từng Superpowers skill
- 📊 Timeline & deliverables
- 🛠️ Commands để nhớ
- 📝 Testing strategy
- 🚨 Red flags - khi nào nên ask for help

**Để dùng:** Hướng dẫn agent thực hiện dự án

---

## 🎯 CÁCH SỬ DỤNG NGAY

### **Bước 1: Yêu Cầu Agent Tạo Project**

```
Bạn: "Tạo một project web Quản lý bán trái cây theo mô hình microservices. 
Sử dụng Java + React + PostgreSQL."
```

### **Bước 2: Agent Sẽ Tự Động**

1. ✅ Đọc `fruit-shop-microservices.prompt.md`
2. ✅ Hỏi bạn 5 câu hỏi rõ ràng (một lần một câu)
   - Domain business
   - Người dùng
   - Quy mô
   - Tích hợp bên ngoài
   - MVP vs Full
3. ✅ Đề xuất 2-3 cách tiếp cận
4. ✅ Trình bày design (diagram, tech stack, flow)
5. ✅ Lưu design vào `docs/superpowers/specs/`
6. ✅ Viết implementation plan (dựa vào `fruit-shop-microservices-IMPLEMENTATION-GUIDE.md`)
7. ✅ Bắt đầu implementation task-by-task

### **Bước 3: Bạn Chỉ Cần**
- ✅ Trả lời câu hỏi rõ ràng
- ✅ Review & approve design
- ✅ Approve implementation plan
- ✅ Xem agent thực hiện từng task
- ✅ Provide feedback khi cần

---

## 📁 FILE STRUCTURE SAU KHOÁ HỌC

```
e:\CamHV\AI Agent\superpowers\
├── fruit-shop-microservices.prompt.md ← MAIN PROMPT
├── fruit-shop-microservices-IMPLEMENTATION-GUIDE.md ← DETAILED GUIDE
├── docs/
│   ├── superpowers/
│   │   ├── specs/
│   │   │   └── 2026-04-12-fruit-shop-design.md (sẽ được tạo)
│   │   └── plans/
│   │       └── 2026-04-12-fruit-shop-implementation.md (sẽ được tạo)
│   └── ...
├── .worktrees/
│   └── fruit-shop/ (isolated workspace)
└── ...
```

---

## 🔥 QUICK COMMAND - BẮT ĐẦU NGAY

Nếu bạn muốn bắt đầu ngay lúc này, copy/paste command sau:

```
Tạo một project web Quản lý bán trái cây theo mô hình microservices. 

Tech stack:
- Backend: Java (Spring Boot, Spring Cloud)
- Frontend: React
- Database: PostgreSQL
- Message Queue: RabbitMQ

Features:
- Quản lý sản phẩm (trái cây)
- Quản lý đơn hàng
- Quản lý khách hàng
- Xử lý thanh toán
- Gửi email xác nhận

Kiến trúc: Full Microservices (mỗi feature là một service riêng)

Hãy bắt đầu với brainstorming và hỏi tôi những câu hỏi cần thiết trước.
```

---

## 🎓 PHƯƠNG PHÁP ĐƯỢC SỬ DỤNG

Prompt này dựa trên **Superpowers Workflow** - một quy trình phát triển phần mềm tiêu chuẩn:

```
Brainstorming (Tìm hiểu)
    ↓
Writing Plans (Lập kế hoạch)
    ↓
Subagent-Driven Development (Thực hiện)
    ↓
Verification & Finishing (Hoàn thiện)
```

Mỗi giai đoạn có các nguyên tắc sắt:
- ✅ **TDD:** Test first, code after
- ✅ **Systematic Debugging:** Find root cause, not symptoms
- ✅ **Verification:** Evidence before claims
- ✅ **Code Review:** Technical over emotional
- ✅ **Git Workflow:** Clean, organized commits

---

## 🚨 ĐIỀU CẦN LƯU Ý

### **Prompt Này Là Gì:**
✅ Hướng dẫn chi tiết để agent tạo project microservices
✅ Ghi lại architecture, tech stack, quy trình
✅ Giúp agent tự động hỏi đúng câu hỏi

### **Prompt Này KHÔNG Phải:**
❌ Code sẵn (bạn phải code)
❌ Final project (bạn phải thực hiện từng task)
❌ Deploy guide (agent sẽ viết trong implementation plan)

### **Bạn Cần Làm Gì:**
1. ✅ Để agent đọc prompt này
2. ✅ Trả lời các câu hỏi của agent
3. ✅ Review design & plan
4. ✅ Cho phép agent thực hiện từng task
5. ✅ Review code & provide feedback khi cần

---

## 📊 EXPECTED DELIVERABLES

### **Design Phase (2-3 giờ)**
- ✅ Requirement rõ ràng
- ✅ Architecture diagram
- ✅ Tech stack confirmed
- ✅ Design document (`docs/superpowers/specs/`)

### **Planning Phase (1-2 giờ)**
- ✅ Detailed implementation plan
- ✅ Task list (bite-sized)
- ✅ Testing strategy
- ✅ Plan document (`docs/superpowers/plans/`)

### **Implementation Phase (5-7 ngày)**
- ✅ 7 microservices (fully tested)
- ✅ React frontend (fully tested)
- ✅ Docker setup (Docker Compose)
- ✅ Full documentation
- ✅ 80%+ test coverage

### **Deployment Phase (1 ngày)**
- ✅ All tests passing
- ✅ Docker images ready
- ✅ Documentation complete
- ✅ PR/Merge ready

---

## 💡 BEST PRACTICES

📌 **Trước Khi Bắt Đầu:**
- [ ] Đảm bảo bạn có **Java JDK 17+**, **Node.js 18+**, **Docker**, **PostgreSQL** cài đặt
- [ ] Có **IDE** tốt (VSCode + Extension, IntelliJ IDEA)
- [ ] Có **Git** cài đặt
- [ ] Hiểu cơ bản về **Microservices** (không cần chuyên sâu)

📌 **Trong Quá Trình Thực Hiện:**
- [ ] Không bỏ qua testing (TDD từ đầu)
- [ ] Commit thường xuyên (sau mỗi small task)
- [ ] Đọc error messages kỹ (debugging systematically)
- [ ] Xác minh trước khi claim success
- [ ] Hỏi nếu không hiểu rõ ràng

📌 **Sau Hoàn Thành:**
- [ ] Run all tests 1 lần nữa
- [ ] Check documentation completeness
- [ ] Prepare for code review
- [ ] Set up CI/CD (optional, advanced)

---

## 🆘 CẦU CỨU - KHI NÀO ASK FOR HELP?

**Agent sẽ tự động stop & ask when:**
- ❌ Test fails (không skip, systematic debugging)
- ❌ Service can't communicate
- ❌ Database issues
- ❌ Unclear requirements
- ❌ Blocker not in plan

**Bạn nên stop & ask when:**
- ❌ Design seems too complex
- ❌ Timeline seems unrealistic
- ❌ Don't understand a requirement
- ❌ Worried about something
- ❌ Need to change scope

**Không bao giờ:**
- ❌ Skip tests vì time pressure
- ❌ Deploy code chưa xác minh
- ❌ Ignore error messages
- ❌ Guess at architecture
- ❌ Commit without testing

---

## 🎯 SUCCESS CRITERIA (BẠNBIẾT KHOÁ HỌC THÀNH CÔNG KHI)

- ✅ Toàn bộ 7 microservices chạy được độc lập
- ✅ API Gateway routing request đúng
- ✅ Services giao tiếp async qua RabbitMQ
- ✅ Frontend render đúng & connect API
- ✅ All tests passing (unit + integration + e2e)
- ✅ Code coverage >= 80%
- ✅ Docker Compose setup hoạt động locally
- ✅ Documentation complete & clear
- ✅ Code review passed
- ✅ Ready for production deployment

---

## 📚 TÀI LIỆU THAM KHẢO

Các file này sử dụng từ **Superpowers Framework:**

```
├── Brainstorming Skill: clarify requirements & design
├── Writing Plans Skill: break into implementation tasks  
├── Subagent-Driven Development: execute tasks systematically
├── TDD Skill: write tests first
├── Systematic Debugging: find root causes
├── Verification: confirm success with evidence
└── Code Review Reception: handle feedback properly
```

Tất cả skills này tự động trigger dựa trên context - **bạn không cần gọi nó**. Agent biết khi nào cần dùng cái nào.

---

## 🚀 NEXT STEP

👉 **Hãy gọi agent của bạn ngay và copy/paste command sau:**

```
Tạo một project web Quản lý bán trái cây theo mô hình microservices. 

Tech stack:
- Backend: Java (Spring Boot)
- Frontend: React
- Database: PostgreSQL
- Message Queue: RabbitMQ

Hãy bắt đầu từ bước brainstorming.
```

---

**🎉 READY? Let's build! 🚀**

