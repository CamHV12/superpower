package com.company.enterprise.report;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportExportServiceTest {

    @Test
    void exportsSummaryToExcel() {
        ReportService reportService = Mockito.mock(ReportService.class);
        Mockito.when(reportService.summary(Mockito.any(), Mockito.any())).thenReturn(sample());
        ReportExportService service = new ReportExportService(reportService);

        byte[] bytes = service.excel(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 31));

        assertTrue(bytes.length > 100);
        assertEquals('P', (char) bytes[0]);
        assertEquals('K', (char) bytes[1]);
    }

    @Test
    void exportsSummaryToPdf() {
        ReportService reportService = Mockito.mock(ReportService.class);
        Mockito.when(reportService.summary(Mockito.any(), Mockito.any())).thenReturn(sample());
        ReportExportService service = new ReportExportService(reportService);

        byte[] bytes = service.pdf(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 31));

        assertTrue(bytes.length > 100);
        assertEquals('%', (char) bytes[0]);
    }

    private ReportResponse sample() {
        ReportSummaryResponse summary = new ReportSummaryResponse(
                new BigDecimal("1000"), new BigDecimal("800"), new BigDecimal("200"),
                new BigDecimal("200"), new BigDecimal("600"),
                4, 3, 1, 5, 3, 20, 15, 6, 5);
        return new ReportResponse(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 3, 31),
                summary,
                List.of(new ReportMonthlyPoint("2026-01", new BigDecimal("1000"),
                        new BigDecimal("800"), new BigDecimal("200"), new BigDecimal("600"))));
    }
}