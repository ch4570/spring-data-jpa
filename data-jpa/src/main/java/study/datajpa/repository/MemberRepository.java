package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 메서드 이름으로 쿼리를 생성해준다 -> 쿼리 메서드 기능
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // 조회 쿼리는 find...By + Condition(조건)으로 해결한다.
    List<Member> findHelloBy();

    // findTop3 <- Limit을 의미한다 findFirst3, findTop3등을 지원한다.
    List<Member> findTop3HelloBy();
}
