package tw.edu.fju.miniclinic.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import tw.edu.fju.miniclinic.interceptor.LoginRequiredInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginRequiredInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
            .addPathPatterns( // 需要登入的路徑
                "/dashboard",
                "/dashboard/**",
                "/api/auth/me",
                "/api/appointments/*/status"
            )
            .excludePathPatterns( // 不需要登入的路徑
                "/login",
                "/logout"
            );
    }
}