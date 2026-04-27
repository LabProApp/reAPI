package com.api.enums;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/master")
public class MasterLookupController {

	private final MasterLookupService service;

	public MasterLookupController(MasterLookupService service) {
		this.service = service;
	}

	@GetMapping("/getMasterValues")
	public List<MasterLookupDto> getMasterValues(@RequestParam String type,
			@RequestParam(defaultValue = "ACTIVE") String status) {
		log.info("GET /api/master/getMasterValues - Fetching master values [type={}, status={}]", type, status);
		List<MasterLookupDto> values = service.getMasterValues(type, status);
		log.info("GET /api/master/getMasterValues - Returned {} values for type={}", values.size(), type);
		return values;
	}

	@GetMapping("/{parentId}/child")
	public List<MasterLookupDto> getChildValues(@PathVariable Long parentId,
			@RequestParam(defaultValue = "ACTIVE") String status, @RequestParam String type) {
		log.info("GET /api/master/{}/child - Fetching child values [type={}, status={}]", parentId, type, status);
		List<MasterLookupDto> response = service.getChildByMaster(parentId, status, type);
		log.info("GET /api/master/{}/child - Returned {} child values", parentId, response.size());
		return response;
	}

	@GetMapping("/enum/{enumName}")
	public ResponseEntity<List<String>> getEnumByName(@PathVariable String enumName) {
		log.info("GET /api/master/enum/{} - Fetching enum values", enumName);
		List<String> values = service.getEnumByName(enumName);
		log.info("GET /api/master/enum/{} - Returned {} values", enumName, values.size());
		return ResponseEntity.ok(values);
	}

	@GetMapping("/allenums")
	public ResponseEntity<Map<String, List<String>>> getAllEnums() {
		log.info("GET /api/master/allenums - Fetching all enums");
		Map<String, List<String>> enums = service.getAllEnums();
		log.info("GET /api/master/allenums - Returned {} enum groups", enums.size());
		return ResponseEntity.ok(enums);
	}
}
