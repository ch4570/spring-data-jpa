package study.datajpa.repository;

public interface NestedClosedProjections {

    // 중첩 프로젝션을 사용하면 첫 번째 엔티티는 지정한 필드만 가져오지만, 중첩된 엔티티의 데이터는 전부 가져온다.
    String getUsername();
    TeamInfo getTeam();

    // 중첩 projections
    interface TeamInfo {
        String getName();
    }
}
