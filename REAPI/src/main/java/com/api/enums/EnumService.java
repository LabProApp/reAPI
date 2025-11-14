package com.api.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class EnumService {

	//test files
	int i=0;
	public List<EnumDTO> getEnumValues(Class<? extends Enum<?>> enumClass) {
		return Arrays.stream(enumClass.getEnumConstants()).map(e -> {
			if (e instanceof BaseEnum derivedEnum) {
				return new EnumDTO(e.name(), derivedEnum.getDescription(), derivedEnum.getCategory());
			}
			return new EnumDTO(e.name(), null, null);
		}).collect(Collectors.toList());
	}

	public Map<String, List<EnumDTO>> getAllEnums() {

		Map<String, List<EnumDTO>> allEnums = new LinkedHashMap<>();

		// Loop through all nested enums in MasterEnums
		for (Class<?> innerEnum : MasterEnums.class.getDeclaredClasses()) {
			if (innerEnum.isEnum() && BaseEnum.class.isAssignableFrom(innerEnum)) {

				Object[] constants = innerEnum.getEnumConstants(); // ✅ non-null for enums
				List<EnumDTO> enumValues = new ArrayList<>();

				for (Object constant : constants) {
					BaseEnum e = (BaseEnum) constant;
					EnumDTO dto = new EnumDTO(e.toString(), e.getDescription(), e.getCategory());
					enumValues.add(dto);
				}

				allEnums.put(innerEnum.getSimpleName(), enumValues);
			}
		}

		return allEnums;
	}
}
