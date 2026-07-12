package in.springproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration for serving uploaded static files from the local filesystem.
 *
 * <p>Maps HTTP requests to {@code /uploads/**} to the configured upload directory
 * on disk, allowing profile images and documents to be accessed directly via URL.
 *
 * <p>The upload directory is configurable via {@code app.storage.local.base-path}
 * in {@code application.yml}, defaulting to {@code ./uploads} if not set.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.storage.local.base-path:./uploads}")
    private String uploadPath;

    /**
     * Registers a resource handler that serves files from the local upload directory.
     * The path is resolved relative to the JVM working directory at runtime.
     *
     * @param registry the ResourceHandlerRegistry to configure
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = "file:" + System.getProperty("user.dir") + "/" + uploadPath + "/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(absolutePath);
    }
}
