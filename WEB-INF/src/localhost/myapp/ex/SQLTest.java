package localhost.myapp.ex;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import localhost.myapp.common.DB;

/**
 * /ex/sql 요청이 들어오면 MySQL의 user 테이블을 조회하여
 * 콘솔에 결과를 출력하는 테스트 서블릿
 */
@WebServlet("/ex/sql")
public class SQLTest extends HttpServlet {

    // 톰캣에 등록된 DBCP(Connection Pool) 객체 가져오기 (싱글톤 캐싱)
    private final DataSource ds = DB.getDataSource();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 응답을 JSON 형태로 설정 (출력은 안하지만 관례적으로 맞춰 둠)
        resp.setContentType("application/json; charset=UTF-8");

        select_test();
        // insert_test();

    }

    // private void insert_test() {

    // String sql = "insert into `test`.`board` (`title`, `content`) values (?,?);";

    // try (Connection con = ds.getConnection(); // 1) 커넥션 풀에서 Connection 가져오기
    // PreparedStatement ps = con.prepareStatement(sql) // 2) PreparedStatement 생성
    // ) {

    // System.out.println("DataSource 연결 성공!");

    // // SQL의 첫 번째 ? 에 값 바인딩
    // ps.setString(1, "제목11");
    // ps.setString(2, "내용22");

    // int flag = ps.executeUpdate();
    // System.out.println("=== MYSQL executeUpdate 실행결과 : " + flag + "개 성공 ===");

    // } catch (SQLException e) {
    // // DB 관련 예외 발생 시 스택 출력
    // e.printStackTrace();
    // }

    // }

    private void select_test() {

        // 파라미터 바인딩이 필요한 SQL
        String sql = "SELECT * FROM user WHERE id = ?";

        /**
         * try-with-resources
         * - Connection, PreparedStatement 객체를 자동으로 close()
         * - DB 리소스는 반드시 닫아야 하므로 이런 방식이 가장 안전함
         */
        try (Connection con = ds.getConnection(); // 1) 커넥션 풀에서 Connection 가져오기
                PreparedStatement ps = con.prepareStatement(sql) // 2) PreparedStatement 생성
        ) {
            System.out.println("DataSource 연결 성공!");

            // SQL의 첫 번째 ? 에 값 바인딩
            ps.setString(1, "test");

            /**
             * ResultSet 역시 닫아야 하는 자원이므로
             * 별도의 try-with-resources 블록으로 묶음
             */
            try (ResultSet rs = ps.executeQuery()) { // SELECT 실행 → 결과 집합 반환

                // 실행된 결과셋(ResultSet)의 메타데이터 (컬럼명, 타입 등 정보)
                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount(); // 전체 컬럼 수

                System.out.println("=== MYSQL TABLE 메타데이터 ===");
                // 1번 컬럼부터 columnCount까지 반복
                System.out.print("| ");
                for (int i = 1; i <= columnCount; i++) {
                    String colName = meta.getColumnLabel(i); // SELECT 결과의 컬럼명
                    String colType = meta.getColumnTypeName(i);
                    System.out.print(colName + "(" + colType + ")" + " | ");
                }
                System.out.println("");

                // 결과가 여러 줄일 수도 있으므로 while 사용
                while (rs.next()) {
                    System.out.println("=== MYSQL TABLE 행 ===");

                    // 1번 컬럼부터 columnCount까지 반복
                    for (int i = 1; i <= columnCount; i++) {
                        String colName = meta.getColumnLabel(i); // SELECT 결과의 컬럼명
                        Object value = rs.getObject(i); // 해당 컬럼의 값
                        System.out.println(colName + ": " + value);
                    }

                    System.out.println("idx : " + rs.getInt("idx"));
                    System.out.println("id : " + rs.getString("id"));

                    System.out.println("=======================");
                }
            }

        } catch (SQLException e) {
            // DB 관련 예외 발생 시 스택 출력
            e.printStackTrace();
        }

    }
}