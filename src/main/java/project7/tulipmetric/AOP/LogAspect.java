package project7.tulipmetric.AOP;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Component // 스프링 빈으로 등록
@Aspect    // AOP Aspect임을 명시
public class LogAspect {

    // @LogExecutionTime 어노테이션이 붙은 메소드 + Service 로 끝나는 빈 에 적용
    // 메서드 실행시간 출력 메서드
//    @Around("@annotation(LogExecutionTime) && bean(*Service)")

    @Around("@annotation(LogExecutionTime)")
    public Object measureExecutionTime(
            ProceedingJoinPoint joinPoint) throws Throwable {

        StopWatch stopWatch = new StopWatch();
        String methodName = joinPoint.getSignature().toShortString();

        try {
            stopWatch.start(methodName);

            // 실제 타겟 메소드 실행 (proceed를 호출해야 원래 로직이 돌아감)
            return joinPoint.proceed();
        } finally {
            stopWatch.stop();

            log.debug("""
                    
                    ┌──────────────────────────────────────────────┐
                    │ ⏱️ 쿼리실행 시간 측정 결과                       │
                    ├──────────────────────────────────────────────┤
                    │ 메서드: {}
                    │ 소요 시간: {} ms
                    └──────────────────────────────────────────────┘""",methodName,stopWatch.getTotalTimeMillis());
        }
    }
}
