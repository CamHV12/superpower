package com.company.enterprise.finance.invoice;

import com.company.enterprise.customer.entity.Customer;
import com.company.enterprise.customer.repository.CustomerRepository;
import com.company.enterprise.finance.invoice.dto.CreateInvoiceRequest;
import com.company.enterprise.finance.invoice.dto.InvoiceResponse;
import com.company.enterprise.finance.invoice.entity.Invoice;
import com.company.enterprise.finance.invoice.repository.InvoiceRepository;
import com.company.enterprise.project.entity.Project;
import com.company.enterprise.project.repository.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final ProjectRepository projectRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, CustomerRepository customerRepository, ProjectRepository projectRepository) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public InvoiceResponse create(CreateInvoiceRequest request) {
        if (invoiceRepository.existsByInvoiceNumber(request.invoiceNumber())) {
            throw new IllegalArgumentException("Số hóa đơn đã tồn tại");
        }
        if (request.issueDate().isAfter(request.dueDate())) {
            throw new IllegalArgumentException("Ngày phát hành không được sau ngày đến hạn");
        }

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng"));

        Project project = null;
        if (request.projectId() != null) {
            project = projectRepository.findById(request.projectId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy dự án"));
            if (project.getCustomer() != null && !project.getCustomer().getId().equals(customer.getId())) {
                throw new IllegalArgumentException("Dự án không thuộc khách hàng của hóa đơn");
            }
        }

        Invoice invoice = new Invoice(request.invoiceNumber(), customer, project,
                request.issueDate(), request.dueDate(), request.taxAmount(),
                request.discountAmount(), request.notes());

        request.items().forEach(item ->
                invoice.addItem(item.description(), item.quantity(), item.unitPrice()));

        if (invoice.getTotalAmount().signum() < 0) {
            throw new IllegalArgumentException("Tổng tiền hóa đơn không được âm");
        }

        return toResponse(invoiceRepository.save(invoice));
    }

    @Transactional(readOnly = true)
    public Page<InvoiceResponse> findAll(Pageable pageable) {
        return invoiceRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public InvoiceResponse findById(UUID id) {
        return toResponse(invoiceRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy hóa đơn")));
    }

    private InvoiceResponse toResponse(Invoice invoice) {
        Customer customer = invoice.getCustomer();
        Project project = invoice.getProject();
        return new InvoiceResponse(invoice.getId(), invoice.getInvoiceNumber(),
                customer.getId(), customer.getName(),
                project == null ? null : project.getId(),
                project == null ? null : project.getName(),
                invoice.getIssueDate(), invoice.getDueDate(), invoice.getStatus(),
                invoice.getSubtotal(), invoice.getTaxAmount(), invoice.getDiscountAmount(),
                invoice.getTotalAmount(), invoice.getNotes(),
                invoice.getItems().stream()
                        .map(item -> new InvoiceResponse.InvoiceItemResponse(item.getId(), item.getDescription(),
                                item.getQuantity(), item.getUnitPrice(), item.getAmount()))
                        .toList());
    }
}
