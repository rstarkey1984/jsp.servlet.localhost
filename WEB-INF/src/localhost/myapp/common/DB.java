package localhost.myapp.common;

import javax.naming.InitialContext;
import javax.naming.Context;
import javax.sql.DataSource;

/**
 * DB DataSource 헬퍼 (JNDI 기반, Lazy-init + Double-Checked Locking)
 *
 * 역할
 * - 톰캣(JNDI)에 등록된 커넥션 풀(javax.sql.DataSource)을 최초 1회만 조회(lookup)하고,
 *   이후에는 같은 인스턴스를 재사용한다(캐싱).
 *
 * 왜 필요한가
 * - 매 요청마다 InitialContext.lookup()을 호출하는 것은 불필요한 오버헤드가 될 수 있다.
 * - 애플리케이션 전역에서 동일한 DataSource를 안전하게 공유하려면 스레드-세이프한 캐시가 유용하다.
 *
 * 전제
 * - 톰캣의 Context 설정에 아래와 같이 Resource가 정의되어 있어야 한다.
 *   <Resource name="jdbc/MyDB" ... type="javax.sql.DataSource" ... />
 * - (선택) web.xml에 <resource-ref>로 res-ref-name/res-type 매핑을 선언하면
 *   컨테이너가 java:comp/env 네임스페이스에 안전하게 바인딩한다.
 *
 * 주의 사항
 * - 실제 커넥션(Connection) 객체는 여기서 만들지 않는다.
 *   DataSource는 '풀'의 핸들이고, Connection은 필요할 때마다 ds.getConnection()으로 빌려 쓰고 닫는다.
 * - DataSource 자체는 닫을 필요가 없다(컨테이너가 라이프사이클 관리).
 */
public class DB {

    /**
     * DataSource 캐시 필드.
     *
     * - volatile:
     *   더블 체크 락킹(DCL) 패턴에서 가시성/재정렬 문제를 방지하기 위해 필요.
     *   (JMM 상 안전한 DCL을 보장하기 위한 핵심 키워드)
     */
    private static volatile DataSource ds;

    /**
     * 애플리케이션 전역 DataSource 접근자.
     *
     * 동작
     * 1) 최초 호출 시에만 JNDI lookup 수행(느긋한 초기화, Lazy Initialization).
     * 2) 이후 호출은 캐시된 ds를 즉시 반환(오버헤드 최소화).
     *
     * 스레드-세이프
     * - DCL(Double-Checked Locking) + synchronized 블록으로 초기화 경쟁 방지.
     *
     * @return 톰캣이 관리하는 javax.sql.DataSource (커넥션 풀 핸들)
     * @throws RuntimeException 초기화 실패(예: 네이밍 불일치, 컨텍스트 미바인딩) 시 래핑하여 던짐
     */
    public static DataSource getDataSource() {
        // 1차 체크: 이미 초기화된 경우 동기화 없이 빠르게 반환
        if (ds == null) {
            synchronized (DB.class) {
                // 2차 체크: 여러 스레드가 동시 접근했더라도 최초 1회만 초기화 보장
                if (ds == null) {
                    try {
                        // JNDI 초기 컨텍스트
                        Context ic = new InitialContext();

                        /*
                         * java:comp/env/ 접두사
                         * - 웹 애플리케이션마다 분리된 "컴포넌트 전용" JNDI 네임스페이스.
                         * - <resource-ref>를 사용하면 res-ref-name으로 이 네임스페이스에 매핑된다.
                         * - 여기서는 "jdbc/MyDB"라는 이름으로 바인딩된 DataSource를 찾는다.
                         *
                         * Lookup 이름 정리
                         * - 애플리케이션 코드에서는 보통 "java:comp/env/jdbc/MyDB"로 조회.
                         * - 톰캣 Context의 <Resource name="jdbc/MyDB" .../> 와 일치해야 한다.
                         */
                        ds = (DataSource) ic.lookup("java:comp/env/jdbc/MyDB");

                        /*
                         * 여기서 DataSource 인스턴스는 '커넥션 풀 관리 객체'이지,
                         * 실제 DB 커넥션을 바로 만드는 것은 아니다.
                         * 실제 커넥션은 아래와 같이 필요 시마다 획득:
                         *
                         * try (Connection con = ds.getConnection()) {
                         *     // SQL 작업
                         * } // con.close() 호출로 커넥션 '반납' (풀로 복귀)
                         */

                    } catch (Exception e) {
                        /*
                         * 대표적인 실패 케이스
                         * - javax.naming.NameNotFoundException:
                         *   "jdbc/MyDB" 이름으로 바인딩된 리소스를 찾지 못했을 때.
                         *   → Context/ROOT.xml(or context.xml)의 <Resource name="jdbc/MyDB".../> 확인
                         *   → web.xml의 <resource-ref> res-ref-name 일치 여부 확인
                         *   → 톰캣 재기동 필요 여부 확인
                         *
                         * - NoInitialContextException:
                         *   컨테이너 외부(예: 단위 테스트)에서 실행했고 JNDI가 구성되지 않았을 때.
                         *
                         * 복구 전략
                         * - 배포 환경: 설정/이름 오타 수정 후 재배포
                         * - 테스트 환경: DataSource를 직접 주입(팩토리/DI), 또는 임베디드 컨테이너 사용
                         */
                        throw new RuntimeException(
                            "JNDI lookup failed for 'jdbc/MyDB'. " +
                            "Check <Resource name> and <resource-ref> naming/binding in Tomcat Context.",
                            e
                        );
                    }
                }
            }
        }
        return ds;
    }
}