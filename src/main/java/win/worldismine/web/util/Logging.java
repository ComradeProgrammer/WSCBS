package win.worldismine.web.util;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.http.ResponseEntity;

@Aspect
@Component
@Slf4j
public class Logging {
    @Pointcut("execution(* win.worldismine.web.controller..*(..))")
    public void joinPoint() {
    }

    @Around("joinPoint()")
    public Object runAround(ProceedingJoinPoint pj) throws Throwable {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String uri = request.getRequestURI();
        String method = request.getMethod();
        log.info("{}: {}", method, uri);

        Object res = pj.proceed();

        if (res instanceof ResponseEntity<?>) {
            ResponseEntity<?> entity = (ResponseEntity<?>) res;
            if (entity.getBody() instanceof ResponseObject) {
                ResponseObject resp = (ResponseObject) (entity.getBody());
                int code = resp.getCode();
                log.info("{}: [{}] {}  Response Data:{}", method, code, uri, resp);
            }
        }
        return res;
    }

}