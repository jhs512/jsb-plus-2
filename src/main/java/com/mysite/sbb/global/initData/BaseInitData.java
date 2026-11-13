package com.mysite.sbb.global.initData;

import com.mysite.sbb.domain.question.question.entity.Question;
import com.mysite.sbb.domain.question.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

// 앱이 시작되자마자 샘플 데이터를 만든다. (14163 14강, 14168 2강)
@Configuration
@RequiredArgsConstructor
public class BaseInitData {
    // 자기 자신을 프록시로 주입받는다.
    // this.work1()으로 직접 부르면 @Transactional이 적용되지 않기 때문에 self.work1()로 호출한다.
    @Autowired
    @Lazy
    private BaseInitData self;
    private final QuestionRepository questionRepository;

    @Bean
    public ApplicationRunner baseInitDataApplicationRunner() {
        return args -> self.work1();
    }

    @Transactional
    public void work1() {
        if (questionRepository.count() > 0) return; // 이미 있으면 다시 만들지 않는다.

        Question q1 = new Question();
        q1.setSubject("sbb가 무엇인가요?");
        q1.setContent("sbb에 대해서 알고 싶습니다.");
        questionRepository.save(q1);

        Question q2 = new Question();
        q2.setSubject("스프링부트 모델 질문입니다.");
        q2.setContent("id는 자동으로 생성되나요?");
        q2.addAnswer("네 자동으로 생성됩니다.");
        questionRepository.save(q2); // q2를 저장할 때 answers에 있는 답변도 함께 INSERT (cascade PERSIST)
    }
}
