package localhost.myapp.common;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * JNDI 기반 DataSource 헬퍼 클래스
 *
 * 역할
 * - 톰캣(JNDI)에 등록된 커넥션 풀(javax.sql.DataSource)을 애플리케이션 전역에서
 * 하나의 정적(static) 인스턴스로 공유한다.
 * - DB 연결은 ds.getConnection() 으로 필요할 때마다 풀에서 빌려 쓰는 방식.
 *
 * 특징
 * - static 초기화 블록에서 딱 한 번 lookup → 캐시
 * - 스레드 안전: JVM이 클래스 로딩 시 static 블록을 단 한 번만 실행하도록 보장
 * - final 키워드로 DataSource 인스턴스 불변성 확보
 */
public class DB {

    /**
     * 톰캣에서 제공하는 DataSource(커넥션 풀) 객체
     *
     * - final: 초기화 이후 값 변경 불가
     * - static: 애플리케이션 전역에서 단 하나의 인스턴스만 사용
     */
    private static final DataSource ds;

    /**
     * static 초기화 블록
     *
     * 동작
     * - 클래스가 JVM에 의해 처음 로딩될 때 단 한 번 실행됨
     * - 여기서 JNDI Lookup을 수행하여 DataSource를 찾고 캐싱함
     *
     * 장점
     * - 스레드-세이프 (JVM 보장)
     * - DB 설정 오류가 있으면 애플리케이션 초기 구동 단계에서 바로 예외 발생 → 문제 조기 발견
     */
    static {
        try {
            // 톰캣이 제공하는 JNDI 초기 컨텍스트
            Context ctx = new InitialContext();

            /**
             * JNDI Lookup
             *
             * "java:comp/env/" :
             * 웹 애플리케이션 전용 JNDI 네임스페이스
             *
             * "jdbc/MyDB" :
             * context.xml 또는 server.xml에 아래처럼 선언한 Resource 이름
             *
             * <Resource name="jdbc/MyDB"
             * type="javax.sql.DataSource"
             * ... />
             */
            ds = (DataSource) ctx.lookup("java:comp/env/jdbc/MyDB");

        } catch (Exception e) {
            /**
             * Lookup 실패 시 발생 가능한 예외
             * - NameNotFoundException : Resource 이름이 틀렸거나 바인딩되지 않았을 때
             * - NoInitialContextException : 컨테이너(JNDI)가 없는 환경에서 실행될 때
             *
             * 예외 발생 시 애플리케이션 초기화 자체를 중단시키는 것이 좋음
             * → DB 연결이 필수인 웹앱의 경우 조기 실패(Fail Fast) 전략이 안정적
             */
            throw new RuntimeException("JNDI DataSource lookup failed: jdbc/MyDB", e);
        }
    }

    /**
     * 유틸리티 클래스이므로 인스턴스 생성 금지
     * (new DB() 하지 못하도록 막음)
     */
    private DB() {
    }

    /**
     * DataSource 전역 접근자
     *
     * @return 톰캣이 관리하는 커넥션 풀 객체(DataSource)
     *
     *         사용 예:
     *         try (Connection con = DB.getDataSource().getConnection()) {
     *         // SQL 작업 수행
     *         }
     */
    public static DataSource getDataSource() {
        return ds;
    }
}
