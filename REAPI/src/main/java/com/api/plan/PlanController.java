package com.api.plan;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.enums.MasterEnums;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/plans")
@Tag(name = "Plans", description = "Subscription plans and feature flags")
public class PlanController {

	private final PlanRepository planRepository;
	private final PlanService planService;

	public PlanController(PlanRepository planRepository, PlanService planService) {
		this.planRepository = planRepository;
		this.planService = planService;
	}

	@Operation(summary = "List all plans with their feature flags")
	@GetMapping
	public ResponseEntity<List<PlanDto>> listPlans() {
		List<PlanDto> dtos = planRepository.findAll().stream()
			.map(p -> new PlanDto(
				p.getPlanName(),
				p.getDisplayName(),
				p.getPriceYearly(),
				planService.getFeatureFlags(p.getPlanName())
			))
			.collect(Collectors.toList());
		return ResponseEntity.ok(dtos);
	}

	@Operation(summary = "Get a single plan with its feature flags")
	@GetMapping("/{planName}")
	public ResponseEntity<PlanDto> getPlan(@PathVariable MasterEnums.PackageEnum planName) {
		return planRepository.findByPlanName(planName)
			.map(p -> ResponseEntity.ok(new PlanDto(
				p.getPlanName(),
				p.getDisplayName(),
				p.getPriceYearly(),
				planService.getFeatureFlags(p.getPlanName())
			)))
			.orElseGet(() -> ResponseEntity.notFound().build());
	}
}
