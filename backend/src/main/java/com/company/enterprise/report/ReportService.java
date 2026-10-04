package com.company.enterprise.report;

import com.company.enterprise.customer.repository.CustomerRepository;
import com.company.enterprise.finance.expense.repository.ExpenseRepository;
import com.company.enterprise.finance.invoice.entity.InvoiceStatus;
import com.company.enterprise.finance.invoice.repository.InvoiceRepository;
import com.company.enterprise.finance.payment.repository.PaymentRepository;
import com.company.enterprise.project.entity.ProjectStatus;
import com.company.enterprise.project.repository.ProjectRepository;
import com.company.enterprise.task.entity.TaskStatus;
import com.company.enterprise.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final ExpenseRepository expenseRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final CustomerRepository customerRepository;

    public ReportService(InvoiceRepository invoiceRepository,
                         PaymentRepository paymentRepository,
                         ExpenseRepository expenseRepository,
                         ProjectRepository projectRepository,
                         TaskRepository taskRepository,
                         CustomerRepository customerRepository) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.expenseRepository = expenseRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public ReportResponse summary(LocalDate from, LocalDate to) {
        validateRange(from, to);

        BigDecimal invoiced = invoiceRepository.sumTotalAmountByIssueDateBetween(from, to);
        BigDecimal paid = paymentRepository.sumAmountByPaymentDateBetween(from, to);
        BigDecimal expense = expenseRepository.sumRecordedAmountByExpenseDateBetween(from, to);

        long invoiceCount = countInvoicesInRange(from, to);
        long overdueCount = countOverdueInvoices(from, to);
        long paidInvoiceCount = invoiceRepository.countByStatus(InvoiceStatus.PAID);

        long projectCount = projectRepository.count();
        long activeProjectCount = projectRepository.countByStatus(ProjectStatus.ACTIVE);
        long taskCount = taskRepository.count();
        long completedTaskCount = taskRepository.countByStatus(TaskStatus.DONE);
        long customerCount = customerRepository.count();
        long activeCustomerCount = customerRepository.countByActiveTrue();

        BigDecimal receivable = invoiced.subtract(paid).max(BigDecimal.ZERO);

        return new ReportResponse(
                from,
                to,
                new ReportSummaryResponse(
                        invoiced, paid, expense, receivable, paid.subtract(expense),
                        invoiceCount, paidInvoiceCount, overdueCount,
                        projectCount, activeProjectCount,
                        taskCount, completedTaskCount,
                        customerCount, activeCustomerCount
                ),
                monthly(from, to)
        );
    }

    private List<ReportMonthlyPoint> monthly(LocalDate from, LocalDate to) {
        List<ReportMonthlyPoint> result = new ArrayList<>();
        YearMonth current = YearMonth.from(from);
        YearMonth end = YearMonth.from(to);

        while (!current.isAfter(end)) {
            LocalDate monthFrom = current.atDay(1).isBefore(from) ? from : current.atDay(1);
            LocalDate monthTo = current.atEndOfMonth().isAfter(to) ? to : current.atEndOfMonth();

            BigDecimal invoiced = invoiceRepository.sumTotalAmountByIssueDateBetween(monthFrom, monthTo);
            BigDecimal paid = paymentRepository.sumAmountByPaymentDateBetween(monthFrom, monthTo);
            BigDecimal expense = expenseRepository.sumRecordedAmountByExpenseDateBetween(monthFrom, monthTo);

            result.add(new ReportMonthlyPoint(
                    current.toString(),
                    invoiced,
                    paid,
                    expense,
                    paid.subtract(expense)
            ));
            current = current.plusMonths(1);
        }

        return result;
    }

    private long countInvoicesInRange(LocalDate from, LocalDate to) {
        return invoiceRepository.findAll((root, query, cb) ->
                cb.and(
                        cb.greaterThanOrEqualTo(root.get("issueDate"), from),
                        cb.lessThanOrEqualTo(root.get("issueDate"), to)
                )).size();
    }

    private long countOverdueInvoices(LocalDate from, LocalDate to) {
        return invoiceRepository.findAll((root, query, cb) ->
                cb.and(
                        cb.greaterThanOrEqualTo(root.get("issueDate"), from),
                        cb.lessThanOrEqualTo(root.get("issueDate"), to),
                        cb.equal(root.get("status"), InvoiceStatus.OVERDUE)
                )).size();
    }

    private void validateRange(LocalDate from, LocalDate to) {
        if (from == null || to == null || from.isAfter(to)) {
            throw new IllegalArgumentException("Khoảng thời gian báo cáo không hợp lệ");
        }
    }
}
