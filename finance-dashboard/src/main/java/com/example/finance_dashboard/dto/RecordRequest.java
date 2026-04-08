package com.example.finance_dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

import com.example.finance_dashboard.model.RecordType;

@Data 
@AllArgsConstructor 
@NoArgsConstructor  
public class RecordRequest {
    private Double amount;
    private RecordType type; 
    private String category;
    private LocalDate date;
    private String notes;
    private Long userId; 
}