package com.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class MasterEnums {

    @Getter
    @RequiredArgsConstructor
    public enum PropertyTypeEnum implements baseEnum {
        PLOT("PLOT", "Residential"),
        APARTMENT("APARTMENT", "Residential"),
        HOUSE("HOUSE", "Residential"),
        BUILDER_FLOOR("BUILDER_FLOOR", "Residential"),
        CONDO("CONDO", "Residential"),

        OFFICE("Office", "Commercial"),
        SHOP("Shop", "Commercial"),
        SHOWROOM("Showroom", "Commercial"),
        PLOT_SHOP("Shop Plot", "Commercial"),

        AGRICULTURAL("Agricultural Land", "Agricultural");

        private final String description;
        private final String category;
    }

    @Getter
    @RequiredArgsConstructor
    public enum UserRoleEnum implements baseEnum {
        ADMIN("Administrator role", "Security"),
        USER("Agent", "Security"),
        CLIENT("Client", "Security");

        private final String description;
        private final String category;
    }

    @Getter
    @RequiredArgsConstructor
    public enum LaunchTypeEnum implements baseEnum {
        READY_TO_MOVE("Ready to Move", "House"),
        PRE_LAUNCH("Pre Launch", "House"),
        RESALE("Resale", "House"),
        UNDER_CONSTRUCTION("Under Construction", "House");

        private final String description;
        private final String category;
    }
}
