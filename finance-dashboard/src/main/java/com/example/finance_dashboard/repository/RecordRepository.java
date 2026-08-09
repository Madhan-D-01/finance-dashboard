package com.example.finance_dashboard.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;
import com.example.finance_dashboard.model.Record;
import com.example.finance_dashboard.model.RecordType;

public interface RecordRepository extends JpaRepository<Record, Long> {

	List<Record> findByType(RecordType type);
	List<Record> findByCategory(String category);
	Page<Record> findAll(Pageable pageable);
	Page<Record> findAllByOrderByDateDesc(Pageable pageable);

	// --- DB-level aggregation, replacing in-memory stream sums ---

	@Query("SELECT COALESCE(SUM(r.amount), 0) FROM Record r WHERE r.type = :type")
	double sumAmountByType(@Param("type") RecordType type);

	@Query("SELECT r.category AS category, SUM(r.amount) AS total FROM Record r GROUP BY r.category")
	List<CategoryTotal> findCategoryTotals();

	@Query("SELECT YEAR(r.date) AS year, MONTH(r.date) AS month, SUM(r.amount) AS total " +
	       "FROM Record r GROUP BY YEAR(r.date), MONTH(r.date) ORDER BY YEAR(r.date), MONTH(r.date)")
	List<MonthlyTotal> findMonthlyTotals();

	// --- Bonus: filtering pushed to the DB too, replacing the in-memory filterRecords() ---

	@Query("SELECT r FROM Record r WHERE " +
	       "(:type IS NULL OR r.type = :type) AND " +
	       "(:category IS NULL OR LOWER(r.category) = LOWER(:category)) AND " +
	       "(:startDate IS NULL OR r.date >= :startDate) AND " +
	       "(:endDate IS NULL OR r.date <= :endDate)")
	List<Record> filterRecords(
			@Param("type") RecordType type,
			@Param("category") String category,
			@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate
	);

	// Projection interfaces — Spring Data maps query columns onto these automatically
	interface CategoryTotal {
		String getCategory();
		Double getTotal();
	}

	interface MonthlyTotal {
		int getYear();
		int getMonth();
		Double getTotal();
	}
}