package id.ac.itb.logistik.ditlog;

import id.ac.itb.logistik.ditlog.config.JwtFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.TimeZone;

@SpringBootApplication
@RestController
public class DitlogApplication {

	/*
	@Bean
	public FilterRegistrationBean jwtFilter() {
		final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		registrationBean.setFilter(new JwtFilter());
		registrationBean.addUrlPatterns("/");

		return registrationBean;
	}*/

	@GetMapping("/")
	public String init(){
		return "Web Service Ditlog API";
	}

	public static void main(String[] args) {
		TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles"); // e.g. "Europe/Rome"
		TimeZone.setDefault(timeZone);

		SpringApplication.run(DitlogApplication.class, args);
	}
}