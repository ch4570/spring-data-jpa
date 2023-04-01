package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 메서드 이름으로 쿼리를 생성해준다 -> 쿼리 메서드 기능
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // 조회 쿼리는 find...By + Condition(조건)으로 해결한다.
    List<Member> findHelloBy();

    // findTop3 <- Limit을 의미한다 findFirst3, findTop3등을 지원한다.
    List<Member> findTop3HelloBy();


    // 엔티티 타입.쿼리 이름으로 먼저 Named Query를 탐색하고 없다면 쿼리 메서드 기능을 사용한다.
    //    @Query(name = "Member.findByUsername") <- 이 코드는 없어도 동작한다.
    List<Member> findByUsername(@Param("username") String username);

    // @Query로 정의한 쿼리는 이름없는 Named Query와 같다고 볼 수 있다.
    // 애플리케이션 로딩 시점에 JPQL을 전부 파싱하여 보관하기 때문에 오타나 문제가 발생하면 서버가 뜨지 않는 장점이 있다.
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

}
