package lk.icbt.dentalclinic.web;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * JAX-RS application configuration. Tells Jersey which package to scan
 * for @Path-annotated resource classes, and enables Jackson so Java
 * objects (Appointment, Bill, etc.) are automatically converted to/from JSON.
 */
public class ApplicationConfig extends ResourceConfig {

    public ApplicationConfig() {
        packages("lk.icbt.dentalclinic.web");
        register(JacksonFeature.class);
        register(ObjectMapperProvider.class);
    }
}