package com.example.finance_dashboard.service;

import com.example.finance_dashboard.exception.ResourceNotFoundException;
import com.example.finance_dashboard.model.Record;
import com.example.finance_dashboard.model.RecordType;
import com.example.finance_dashboard.model.User;
import com.example.finance_dashboard.repository.RecordRepository;
import com.example.finance_dashboard.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecordService {

	@Autowired
	private RecordRepository recordRepository;
	@Autowired
	private UserRepository userRepository;

	public Record createRecord(Record record) {
		if (record.getUser() != null && record.getUser().getId() != null) {
			User existingUser = userRepository.findById(record.getUser().getId())
					.orElseThrow(() -> new ResourceNotFoundException("User not found"));
			record.setUser(existingUser);
		}
		return recordRepository.save(record);
	}

	public Page<Record> getAllRecords(Pageable pageable) {
		return recordRepository.findAll(pageable);
	}

	public Record updateRecord(Long id, Record updatedRecord) {
		Record record = recordRepository.findById(id)
		        .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));
		record.setAmount(updatedRecord.getAmount());
		record.setType(updatedRecord.getType());
		record.setCategory(updatedRecord.getCategory());
		record.setDate(updatedRecord.getDate());
		record.setNotes(updatedRecord.getNotes());

		if (updatedRecord.getUser() != null && updatedRecord.getUser().getId() != null) {
			User user = userRepository.findById(updatedRecord.getUser().getId())
					.orElseThrow(() -> new ResourceNotFoundException("User not found"));
			record.setUser(user);
		}
		return recordRepository.save(record);
	}

	public void deleteRecord(Long id) {
		if (!recordRepository.existsById(id)) {
			throw new ResourceNotFoundException("Record not found with id: " + id);
		}
		recordRepository.deleteById(id);
	}

	public double getTotalIncome() {
		return recordRepository.sumAmountByType(RecordType.INCOME);
	}

	public double getTotalExpense() {
		return recordRepository.sumAmountByType(RecordType.EXPENSE);
	}

	public Map<String, Double> getCategoryWiseTotals() {
		return recordRepository.findCategoryTotals().stream()
				.collect(Collectors.toMap(
						RecordRepository.CategoryTotal::getCategory,
						RecordRepository.CategoryTotal::getTotal
				));
	}

	public Map<String, Double> getMonthlyTrends() {
		return recordRepository.findMonthlyTotals().stream()
				.collect(Collectors.toMap(
						m -> String.format("%04d-%02d", m.getYear(), m.getMonth()),
						RecordRepository.MonthlyTotal::getTotal,
						(a, b) -> a,
						LinkedHashMap::new
				));
	}

	public List<Record> filterRecords(String type, String category, String startDate, String endDate) {
		RecordType recordType = (type != null && !type.isBlank())
				? RecordType.valueOf(type.toUpperCase())
				: null;
		LocalDate start = (startDate != null && !startDate.isBlank()) ? LocalDate.parse(startDate) : null;
		LocalDate end = (endDate != null && !endDate.isBlank()) ? LocalDate.parse(endDate) : null;
		String categoryValue = (category != null && !category.isBlank()) ? category : null;

		return recordRepository.filterRecords(recordType, categoryValue, start, end);
	}

	public List<Record> getRecentActivities(int limit) {
		return recordRepository.findAllByOrderByDateDesc(PageRequest.of(0, limit)).getContent();
	}
}