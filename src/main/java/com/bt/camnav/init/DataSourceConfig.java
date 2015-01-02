package com.bt.camnav.init;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan("com.bt.camnav")
@EnableWebMvc
@EnableTransactionManagement
@PropertySource("classpath:prod-application.properties")
@Profile("prod")
public class DataSourceConfig {

	private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";
	private static final String PROPERTY_NAME_DATABASE_PASSWORD = "db.password";
	private static final String PROPERTY_NAME_DATABASE_URL = "db.url";
	private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.username";

	private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
	private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
	private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
	private static final String PROPERTY_NAME_HIBERNATE_HBM2DLL_AUTO = "hibernate.hbm2ddl.auto";
	private static final String PROPERTY_NAME_HIBERNATE_EJB_NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
	private static final String PROPERTY_NAME_HIBERNATE_CONNECTION_CHARSET = "hibernate.connection.charSet";

	private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN = "entitymanager.packages.to.scan";

	@Resource
	private Environment env;

	@Bean
	public BasicDataSource dataSource() {
		String dbUrl = env.getRequiredProperty(PROPERTY_NAME_DATABASE_URL);
		String username = env.getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME);
		String password = env.getRequiredProperty(PROPERTY_NAME_DATABASE_PASSWORD);
		
		try {
			URI dbUri = new URI(System.getenv("DATABASE_URL"));
			
			username = dbUri.getUserInfo().split(":")[0];
			password = dbUri.getUserInfo().split(":")[1];
			dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':'
					+ dbUri.getPort() + dbUri.getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		BasicDataSource basicDataSource = new BasicDataSource();
		basicDataSource.setUrl(dbUrl);
		basicDataSource.setUsername(username);
		basicDataSource.setPassword(password);
		basicDataSource.setDriverClassName(env.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));

		return basicDataSource;
	}

	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource());
		sessionFactoryBean
				.setPackagesToScan(env
						.getRequiredProperty(PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN));
		sessionFactoryBean.setHibernateProperties(hibProperties());
		return sessionFactoryBean;
	}

	private Properties hibProperties() {
		Properties properties = new Properties();
		properties.put(PROPERTY_NAME_HIBERNATE_DIALECT,
				env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
		properties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL,
				env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));
		properties.put(PROPERTY_NAME_HIBERNATE_FORMAT_SQL,
				env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_FORMAT_SQL));
		properties.put(PROPERTY_NAME_HIBERNATE_HBM2DLL_AUTO,
				env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_HBM2DLL_AUTO));
		properties
				.put(PROPERTY_NAME_HIBERNATE_EJB_NAMING_STRATEGY,
						env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_EJB_NAMING_STRATEGY));
		properties
				.put(PROPERTY_NAME_HIBERNATE_CONNECTION_CHARSET,
						env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_CONNECTION_CHARSET));
		return properties;
	}

}
