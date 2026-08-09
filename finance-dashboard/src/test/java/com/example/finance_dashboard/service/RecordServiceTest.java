package com.example.finance_dashboard.service;

import com.example.finance_dashboard.exception.ResourceNotFoundException;
import com.example.finance_dashboard.model.Record;
import com.example.finance_dashboard.model.RecordType;
import com.example.finance_dashboard.model.User;
import com.example.finance_dashboard.repository.RecordRepository;
import com.example.finance_dashboard.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecordServiceTest {

	@Mock
	private RecordRepository recordRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private RecordService recordService;

	private User existingUser;

	@BeforeEach
	void setUp() {
		existingUser = new User();
		existingUser.setId(1L);
		existingUser.setName("Test User");
		existingUser.setEmail("test@example.com");
	}

	// --- createRecord ---

	@Test
	void createRecord_withValidUserId_savesRecordLinkedToUser() {
		Record record = new Record();
		record.setAmount(500.0);
		record.setType(RecordType.INCOME);
		record.setCategory("Salary");
		record.setDate(LocalDate.of(2026, 7, 1));

		User linkedUser = new User();
		linkedUser.setId(1L);
		record.setUser(linkedUser);

		when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
		when(recordRepository.save(any(Record.class))).thenAnswer(inv -> inv.getArgument(0));

		Record saved = recordService.createRecord(record);

		assertThat(saved.getUser()).isEqualTo(existingUser);
		verify(recordRepository).save(record);
	}

	@Test
	void createRecord_withUnknownUserId_throwsResourceNotFound() {
		Record record = new Record();
		User missingUser = new User();
		missingUser.setId(99L);
		record.setUser(missingUser);

		when(userRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> recordService.createRecord(record))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("User not found");

		verify(recordRepository, never()).save(any());
	}

	// --- deleteRecord ---

	@Test
	void deleteRecord_whenExists_deletesSuccessfully() {
		when(recordRepository.existsById(10L)).thenReturn(true);

		recordService.deleteRecord(10L);

		verify(recordRepository).deleteById(10L);
	}

	@Test
	void deleteRecord_whenMissing_throwsResourceNotFound() {
		when(recordRepository.existsById(404L)).thenReturn(false);

		assertThatThrownBy(() -> recordService.deleteRecord(404L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("404");

		verify(recordRepository, never()).deleteById(any());
	}

	// --- updateRecord ---

	@Test
	void updateRecord_whenMissing_throwsResourceNotFound() {
		when(recordRepository.findById(5L)).thenReturn(Optional.empty());

		Record updated = new Record();
		updated.setAmount(100.0);

		assertThatThrownBy(() -> recordService.updateRecord(5L, updated))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("Record not found");
	}

	// --- DB-level aggregation: totals ---

	@Test
	void getTotalIncome_delegatesToSumAmountByType() {
		when(recordRepository.sumAmountByType(RecordType.INCOME)).thenReturn(95000.0);

		double result = recordService.getTotalIncome();

		assertThat(result).isEqualTo(95000.0);
		verify(recordRepository).sumAmountByType(RecordType.INCOME);
	}

	@Test
	void getTotalExpense_delegatesToSumAmountByType() {
		when(recordRepository.sumAmountByType(RecordType.EXPENSE)).thenReturn(8000.0);

		double result = recordService.getTotalExpense();

		assertThat(result).isEqualTo(8000.0);
		verify(recordRepository).sumAmountByType(RecordType.EXPENSE);
	}

	// --- DB-level aggregation: category totals ---

	@Test
	void getCategoryWiseTotals_mapsProjectionRowsToMap() {
		RecordRepository.CategoryTotal salary = mock(RecordRepository.CategoryTotal.class);
		when(salary.getCategory()).thenReturn("Salary");
		when(salary.getTotal()).thenReturn(95000.0);

		RecordRepository.CategoryTotal groceries = mock(RecordRepository.CategoryTotal.class);
		when(groceries.getCategory()).thenReturn("Groceries");
		when(groceries.getTotal()).thenReturn(8000.0);

		when(recordRepository.findCategoryTotals()).thenReturn(List.of(salary, groceries));

		Map<String, Double> result = recordService.getCategoryWiseTotals();

		assertThat(result)
				.containsEntry("Salary", 95000.0)
				.containsEntry("Groceries", 8000.0)
				.hasSize(2);
	}

	@Test
	void getCategoryWiseTotals_withNoRecords_returnsEmptyMap() {
		when(recordRepository.findCategoryTotals()).thenReturn(List.of());

		Map<String, Double> result = recordService.getCategoryWiseTotals();

		assertThat(result).isEmpty();
	}

	// --- DB-level aggregation: monthly trends ---

	@Test
	void getMonthlyTrends_formatsKeysAsYearDashMonth_andPreservesOrder() {
		RecordRepository.MonthlyTotal june = mock(RecordRepository.MonthlyTotal.class);
		when(june.getYear()).thenReturn(2026);
		when(june.getMonth()).thenReturn(6);
		when(june.getTotal()).thenReturn(45000.0);

		RecordRepository.MonthlyTotal july = mock(RecordRepository.MonthlyTotal.class);
		when(july.getYear()).thenReturn(2026);
		when(july.getMonth()).thenReturn(7);
		when(july.getTotal()).thenReturn(58000.0);

		// Repository query already orders by year, month — service must preserve that order
		when(recordRepository.findMonthlyTotals()).thenReturn(List.of(june, july));

		Map<String, Double> result = recordService.getMonthlyTrends();

		assertThat(result.keySet()).containsExactly("2026-06", "2026-07");
		assertThat(result.get("2026-06")).isEqualTo(45000.0);
		assertThat(result.get("2026-07")).isEqualTo(58000.0);
	}

	@Test
	void getMonthlyTrends_padsSingleDigitMonths_forCorrectSorting() {
		// Regression guard: keys must be "2026-04" not "2026-4", or string
		// sorting breaks (e.g. "2026-11" would sort before "2026-4").
		RecordRepository.MonthlyTotal april = mock(RecordRepository.MonthlyTotal.class);
		when(april.getYear()).thenReturn(2026);
		when(april.getMonth()).thenReturn(4);
		when(april.getTotal()).thenReturn(1000.0);

		when(recordRepository.findMonthlyTotals()).thenReturn(List.of(april));

		Map<String, Double> result = recordService.getMonthlyTrends();

		assertThat(result).containsOnlyKeys("2026-04");
	}

	// --- filterRecords: parameter parsing ---

	@Test
	void filterRecords_withAllParamsBlank_passesNullsToRepository() {
		when(recordRepository.filterRecords(null, null, null, null)).thenReturn(List.of());

		recordService.filterRecords("", "", "", "");

		verify(recordRepository).filterRecords(null, null, null, null);
	}

	@Test
	void filterRecords_withTypeAndDateRange_parsesCorrectly() {
		LocalDate start = LocalDate.of(2026, 7, 1);
		LocalDate end = LocalDate.of(2026, 7, 31);

		when(recordRepository.filterRecords(eq(RecordType.INCOME), eq("Salary"), eq(start), eq(end)))
				.thenReturn(List.of());

		recordService.filterRecords("income", "Salary", "2026-07-01", "2026-07-31");

		verify(recordRepository).filterRecords(RecordType.INCOME, "Salary", start, end);
	}

	@Test
	void filterRecords_withInvalidType_throwsIllegalArgumentException() {
		assertThatThrownBy(() -> recordService.filterRecords("NOT_A_TYPE", null, null, null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	// --- getRecentActivities ---

	@Test
	void getRecentActivities_requestsFirstPageWithGivenLimit() {
		Record r = new Record();
		when(recordRepository.findAllByOrderByDateDesc(any()))
				.thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(r)));

		List<Record> result = recordService.getRecentActivities(5);

		assertThat(result).hasSize(1);
	}
}
