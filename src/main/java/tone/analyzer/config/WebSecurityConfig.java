package tone.analyzer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.springframework.data.mongodb.core.aggregation.BooleanOperators.And.and;

/** Created by mozammal on 4/18/17. */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired private UserDetailsService userDetailsService;

  private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.authorizeRequests()
        .antMatchers("/resources*", "/registration")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .csrf()
        .disable()
        .formLogin()
        .loginPage("/login")
        .successHandler(
            new AuthenticationSuccessHandler() {
              @Override
              public void onAuthenticationSuccess(
                  HttpServletRequest request,
                  HttpServletResponse response,
                  Authentication authentication)
                  throws IOException, ServletException {
                redirectStrategy.sendRedirect(request, response, "/chat");
              }
            })
        .permitAll()
        /* .
        failureHandler(new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                httpServletRequest.setAttribute("error", "error");
                httpServletResponse.sendRedirect("/login?error");
            }
        })*/
        .usernameParameter("name")
        .passwordParameter("password")
        .and()
        .logout()
        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
        .logoutSuccessUrl("/login")
        .permitAll()
        .and()
        .authorizeRequests()
        .antMatchers("/js*", "/lib*", "/images*", "/css*", "/chat.html", "/")
        .permitAll()
        .anyRequest()
        .authenticated();

    /*     http
    .csrf().disable()
    .formLogin()
    .loginPage("/chat.html")
    .defaultSuccessUrl("/chat.html")
    .permitAll()
    .and()
    .logout()
    .logoutSuccessUrl("/chat.html")
    .permitAll()
    .and()
    .authorizeRequests()
    .antMatchers("/js*/
    /** ", "/lib */
    /** ", "/images */
    /** ", "/css */
    /**
     * ", "/chat.html", "/").permitAll() .antMatchers("/websocket").hasRole("ADMIN")
     * .anyRequest().authenticated();
     */
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
  }
}
