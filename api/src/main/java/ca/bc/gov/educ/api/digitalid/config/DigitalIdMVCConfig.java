package ca.bc.gov.educ.api.digitalid.config;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class DigitalIdMVCConfig implements WebMvcConfigurer {

    @Getter(AccessLevel.PRIVATE)
    private final RequestResponseInterceptor requestResponseInterceptor;

    @Autowired
    public DigitalIdMVCConfig(final RequestResponseInterceptor requestResponseInterceptor){
        this.requestResponseInterceptor = requestResponseInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestResponseInterceptor).addPathPatterns("/**");
    }
}
