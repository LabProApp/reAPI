package com.api.tools;



import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class LoanRequest {

    @NotNull(message = "principal is required")
    @DecimalMin(value = "0.01", message = "principal must be > 0")
    private BigDecimal principal;

    /**
     * Annual interest rate as percentage (e.g., 7.5 for 7.5%).
     */
    @NotNull(message = "annualInterestRate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "annualInterestRate must be > 0")
    private BigDecimal annualInterestRate;

    /**
     * Tenure length in years (e.g., 20)
     */
    @NotNull(message = "tenureYears is required")
    @Min(value = 1, message = "tenureYears must be >= 1")
    private Integer tenureYears;

    /**
     * Optional: include amortization schedule boolean (default false)
     */
    private Boolean includeSchedule = Boolean.FALSE;

    // getters and setters
    public BigDecimal getPrincipal() { return principal; }
    public void setPrincipal(BigDecimal principal) { this.principal = principal; }
    public BigDecimal getAnnualInterestRate() { return annualInterestRate; }
    public void setAnnualInterestRate(BigDecimal annualInterestRate) { this.annualInterestRate = annualInterestRate; }
    public Integer getTenureYears() { return tenureYears; }
    public void setTenureYears(Integer tenureYears) { this.tenureYears = tenureYears; }
    public Boolean getIncludeSchedule() { return includeSchedule == null ? Boolean.FALSE : includeSchedule; }
    public void setIncludeSchedule(Boolean includeSchedule) { this.includeSchedule = includeSchedule; }
}
