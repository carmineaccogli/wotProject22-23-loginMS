package it.safesiteguard.ms.loginms_ssguard.security;

import java.util.List;

public class SecurityConstants {
    public static final String JWT_SECRET = "seedseedseedseedseedseedseedseedseedseedseed"; // sarà utilizzato per l'algoritmo di firma

    public static final String ISSUER ="http://loginService:8080";

    public static final String THIS_MICROSERVICE ="http://loginService:8080";

    public static final List<String> AUDIENCE = List.of("http://loginService:8080");
}
