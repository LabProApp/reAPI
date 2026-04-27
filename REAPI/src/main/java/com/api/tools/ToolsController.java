package com.api.tools;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/tools")
@Tag(name = "Tools APIs", description = "Operations related to Tools like Home Loan Calculator")
public class ToolsController {

    @Autowired
    private ToolService toolService;

    @PostMapping("/homeloancalculator")
    public ResponseEntity<calcLoanResponse> calculate(@Valid @RequestBody calcLoanRequest request) {
        log.info("POST /api/tools/homeloancalculator - Calculating loan [principal={}, rate={}, tenure={}yrs]",
                request.getPrincipal(), request.getAnnualInterestRate(), request.getTenureYears());
        calcLoanResponse response = toolService.calculate(request);
        log.info("POST /api/tools/homeloancalculator - Calculation complete [emi={}]", response.getMonthlyEmi());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/calculateAffordability")
    public calcLoanResponse calculateAffordability(@RequestBody calcLoanRequest request) {
        log.info("POST /api/tools/calculateAffordability - Calculating affordability [principal={}, rate={}]",
                request.getPrincipal(), request.getAnnualInterestRate());
        calcLoanResponse response = toolService.calculateAffordability(request);
        log.info("POST /api/tools/calculateAffordability - Affordability calculation complete");
        return response;
    }
}
