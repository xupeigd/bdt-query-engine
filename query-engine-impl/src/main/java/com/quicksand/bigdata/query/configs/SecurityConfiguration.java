package com.quicksand.bigdata.query.configs;

import com.quicksand.bigdata.query.utils.AuthUtil;
import com.quicksand.bigdata.vars.http.TraceId;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.security.VarsSecurityContextPersistenceFilter;
import com.quicksand.bigdata.vars.util.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * SecurityConfiguration
 *
 * @author xupei
 * @date 2020/8/20 18:38
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration
        extends WebSecurityConfigurerAdapter {

    @Value("${query.security.identify.url:\"http://127.0.0.1:9909/monitor?refer=%s\"}")
    String identifyUrl;
    @Resource
    VarsSecurityContextPersistenceFilter varsSecurityContextPersistenceFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //放行登陆的接口
        http.authorizeRequests()
                //监控节点
                .antMatchers("/health").permitAll()
                .antMatchers("/monitor/health").permitAll();
        //前置拦截器(用于加载其他来源的token/安全标识)
        http.addFilterBefore(varsSecurityContextPersistenceFilter, SecurityContextPersistenceFilter.class);
        http.addFilterAfter(new SecurityContextPersistenceFilter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                if (request instanceof HttpServletRequest) {
                    HttpServletRequest httpRequest = (HttpServletRequest) request;
                    HttpServletResponse httpResponse = (HttpServletResponse) response;
                    String traceId = (httpRequest).getHeader(TraceId.FLAG);
                    boolean fromWeb = !StringUtils.hasText(traceId);
                    String requestURI = httpRequest.getRequestURI();
                    if (null != AuthUtil.getUserDetail()
                            || requestURI.equalsIgnoreCase("/monitor/health")
                            || requestURI.equalsIgnoreCase("/health")) {
                        super.doFilter(request, response, chain);
                    } else if (fromWeb) {
                        httpResponse.sendRedirect(String.format(identifyUrl, httpRequest.getRequestURL().toString()).replaceAll("\"", ""));
                    } else {
                        httpResponse.reset();
                        httpResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
                        httpResponse.getWriter().write(JsonUtils.toJsonString(Response.response(HttpStatus.NON_AUTHORITATIVE_INFORMATION, "禁止非授权访问！")));
                        httpResponse.setStatus(HttpStatus.NON_AUTHORITATIVE_INFORMATION.value());
                    }
                } else {
                    super.doFilter(request, response, chain);
                }
            }
        }, VarsSecurityContextPersistenceFilter.class);
        //虚位过滤器（用于扩展）
        http.csrf().disable();
        http.httpBasic().disable();
        http.authorizeRequests()
                .antMatchers("/v2/**").hasAnyRole("ADMIN", "SYS", "DEV")
                .antMatchers("/swagger**").hasAnyRole("ADMIN", "SYS", "DEV")
                .antMatchers("/monitor/**").hasAnyRole("ADMIN", "SYS", "DEV");
        http.authorizeRequests((requests) -> requests.anyRequest().authenticated());
    }

}