package com.example.finance_dashboard.controller;

import com.example.finance_dashboard.dto.DashboardResponse;
import com.example.finance_dashboard.model.Record;
import com.example.finance_dashboard.service.RecordService;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private RecordService recordService;
    
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @GetMapping("/summary")
    public DashboardResponse getSummary() {

        double income = recordService.getTotalIncome();
        double expense = recordService.getTotalExpense();

        return new DashboardResponse(
                income,
                expense,
                income - expense
        );
    }
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @GetMapping("/category-summary")
    public Map<String, Double> getCategorySummary() {
        return recordService.getCategoryWiseTotals();
    }
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @GetMapping("/recent")
    public List<Record> getRecentActivities(
            @RequestParam(defaultValue = "5") int limit) {
        return recordService.getRecentActivities(limit);
    }
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @GetMapping("/monthly-trends")
    public Map<String, Double> getMonthlyTrends() {
        return recordService.getMonthlyTrends();
    }
}
