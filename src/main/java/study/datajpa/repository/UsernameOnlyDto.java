package study.datajpa.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class UsernameOnlyDto {

    // class를 직접 지정해서 작동하는 Projection의 경우, 생성자의 파라미터 이름으로 분석하기 때문에 오타가 나면 에러가 난다.
    private final String username;
}
