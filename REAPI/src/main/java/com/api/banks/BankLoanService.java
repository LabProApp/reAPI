package com.api.banks;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class BankLoanService {

	private final BankLoanRepository bankLoanRepository;

	public BankLoanService(BankLoanRepository bankLoanRepository) {
		this.bankLoanRepository = bankLoanRepository;
	}

	public List<BankLoan> getAllLoans() {
		return bankLoanRepository.findAll();
	}

	public BankLoan addLoan(BankLoan loan) {
		return bankLoanRepository.save(loan);
	}

	public List<BankLoan> filterLoans(Double maxRate, Integer minCibil) {
		return bankLoanRepository.findAll().stream()
				.filter(loan -> (maxRate == null || loan.getInterestRate() <= maxRate))
				.filter(loan -> (minCibil == null || loan.getMinCibilScore() <= minCibil)).collect(Collectors.toList());
	}
}
