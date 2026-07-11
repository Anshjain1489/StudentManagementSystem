ackage in.springproject.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3.0 / Swagger UI configuration for the Student Management System API.
 *
 * <p>Configures:
 * <ul>
 *   <li>API metadata (title, version, contact, license)</li>
 *   <li>Server environments (local and production)</li>
 *   <li>JWT Bearer token security scheme applied globally to all endpoints</li>
 * </ul>
 *
 * <p>Access Swagger UI at: <a href="http://localhost:8080/swagger-ui.html">http://localhost:8080/swagger-ui.html</a>
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Student Management System API",
        version = "1.0.0",
        description = "Enterprise-grade REST API for managing students, teachers, courses, " +
                      "attendance, exams, fees, and more.",
        contact = @Contact(
            name = "SMS Support",
            email = "support@sms.edu"
        ),
        license = @License(
            name = "MIT",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Local Development Server"),
        @Server(url = "https://sms-api.onrender.com", description = "Production Server")
    },
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT Bearer Token Authentication. Use /api/v1/auth/login to obtain a token.",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {
    // No bean definitions needed — configuration is annotation-driven via springdoc-openapi.
}
