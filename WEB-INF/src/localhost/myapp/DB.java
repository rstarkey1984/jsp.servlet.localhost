package localhost.myapp;

import javax.naming.InitialContext;
import javax.naming.Context;
import javax.sql.DataSource;

/**
 * JNDI에서 톰캣 커넥션풀(javax.sql.DataSource)을 1회만 조회해 재사용하는 헬퍼.
 */
public class DB {
    private static volatile DataSource ds;

    public static DataSource getDataSource() {
        if (ds == null) {
            synchronized (DB.class) {
                if (ds == null) {
                    try {
                        Context ic = new InitialContext();
                        // java:comp/env/  접두사는 웹앱 내부 JNDI 네임스페이스
                        ds = (DataSource) ic.lookup("java:comp/env/jdbc/MyDB");
                    } catch (Exception e) {
                        // 배포/부팅 시 NameNotFoundException 발생하면, Context/Resource 위치와 이름을 우선 확인
                        throw new RuntimeException("JNDI lookup failed for 'jdbc/MyDB'. Check Context/Resource naming.", e);
                    }
                }
            }
        }
        return ds;
    }
}