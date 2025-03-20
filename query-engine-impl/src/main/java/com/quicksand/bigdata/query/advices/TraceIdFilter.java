//package com.quicksand.bigdata.query.advices;
//
//import com.quicksand.bigdata.vars.http.TraceId;
//import com.quicksand.bigdata.vars.http.exts.TraceIdExtBase;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.MDC;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * RestInterceptor
// * <p>
// * com.quicksand.bigdata.chart.engine.rests
// *
// * @author xupei
// * @date 2021/3/25
// */
//@Slf4j
//@Order(-1)
//@Configuration
//public class TraceIdFilter
//        extends HttpFilter {
//
//    @Override
//    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
//        try {
//            long startMills = System.currentTimeMillis();
//            TraceIdExtBase.extendOrMakeRandom(request);
//            MDC.put("costTime", "0");
//            super.doFilter(request, response, chain);
//            long costTime = System.currentTimeMillis() - startMills;
//            MDC.put("costTime", String.valueOf(costTime));
//            log.info("TraceIdFilter handler complete ! costTime:{}`method:{}`path:{}`", costTime, request.getMethod(), request.getRequestURI());
//        } catch (Exception e) {
//            log.error(String.format("TraceIdFilter error! path:【%s】`", request.getRequestURI()), e);
//            throw e;
//        } finally {
//            TraceId.destory();
//            MDC.remove("costTime");
//        }
//    }
//
//    @SuppressWarnings({"rawtypes", "unchecked"})
//    @Bean
//    public FilterRegistrationBean registFilter() {
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setFilter(new TraceIdFilter());
//        registration.addUrlPatterns("/*");
//        registration.setName("restFilter");
//        registration.setOrder(1);
//        return registration;
//    }
//
//}
