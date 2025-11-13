package com.api.tools;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;


@Service
public class ToolService {

    private static final int SCALE = 10; // internal calculation scale
    private static final int OUTPUT_SCALE = 2;

    public LoanResponse calculate(LoanRequest req) {
        BigDecimal principal = req.getPrincipal();
        BigDecimal annualRatePercent = req.getAnnualInterestRate();
        int years = req.getTenureYears();
        boolean includeSchedule = req.getIncludeSchedule();

        int totalMonths = years * 12;
        // monthly rate as decimal
        BigDecimal monthlyRate = annualRatePercent.divide(BigDecimal.valueOf(100), SCALE, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), SCALE, RoundingMode.HALF_UP);

        // Monthly payment formula: M = P * r * (1+r)^n / ((1+r)^n - 1)
        BigDecimal onePlusRPowerN = (BigDecimal.ONE.add(monthlyRate)).pow(totalMonths);
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(onePlusRPowerN);
        BigDecimal denominator = onePlusRPowerN.subtract(BigDecimal.ONE);

        BigDecimal monthlyPayment;
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            monthlyPayment = principal.divide(BigDecimal.valueOf(totalMonths), OUTPUT_SCALE, RoundingMode.HALF_UP);
        } else {
            monthlyPayment = numerator.divide(denominator, OUTPUT_SCALE, RoundingMode.HALF_UP);
        }

        BigDecimal totalPayment = monthlyPayment.multiply(BigDecimal.valueOf(totalMonths)).setScale(OUTPUT_SCALE, RoundingMode.HALF_UP);
        BigDecimal totalInterest = totalPayment.subtract(principal).setScale(OUTPUT_SCALE, RoundingMode.HALF_UP);

        LoanResponse resp = new LoanResponse();
        resp.setMonthlyPayment(monthlyPayment);
        resp.setTotalPayment(totalPayment);
        resp.setTotalInterest(totalInterest);
        resp.setTotalMonths(totalMonths);

        if (includeSchedule) {
            resp.setAmortizationSchedule(generateSchedule(principal, monthlyRate, monthlyPayment, totalMonths));
        }
        return resp;
    }

    private List<AmortizationEntry> generateSchedule(BigDecimal principal, BigDecimal monthlyRate, BigDecimal monthlyPayment, int totalMonths) {
        List<AmortizationEntry> schedule = new ArrayList<>();
        BigDecimal balance = principal.setScale(OUTPUT_SCALE + 2, RoundingMode.HALF_UP);
        LocalDate paymentDate = LocalDate.now().plusMonths(1);

        for (int m = 1; m <= totalMonths; m++) {
            BigDecimal interest = balance.multiply(monthlyRate).setScale(OUTPUT_SCALE, RoundingMode.HALF_UP);
            BigDecimal principalComponent = monthlyPayment.subtract(interest).setScale(OUTPUT_SCALE, RoundingMode.HALF_UP);

            // last payment adjustment to avoid negative balance due to rounding
            if (m == totalMonths) {
                principalComponent = balance;
                monthlyPayment = principalComponent.add(interest).setScale(OUTPUT_SCALE, RoundingMode.HALF_UP);
            }

            BigDecimal endingBalance = balance.subtract(principalComponent).setScale(OUTPUT_SCALE, RoundingMode.HALF_UP);

            AmortizationEntry entry = new AmortizationEntry();
            entry.setMonthNumber(m);
            entry.setPaymentDate(paymentDate);
            entry.setBeginningBalance(balance.setScale(OUTPUT_SCALE, RoundingMode.HALF_UP));
            entry.setScheduledPayment(monthlyPayment);
            entry.setInterestComponent(interest);
            entry.setPrincipalComponent(principalComponent);
            entry.setEndingBalance(endingBalance.max(BigDecimal.ZERO));

            schedule.add(entry);

            // next iteration
            balance = endingBalance;
            paymentDate = paymentDate.plusMonths(1);
        }
        return schedule;
    }
}
