package com.api.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ToolService {

	private static final int OUTPUT_SCALE = 2;

	public calcLoanResponse calculate(calcLoanRequest req) {
		log.info("calculate - Loan calculation [principal={}, rate={}%, tenure={}yrs, includeSchedule={}]",
				req.getPrincipal(), req.getAnnualInterestRate(), req.getTenureYears(), req.getIncludeSchedule());

		BigDecimal principal = req.getPrincipal();
		BigDecimal annualRatePercent = BigDecimal.valueOf(req.getAnnualInterestRate());
		int years = req.getTenureYears();
		boolean includeSchedule = req.getIncludeSchedule();
		int totalMonths = years * 12;

		BigDecimal monthlyRate = annualRatePercent
				.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
				.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

		BigDecimal monthlyPayment;

		if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
			monthlyPayment = principal.divide(BigDecimal.valueOf(totalMonths), OUTPUT_SCALE, RoundingMode.HALF_UP);
		} else {
			BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
			BigDecimal onePlusRPowerN = onePlusR.pow(totalMonths);
			BigDecimal numerator = principal.multiply(monthlyRate).multiply(onePlusRPowerN);
			BigDecimal denominator = onePlusRPowerN.subtract(BigDecimal.ONE);
			monthlyPayment = numerator.divide(denominator, OUTPUT_SCALE, RoundingMode.HALF_UP);
		}

		BigDecimal totalPayment = monthlyPayment.multiply(BigDecimal.valueOf(totalMonths))
				.setScale(OUTPUT_SCALE, RoundingMode.HALF_UP);
		BigDecimal totalInterest = totalPayment.subtract(principal).setScale(OUTPUT_SCALE, RoundingMode.HALF_UP);

		calcLoanResponse resp = new calcLoanResponse();
		resp.setMonthlyPayment(monthlyPayment);
		resp.setTotalPayment(totalPayment);
		resp.setTotalInterest(totalInterest);
		resp.setTotalMonths(totalMonths);

		if (includeSchedule) {
			log.debug("calculate - Generating amortization schedule for {} months", totalMonths);
			resp.setAmortizationSchedule(generateSchedule(principal, monthlyRate, monthlyPayment, totalMonths));
		}

		log.info("calculate - EMI={}, totalInterest={}, totalPayment={}",
				monthlyPayment, totalInterest, totalPayment);
		return resp;
	}

	public calcLoanResponse calculateAffordability(calcLoanRequest req) {
		log.info("calculateAffordability - [monthlyIncome={}, existingEmi={}, rate={}%, foir={}%, tenure={}yrs]",
				req.getMonthlyIncome(), req.getExistingEmi(), req.getAnnualInterestRate(),
				req.getFoirPercent(), req.getTenureYears());

		BigDecimal monthlyIncome = BigDecimal.valueOf(req.getMonthlyIncome());
		BigDecimal existingEmi = BigDecimal.valueOf(req.getExistingEmi());
		BigDecimal annualRatePercent = BigDecimal.valueOf(req.getAnnualInterestRate());
		BigDecimal foirPercent = BigDecimal.valueOf(req.getFoirPercent());
		int tenureMonths = req.getTenureYears() * 12;

		BigDecimal foir = foirPercent.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
		BigDecimal affordableEmi = monthlyIncome.multiply(foir).subtract(existingEmi);

		if (affordableEmi.compareTo(BigDecimal.ZERO) < 0) {
			log.warn("calculateAffordability - Affordable EMI is negative, capping to 0");
			affordableEmi = BigDecimal.ZERO;
		}

		BigDecimal monthlyRate = annualRatePercent
				.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
				.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

		BigDecimal eligibleLoan;
		if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
			eligibleLoan = affordableEmi.multiply(BigDecimal.valueOf(tenureMonths));
		} else {
			BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
			BigDecimal onePlusRPowerN = onePlusR.pow(tenureMonths);
			BigDecimal numerator = onePlusRPowerN.subtract(BigDecimal.ONE);
			BigDecimal denominator = monthlyRate.multiply(onePlusRPowerN);
			eligibleLoan = affordableEmi.multiply(numerator).divide(denominator, OUTPUT_SCALE, RoundingMode.HALF_UP);
		}

		eligibleLoan = eligibleLoan.setScale(OUTPUT_SCALE, RoundingMode.HALF_UP);
		affordableEmi = affordableEmi.setScale(OUTPUT_SCALE, RoundingMode.HALF_UP);

		log.info("calculateAffordability - eligibleLoan={}, affordableEmi={}", eligibleLoan, affordableEmi);

		calcLoanResponse resp = new calcLoanResponse();
		resp.setMonthlyPayment(affordableEmi);
		resp.setEligibleLoan(eligibleLoan.doubleValue());
		resp.setTotalMonths(tenureMonths);
		return resp;
	}

	private List<AmortizationEntry> generateSchedule(BigDecimal principal, BigDecimal monthlyRate,
			BigDecimal monthlyPayment, int totalMonths) {
		List<AmortizationEntry> schedule = new ArrayList<>();
		BigDecimal balance = principal.setScale(OUTPUT_SCALE + 2, RoundingMode.HALF_UP);
		LocalDate paymentDate = LocalDate.now().plusMonths(1);

		for (int m = 1; m <= totalMonths; m++) {
			BigDecimal interest = balance.multiply(monthlyRate).setScale(OUTPUT_SCALE, RoundingMode.HALF_UP);
			BigDecimal principalComponent = monthlyPayment.subtract(interest).setScale(OUTPUT_SCALE, RoundingMode.HALF_UP);

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
			balance = endingBalance;
			paymentDate = paymentDate.plusMonths(1);
		}
		return schedule;
	}
}
