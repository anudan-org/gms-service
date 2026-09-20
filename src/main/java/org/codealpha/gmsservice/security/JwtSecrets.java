package org.codealpha.gmsservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Single place the JWT/security-code signing secrets are read from configuration
 * (application.properties / environment). They used to be hardcoded string literals in
 * TokenAuthenticationService, GrantService and ReportService, which is unsafe in a public repo.
 * Fails fast at startup if any is missing, rather than silently signing with an empty key.
 * Static accessors because TokenAuthenticationService is a static utility used from servlet filters.
 */
@Component
public class JwtSecrets {

    private static String login;
    private static String grantCode;
    private static String reportCode;

    public JwtSecrets(@Value("${security.jwt.secret}") String loginSecret,
                      @Value("${security.grant-code.secret}") String grantCodeSecret,
                      @Value("${security.report-code.secret}") String reportCodeSecret) {
        login = require(loginSecret, "security.jwt.secret");
        grantCode = require(grantCodeSecret, "security.grant-code.secret");
        reportCode = require(reportCodeSecret, "security.report-code.secret");
    }

    /** Signs login/session tokens. */
    public static String login() {
        return get(login, "security.jwt.secret");
    }

    /** Signs the secure codes embedded in grant links. */
    public static String grantCode() {
        return get(grantCode, "security.grant-code.secret");
    }

    /** Signs the secure codes embedded in report links. */
    public static String reportCode() {
        return get(reportCode, "security.report-code.secret");
    }

    private static String require(String value, String property) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Required configuration property '" + property + "' is not set");
        }
        return value;
    }

    private static String get(String value, String property) {
        if (value == null) {
            throw new IllegalStateException("JwtSecrets not initialised; '" + property + "' unavailable");
        }
        return value;
    }
}
