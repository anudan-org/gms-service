package org.codealpha.gmsservice;

import org.codealpha.gmsservice.security.ApiInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Properties;
import java.util.concurrent.Executor;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@EnableScheduling
@EnableAsync
@EnableSwagger2
public class GmsServiceApplication {


    public static void main(String[] args) {
        SpringApplication.run(GmsServiceApplication.class, args);
    }


    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("admin@anudan.org");
        mailSender.setPassword("Social@123");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }


    @Configuration
    @EnableAsync
    public class SpringAsyncConfig {

        @Bean(name = "threadPoolTaskExecutor")
        public Executor threadPoolTaskExecutor() {
            return new ThreadPoolTaskExecutor();
        }
    }

    @Configuration
    public class InterceptorConfig extends WebMvcConfigurerAdapter {

        @Autowired
        private ApiInterceptor apiInterceptor;
        @Override
        public void addInterceptors(InterceptorRegistry registry) {

            registry.addInterceptor(apiInterceptor).addPathPatterns("/**").excludePathPatterns("/public/**").excludePathPatterns("/authenticate");
        }
    }

}
