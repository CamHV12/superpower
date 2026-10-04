package com.company.enterprise.report;

import com.company.enterprise.finance.invoice.repository.InvoiceRepository;
import com.company.enterprise.finance.payment.repository.PaymentRepository;
import com.company.enterprise.project.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ReportPerformanceService {
    private final ProjectRepository projectRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    public ReportPerformanceService(ProjectRepository projectRepository,
                                    InvoiceRepository invoiceRepository,
                                    PaymentRepository paymentRepository) {
        this.projectRepository = projectRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public ReportPerformanceResponse performance() {
        List<ReportProjectPerformance> projects = projectRepository.performanceReport().stream()
                .map(this::mapProject)
                .toList();

        Map<UUID, BigDecimal> invoicedByCustomer = toAmountMap(invoiceRepository.sumAmountsByCustomer());
        Map<UUID, BigDecimal> paidByCustomer = toAmountMap(paymentRepository.sumAmountsByCustomer());

        List<ReportCustomerPerformance> customers = projectRepository.customerPerformanceReport().stream()
                .map(row -> mapCustomer(row, invoicedByCustomer, paidByCustomer))
                .toList();

        return new ReportPerformanceResponse(projects, customers);
    }

    private ReportProjectPerformance mapProject(Object[] row) {
        return new ReportProjectPerformance(
                (UUID) row[0],
                (String) row[1],
                (String) row[2],
                (String) row[3],
                row[4].toString(),
                ((Number) row[5]).intValue(),
                money(row[6]),
                ((Number) row[7]).longValue(),
                ((Number) row[8]).longValue(),
                ((Number) row[9]).longValue(),
                money(row[10]),
                money(row[11])
        );
    }

    private ReportCustomerPerformance mapCustomer(Object[] row,
                                                  Map<UUID, BigDecimal> invoiced,
                                                  Map<UUID, BigDecimal> paid) {
        UUID id = (UUID) row[0];
        BigDecimal invoicedAmount = invoiced.getOrDefault(id, BigDecimal.ZERO);
        BigDecimal paidAmount = paid.getOrDefault(id, BigDecimal.ZERO);
        return new ReportCustomerPerformance(
                id,
                (String) row[1],
                (String) row[2],
                (Boolean) row[3],
                ((Number) row[4]).longValue(),
                ((Number) row[5]).longValue(),
                money(row[6]),
                invoicedAmount,
                paidAmount,
                invoicedAmount.subtract(paidAmount).max(BigDecimal.ZERO)
        );
    }

    private Map<UUID, BigDecimal> toAmountMap(List<Object[]> rows) {
        Map<UUID, BigDecimal> result = new HashMap<>();
        for (Object[] row : rows) {
            result.put((UUID) row[0], money(row[1]));
        }
        return result;
    }

    private BigDecimal money(Object value) {
        return value == null ? BigDecimal.ZERO : (BigDecimal) value;
    }
}
