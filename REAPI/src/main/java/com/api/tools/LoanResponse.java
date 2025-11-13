package com.api.tools;

import java.math.BigDecimal;
import java.util.List;

public class LoanResponse {
    private BigDecimal monthlyPayment;
    private BigDecimal totalPayment;
    private BigDecimal totalInterest;
    private int totalMonths;
    private List<AmortizationEntry> amortizationSchedule; // optional

    // getters and setters
    public BigDecimal getMonthlyPayment() { return monthlyPayment; }
    public void setMonthlyPayment(BigDecimal monthlyPayment) { this.monthlyPayment = monthlyPayment; }
    public BigDecimal getTotalPayment() { return totalPayment; }
    public void setTotalPayment(BigDecimal totalPayment) { this.totalPayment = totalPayment; }
    public BigDecimal getTotalInterest() { return totalInterest; }
    public void setTotalInterest(BigDecimal totalInterest) { this.totalInterest = totalInterest; }
    public int getTotalMonths() { return totalMonths; }
    public void setTotalMonths(int totalMonths) { this.totalMonths = totalMonths; }
    public List<AmortizationEntry> getAmortizationSchedule() { return amortizationSchedule; }
    public void setAmortizationSchedule(List<AmortizationEntry> amortizationSchedule) { this.amortizationSchedule = amortizationSchedule; }
}
