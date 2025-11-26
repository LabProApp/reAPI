package com.api.enums;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mastervalues")
public class MasterLookupController {

	private final MasterLookupService service;

	public MasterLookupController(MasterLookupService service) {
		this.service = service;
	}

	/**
	 * Get master lookup values by type and status Example:
	 * /api/master?type=STATE&status=ACTIVE
	 */
	@GetMapping
	public List<MasterLookup> getMasterValues(@RequestParam String type,
			@RequestParam(defaultValue = "ACTIVE") String status) {

		return service.getMasterValues(type, status);
	}

	/**
	 * Get child values by parent master ID Example:
	 * /api/master/1/children?status=ACTIVE (Fetch all cities for stateId = 1)
	 */
	@GetMapping("/{parentId}/children")
	public List<MasterLookup> getChildValues(@PathVariable Long parentId,
			@RequestParam(defaultValue = "ACTIVE") String status) {

		return service.getChildByMaster(parentId, status);
	}

	
}
