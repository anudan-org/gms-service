package org.codealpha.gmsservice;

import java.util.Properties;
import java.util.concurrent.Executor;
import org.codealpha.gmsservice.interceptors.SecurityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class GmsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmsServiceApplication.class, args);
	}


	@Bean
	public JavaMailSender getJavaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.gmail.com");
		mailSender.setPort(587);

		mailSender.setUsername("ranjit.victor@enstratify.com");
		mailSender.setPassword("enstratify123$");

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "true");

		return mailSender;
	}

	/*@Configuration
	public class AdminAPIInterceptorConfigurer extends WebMvcConfigurerAdapter {


		@Autowired
		private SecurityInterceptor securityInterceptor;


		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(securityInterceptor)
					.addPathPatterns("/api/app/**")
					.excludePathPatterns("/api/app/images");
		}
	}*/

	@Configuration
	@EnableAsync
	public class SpringAsyncConfig {

		@Bean(name = "threadPoolTaskExecutor")
		public Executor threadPoolTaskExecutor() {
			return new ThreadPoolTaskExecutor();
		}
	}

}
