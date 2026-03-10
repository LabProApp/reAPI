package com.api.enums;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class MasterLookupService {

	private final MasterLookupRepository repository;
	private final ModelMapper mapper;

	// Constructor Injection (Recommended by Spring)
	public MasterLookupService(MasterLookupRepository repository, ModelMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	// Generic master lookup
	public List<MasterLookupDto> getMasterValues(String type, String status) {
		return repository.findByTypeIgnoreCaseAndStatusIgnoreCase(type, status).stream()
				.map(entity -> mapper.map(entity, MasterLookupDto.class)).collect(Collectors.toList());
	}

	public List<MasterLookupDto> getChildByMaster(Long parentId, String status, String type) {

		List<MasterLookup> entities = repository.findByParentIdAndStatusAndType(parentId, status, type);

		return entities.stream().map(e -> new MasterLookupDto(e.getId(), e.getValue(), e.getType()))
				.toList();
	}

	/**
	 * Get values of a specific enum by name
	 */
	public List<String> getEnumByName(String enumName) {

		for (Class<?> clazz : MasterEnums.class.getDeclaredClasses()) {
			if (clazz.isEnum() && clazz.getSimpleName().equalsIgnoreCase(enumName)) {
				return getEnumValues((Class<? extends Enum<?>>) clazz);
			}
		}
		throw new IllegalArgumentException("Enum not found: " + enumName);
	}

	/**
	 * Get all enums
	 */
	public Map<String, List<String>> getAllEnums() {

		Map<String, List<String>> result = new LinkedHashMap<>();

		for (Class<?> clazz : MasterEnums.class.getDeclaredClasses()) {
			if (clazz.isEnum()) {
				result.put(clazz.getSimpleName(), getEnumValues((Class<? extends Enum<?>>) clazz));
			}
		}
		return result;
	}

	/**
	 * Convert enum constants to list of strings
	 */
	private List<String> getEnumValues(Class<? extends Enum<?>> enumClass) {
		return Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).toList();
	}
}
