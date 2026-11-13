package com.mysite.sbb;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

// 일부러 @ActiveProfiles("test")를 붙이지 않은 테스트.
// build.gradle.kts의 systemProperty("spring.profiles.active", "test") 덕분에 메모리 DB를 써야 한다.
@SpringBootTest
class DefaultTestProfileTest {
    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Test
    @DisplayName("@ActiveProfiles 없이도 test 프로파일(메모리 DB)로 실행된다")
    void t1() {
        assertThat(datasourceUrl).startsWith("jdbc:h2:mem:");
    }
}
