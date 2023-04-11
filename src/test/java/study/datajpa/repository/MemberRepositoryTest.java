package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member);

    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        Assertions.assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        Assertions.assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        Assertions.assertThat(result.get(0).getAge()).isEqualTo(20);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findHelloBy() {
        List<Member> helloBy = memberRepository.findHelloBy();
        List<Member> helloByTop3 = memberRepository.findTop3HelloBy();
    }

    @Test
    public void testNamedQuery() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        Assertions.assertThat(findMember).isEqualTo(member1);
    }

    @Test
    public void testQuery() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        Assertions.assertThat(result.get(0)).isEqualTo(member1);

    }

    @Test
    public void findUsernameList() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> result = memberRepository.findUsernameList();

        for (String s : result) {
            System.out.println("username = " + s);
        }

    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member1 = new Member("AAA", 10);
        member1.setTeam(team);
        memberRepository.save(member1);


        List<MemberDto> result = memberRepository.findMemberDto();

        for (MemberDto dto : result) {
            System.out.println("dto = " + dto);
        }

    }

    @Test
    public void findByNames() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        for (Member member : result) {
            System.out.println("member = " + member);
        }

    }

    @Test
    public void retrunType() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> listMembers = memberRepository.findListByUsername("AAA");

        // Spring Data JPA는 없는 데이터를 조회할경우, 예외가 터지지 않고 null을 반환한다.
        // JPA 에서는 없는 데이터를 singleResult()로 조회하면 NoResultException이 발생한다.
        Member normalMember = memberRepository.findMemberByUsername("AAA");

        Optional<Member> optionalMember = memberRepository.findOptionalByUsername("AAA");

        System.out.println("findMember = " + optionalMember.orElseThrow(() -> { throw new IllegalStateException("");}));

    }

    @Test
    public void paging() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        // 반환타입을 Page<T>로 하면 TotalCount 쿼리가 같이 나간다.
        Page<Member> members = memberRepository.findByAge(10, pageRequest);

        // Page의 내부 데이터를 map()을 이용해서 DTO로 변환할 수 있다.
        Page<MemberDto> toMap = members.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        // then
        List<Member> content = members.getContent();
        long totalElement = members.getTotalElements();

        Assertions.assertThat(content.size()).isEqualTo(3);
        Assertions.assertThat(totalElement).isEqualTo(5);

        // 현재 페이지
        Assertions.assertThat(members.getNumber()).isEqualTo(0);

        // 총 페이지 수
        Assertions.assertThat(members.getTotalPages()).isEqualTo(2);

        // 첫 페이지인지 여부
        Assertions.assertThat(members.isFirst()).isTrue();

        // 다음 페이지가 있는지 여부
        Assertions.assertThat(members.hasNext()).isTrue();
    }

    @Test
    public void slice() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        // 반환타입을 Slice<T>로 하면 내부적으로 limit + 1로 쿼리가 나간다(카운트 쿼리는 안나간다)
        Slice<Member> members = memberRepository.findByAge2(10, pageRequest);

        // then
        List<Member> content = members.getContent();
        Assertions.assertThat(content.size()).isEqualTo(3);

        // 현재 페이지
        Assertions.assertThat(members.getNumber()).isEqualTo(0);


        // 첫 페이지인지 여부
        Assertions.assertThat(members.isFirst()).isTrue();

        // 다음 페이지가 있는지 여부
        Assertions.assertThat(members.hasNext()).isTrue();
    }

    @Test
    public void selectListLimit() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        // 반환타입을 List<T>로 하면 TotalCount 쿼리가 안나간다.
        // select from member where age = 10 limit 3 으로 쿼리가 나간다.
        List<Member> members = memberRepository.findByAge3(10, pageRequest);

    }

    @Test
    public void bulkUpdate() {

        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        // Bulk 연산은 영속성 컨텍스트를 거치지않고 데이터베이스에 직접 쿼리하기 때문에, 영속성 컨텍스트를 clear 해줘야한다.
        // clear는 JpaRepository에 없으므로, EntityManager를 주입받아서 호출해야한다.
        // Repository에서 @Modifying(clearAutomatically = true) 옵션을 주면 EntityManager를 주입받아서 호출하지 않아도 된다.
        int result = memberRepository.bulkAgePlus(20);
//        em.clear();

        List<Member> findMember = memberRepository.findByUsername("member5");
        Member member5 = findMember.get(0);
        System.out.println("member5 = " + member5);

        // then
        Assertions.assertThat(result).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        // given

        // member1 -> teamA
        // member2 -> teamB

        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when N + 1 문제 발생
        // select Member
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }

    }

    @Test
    public void queryHint() {
        // given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findReadOnlyByUsername(member1.getUsername());
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    public void lockTest() {
        // given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        // when
        List<Member> result = memberRepository.findLockByUsername("member1");

    }

    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void specBasic() {
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("m1", 0, teamA);
        Member member2 = new Member("m2", 0, teamA);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        // when
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        // then
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void queryByExample() {
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("m1", 0, teamA);
        Member member2 = new Member("m2", 0, teamA);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        // when
        //Probe -> 필드에 데이터가 있는 실제 도메인 객체를 의미한다.
        // QueryByExample의 한계는 inner join 까지만 해결이 가능하다.
        Member member = new Member("m1");
        Team team = new Team("teamA");
        member.setTeam(team);

        // 조건절에서 빼야하는 속성을 ignore 할 수 있다.
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("age");

        // Probe와 ExampleMatcher로 구성한다.
        Example<Member> example = Example.of(member, matcher);

        List<Member> result = memberRepository.findAll(example);

        // then
        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("m1");

    }

}
