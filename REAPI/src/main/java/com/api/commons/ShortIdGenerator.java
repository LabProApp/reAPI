package com.api.commons;

import java.security.SecureRandom;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.persister.entity.EntityPersister;

public abstract class ShortIdGenerator implements IdentifierGenerator {

    private static final String ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int LENGTH = 6;

    static String generateRandomId() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(ALPHANUM.charAt(RANDOM.nextInt(ALPHANUM.length())));
        }
        return sb.toString();
    }


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
