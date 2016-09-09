package com.khresterion.due.config;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.ui.velocity.VelocityEngineFactory;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.velocity.VelocityConfig;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;
import org.springframework.web.servlet.view.velocity.VelocityView;
import org.springframework.web.servlet.view.velocity.VelocityViewResolver;

import com.khresterion.util.Charsets;
import com.khresterion.util.log.KhresterionLogger;
import com.khresterion.web.kbuilder.jsonmapping.CustomObjectMapper;

/**
 * mvc servlet configuration
 * 
 * @author nmdev
 *
 */
@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan(basePackages = { 
    "com.khresterion.due.controllers","com.khresterion.web.settings.controllers",
    "com.khresterion.web.user.controllers",
    "com.khresterion.web.admin.controllers"})
public class WebConfig extends WebMvcConfigurerAdapter {

    private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=UTF-8";
    private static final String VIEW_RESOLVER_PREFIX = "/WEB-INF/views/";
    private static final String VIEW_RESOLVER_SUFFIX = ".html";
    private static final KhresterionLogger LOG = KhresterionLogger
            .getLogger(com.khresterion.due.config.WebConfig.class);

    @PostConstruct
    public void onStartup(){
        LOG.warning("Configure web MVC layer...");
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations(
                "/static/");
    }

    @Override
    public void configureDefaultServletHandling(
            DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    /*
     * <bean id="velocityConfig"
     * class="org.springframework.web.servlet.view.velocity.VelocityConfigurer">
     * <property name="resourceLoaderPath" value="/WEB-INF/views/" /> </bean>
     * 
     * <bean id="viewResolver"
     * class="org.springframework.web.servlet.view.velocity.VelocityViewResolver"
     * > <property name="cache" value="true" /> <property name="prefix" value=""
     * /> <property name="suffix" value=".html" /> <property
     * name="exposeSpringMacroHelpers" value="true" /> </bean>
     */
    @Bean
    public VelocityConfig velocityConfig() {
        VelocityConfig vconfig = new VelocityConfigurer();

        ((VelocityEngineFactory) vconfig)
                .setResourceLoaderPath(VIEW_RESOLVER_PREFIX);

        return vconfig;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ViewResolver viewResolver() {
        VelocityViewResolver viewResolver = new VelocityViewResolver();

        viewResolver.setViewClass(VelocityView.class);
        viewResolver.setCache(true);
        viewResolver.setPrefix("");
        viewResolver.setSuffix(VIEW_RESOLVER_SUFFIX);
        viewResolver.setExposeSpringMacroHelpers(true);
        viewResolver.setContentType(TEXT_HTML_CHARSET_UTF_8);
        return viewResolver;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
     * #configureMessageConverters(java.util.List)
     */
    @Override
    public void configureMessageConverters(
            List<HttpMessageConverter<?>> converters) {

        converters.add(converter());
        super.configureMessageConverters(converters);
    }

    @Bean
    public MappingJackson2HttpMessageConverter converter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new CustomObjectMapper());
        converter.setPrettyPrint(true);

        return converter;
    }

    @Bean
    MultipartResolver multipartResolver() {
        CommonsMultipartResolver cmpr = new CommonsMultipartResolver();
        cmpr.setMaxUploadSize(2000000);
        cmpr.setDefaultEncoding(Charsets.UTF8);
        return cmpr;
    }

}
