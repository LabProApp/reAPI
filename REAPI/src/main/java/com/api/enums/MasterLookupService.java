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

/**
 * Service layer for master lookup and enum retrieval operations.
 *
 * <p>Provides methods to:
 * <ul>
 *   <li>Query the {@code master_lookup} database table for reference data</li>
 *   <li>Reflect over {@link MasterEnums} nested classes to expose enum constants
 *       at runtime without hard-coding them in controller logic</li>
 * </ul>
 *
 * <p>Dependencies are injected via constructor injection per Spring best practice.
 */
@Service
public class MasterLookupService {

	private static final Logger log = LoggerFactory.getLogger(MasterLookupService.class);

	private final MasterLookupRepository repository;
	private final ModelMapper mapper;

	/**
	 * Constructs a {@code MasterLookupService} with the required collaborators.
	 *
	 * @param repository the JPA repository for {@link MasterLookup} entities
	 * @param mapper     the ModelMapper instance for DTO conversion (available for future use)
	 */
	// Constructor Injection (Recommended by Spring)
	public MasterLookupService(MasterLookupRepository repository, ModelMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	/**
	 * Fetches all master lookup entries of the given type and status.
	 *
	 * <p>Both {@code type} and {@code status} comparisons are case-insensitive.
	 *
	 * @param type   the category type to filter on (e.g., {@code "CITY"})
	 * @param status the status to filter on (e.g., {@code "ACTIVE"})
	 * @return a list of {@link MasterLookupDto} entries; never {@code null}
	 */
	public List<MasterLookupDto> getMasterValues(String type, String status) {
		log.info("getMasterValues - Fetching master values [type={}, status={}]", type, status);
		List<MasterLookup> entities = repository.findByTypeIgnoreCaseAndStatusIgnoreCase(type, status);
		log.info("getMasterValues - Returned {} values for type={}", entities.size(), type);
		return entities.stream().map(e -> new MasterLookupDto(e.getId(), e.getValue(), e.getType())).toList();
	}

	/**
	 * Fetches child lookup entries belonging to the specified parent.
	 *
	 * <p>Typical use: retrieve all {@code CITY} entries whose parent is a given {@code STATE}.
	 *
	 * @param parentId the surrogate ID of the parent {@link MasterLookup} entry
	 * @param status   the status to filter on (e.g., {@code "ACTIVE"})
	 * @param type     the category type of the child entries (e.g., {@code "CITY"})
	 * @return a list of matching {@link MasterLookupDto} entries; never {@code null}
	 */
	public List<MasterLookupDto> getChildByMaster(Long parentId, String status, String type) {
		log.info("getChildByMaster - Fetching child values [parentId={}, type={}, status={}]", parentId, type, status);
		List<MasterLookup> entities = repository.findByParentIdAndStatusAndType(parentId, status, type);
		log.info("getChildByMaster - Returned {} child values for parentId={}", entities.size(), parentId);
		return entities.stream().map(e -> new MasterLookupDto(e.getId(), e.getValue(), e.getType())).toList();
	}

	/**
	 * Returns all constant names of a named enum declared inside {@link MasterEnums}.
	 *
	 * <p>The lookup is performed via reflection using a case-insensitive comparison of
	 * the enum's simple class name against {@code enumName}.
	 *
	 * @param enumName the simple class name of the desired enum (case-insensitive),
	 *                 e.g., {@code "UserRoleEnum"}
	 * @return a list of constant name strings for the matched enum
	 * @throws IllegalArgumentException if no enum with the given name exists in {@link MasterEnums}
	 */
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

	/**
	 * Returns all enums declared inside {@link MasterEnums}, mapped by their simple class name.
	 *
	 * <p>The result map preserves insertion order (backed by {@link LinkedHashMap}) so that
	 * clients receive enums in a consistent, predictable sequence.
	 *
	 * @return a map of enum simple-class-name to list of constant name strings
	 */
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

	/**
	 * Convert enum constants to list of strings
	 *
	 * @param enumClass the enum class whose constants are to be listed
	 * @return a list of constant name strings as returned by {@link Enum#name()}
	 */
	private List<String> getEnumValues(Class<? extends Enum<?>> enumClass) {
		return Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).toList();
	}
}
