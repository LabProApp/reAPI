package com.api.tools;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tools")
@Tag(name = "Tools APIs", description = "Operations related to Tools like Home Loan Calculator")
public class ToolsController {

    @Autowired
    private ToolService toolService;

    /**
     * Calculate home vendor details. JSON body required.
     * Example: { "principal": 5000000, "annualInterestRate": 7.5, "tenureYears": 20, "includeSchedule": true }
     */
    @PostMapping("/homevendor")
    public ResponseEntity<LoanResponse> calculate(@Valid @RequestBody LoanRequest request) {
       LoanResponse response = toolService.calculate(request);
        return ResponseEntity.ok(response);
    }
}
