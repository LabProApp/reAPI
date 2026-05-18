package com.api.plan;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.enums.MasterEnums;

public interface PlanFeatureRepository extends JpaRepository<PlanFeature, Long> {

	List<PlanFeature> findByPlanName(MasterEnums.PackageEnum planName);
}
