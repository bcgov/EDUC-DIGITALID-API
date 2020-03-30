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
    private final DigitalIdRequestInterceptor digitalIdRequestInterceptor;

    @Autowired
    public DigitalIdMVCConfig(final DigitalIdRequestInterceptor digitalIdRequestInterceptor){
        this.digitalIdRequestInterceptor = digitalIdRequestInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(digitalIdRequestInterceptor).addPathPatterns("/**/**/");
    }
}
