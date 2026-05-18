package com.api.admin;

import java.util.LinkedHashMap;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.enums.MasterEnums;
import com.api.plan.PlanChangeRequestRepository;
import com.api.plan.PlanChangeRequestStatus;
import com.api.prop.PropertyRepository;
import com.api.user.UserDto;
import com.api.user.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Read-only admin support for the mobile app's admin dashboard.
 *
 * <p>Write actions live on their existing controllers — the admin client
 * calls {@code POST /api/user/{id}/plan} for direct plan assignment and
 * {@code POST /api/plan-requests/{id}/approve|reject} for the queue. This
 * controller only adds aggregation + a searchable user list.</p>
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin-only dashboard aggregations and user listing")
public class AdminController {

	private static final Logger log = LoggerFactory.getLogger(AdminController.class);

	private final UserRepository userRepository;
	private final PropertyRepository propertyRepository;
	private final PlanChangeRequestRepository planRequestRepository;
	private final ModelMapper mapper;

	public AdminController(UserRepository userRepository,
			PropertyRepository propertyRepository,
			PlanChangeRequestRepository planRequestRepository,
			ModelMapper mapper) {
		this.userRepository = userRepository;
		this.propertyRepository = propertyRepository;
		this.planRequestRepository = planRequestRepository;
		this.mapper = mapper;
	}

	@Operation(summary = "Get dashboard counts for the admin home screen")
	@GetMapping("/stats")
	public ResponseEntity<AdminStatsDto> stats() {
		AdminStatsDto dto = new AdminStatsDto();

		long total = userRepository.count();
		dto.setTotalUsers(total);

		long sale = propertyRepository.countByRentOrSaleIgnoreCase("SALE");
		long rent = propertyRepository.countByRentOrSaleIgnoreCase("RENT");
		dto.setPropertiesForSale(sale);
		dto.setPropertiesForRent(rent);
		dto.setTotalProperties(sale + rent);

		dto.setPendingPlanRequests(
			planRequestRepository.findByStatusOrderByRequestedAtAsc(
					PlanChangeRequestStatus.PENDING).size());

		Map<String, Long> byPlan = new LinkedHashMap<>();
		long active = 0;
		for (MasterEnums.PackageEnum p : MasterEnums.PackageEnum.values()) {
			long c = userRepository.countByUserPackage(p);
			byPlan.put(p.name(), c);
			if (p == MasterEnums.PackageEnum.DELUX || p == MasterEnums.PackageEnum.PREMIUM) {
				active += c;
			}
		}
		dto.setUsersByPlan(byPlan);
		dto.setActiveSubscriptions(active);

		log.info("GET /api/admin/stats - users={} properties={} pending={} active={}",
				total, sale + rent, dto.getPendingPlanRequests(), active);
		return ResponseEntity.ok(dto);
	}

	@Operation(summary = "Paginated user search — filter by plan and/or free-text")
	@GetMapping("/users")
	public ResponseEntity<PagedUsersDto> listUsers(
			@RequestParam(required = false) MasterEnums.PackageEnum plan,
			@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "50") int size) {
		int safeSize = Math.min(Math.max(size, 1), 200);
		String q = (search == null || search.isBlank()) ? null : search.trim();
		Page<com.api.user.User> result =
			userRepository.adminSearch(plan, q, PageRequest.of(Math.max(page, 0), safeSize));
		return ResponseEntity.ok(new PagedUsersDto(
			result.getContent().stream()
				.map(u -> mapper.map(u, UserDto.class))
				.toList(),
			result.getNumber(),
			result.getSize(),
			result.getTotalElements()
		));
	}
}
