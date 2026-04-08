package com.example.finance_dashboard.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;
import com.example.finance_dashboard.model.Record;
import com.example.finance_dashboard.model.RecordType;
	public interface RecordRepository extends JpaRepository<Record, Long> {

		List<Record> findByType(RecordType type);

	    List<Record> findByCategory(String category);
	    Page<Record> findAll(Pageable pageable);
	    Page<Record> findAllByOrderByDateDesc(Pageable pageable);
	}

