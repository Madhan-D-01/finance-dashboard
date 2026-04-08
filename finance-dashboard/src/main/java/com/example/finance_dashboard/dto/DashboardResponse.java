package com.example.finance_dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardResponse {
    private double totalIncome;
    private double totalExpense;
    private double netBalance;
}
