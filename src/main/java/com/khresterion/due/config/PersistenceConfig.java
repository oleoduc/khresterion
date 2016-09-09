package com.khresterion.due.config;

import java.beans.PropertyVetoException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;

import com.khresterion.util.log.KhresterionLogger;
import com.khresterion.web.jpa.KengineAuditorAware;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(
    basePackages = {"com.khresterion.web.jpa.repository", "com.khresterion.web.user.repository",
        "com.khresterion.web.admin.repository","com.khresterion.web.settings.repository", "com.khresterion.web.docbase.repository"},
    entityManagerFactoryRef = "entityManagerFactory", transactionManagerRef = "transactionManager")
@EnableTransactionManagement
@EnableJpaAuditing(auditorAwareRef = "kengineAuditorProvider")
@PropertySource("classpath:META-INF/due/hibernate.properties")
public class PersistenceConfig {

  private static final String CLASSPATH_DB_SQL = "flyway.sql.path";
  private static final String HIBERNATE_C3PO_PROVIDER = "hibernate.connection.provider_class";
  private static final String HIBERNATE_C3P0_MAX_CONNECTION_AGE = "hibernate.c3p0.maxConnectionAge";
  private static final String HIBERNATE_C3P0_PREFERRED_TEST_QUERY =
      "hibernate.c3p0.preferredTestQuery";
  private static final String HIBERNATE_C3P0_TEST_CONNECTION_ON_CHECKOUT =
      "hibernate.c3p0.testConnectionOnCheckout";
  private static final String HIBERNATE_C3P0_TEST_CONNECTION_ON_CHECKIN = "hibernate.c3p0.testConnectionOnCheckin";
  private static final String HIBERNATE_C3P0_MAX_STATEMENTS = "hibernate.c3p0.max_statements";
  private static final String HIBERNATE_C3P0_TIMEOUT = "hibernate.c3p0.timeout";
  private static final String HIBERNATE_C3P0_MAX_SIZE = "hibernate.c3p0.max_size";
  private static final String HIBERNATE_C3P0_MIN_SIZE = "hibernate.c3p0.min_size";
  private static final String HIBERNATE_C3P0_IDLE_TEST_PERIOD = "hibernate.c3p0.idle_test_period";
  private static final String HIBERNATE_C3P0_UNRETURNED_TIMEOUT =
      "hibernate.c3p0.unreturnedConnectionTimeout";
  private static final String HIBERNATE_C3P0_UNRETURNED_DEBUG =
      "hibernate.c3p0.debugUnreturnedConnectionStackTraces";
  protected static final String PROPERTY_NAME_DATABASE_DRIVER = "hibernate.connection.driver_class";
  protected static final String PROPERTY_NAME_DATABASE_PASSWORD = "hibernate.connection.password";
  protected static final String PROPERTY_NAME_DATABASE_URL = "hibernate.connection.url";
  protected static final String PROPERTY_NAME_DATABASE_USERNAME = "hibernate.connection.username";
  private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
  private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
  private static final String PROPERTY_NAME_HIBERNATE_SEQUENCE =
      "hibernate.id.new_generator_mappings";
  private static final String PROPERTY_NAME_LAZYLOADING = "hibernate.enable_lazy_load_no_trans";
  private static final String PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
  private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
  private static final String PROPERTY_PACKAGES_TO_SCAN = "entitymanager.packages.to.scan";
  private static final String PROPERTY_ADMINPACKAGES_TO_SCAN =
      "entitymanager.adminpackages.to.scan";
  private static final String PROPERTY_USERPACKAGES_TO_SCAN = "entitymanager.userpackages.to.scan";
  private static final String PROPERTY_SETTINGS_TO_SCAN = "entitymanager.settings.to.scan";
  private static final String PROPERTY_CACHE_PROVIDER = "hibernate.cache.provider_class";
  private static final String PROPERTY_CACHE_REGION_FACTORY_CLASS =
      "hibernate.cache.region.factory_class";
  private static final String PROPERTY_CACHE_QUERY = "hibernate.cache.use_query_cache";
  private static final String PROPERTY_CACHE_SECOND_LEVEL =
      "hibernate.cache.use_second_level_cache";
  private static final String PROPERTY_DEFAULT_SCHEMA = "hibernate.default_schema";
  private static final String PROPERTY_DOCUMENT_TO_SCAN = "doc.packages.to.scan";
  private static final String PROPERTY_NAME_HIBERNATE_JDBCFETCH_SIZE = "hibernate.jdbc.fetch_size";
  private static final String PROPERTY_NAME_HIBERNATE_JDBCBATCH_SIZE = "hibernate.jdbc.batch_size";
  private static final String PROPERTY_NAME_HIBERNATE_CONNECTION_RELEASE_MODE = "hibernate.connection.release_mode";
  private static final KhresterionLogger LOG =
      KhresterionLogger.getLogger(com.khresterion.due.config.PersistenceConfig.class);

  @Configuration
  @Profile(ProfileConstants.POSTGRESQL_DB)
  @PropertySource("classpath:db/postgresql.properties")
  static class DBPostgresql {
  }

  @Configuration
  @Profile(ProfileConstants.MYSQL_DB)
  @PropertySource("classpath:db/mysql.properties")
  static class DBMysql {
  }

  @Configuration
  @Profile(ProfileConstants.SQLSERVER_DB)
  @PropertySource("classpath:db/sqlserver.properties")
  static class DBSQLServer {
  }
  
  @Configuration
  @Profile(ProfileConstants.HSQL_DB)
  @PropertySource("classpath:db/hsql.properties")
  static class HSQLServer {
  }

  @Resource
  Environment env;

  @PostConstruct
  public void onStartup() {
    LOG.warning("Configure persistence layer...");
  }

  @Bean(name = "dataSource")
  public DataSource dataSource() throws IllegalStateException, PropertyVetoException {

    DriverManagerDataSource ds = new DriverManagerDataSource();

    /* use default configuration */
    ds.setDriverClassName(env.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
    ds.setUrl(env.getRequiredProperty(PROPERTY_NAME_DATABASE_URL));
    ds.setUsername(env.getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME));
    ds.setPassword(env.getRequiredProperty(PROPERTY_NAME_DATABASE_PASSWORD));    
    LOG.warning("Data source: " + ds.getUrl());

    return ds;
  }

  @Bean(name = "flyway", initMethod = "migrate")
  public Flyway flyway() throws IllegalStateException, PropertyVetoException {
    Flyway flywaybean = new Flyway();

    flywaybean.setDataSource(dataSource());
    flywaybean.setBaselineOnMigrate(true);
    flywaybean.setLocations(env.getRequiredProperty(CLASSPATH_DB_SQL));
    LOG.warning("flyway baseline version: " + flywaybean.getBaselineVersion());
    return flywaybean;
  }

  @Bean(name = "entityManagerFactory")
  @DependsOn("flyway")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory()
      throws IllegalStateException, PropertyVetoException {

    LocalContainerEntityManagerFactoryBean entityManagerFactoryBean =
        new LocalContainerEntityManagerFactoryBean();

    entityManagerFactoryBean
        .setPersistenceUnitName(com.khresterion.web.jpa.Constants.PERSISTENCE_UNIT);
    
    entityManagerFactoryBean.setDataSource(dataSource());
    
    HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
    hibernateJpaVendorAdapter.setGenerateDdl(false);
    hibernateJpaVendorAdapter.setShowSql(false);    
    /* if the database change do not forget to change also here */
    hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);

    entityManagerFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
    entityManagerFactoryBean.setPackagesToScan(env.getRequiredProperty(PROPERTY_PACKAGES_TO_SCAN),
        env.getRequiredProperty(PROPERTY_ADMINPACKAGES_TO_SCAN),
        env.getRequiredProperty(PROPERTY_USERPACKAGES_TO_SCAN),
        env.getRequiredProperty(PROPERTY_DOCUMENT_TO_SCAN), env.getRequiredProperty(PROPERTY_SETTINGS_TO_SCAN));

    Properties jpaProperties = new Properties();    

    /* use default configuration */
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_SEQUENCE,
        env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SEQUENCE));
    jpaProperties.put(PROPERTY_NAME_LAZYLOADING,
        env.getRequiredProperty(PROPERTY_NAME_LAZYLOADING));
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_JDBCFETCH_SIZE,
        env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_JDBCFETCH_SIZE));
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_JDBCBATCH_SIZE,
        env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_JDBCBATCH_SIZE));
    
    /* driver */ 
    jpaProperties.put(PROPERTY_NAME_DATABASE_DRIVER,
        env.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
    
    jpaProperties.put(PROPERTY_NAME_DATABASE_USERNAME,
        env.getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME));
    jpaProperties.put(PROPERTY_NAME_DATABASE_PASSWORD,
        env.getRequiredProperty(PROPERTY_NAME_DATABASE_PASSWORD));
    jpaProperties.put(PROPERTY_NAME_DATABASE_URL,
        env.getRequiredProperty(PROPERTY_NAME_DATABASE_URL));
    jpaProperties.put(PROPERTY_DEFAULT_SCHEMA, env.getRequiredProperty(PROPERTY_DEFAULT_SCHEMA));

    jpaProperties.put(PROPERTY_NAME_HIBERNATE_DIALECT,
        env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO,
        env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO));
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_FORMAT_SQL,
        env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_FORMAT_SQL));
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL,
        env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));
    
    jpaProperties.put(PROPERTY_CACHE_PROVIDER, env.getRequiredProperty(PROPERTY_CACHE_PROVIDER));
    jpaProperties.put(PROPERTY_CACHE_REGION_FACTORY_CLASS,
        env.getRequiredProperty(PROPERTY_CACHE_REGION_FACTORY_CLASS));
    jpaProperties.put(PROPERTY_CACHE_QUERY, env.getRequiredProperty(PROPERTY_CACHE_QUERY));
    jpaProperties.put(PROPERTY_CACHE_SECOND_LEVEL,
        env.getRequiredProperty(PROPERTY_CACHE_SECOND_LEVEL));
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_CONNECTION_RELEASE_MODE,
        env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_CONNECTION_RELEASE_MODE));

    /* c3p0 */
    jpaProperties.put(HIBERNATE_C3PO_PROVIDER, env.getRequiredProperty(HIBERNATE_C3PO_PROVIDER));
    jpaProperties.put(HIBERNATE_C3P0_PREFERRED_TEST_QUERY,
        env.getRequiredProperty(HIBERNATE_C3P0_PREFERRED_TEST_QUERY));
    jpaProperties.put(HIBERNATE_C3P0_TEST_CONNECTION_ON_CHECKOUT,
        env.getRequiredProperty(HIBERNATE_C3P0_TEST_CONNECTION_ON_CHECKOUT));
    jpaProperties.put(HIBERNATE_C3P0_TEST_CONNECTION_ON_CHECKIN,
        env.getRequiredProperty(HIBERNATE_C3P0_TEST_CONNECTION_ON_CHECKIN));    
    jpaProperties.put(HIBERNATE_C3P0_IDLE_TEST_PERIOD,
        env.getRequiredProperty(HIBERNATE_C3P0_IDLE_TEST_PERIOD));
    jpaProperties.put(HIBERNATE_C3P0_MIN_SIZE, env.getRequiredProperty(HIBERNATE_C3P0_MIN_SIZE));
    jpaProperties.put(HIBERNATE_C3P0_MAX_SIZE, env.getRequiredProperty(HIBERNATE_C3P0_MAX_SIZE));
    jpaProperties.put(HIBERNATE_C3P0_TIMEOUT, env.getRequiredProperty(HIBERNATE_C3P0_TIMEOUT));
    jpaProperties.put(HIBERNATE_C3P0_MAX_STATEMENTS,
        env.getRequiredProperty(HIBERNATE_C3P0_MAX_STATEMENTS));
    jpaProperties.put(HIBERNATE_C3P0_MAX_CONNECTION_AGE,
        env.getRequiredProperty(HIBERNATE_C3P0_MAX_CONNECTION_AGE));
    jpaProperties.put(HIBERNATE_C3P0_UNRETURNED_TIMEOUT,
        env.getRequiredProperty(HIBERNATE_C3P0_UNRETURNED_TIMEOUT));
    jpaProperties.put(HIBERNATE_C3P0_UNRETURNED_DEBUG,
        env.getRequiredProperty(HIBERNATE_C3P0_UNRETURNED_DEBUG));

    entityManagerFactoryBean.setJpaProperties(jpaProperties);

    entityManagerFactoryBean.afterPropertiesSet();

    return entityManagerFactoryBean;
  }

  @Bean(name = "transactionManager")
  public PlatformTransactionManager transactionManager()
      throws IllegalStateException, PropertyVetoException {
    JpaTransactionManager transactionManager = new JpaTransactionManager();

    transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
    transactionManager.setJpaDialect(new HibernateJpaDialect());
    transactionManager.setDataSource(dataSource());
    return transactionManager;
  }

  @Bean(name = "hibernateExceptionTranslator")
  public HibernateExceptionTranslator hibernateExceptionTranslator() {
    return new HibernateExceptionTranslator();
  }

  @Bean(name = "exceptionTranslation")
  public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
    return new PersistenceExceptionTranslationPostProcessor();
  }

  @Bean(name = "kengineAuditorProvider")
  public AuditorAware<String> kengineAuditorProvider() {
    return new KengineAuditorAware();
  }

}
