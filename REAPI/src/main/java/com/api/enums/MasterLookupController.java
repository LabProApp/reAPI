package com.api.enums;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/master")
public class MasterLookupController {

	private final MasterLookupService service;

	public MasterLookupController(MasterLookupService service) {
		this.service = service;

	}

	/**
	 * Get master lookup values by type and status Example:
	 * /api/mastervalues/getMasterValues?type=STATE&status=ACTIVE
	 * 
	 */
	@GetMapping("/getMasterValues")
	public List<MasterLookupDto> getMasterValues(@RequestParam String type,
			@RequestParam(defaultValue = "ACTIVE") String status) {

		return service.getMasterValues(type, status);
	}

	/**
	 * Get child values by parent master ID Example:
	 * /api/master/1/cities?status=ACTIVE (Fetch all cities for stateId = 1)
	 */
	 @GetMapping("/{parentId}/child")
	    public List<MasterLookupDto> getChildValues(
	            @PathVariable Long parentId,
	            @RequestParam(defaultValue = "ACTIVE") String status,
	            @RequestParam String type) {

	       
	        List<MasterLookupDto> response = service.getChildByMaster(parentId, status, type);

	     

	        return response;
	    }

	/**
     * GET all enums
     */
    
	
	 /**
     * GET specific enum by name
     */
    @GetMapping("/enum/{enumName}")
    public ResponseEntity<List<String>> getEnumByName(
            @PathVariable String enumName) {
        return ResponseEntity.ok(service.getEnumByName(enumName));
    }

    /**
     * GET all enums
     */
    @GetMapping("/allenums")
    public ResponseEntity<Map<String, List<String>>> getAllEnums() {
        return ResponseEntity.ok(service.getAllEnums());
    }

}
