package com.api.tools;



import java.math.BigDecimal;
import java.time.LocalDate;

public class AmortizationEntry {
    private int monthNumber;
    private LocalDate paymentDate;
    private BigDecimal beginningBalance;
    private BigDecimal scheduledPayment;
    private BigDecimal principalComponent;
    private BigDecimal interestComponent;
    private BigDecimal endingBalance;

    // getters and setters
    public int getMonthNumber() { return monthNumber; }
    public void setMonthNumber(int monthNumber) { this.monthNumber = monthNumber; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    public BigDecimal getBeginningBalance() { return beginningBalance; }
    public void setBeginningBalance(BigDecimal beginningBalance) { this.beginningBalance = beginningBalance; }
    public BigDecimal getScheduledPayment() { return scheduledPayment; }
    public void setScheduledPayment(BigDecimal scheduledPayment) { this.scheduledPayment = scheduledPayment; }
    public BigDecimal getPrincipalComponent() { return principalComponent; }
    public void setPrincipalComponent(BigDecimal principalComponent) { this.principalComponent = principalComponent; }
    public BigDecimal getInterestComponent() { return interestComponent; }
    public void setInterestComponent(BigDecimal interestComponent) { this.interestComponent = interestComponent; }
    public BigDecimal getEndingBalance() { return endingBalance; }
    public void setEndingBalance(BigDecimal endingBalance) { this.endingBalance = endingBalance; }
}

