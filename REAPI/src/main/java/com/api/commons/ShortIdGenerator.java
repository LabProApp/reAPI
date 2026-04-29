package com.api.commons;

import java.security.SecureRandom;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.persister.entity.EntityPersister;

/**
 * Abstract Hibernate {@link IdentifierGenerator} that produces short, unique,
 * alphanumeric codes for entity records.
 *
 * <p>Generated codes are 6 characters long and drawn from the character set
 * {@code A-Z0-9}, yielding over 2 billion possible values. Uniqueness is
 * guaranteed by checking the target table for collisions and regenerating until
 * a free code is found.</p>
 *
 * <p>The static helper {@link #generateRandomId()} is also used by
 * {@link BaseEntity#prePersist()} to pre-populate the {@code code} column
 * before the entity is first inserted.</p>
 */
public abstract class ShortIdGenerator implements IdentifierGenerator {

    /**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final String ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int LENGTH = 6;

    /**
     * Generates a random alphanumeric string of length {@value #LENGTH}.
     *
     * <p>Uses a cryptographically strong {@link SecureRandom} source so that
     * generated codes are unpredictable and resistant to enumeration.</p>
     *
     * @return a {@value #LENGTH}-character uppercase alphanumeric string
     */
    static String generateRandomId() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(ALPHANUM.charAt(RANDOM.nextInt(ALPHANUM.length())));
        }
        return sb.toString();
    }


    /**
     * Hibernate callback that generates a unique {@code code} value for a new entity row.
     *
     * <p>Repeatedly calls {@link #generateRandomId()} and queries the entity's
     * table until a code that does not already exist is found, then returns it
     * as the identifier value.</p>
     *
     * @param session the current Hibernate session used to execute the uniqueness check
     * @param object  the entity instance for which an identifier is being generated
     * @return a unique alphanumeric code that can be safely inserted into the
     *         {@code code} column of the entity's table
     */
    public String  generate(SharedSessionContractImplementor session, Object object) {

        String code;
        boolean exists;

        // Get table name dynamically
        EntityPersister persister = session.getEntityPersister(object.getClass().getName(), object);
        String tableName = persister.getEntityMetamodel().getName(); // optional fallback
        // If you want actual DB table name:
      //  tableName = persister.getTableName();

        do {
            code = generateRandomId();

            exists = session.createNativeQuery("SELECT 1 FROM " + tableName + " WHERE code = :code")
                    .setParameter("code", code)
                    .uniqueResult() != null;

        } while (exists);

        return code;
    }
}
