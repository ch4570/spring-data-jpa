package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

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

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String name); // 컬렉션

    Member findMemberByUsername(String name); // 단건

    Optional<Member> findOptionalByUsername(String name); // 단건 Optional

    // 반환타입을 Page로 하면 TotalCount 쿼리가 같이 나간다.
    // 조회 쿼리와 카운트 쿼리를 분리할 수 있다.
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    // 반환타입을 Slice로 하면 TotalCount 쿼리가 안나간다.
    @Query("select m from Member m where m.age = :age")
    Slice<Member> findByAge2(@Param("age") int age, Pageable pageable);

    @Query("select m from Member m where m.age = :age")
    List<Member> findByAge3(@Param("age") int age, Pageable pageable);

    // @Modifying 애너테이션을 붙여줘야 EntityManager의 executeUpdate를 실행한다.
    // clearAutomatically 옵션을 true로 설정하면, 영속성컨텍스트를 비우지 않아도 자동으로 비워진다.
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // @NamedEntityGraph를 이용해서도 사용이 가능하다.
    @EntityGraph("Member.all")
    List<Member> findEntityGraphByUsername(@Param("username") String username);


    // QueryHints를 이용한 JPA Hint 사용
    // readOnly 힌트를 주고 조회할 경우, 스냅샷을 만들지 않기 때문에 더티체킹이나 수정이 되지 않는다.
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    // select for update -> 비관적 락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
