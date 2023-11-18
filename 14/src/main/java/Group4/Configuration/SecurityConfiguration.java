package Group4.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import Group4.Security.JwtAuthenticationEntryPoint;
import Group4.Security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	

	@Autowired
	private JwtAuthenticationEntryPoint handler;
	
    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() {
    	return new JwtAuthenticationFilter();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
    @Bean
    CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedMethod("HEAD");
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("PATCH");
        source.registerCorsConfiguration("/**", configuration);
        return new CorsFilter(source);
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    	httpSecurity.cors()
    	.and()
    	.csrf().disable()
    	.exceptionHandling().authenticationEntryPoint(handler).and()
    	.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
    	.authorizeRequests()
    	.antMatchers("/authentication/**")
    	.permitAll()
    	.antMatchers("/admin/**")
    	.permitAll()
    	.antMatchers("/student/**")
    	.permitAll()
    	.antMatchers("/instructor/**")
    	.permitAll()
    	.antMatchers("/departmentManager/**")
    	.permitAll()
    	.anyRequest().permitAll();
    	httpSecurity.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    	return httpSecurity.build();
    }
    /*
    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    	httpSecurity.cors()
    	.and()
    	.csrf().disable()
    	.exceptionHandling().authenticationEntryPoint(handler).and()
    	.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
    	.authorizeRequests()
    	.antMatchers("/authentication/**")
    	.permitAll()
    	.antMatchers("/admin/**")
    	.hasAnyAuthority("admin")
    	.antMatchers("/student/**")
    	.hasAnyAuthority("student")
    	.antMatchers("/instructor/**")
    	.hasAnyAuthority("instructor")
    	.antMatchers("/departmentManager/**")
    	.hasAnyAuthority("DM")
    	.anyRequest().authenticated();
    	httpSecurity.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    	return httpSecurity.build();
    }
     */
}

