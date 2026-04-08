package com.example.finance_dashboard.controller;

import com.example.finance_dashboard.dto.RecordRequest;
import com.example.finance_dashboard.exception.ResourceNotFoundException;
import com.example.finance_dashboard.model.Record;
import com.example.finance_dashboard.model.RecordType;
import com.example.finance_dashboard.model.User;
import com.example.finance_dashboard.repository.RecordRepository;
import com.example.finance_dashboard.repository.UserRepository;
import com.example.finance_dashboard.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
public class RecordController {

    @Autowired
    private RecordService recordService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Record createRecord(@RequestBody RecordRequest recordRequest) {
    	 Record record = new Record();
    	    record.setAmount(recordRequest.getAmount());
    	    record.setType(recordRequest.getType());
    	    record.setCategory(recordRequest.getCategory());
    	    record.setDate(recordRequest.getDate());
    	    record.setNotes(recordRequest.getNotes());

    	    
    	    User user = new User();
    	    user.setId(recordRequest.getUserId());
    	    record.setUser(user);

    	    return recordService.createRecord(record); 
    	}
    


    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping
    public Page<Record> getAllRecords(Pageable pageable) {
        return recordService.getAllRecords(pageable); 
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Record updateRecord(@PathVariable Long id, @RequestBody Record record) {
        return recordService.updateRecord(id, record);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteRecord(@PathVariable Long id) {
        recordService.deleteRecord(id);
        return "Record deleted successfully";
    }
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/filter")
    public List<Record> filterRecords(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        return recordService.filterRecords(type, category, startDate, endDate);
    }
    
}
