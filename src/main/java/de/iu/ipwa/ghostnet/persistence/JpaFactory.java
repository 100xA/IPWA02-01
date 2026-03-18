package de.iu.ipwa.ghostnet.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public final class JpaFactory {

    private static final String PERSISTENCE_UNIT = "ghostnetfishingPU";
    private static final Object LOCK = new Object();
    private static volatile EntityManagerFactory entityManagerFactory;

    private JpaFactory() {
    }

    public static EntityManager createEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static void shutdown() {
        EntityManagerFactory local = entityManagerFactory;
        if (local != null && local.isOpen()) {
            local.close();
        }
    }

    private static EntityManagerFactory getEntityManagerFactory() {
        EntityManagerFactory local = entityManagerFactory;
        if (local != null && local.isOpen()) {
            return local;
        }
        synchronized (LOCK) {
            local = entityManagerFactory;
            if (local == null || !local.isOpen()) {
                try {
                    local = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, buildOverrides());
                    entityManagerFactory = local;
                } catch (RuntimeException e) {
                    throw new IllegalStateException(
                            "JPA-Initialisierung fehlgeschlagen. "
                                    + "Bitte DB-Zugangsdaten prüfen "
                                    + "(System-Properties: -Ddb.url/-Ddb.user/-Ddb.password "
                                    + "oder Env: GHOSTNET_DB_URL/GHOSTNET_DB_USER/GHOSTNET_DB_PASSWORD).",
                            e
                    );
                }
            }
            return local;
        }
    }

    private static Map<String, Object> buildOverrides() {
        Map<String, Object> overrides = new HashMap<>();

        putIfPresent(overrides, "jakarta.persistence.jdbc.url",
                firstNonBlank(System.getProperty("db.url"), System.getenv("GHOSTNET_DB_URL")));
        putIfPresent(overrides, "jakarta.persistence.jdbc.user",
                firstNonBlank(System.getProperty("db.user"), System.getenv("GHOSTNET_DB_USER")));
        putIfDefined(overrides, "jakarta.persistence.jdbc.password",
                firstDefined(System.getProperty("db.password"), System.getenv("GHOSTNET_DB_PASSWORD")));
        putIfPresent(overrides, "jakarta.persistence.jdbc.driver",
                firstNonBlank(System.getProperty("db.driver"), System.getenv("GHOSTNET_DB_DRIVER")));
        putIfPresent(overrides, "hibernate.dialect",
                firstNonBlank(System.getProperty("db.dialect"), System.getenv("GHOSTNET_DB_DIALECT")));

        return overrides;
    }

    private static void putIfPresent(Map<String, Object> map, String key, String value) {
        if (value != null && !value.isBlank()) {
            map.put(key, value);
        }
    }

    private static void putIfDefined(Map<String, Object> map, String key, String value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (second != null && !second.isBlank()) {
            return second;
        }
        return null;
    }

    private static String firstDefined(String first, String second) {
        if (first != null) {
            return first;
        }
        if (second != null) {
            return second;
        }
        return null;
    }
}
