package com.api.enums;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MasterLookupService {

	private static final Logger log = LoggerFactory.getLogger(MasterLookupService.class);

	private final MasterLookupRepository repository;
	private final ModelMapper mapper;

	
	// Constructor Injection (Recommended by Spring)
	public MasterLookupService(MasterLookupRepository repository, ModelMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	
	public List<MasterLookupDto> getMasterValues(String type, String status) {
		log.info("getMasterValues - Fetching master values [type={}, status={}]", type, status);
		List<MasterLookup> entities = repository.findByTypeIgnoreCaseAndStatusIgnoreCase(type, status);
		log.info("getMasterValues - Returned {} values for type={}", entities.size(), type);
		return entities.stream().map(e -> new MasterLookupDto(e.getId(), e.getValue(), e.getType())).toList();
	}

	
	public List<MasterLookupDto> getChildByMaster(Long parentId, String status, String type) {
		log.info("getChildByMaster - Fetching child values [parentId={}, type={}, status={}]", parentId, type, status);
		List<MasterLookup> entities = repository.findByParentIdAndStatusAndType(parentId, status, type);
		log.info("getChildByMaster - Returned {} child values for parentId={}", entities.size(), parentId);
		return entities.stream().map(e -> new MasterLookupDto(e.getId(), e.getValue(), e.getType())).toList();
	}

	
	public List<String> getEnumByName(String enumName) {
		log.info("getEnumByName - Fetching enum: {}", enumName);
		for (Class<?> clazz : MasterEnums.class.getDeclaredClasses()) {
			if (clazz.isEnum() && clazz.getSimpleName().equalsIgnoreCase(enumName)) {
				List<String> values = getEnumValues((Class<? extends Enum<?>>) clazz);
				log.info("getEnumByName - Returned {} values for enum={}", values.size(), enumName);
				return values;
			}
		}
		log.warn("getEnumByName - Enum not found: {}", enumName);
		throw new IllegalArgumentException("Enum not found: " + enumName);
	}

	
	public Map<String, List<String>> getAllEnums() {
		log.info("getAllEnums - Fetching all enums");
		Map<String, List<String>> result = new LinkedHashMap<>();
		for (Class<?> clazz : MasterEnums.class.getDeclaredClasses()) {
			if (clazz.isEnum()) {
				result.put(clazz.getSimpleName(), getEnumValues((Class<? extends Enum<?>>) clazz));
			}
		}
		log.info("getAllEnums - Returned {} enum groups", result.size());
		return result;
	}

	
	private List<String> getEnumValues(Class<? extends Enum<?>> enumClass) {
		return Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).toList();
	}
}
