package com.api.enums;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing reference-data endpoints under {@code /api/master}.
 *
 * <p>Provides read-only access to:
 * <ul>
 *   <li>Database-backed master lookup values (cities, states, banks, etc.) via
 *       {@link MasterLookupService}</li>
 *   <li>In-memory enum constant listings derived from {@link MasterEnums}</li>
 * </ul>
 *
 * <p>All write-heavy business logic is delegated to {@link MasterLookupService}.
 */
@RestController
@RequestMapping("/api/master")
public class MasterLookupController {

	private static final Logger log = LoggerFactory.getLogger(MasterLookupController.class);

	private final MasterLookupService service;

	/**
	 * Constructs a {@code MasterLookupController} with the required service dependency.
	 *
	 * @param service the service handling master lookup and enum retrieval
	 */
	public MasterLookupController(MasterLookupService service) {
		this.service = service;
	}

	/**
	 * Returns a list of master lookup values filtered by type and status.
	 *
	 * <p>{@code GET /api/master/getMasterValues?type=CITY&status=ACTIVE}
	 *
	 * @param type   the category type of the lookup values to retrieve (e.g., {@code "CITY"})
	 * @param status the status filter; defaults to {@code "ACTIVE"} if not specified
	 * @return a list of {@link MasterLookupDto} entries matching the given type and status
	 */
	@GetMapping("/getMasterValues")
	public List<MasterLookupDto> getMasterValues(@RequestParam String type,
			@RequestParam(defaultValue = "ACTIVE") String status) {
		log.info("GET /api/master/getMasterValues - Fetching master values [type={}, status={}]", type, status);
		List<MasterLookupDto> values = service.getMasterValues(type, status);
		log.info("GET /api/master/getMasterValues - Returned {} values for type={}", values.size(), type);
		return values;
	}

	/**
	 * Returns child lookup entries belonging to a specific parent entry.
	 *
	 * <p>{@code GET /api/master/{parentId}/child?type=CITY&status=ACTIVE}
	 *
	 * <p>Typical use: fetch all cities under a given state.
	 *
	 * @param parentId the surrogate ID of the parent {@link MasterLookup} entry
	 * @param status   the status filter; defaults to {@code "ACTIVE"} if not specified
	 * @param type     the category type of child entries to retrieve (e.g., {@code "CITY"})
	 * @return a list of {@link MasterLookupDto} child entries
	 */
	@GetMapping("/{parentId}/child")
	public List<MasterLookupDto> getChildValues(@PathVariable Long parentId,
			@RequestParam(defaultValue = "ACTIVE") String status, @RequestParam String type) {
		log.info("GET /api/master/{}/child - Fetching child values [type={}, status={}]", parentId, type, status);
		List<MasterLookupDto> response = service.getChildByMaster(parentId, status, type);
		log.info("GET /api/master/{}/child - Returned {} child values", parentId, response.size());
		return response;
	}

	/**
	 * Returns all constants of a named enum from {@link MasterEnums} as strings.
	 *
	 * <p>{@code GET /api/master/enum/{enumName}}
	 *
	 * @param enumName the simple class name of the desired enum (case-insensitive),
	 *                 e.g., {@code "UserRoleEnum"} or {@code "PropertyTypeEnum"}
	 * @return {@code 200 OK} with a list of constant name strings,
	 *         or {@code 400 Bad Request} if the enum is not found
	 */
	@GetMapping("/enum/{enumName}")
	public ResponseEntity<List<String>> getEnumByName(@PathVariable String enumName) {
		log.info("GET /api/master/enum/{} - Fetching enum values", enumName);
		List<String> values = service.getEnumByName(enumName);
		log.info("GET /api/master/enum/{} - Returned {} values", enumName, values.size());
		return ResponseEntity.ok(values);
	}

	/**
	 * Returns all enums declared inside {@link MasterEnums}, grouped by their simple class name.
	 *
	 * <p>{@code GET /api/master/allenums}
	 *
	 * @return {@code 200 OK} with a map of enum simple-class-name to list of constant name strings
	 */
	@GetMapping("/allenums")
	public ResponseEntity<Map<String, List<String>>> getAllEnums() {
		log.info("GET /api/master/allenums - Fetching all enums");
		Map<String, List<String>> enums = service.getAllEnums();
		log.info("GET /api/master/allenums - Returned {} enum groups", enums.size());
		return ResponseEntity.ok(enums);
	}
}
