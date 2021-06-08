package com.mythsman.server.advice;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class WebLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(WebLogAspect.class);
    private final static ThreadLocal<LogFormatter> threadLocalLog = ThreadLocal.withInitial(LogFormatter::new);
    private static final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.GetMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.PostMapping)")
    public Object processServlet(ProceedingJoinPoint point) throws Throwable {
        LogFormatter logFormatter = threadLocalLog.get();
        logFormatter.startLog();

        Object[] args = point.getArgs();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        logFormatter.operateLog("Url", request.getRequestURL().toString());
        logFormatter.operateLog("HttpMethod", request.getMethod());

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            headerMap.put(name, value);
        }
        logFormatter.operateLog("Header", headerMap);
        if (args.length > 0) {
            String[] parameterNames = discoverer.getParameterNames(method);
            Map<String, Object> logArgsMap = new HashMap<>();
            for (int pos = 0; pos < args.length; pos++) {
                Object arg = args[pos];
                if (!(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse)) {
                    logArgsMap.put(parameterNames[pos], arg);
                }
            }

            logFormatter.operateLog("RequestBody", logArgsMap);
        }
        Object returnValue = null;
        try {
            returnValue = point.proceed(args);
        } catch (Throwable t) {
            logFormatter.operateLog("Error", t.getMessage());
            throw t;
        } finally {
            logFormatter.operateLog("ResponseBody", returnValue);
            logger.info("[InterfaceLog]: " + logFormatter.endLog());
        }
        return returnValue;
    }
}
