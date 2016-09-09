package com.khresterion.due.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.session.ConcurrentSessionFilter;

import com.google.common.collect.Lists;
import com.khresterion.util.log.KhresterionLogger;
import com.khresterion.web.user.services.KmodelUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  
    @Autowired
    private KmodelUserDetailsService userDetailsService;

    @Bean
    BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder(10);
    }
    
    @Bean(name="sessionRegistry")
    public SessionRegistry sessionRegistry(){
      return new SessionRegistryImpl();
    }
    
    @Bean(name="compositeSas")
    public CompositeSessionAuthenticationStrategy compositeSas(){
      
      List<SessionAuthenticationStrategy> list = Lists.newArrayList();
      
      ConcurrentSessionControlAuthenticationStrategy csa = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry());
      csa.setMaximumSessions(1);
      csa.setExceptionIfMaximumExceeded(false);      
      SessionFixationProtectionStrategy sessionFixation = new SessionFixationProtectionStrategy();
      sessionFixation.setMigrateSessionAttributes(false);
      list.add(csa);
      list.add(sessionFixation);
      list.add(new RegisterSessionAuthenticationStrategy(sessionRegistry()));
      
      CompositeSessionAuthenticationStrategy csas = new CompositeSessionAuthenticationStrategy(list);
      
      return csas;
    }

    @Bean(name="kbAuthFilter")
    public UsernamePasswordAuthenticationFilter kbAuthFilter(AuthenticationManager authenticationManager){
      UsernamePasswordAuthenticationFilter upasf = new UsernamePasswordAuthenticationFilter();
      upasf.setSessionAuthenticationStrategy(compositeSas());
      upasf.setAuthenticationManager(authenticationManager);
      upasf.setAuthenticationFailureHandler(authenticationFailureHandler());
      
      return upasf;
    }
    
    @Bean(name="concurrentSessionFilter")
    public ConcurrentSessionFilter concurrentSessionFilter(){
      ConcurrentSessionFilter csf = new ConcurrentSessionFilter(sessionRegistry(), "/logout");
      
      return csf;
    }        
    
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler(){
      SimpleUrlAuthenticationFailureHandler authFailureHandler = new SimpleUrlAuthenticationFailureHandler("/connect?error");
  
      return authFailureHandler;
    }
    
    /**
     * basic auth needs to be improved
     * 
     * @param auth
     * @throws Exception
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception {

        auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.config.annotation.web.configuration.
     * WebSecurityConfigurerAdapter
     * #configure(org.springframework.security.config
     * .annotation.web.builders.WebSecurity)
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/static/**", "/views/**");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.config.annotation.web.configuration.
     * WebSecurityConfigurerAdapter
     * #configure(org.springframework.security.config
     * .annotation.web.builders.HttpSecurity)
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
                       
        http.authorizeRequests().antMatchers("/member/**").hasAnyRole("MANAGER","USER")
        .antMatchers("/role/**").hasAnyRole("MANAGER","USER")        
        .antMatchers("/login*").anonymous().antMatchers("/connect*").anonymous()
        .anyRequest()
                .authenticated().and().formLogin()
                .loginPage("/connect").failureHandler(authenticationFailureHandler()).permitAll().and().logout().invalidateHttpSession(true)
                .permitAll().and().csrf().disable();
        http.sessionManagement().sessionAuthenticationStrategy(compositeSas()).and()
        .addFilter(kbAuthFilter(super.authenticationManagerBean())).addFilter(concurrentSessionFilter());
    }    

}
