package localhost.myapp.ex;

import java.io.IOException;

import com.google.gson.Gson;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import localhost.myapp.dto.ServiceResult;
import localhost.myapp.user.UserService;

@WebServlet("/ex/service")
public class service extends HttpServlet {

    // 서비스 레이어: 비즈니스 로직(검증/처리)을 담당
    private final UserService userService = new UserService();

    // Gson 인스턴스 재사용
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // 1) 응답을 UTF-8로 인코딩 (한글 깨짐 방지)
        resp.setCharacterEncoding("UTF-8");

        // 2) JSON 응답임을 브라우저에게 안내
        resp.setContentType("application/json; charset=UTF-8");

        // 3) 서비스 레이어 호출 (회원가입 로직 실행 예제)
        ServiceResult r = userService.register("test1", "test1", "test@test.com");

        // 4) 응답 객체(ServiceResult)를 JSON 문자열로 변환
        String json = gson.toJson(r);

        // 5) JSON을 HTTP 응답으로 전송
        resp.getWriter().print(json);
    }
}
