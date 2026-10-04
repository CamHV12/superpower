package com.company.enterprise.report;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class ReportExportService {
    private final ReportService reportService;

    public ReportExportService(ReportService reportService) {
        this.reportService = reportService;
    }

    public byte[] excel(LocalDate from, LocalDate to) {
        ReportResponse report = reportService.summary(from, to);
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet summary = workbook.createSheet("Summary");
            writeRow(summary, 0, "Enterprise Report", from + " → " + to);
            writeRow(summary, 2, "Metric", "Value");
            int row = 3;
            row = writeRow(summary, row, "Invoiced", money(report.summary().invoicedAmount()));
            row = writeRow(summary, row, "Paid", money(report.summary().paidAmount()));
            row = writeRow(summary, row, "Expense", money(report.summary().expenseAmount()));
            row = writeRow(summary, row, "Receivable", money(report.summary().receivableAmount()));
            row = writeRow(summary, row, "Net Cash Flow", money(report.summary().netCashFlow()));
            row = writeRow(summary, row, "Invoice Count", report.summary().invoiceCount());
            row = writeRow(summary, row, "Paid Invoice Count", report.summary().paidInvoiceCount());
            row = writeRow(summary, row, "Overdue Invoice Count", report.summary().overdueInvoiceCount());
            row = writeRow(summary, row, "Project Count", report.summary().projectCount());
            row = writeRow(summary, row, "Active Project Count", report.summary().activeProjectCount());
            row = writeRow(summary, row, "Task Count", report.summary().taskCount());
            row = writeRow(summary, row, "Completed Task Count", report.summary().completedTaskCount());
            row = writeRow(summary, row, "Customer Count", report.summary().customerCount());
            writeRow(summary, row, "Active Customer Count", report.summary().activeCustomerCount());
            autoSize(summary, 0, 1);

            Sheet monthly = workbook.createSheet("Monthly");
            writeRow(monthly, 0, "Month", "Invoiced", "Paid", "Expense", "Net Cash Flow");
            for (int i = 0; i < report.monthly().size(); i++) {
                ReportMonthlyPoint p = report.monthly().get(i);
                writeRow(monthly, i + 1, p.month(), money(p.invoicedAmount()), money(p.paidAmount()), money(p.expenseAmount()), money(p.netCashFlow()));
            }
            autoSize(monthly, 0, 4);
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Không thể tạo file Excel báo cáo", e);
        }
    }

    public byte[] pdf(LocalDate from, LocalDate to) {
        ReportResponse report = reportService.summary(from, to);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph("ENTERPRISE MANAGEMENT REPORT"));
            document.add(new Paragraph("Period: " + from + " → " + to));
            document.add(new Paragraph(" "));
            Table table = new Table(2);
            table.addCell("Metric");
            table.addCell("Value");
            addMetric(table, "Invoiced", report.summary().invoicedAmount());
            addMetric(table, "Paid", report.summary().paidAmount());
            addMetric(table, "Expense", report.summary().expenseAmount());
            addMetric(table, "Receivable", report.summary().receivableAmount());
            addMetric(table, "Net Cash Flow", report.summary().netCashFlow());
            addMetric(table, "Invoices", BigDecimal.valueOf(report.summary().invoiceCount()));
            addMetric(table, "Overdue Invoices", BigDecimal.valueOf(report.summary().overdueInvoiceCount()));
            addMetric(table, "Projects", BigDecimal.valueOf(report.summary().projectCount()));
            addMetric(table, "Tasks", BigDecimal.valueOf(report.summary().taskCount()));
            addMetric(table, "Customers", BigDecimal.valueOf(report.summary().customerCount()));
            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Monthly"));
            Table monthly = new Table(5);
            for (String header : new String[]{"Month", "Invoiced", "Paid", "Expense", "Net Cash Flow"}) monthly.addCell(header);
            for (ReportMonthlyPoint p : report.monthly()) {
                monthly.addCell(p.month());
                monthly.addCell(money(p.invoicedAmount()));
                monthly.addCell(money(p.paidAmount()));
                monthly.addCell(money(p.expenseAmount()));
                monthly.addCell(money(p.netCashFlow()));
            }
            document.add(monthly);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Không thể tạo file PDF báo cáo", e);
        }
    }

    private static void addMetric(Table table, String name, BigDecimal value) {
        table.addCell(name);
        table.addCell(money(value));
    }

    private static int writeRow(Sheet sheet, int row, Object... values) {
        Row excelRow = sheet.createRow(row);
        for (int i = 0; i < values.length; i++) {
            Cell cell = excelRow.createCell(i);
            cell.setCellValue(String.valueOf(values[i]));
        }
        return row + 1;
    }

    private static void autoSize(Sheet sheet, int from, int to) {
        for (int i = from; i <= to; i++) sheet.autoSizeColumn(i);
    }

    private static String money(BigDecimal value) {
        return value == null ? "0" : value.toPlainString();
    }
}