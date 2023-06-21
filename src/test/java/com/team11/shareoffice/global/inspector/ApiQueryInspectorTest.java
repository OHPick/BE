package com.team11.shareoffice.global.inspector;

import org.assertj.core.api.NumberAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.ClassBasedNavigableIterableAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ApiQueryInspectorTest {

    @Autowired
    private ApiQueryInspector apiQueryInspector;

    @Autowired
    private ApiQueryCounter apiQueryCounter;

    @Test
    void request_안에서_inspect를_호출하면_QueryCounter를_증가시킨다() {
        // given
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        // when
        apiQueryInspector.inspect("sql");

        // then
        assertEquals(apiQueryCounter.getCount(),1);
    }

}