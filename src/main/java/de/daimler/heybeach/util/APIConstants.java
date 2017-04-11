package de.daimler.heybeach.util;

public interface APIConstants {
    String BASE_PATH = "/api/v1/";
    String AUTHENTICATE_URL = BASE_PATH + "authenticate";

    // Spring Boot Actuator services
    String AUTOCONFIG_ENDPOINT = "/autoconfig";
    String BEANS_ENDPOINT = "/beans";
    String CONFIGPROPS_ENDPOINT = "/configprops";
    String ENV_ENDPOINT = "/env";
    String MAPPINGS_ENDPOINT = "/mappings";
    String METRICS_ENDPOINT = "/metrics";
    String SHUTDOWN_ENDPOINT = "/shutdown";

    String[] ACTUATOR_ENDPOINTS = new String[]{AUTOCONFIG_ENDPOINT, BEANS_ENDPOINT, CONFIGPROPS_ENDPOINT,
            ENV_ENDPOINT, MAPPINGS_ENDPOINT, METRICS_ENDPOINT, SHUTDOWN_ENDPOINT};
}
