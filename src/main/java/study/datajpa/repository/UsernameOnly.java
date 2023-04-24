package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

/*
*   인터페이스만 정의하면 Spring Data JPA가 구현체를 만들어서 데이터를 담아서 반환한다 -> Close Projection
* */
public interface UsernameOnly {

    // Spring SPL 문법도 지원을 한다 -> Open Projection
    @Value("#{target.username + ' ' + target.age}")
    String getUsername();
}
