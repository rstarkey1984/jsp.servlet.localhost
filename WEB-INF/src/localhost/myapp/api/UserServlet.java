package localhost.myapp.api;

import localhost.myapp.dao.UserDao;
import localhost.myapp.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * /api/user/* : 회원가입/로그인
 * - POST /api/user/register
 * - POST /api/user/login
 */
@WebServlet("/api/user/*")
public class UserServlet extends HttpServlet {
    private final UserDao dao = new UserDao();
    private final Gson gson = new Gson();

    private JsonObject readJson(HttpServletRequest req) throws IOException {
        BufferedReader br = req.getReader();
        return gson.fromJson(br, JsonObject.class);
    }

    // (선택) 간단 CORS 허용: 필요 시 톰캣 필터로 분리해서 적용 권장
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
            JsonObject json = readJson(req);
            
            if ("/register".equals(path)) {

                // 필수값 검증
                if (json == null || !json.has("id") || !json.has("password") || !json.has("email")) {
                    resp.setStatus(400);
                    resp.getWriter().write("{\"error\":\"invalid_body\"}");
                    return;
                }

                User u = new User();
                u.id = json.get("id").getAsString();
                u.password = json.get("password").getAsString();
                u.email = json.get("email").getAsString();
                dao.insert(u);
                resp.getWriter().write("{\"message\":\"registered\"}");
            } else if ("/login".equals(path)) {

                // 필수값 검증
                if (json == null || !json.has("id") || !json.has("password")) {
                    resp.setStatus(400);
                    resp.getWriter().write("{\"error\":\"invalid_body\"}");
                    return;
                }
                

                boolean ok = dao.login(json.get("id").getAsString(), json.get("password").getAsString());
                resp.getWriter().write("{\"login\":" + ok + "}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
