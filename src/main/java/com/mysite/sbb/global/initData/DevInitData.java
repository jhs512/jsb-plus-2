package com.mysite.sbb.global.initData;

import com.mysite.sbb.domain.question.category.entity.Category;
import com.mysite.sbb.domain.question.category.service.CategoryService;
import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.question.question.repository.QuestionRepository;
import com.mysite.sbb.domain.user.user.entity.SiteUser;
import com.mysite.sbb.domain.user.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

// 개발(dev) 환경에서만 페이징 확인용 대량 데이터를 만든다. 테스트(test)에는 영향이 없다.
// 교재는 테스트 코드로 개발 DB에 300개를 넣지만, test/dev 프로파일을 분리했으므로 dev 전용 초기 데이터로 만든다.
@Profile("dev")
@Configuration
@RequiredArgsConstructor
public class DevInitData {
    @Autowired
    @Lazy
    private DevInitData self;
    private final QuestionRepository questionRepository;
    private final UserService userService;
    private final CategoryService categoryService;

    @Bean
    @Order(2) // BaseInitData(샘플 2개) 다음에 실행
    public ApplicationRunner devInitDataApplicationRunner() {
        return args -> self.work1();
    }

    @Transactional
    public void work1() {
        if (questionRepository.count() > 2) return;

        SiteUser user1 = userService.getUser("user1");
        Category free = categoryService.getByCode("free");

        for (int i = 1; i <= 300; i++) {
            questionRepository.save(new Question(
                    user1,
                    free,
                    "테스트 데이터입니다:[%03d]".formatted(i),
                    "내용무"
            ));
        }
    }
}
