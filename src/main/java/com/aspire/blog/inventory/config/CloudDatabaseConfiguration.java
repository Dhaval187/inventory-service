package com.aspire.blog.inventory.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.github.jhipster.config.JHipsterConstants;

@Configuration
@Profile(JHipsterConstants.SPRING_PROFILE_CLOUD)
public class CloudDatabaseConfiguration extends AbstractCloudConfig {

	private final Logger log = LoggerFactory.getLogger(CloudDatabaseConfiguration.class);

	private static final String CLOUD_CONFIGURATION_HIKARI_PREFIX = "spring.datasource.hikari";

	@Bean
	@ConfigurationProperties(CLOUD_CONFIGURATION_HIKARI_PREFIX)
	public DataSource dataSource() {
		log.info("Configuring JDBC datasource from a cloud provider");
		return connectionFactory().dataSource();
	}
}
