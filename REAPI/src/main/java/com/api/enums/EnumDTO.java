package com.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Generic Data Transfer Object used to carry a single enum constant name
 * across API boundaries.
 *
 * <p>Typically returned when the caller requests the list of values for a
 * specific enum type, providing the constant's {@code name()} string so that
 * clients do not need to depend on ordinal values.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnumDTO {

    /** The name of the enum constant (as returned by {@link Enum#name()}). */
    private String name;
}
