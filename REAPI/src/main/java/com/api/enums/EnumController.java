package com.api.enums;


import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/enums")
public class EnumController {

    private final EnumService enumService;

    public EnumController(EnumService enumService) {
        this.enumService = enumService;
    }

    @GetMapping("/{enumName}")
    public List<EnumDTO> getEnum(@PathVariable String enumName) throws ClassNotFoundException {
        // Assumes enums are in "com.api.enums" package
        Class<?> clazz = Class.forName("com.api.enums." + enumName);
        if (!clazz.isEnum()) {
            throw new IllegalArgumentException("Not an enum: " + enumName);
        }
        return enumService.getEnumValues((Class<? extends Enum<?>>) clazz);
    }
    @GetMapping("/all")
    public Map<String, List<EnumDTO>> getAllEnums() {
        return enumService.getAllEnums();
    }
}
