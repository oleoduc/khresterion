package com.khresterion.due.config;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.khresterion.web.docbase.config.DocbaseWebConfig;


/**
 * application level configuration
 * 
 * @author nmdev
 *
 */
@Configuration
@EnableScheduling
@Import({PersistenceConfig.class, Booter.class, SecurityConfig.class,DocbaseWebConfig.class })
@ComponentScan(basePackages = {"com.khresterion.due.services","com.khresterion.web.kbuilder",
    "com.khresterion.web.jpa.services",
    "com.khresterion.web.report.services", "com.khresterion.web.user.services",
    "com.khresterion.web.admin.services","com.khresterion.web.settings.services"})
@PropertySources({
  @PropertySource("classpath:META-INF/due/schedule.properties"),
  @PropertySource(value="file:${catalina.base}/deploy.properties", ignoreResourceNotFound=true)
})
public class AppConfig {
    
    @Resource
    Environment env;

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource msgSource = new ResourceBundleMessageSource();

        msgSource.setBasenames("META-INF/due/bundles/i18n",
                "META-INF/kbuilder/bundles/i18n");
        msgSource.setUseCodeAsDefaultMessage(true);
        // msgSource.setDefaultEncoding(UTF_8);

        return msgSource;
    }
}
