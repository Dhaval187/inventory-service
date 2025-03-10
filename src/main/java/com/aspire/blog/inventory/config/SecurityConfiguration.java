package com.aspire.blog.inventory.config;

import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import com.aspire.blog.inventory.security.AuthoritiesConstants;
import com.aspire.blog.inventory.security.jwt.JWTConfigurer;
import com.aspire.blog.inventory.security.jwt.TokenProvider;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private final TokenProvider tokenProvider;
	private final SecurityProblemSupport problemSupport;

	public SecurityConfiguration(TokenProvider tokenProvider, SecurityProblemSupport problemSupport) {
		this.tokenProvider = tokenProvider;
		this.problemSupport = problemSupport;
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.csrf().disable().exceptionHandling().authenticationEntryPoint(problemSupport)
				.accessDeniedHandler(problemSupport).and().headers()
				.contentSecurityPolicy(
						"default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:")
				.and().referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN).and()
				.featurePolicy(
						"geolocation 'none'; midi 'none'; sync-xhr 'none'; microphone 'none'; camera 'none'; magnetometer 'none'; gyroscope 'none'; speaker 'none'; fullscreen 'self'; payment 'none'")
				.and().frameOptions().deny().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
				.antMatchers("/api/authenticate").permitAll().antMatchers("/api/**").authenticated()
				.antMatchers("/management/health").permitAll().antMatchers("/management/info").permitAll()
				.antMatchers("/management/prometheus").permitAll().antMatchers("/management/**")
				.hasAuthority(AuthoritiesConstants.ADMIN).and().apply(securityConfigurerAdapter());
		// @formatter:on
	}

	private JWTConfigurer securityConfigurerAdapter() {
		return new JWTConfigurer(tokenProvider);
	}
}
