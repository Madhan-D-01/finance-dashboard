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
import java.util.List;
import java.util.Map;
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

	public double getTotalIncome() {
		return recordRepository.findAll().stream().filter(r -> r.getType() == RecordType.INCOME)
				.mapToDouble(Record::getAmount).sum();
	}

	public double getTotalExpense() {
		return recordRepository.findAll().stream().filter(r -> r.getType() == RecordType.EXPENSE)
				.mapToDouble(Record::getAmount).sum();
	}

	public void deleteRecord(Long id) {
		if (!recordRepository.existsById(id)) {
			throw new ResourceNotFoundException("Record not found with id: " + id);
		}
		recordRepository.deleteById(id);
	}

	public List<Record> filterRecords(String type, String category, String startDate, String endDate) {

		List<Record> records = recordRepository.findAll();

		if (type != null && !type.isBlank()) {
			RecordType recordType = RecordType.valueOf(type.toUpperCase());
			records = records.stream().filter(r -> r.getType() == recordType).toList();
		}

		if (category != null && !category.isBlank()) {
			records = records.stream().filter(r -> r.getCategory().equalsIgnoreCase(category)).toList();
		}

		if (startDate != null && endDate != null) {
			LocalDate start = LocalDate.parse(startDate);
			LocalDate end = LocalDate.parse(endDate);

			records = records.stream().filter(r -> !r.getDate().isBefore(start) && !r.getDate().isAfter(end)).toList();
		}

		return records;
	}

	public Map<String, Double> getCategoryWiseTotals() {

		return recordRepository.findAll().stream()
				.collect(Collectors.groupingBy(Record::getCategory, Collectors.summingDouble(Record::getAmount)));
	}

	public List<Record> getRecentActivities(int limit) {
		return recordRepository.findAllByOrderByDateDesc(PageRequest.of(0, limit)).getContent();
	}

	public Map<String, Double> getMonthlyTrends() {

		return recordRepository.findAll().stream()
				.collect(Collectors.groupingBy(r -> r.getDate().getYear() + "-" + r.getDate().getMonth(),
						Collectors.summingDouble(Record::getAmount)));
	}
}