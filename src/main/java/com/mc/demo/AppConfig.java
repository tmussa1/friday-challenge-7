package com.mc.demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class AppConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    AppUserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return new SSUDS(userRepository);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception
    {
        //Restricts access to routes
        http.authorizeRequests()
                .antMatchers("/", "/signup", "/search", "/h2/**").permitAll()
                .antMatchers("/sell", "/buy", "/changeavailability").hasAnyAuthority("ADMIN", "USER")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login").permitAll();
        http.csrf().disable();
        http.headers().frameOptions().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth)throws Exception{
        PasswordEncoder theEncoder = encoder();
        auth.inMemoryAuthentication().withUser("DaveWolf").password(encoder().encode("beastmaster")).authorities("ADMIN")
                .and()
                .withUser("auser").password(encoder().encode("apassword")).authorities("USER")
                .and().passwordEncoder(theEncoder);
        auth
                .userDetailsService(userDetailsServiceBean());
    }
}

