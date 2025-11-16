package localhost.myapp.board; // 현재 클래스가 속한 패키지

import localhost.myapp.common.DB; // DB 커넥션 풀(DataSource)을 제공하는 DB 유틸 클래스

import javax.sql.DataSource; // DataSource 인터페이스 (커넥션 풀)
import java.sql.*; // JDBC 관련 클래스 (Connection, PreparedStatement 등)
import java.util.ArrayList; // ArrayList 사용
import java.util.List; // List 인터페이스

/**
 * 게시판 CRUD 전용 DAO 클래스
 * DAO(Data Access Object)는 DB 처리 로직만 담당한다.
 * Controller/Service는 DB 코드를 직접 작성하지 않고 DAO에게 맡긴다.
 */
public class BoardDao {

    private final DataSource ds = DB.getDataSource(); // DB.getDataSource()로 커넥션 풀 객체 생성

    /**
     * 게시글 목록 조회 (페이징)
     * page(1부터 시작), size(한 페이지의 개수)
     */
    public List<Board> findAll(int page, int size) throws SQLException {

        int limit = Math.max(1, Math.min(size, 100)); // size는 최소 1, 최대 100으로 제한
        int offset = Math.max(0, (page - 1) * limit); // OFFSET 계산 (page=1이면 offset=0)

        // DESC 정렬로 최신 글 먼저 → LIMIT/OFFSET으로 페이징
        String sql = "SELECT idx, title, content, reg_date, fk_user_id " +
                "FROM board " +
                "ORDER BY idx DESC " +
                "LIMIT ? OFFSET ?";

        try (Connection con = ds.getConnection(); // 커넥션 풀에서 Connection 하나 가져오기
                PreparedStatement ps = con.prepareStatement(sql)) { // SQL을 준비하는 PreparedStatement 생성

            ps.setInt(1, limit); // 첫 번째 ? = LIMIT
            ps.setInt(2, offset); // 두 번째 ? = OFFSET

            try (ResultSet rs = ps.executeQuery()) { // SELECT 실행 → 결과 ResultSet 반환

                List<Board> list = new ArrayList<>(); // 결과 목록을 담을 리스트

                while (rs.next()) { // 결과행이 있을 때까지 반복

                    Board b = new Board(); // Board 객체 생성

                    b.idx = rs.getInt("idx"); // DB의 idx 컬럼 값을 Board.idx 필드에 저장
                    b.title = rs.getString("title"); // DB title → Board.title
                    b.content = rs.getString("content"); // DB content → Board.content
                    b.regDate = rs.getString("reg_date");// DB reg_date → Board.regDate
                    b.fk_user_id = rs.getString("fk_user_id"); // DB fk_user_id → Board.fk_user_id

                    list.add(b); // 리스트에 객체 추가
                }

                return list; // 최종 목록 반환
            }
        }
    }

    /**
     * 한 개의 게시글 상세 조회
     * idx(PK)를 기준으로 조회
     */
    public Board findById(int idx) throws SQLException {

        String sql = "SELECT * FROM board WHERE idx=?"; // PK 조건 조회

        try (Connection con = ds.getConnection(); // 커넥션 얻기
                PreparedStatement ps = con.prepareStatement(sql)) { // SQL 준비

            ps.setInt(1, idx); // 첫 번째 ?에 idx 바인딩

            try (ResultSet rs = ps.executeQuery()) { // SELECT 실행 → ResultSet 반환

                if (rs.next()) { // 결과가 존재하면

                    Board b = new Board(); // Board 객체 생성

                    b.idx = rs.getInt("idx"); // idx 컬럼 가져와 저장
                    b.fk_user_id = rs.getString("fk_user_id"); // fk_user_id 컬럼 가져와 저장
                    b.title = rs.getString("title"); // title 저장
                    b.content = rs.getString("content"); // content 저장
                    b.regDate = rs.getString("reg_date");// reg_date 저장

                    return b; // 객체 반환
                }

                return null; // 결과가 없을 경우 null 반환
            }
        }
    }

    /**
     * 게시글 등록
     */
    public Integer insert(Board b) throws SQLException {

        String sql = "INSERT INTO board (title, content, fk_user_id) VALUES (?, ?, ?)"; // INSERT SQL

        try (Connection con = ds.getConnection(); // 커넥션 얻기
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // SQL 준비

            ps.setString(1, b.title); // 첫 번째 ? = title
            ps.setString(2, b.content); // 두 번째 ? = content
            ps.setString(3, b.fk_user_id);

            int affected = ps.executeUpdate(); // INSERT 실행

            if (affected == 0) {
                return null; // INSERT 실패
            }

            // 생성된 PK(idx) 가져오기
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // PK (AUTO_INCREMENT)
                }
            }

            return null; // 혹시 키가 없으면 null
        }
    }

    /**
     * 게시글 수정
     */
    public boolean update(Board b) throws SQLException {

        String sql = "UPDATE board SET title=?, content=? WHERE idx=?"; // UPDATE SQL

        try (Connection con = ds.getConnection(); // 커넥션 얻기
                PreparedStatement ps = con.prepareStatement(sql)) { // SQL 준비

            ps.setString(1, b.title); // 1번 파라미터 = 새 title
            ps.setString(2, b.content); // 2번 파라미터 = 새 content
            ps.setInt(3, b.idx); // 3번 파라미터 = 조건 idx

            return ps.executeUpdate() == 1; // 1행이 변경되면 true
        }
    }

    /**
     * 게시글 삭제
     */
    public boolean delete(int idx) throws SQLException {

        String sql = "DELETE FROM board WHERE idx=?"; // DELETE SQL

        try (Connection con = ds.getConnection(); // 커넥션 얻기
                PreparedStatement ps = con.prepareStatement(sql)) { // SQL 준비

            ps.setInt(1, idx); // 첫 번째 ? = 삭제할 idx

            return ps.executeUpdate() == 1; // 삭제 성공이면 true
        }
    }

    /**
     * 전체 게시글 개수 조회
     */
    public int countAll() throws SQLException {

        String sql = "SELECT COUNT(*) FROM board"; // 전체 행 개수 구하는 SQL

        try (Connection con = ds.getConnection(); // 커넥션 얻기
                PreparedStatement ps = con.prepareStatement(sql); // SQL 준비
                ResultSet rs = ps.executeQuery()) { // 실행 후 ResultSet 얻기

            if (rs.next()) { // COUNT(*)는 한 행만 반환됨
                return rs.getInt(1); // 첫 번째 컬럼(int) = 전체 개수
            }

            return 0; // 비정상 상황 대비
        }
    }

}
