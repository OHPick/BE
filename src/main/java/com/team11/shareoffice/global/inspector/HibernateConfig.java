package com.team11.shareoffice.global.inspector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {

    @Autowired
    private ApiQueryInspector apiQueryInspector;
    //로컬환경에서 작동할 때만 주석 풀어주세요.
    //ApiQueryInspector를 hibernate properties에 추가 해줍니다.
//    @Bean
//    public HibernatePropertiesCustomizer hibernateCustomizer() {
//        return (properties) -> properties.put(AvailableSettings.STATEMENT_INSPECTOR, apiQueryInspector);
//    }

}
