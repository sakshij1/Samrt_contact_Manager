//package com.smart.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//@Configuration
//@EnableWebSecurity
//public class MyConfig extends WebSecurityConfiguration{
//
//    @Bean
//    public UserDetailsService getUserDetailsService() {
//        return new UserDetailsServiceImpl();
//    }
//
//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//        daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
//        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
//        return daoAuthenticationProvider;
//    }
//
//    // Configure AuthenticationManager
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider(authenticationProvider());
//    }
//
//    @SuppressWarnings({ "removal", "deprecation" })
//	protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests().requestMatchers("/ADMIN/**").hasRole("ADMIN").requestMatchers("/USER/**").
//        hasRole("USER").requestMatchers("/**").permitAll().and().formLogin().and().csrf().disable();
//
//    }
//
//}
//package com.smart.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class MyConfig {
//
//    @Bean
//    public UserDetailsService getUserDetailsService() {
//        return new UserDetailsServiceImpl();
//    }
//
//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//        daoAuthenticationProvider.setUserDetailsService(getUserDetailsService());
//        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
//        return daoAuthenticationProvider;
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//        return http.getSharedObject(AuthenticationManagerBuilder.class)
//                .authenticationProvider(authenticationProvider())
//                .build();
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .requestMatchers("/ADMIN/**").hasRole("ADMIN")
//                .requestMatchers("/USER/**").hasRole("USER")
//                .requestMatchers("/**").permitAll()
//                .and()
//                .formLogin()
//                .and()
//                .csrf().disable();
//
//        return http.build();
//    }
//}

package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MyConfig {

    @Bean
    public UserDetailsService getUserDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(getUserDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider())
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> {
        	authorize.requestMatchers("/user/**").authenticated();
        	authorize.requestMatchers("/**").permitAll();
        });
        
        http.formLogin(formlogin->{
        	formlogin.loginPage("/login");
        	formlogin.loginProcessingUrl("/authenticate");
        	formlogin.successForwardUrl("/user/index");
        	//formlogin.failureForwardUrl("/login?error=true");
        	formlogin.usernameParameter("email");
        	formlogin.passwordParameter("password");
        	//formlogin.failureHandler(null)/Smart_Contact_Manager/src/main/resources/templates/user_dashboard.html
        });
        http.csrf(AbstractHttpConfigurer::disable);
        // oauth configurations
        
        http.logout(logoutForm -> {
            logoutForm.logoutUrl("/do-logout");
            logoutForm.logoutSuccessUrl("/login?logout=true");
        });
           
        return http.build();
    }
    
}


