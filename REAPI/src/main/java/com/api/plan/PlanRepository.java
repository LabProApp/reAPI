package com.api.plan;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.enums.MasterEnums;

public interface PlanRepository extends JpaRepository<Plan, Long> {

	Optional<Plan> findByPlanName(MasterEnums.PackageEnum planName);
}
