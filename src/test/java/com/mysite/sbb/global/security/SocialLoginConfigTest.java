package com.mysite.sbb.global.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 실제 구글 키 없이, 설정이 들어오면 소셜 로그인 흐름이 켜지는지만 확인한다. (가짜 client-id 사용, 네트워크 호출 없음)
@ActiveProfiles("test")
@SpringBootTest(properties = {
        "spring.security.oauth2.client.registration.google.client-id=test-client-id",
        "spring.security.oauth2.client.registration.google.client-secret=test-client-secret",
        "spring.security.oauth2.client.registration.google.scope=openid,profile,email"
})
@AutoConfigureMockMvc
class SocialLoginConfigTest {
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("로그인 화면에 Google로 로그인 버튼")
    void t1() throws Exception {
        mvc.perform(get("/user/login"))
                .andExpect(content().string(containsString("/oauth2/authorization/google")))
                .andExpect(content().string(containsString("Google로 로그인")));
    }

    @Test
    @DisplayName("/oauth2/authorization/google -> 구글 로그인 화면으로 리다이렉트(client_id 포함)")
    void t2() throws Exception {
        mvc.perform(get("/oauth2/authorization/google"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", startsWith("https://accounts.google.com/o/oauth2/v2/auth")))
                .andExpect(header().string("Location", containsString("client_id=test-client-id")));
    }
}
