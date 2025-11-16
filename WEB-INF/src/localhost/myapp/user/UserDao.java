package localhost.myapp.user; // UserDao 클래스가 속한 패키지 선언

import localhost.myapp.common.DB; // DB 커넥션 풀(DataSource) 제공 클래스 import

import javax.sql.DataSource; // DataSource 인터페이스
import java.sql.*; // JDBC 관련 클래스들 import

/**
 * UserDao: user 테이블에 대한 CRUD 중 일부 기능을 담당하는 DAO 클래스
 * - 회원가입(insert)
 * - 아이디로 회원 조회(findById)
 * - 아이디 존재 여부 확인(existsById)
 * - 로그인 검증(login)
 */
public class UserDao {

    private final DataSource ds = DB.getDataSource(); // DB.getDataSource()로 커넥션 풀 객체 생성

    /**
     * 회원가입 (INSERT)
     * 비밀번호는 DB에서 sha2(?, 256)으로 단방향 해싱하여 저장
     */
    public boolean insert(User u) throws SQLException {

        // password는 sha2(?,256)으로 서버가 아닌 MySQL에서 해싱 처리함
        String sql = "INSERT INTO user (id, password, email) VALUES (?, sha2(?, 256), ?)";

        try (Connection con = ds.getConnection(); // 커넥션 풀에서 Connection 가져오기
                PreparedStatement ps = con.prepareStatement(sql)) { // 쿼리 준비

            ps.setString(1, u.id); // 첫 번째 ? = 사용자 ID
            ps.setString(2, u.password); // 두 번째 ? = 평문 password → MySQL sha2()로 해싱됨
            ps.setString(3, u.email); // 세 번째 ? = email

            return ps.executeUpdate() == 1; // INSERT 실행 → 영향받은 행이 1이면 성공
        }
    }

    /**
     * 아이디로 사용자 한 명 조회
     * 회원정보 보여주기/로그인 전 아이디 확인 등에서 사용
     */
    public User findById(String id) throws SQLException {

        String sql = "SELECT * FROM user WHERE id=?"; // 특정 id로 조회하는 SQL

        try (Connection con = ds.getConnection(); // 커넥션 가져오기
                PreparedStatement ps = con.prepareStatement(sql)) { // 쿼리 준비

            ps.setString(1, id); // 첫 번째 ? = 검색할 사용자 ID

            try (ResultSet rs = ps.executeQuery()) { // SELECT 실행 후 결과를 ResultSet으로 받음

                if (rs.next()) { // 조회 결과가 있을 경우

                    User u = new User(); // User DTO 객체 생성

                    u.idx = rs.getInt("idx"); // idx 컬럼 값 저장
                    u.id = rs.getString("id"); // id 저장
                    u.email = rs.getString("email"); // email 저장
                    u.regDate = rs.getString("reg_date"); // 가입일 저장

                    return u; // 완성된 User 객체 반환
                }

                return null; // 조회 결과 없음 → null 반환
            }
        }
    }

    /**
     * 특정 ID가 존재하는지 확인 (회원가입 중복 체크에 사용)
     * idx만 가져오므로 빠르고 가볍다.
     */
    public User existsById(String id) throws SQLException {

        String sql = "SELECT idx FROM user WHERE id=?"; // 존재 여부 조회 → idx만 SELECT

        try (Connection con = ds.getConnection(); // 커넥션 가져오기
                PreparedStatement ps = con.prepareStatement(sql)) { // 쿼리 준비

            ps.setString(1, id); // 첫 번째 ? = 아이디

            try (ResultSet rs = ps.executeQuery()) { // SELECT 실행

                if (rs.next()) { // 결과 존재 시

                    User u = new User(); // User 객체 생성
                    u.idx = rs.getInt("idx"); // idx만 저장하여 빠르게 체크

                    return u; // 존재하면 User 반환
                }

                return null; // 존재하지 않으면 null
            }
        }
    }

    /**
     * 로그인 (ID + PASSWORD 일치 여부 확인)
     * 비밀번호는 SQL에서 sha2(?, 256)을 사용해 비교
     */
    public boolean login(String id, String password) throws SQLException {

        // 입력된 패스워드를 sha2(?,256)으로 해싱해서 DB에 저장된 값과 비교
        String sql = "SELECT COUNT(*) FROM user WHERE id=? AND password=sha2(?, 256)";

        try (Connection con = ds.getConnection(); // 커넥션 가져오기
                PreparedStatement ps = con.prepareStatement(sql)) { // SQL 준비

            ps.setString(1, id); // 첫 번째 ? = ID
            ps.setString(2, password); // 두 번째 ? = 평문 password (SQL에서 해싱됨)

            try (ResultSet rs = ps.executeQuery()) { // SELECT 실행 → COUNT(*) 결과

                rs.next(); // COUNT(*)는 무조건 한 행이므로 next() 한 번 호출
                return rs.getInt(1) == 1; // 결과가 1이면 로그인 성공, 0이면 실패
            }
        }
    }
}
