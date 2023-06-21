package com.team11.shareoffice.global.inspector;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApiQueryInspector implements StatementInspector {

    private final ApiQueryCounter apiQueryCounter;
    private static ThreadLocal<ApiQueryCounter> queryCount = new ThreadLocal<>();
    public ApiQueryInspector(final ApiQueryCounter apiQueryCounter) {
        this.apiQueryCounter = apiQueryCounter;
    }
    void start() {
        queryCount.set(new ApiQueryCounter(0, System.currentTimeMillis()));
    }

    ApiQueryCounter getApiQueryCounter(){
        return queryCount.get();
    }

    public void clearCounter() {
        queryCount.remove();
    }
    @Override
    public String inspect(String sql) {
        ApiQueryCounter counter = queryCount.get();
        if (counter != null) {
            counter.increaseCount();
        }
        return sql;
    }
}