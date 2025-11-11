package localhost.myapp.api;

import localhost.myapp.user.UserService;
import localhost.myapp.dto.ServiceResult;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * /api/user/*
 * - POST /api/user/register : 회원가입
 * - POST /api/user/login    : 로그인
 */
@WebServlet("/api/user/*")
public class UserAPI extends HttpServlet {
    private final Gson gson = new Gson();
    private final UserService userService = new UserService();

    private JsonObject readJson(HttpServletRequest req) throws IOException {
        return gson.fromJson(req.getReader(), JsonObject.class);
    }

    // CORS (필요 시 필터로 분리 권장)
    private void setCors(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setCors(resp);
        resp.setStatus(204);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        resp.setContentType("application/json; charset=UTF-8");

        try {
            String path = req.getPathInfo();
            if (path == null) {
                writeJson(resp, 404, false, "요청 경로를 확인하세요.");
                return;
            }

            JsonObject json = readJson(req);
            if (json == null) {
                writeJson(resp, 400, false, "잘못된 요청 형식입니다.");
                return;
            }

            switch (path) {
                case "/register": {
                    if (!json.has("id") || !json.has("password") || !json.has("email")) {
                        writeJson(resp, 400, false, "필수 필드(id, password, email)가 없습니다.");
                        return;
                    }

                    String id = json.get("id").getAsString();
                    String password = json.get("password").getAsString();
                    String email = json.get("email").getAsString();

                    ServiceResult r = userService.register(id, password, email);
                    // 생성 성공은 201
                    resp.setStatus(r.success ? 201 : 400);
                    resp.getWriter().write(gson.toJson(r));
                    break;
                }

                case "/login": {
                    if (!json.has("id") || !json.has("password")) {
                        writeJson(resp, 400, false, "필수 필드(id, password)가 없습니다.");
                        return;
                    }

                    String id = json.get("id").getAsString();
                    String password = json.get("password").getAsString();

                    ServiceResult r = userService.login(id, password);
                    // 성공 200 / 실패 400 (인증 실패를 401로 바꾸고 싶으면 여기서 조정)
                    resp.setStatus(r.success ? 200 : 400);
                    resp.getWriter().write(gson.toJson(r));
                    break;
                }

                default:
                    writeJson(resp, 404, false, "지원하지 않는 경로입니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            writeJson(resp, 500, false, "서버 오류: " + e.getMessage());
        }
    }

    private void writeJson(HttpServletResponse resp, int status, boolean success, String msg) throws IOException {
        resp.setStatus(status);
        ServiceResult r = new ServiceResult();
        r.success = success;
        r.message = msg;
        resp.getWriter().write(gson.toJson(r));
    }
}
