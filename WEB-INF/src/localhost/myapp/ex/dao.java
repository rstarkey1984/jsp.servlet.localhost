package localhost.myapp.ex;

import java.io.IOException;
import java.sql.SQLException;

import com.google.gson.Gson;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import localhost.myapp.user.User;
import localhost.myapp.user.UserDao;

@WebServlet("/ex/dao")
public class dao extends HttpServlet {

    // 데이터베이스 접근 객체(DAO)
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // 1) 응답 인코딩 설정 (한글 깨짐 방지)
        resp.setCharacterEncoding("UTF-8");

        // 2) JSON 응답임을 브라우저에게 알림
        resp.setContentType("application/json; charset=UTF-8");

        try {
            // 3) DB에서 사용자 정보 조회
            User u = userDao.findById("1234");

            // 4) 조회된 User 객체를 JSON 문자열로 변환
            String json = new Gson().toJson(u);

            // 5) JSON 응답 출력
            resp.getWriter().print(json);

        } catch (SQLException e) {
            // 6) DB 예외 발생 시 서버 로그 출력
            e.printStackTrace();

            // 7) 클라이언트에게 오류 응답(JSON) 보내기
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print("{\"error\": \"DB 조회 중 오류가 발생했습니다.\"}");
        }
    }
}