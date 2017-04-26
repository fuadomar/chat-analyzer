package tone.analyzer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.*;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CompositeFilter;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.repository.AccountRepository;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

/** Created by mozammal on 4/18/17. */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableOAuth2Client
@EnableAuthorizationServer
@Order(6)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
    implements AuthorizationServerConfigurer {

  @Autowired private UserDetailsService userDetailsService;

  private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

  private final OAuth2ClientContext oauth2ClientContext;

  private final ClientDetailsService clientDetailsService;

  @Autowired private AccountRepository userRepository;

  @Autowired
  public WebSecurityConfig(
      OAuth2ClientContext oauth2ClientContext, ClientDetailsService clientDetailsService) {
    super();
    this.oauth2ClientContext = oauth2ClientContext;
    this.clientDetailsService = clientDetailsService;
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  public void configure(
      AuthorizationServerEndpointsConfigurer authorizationServerEndpointsConfigurer)
      throws Exception {}

  @Configuration
  @EnableResourceServer
  protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
      http.antMatcher("/chat").authorizeRequests().anyRequest().authenticated();
    }
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.authorizeRequests()
        .antMatchers("/login/**", "/resources/**", "/registration/**")
        .permitAll()
        .antMatchers("/admin/**")
        .hasRole("ADMIN")
        .antMatchers("/chat/**")
        .hasAnyRole("USER", "ADMIN")
        .anyRequest()
        .authenticated()
        .and()
        .csrf()
        .disable()
        .formLogin()
        .loginPage("/login")
        .defaultSuccessUrl("/user")
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
        .usernameParameter("name")
        .passwordParameter("password")
        .and()
        .logout()
        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
        .logoutSuccessUrl("/login")
        .and()
        .csrf()
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .and()
        .authorizeRequests()
        .anyRequest()
        .authenticated()
        .and()
        .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
  }

  @Bean
  FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
    FilterRegistrationBean registration = new FilterRegistrationBean();
    registration.setFilter(filter);
    registration.setOrder(-100);
    return registration;
  }

  @Bean
  @ConfigurationProperties("google")
  ClientResources google() {
    return new ClientResources();
  }

  private Filter ssoFilter() {
    CompositeFilter filter = new CompositeFilter();
    List<Filter> filters = new ArrayList<>();
    filters.add(ssoFilter(google(), "/login/google"));
    filter.setFilters(filters);
    return filter;
  }

  private Filter ssoFilter(ClientResources client, String path) {
    OAuth2ClientAuthenticationProcessingFilter oAuth2ClientAuthenticationFilter =
        new OAuth2ClientAuthenticationProcessingFilter(path);
    OAuth2RestTemplate oAuth2RestTemplate =
        new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
    oAuth2ClientAuthenticationFilter.setRestTemplate(oAuth2RestTemplate);
    UserInfoTokenServices tokenServices =
        new UserInfoTokenServices(
            client.getResource().getUserInfoUri(), client.getClient().getClientId());
    tokenServices.setRestTemplate(oAuth2RestTemplate);
    oAuth2ClientAuthenticationFilter.setTokenServices(tokenServices);
    oAuth2ClientAuthenticationFilter.setAuthenticationSuccessHandler(
        new AuthenticationSuccessHandler() {
          private final Logger log = LoggerFactory.getLogger(AuthenticationSuccessHandler.class);

          @Override
          public void onAuthenticationSuccess(
              HttpServletRequest httpServletRequest,
              HttpServletResponse httpServletResponse,
              Authentication authentication)
              throws IOException, ServletException {

            OAuth2Authentication userPrincipal = (OAuth2Authentication) authentication;
            org.springframework.security.core.Authentication authentication1 =
                userPrincipal.getUserAuthentication();

            Map<String, String> details = new LinkedHashMap<>();
            details = (Map<String, String>) authentication1.getDetails();
            String displayName = details.get("displayName").replaceAll("\\s+", "");
            if (authentication.isAuthenticated()) {
              Account account = userRepository.findByName(displayName);
              if (account == null) userRepository.save(new Account(displayName, ""));
              redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, "/chat");
            }
          }
        });
    return oAuth2ClientAuthenticationFilter;
  }

  @Override
  public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {}

  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    clients.withClientDetails(this.clientDetailsService);
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
  }
}

class ClientResources {

  @NestedConfigurationProperty
  private AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();

  @NestedConfigurationProperty
  private ResourceServerProperties resource = new ResourceServerProperties();

  public AuthorizationCodeResourceDetails getClient() {
    return client;
  }

  public ResourceServerProperties getResource() {
    return resource;
  }
}
