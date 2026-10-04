package com.company.enterprise.finance.expense.repository;

import com.company.enterprise.finance.expense.entity.Expense;
import com.company.enterprise.finance.expense.entity.ExpenseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID>, JpaSpecificationExecutor<Expense> {

    List<Expense> findByStatusAndExpenseDateBetweenOrderByExpenseDateAsc(
            ExpenseStatus status, LocalDate from, LocalDate to);
    @Query("""
            select coalesce(sum(e.amount), 0)
            from Expense e
            where e.status = :status
            """)
    BigDecimal sumAmountByStatus(@Param("status") ExpenseStatus status);

    @Query("""
            select coalesce(sum(e.amount), 0)
            from Expense e
            where e.status = :status
              and e.expenseDate between :from and :to
            """)
    BigDecimal sumAmountByStatusAndDateBetween(
            @Param("status") ExpenseStatus status,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    @Query("""
            select e.id, e.category, e.amount, e.createdAt
            from Expense e
            order by e.createdAt desc
            """)
    List<Object[]> findRecentActivities();
}
