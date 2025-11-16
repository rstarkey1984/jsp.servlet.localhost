package localhost.myapp.api;
// API 서블릿들을 모아둔 패키지.

import localhost.myapp.user.UserService;
import localhost.myapp.dto.ServiceResult;
// 사용자 비즈니스 로직(UserService), 공통 응답 DTO(ServiceResult) import.

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
// JSON 변환을 위한 Gson 라이브러리.
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
// 서블릿 애노테이션과 HttpServlet, HttpServletRequest/Response 사용.

import java.io.IOException;
// 입출력 예외 처리를 위한 import.

/**
 * /api/user/*
 * - POST /api/user/register : 회원가입
 * - POST /api/user/login : 로그인
 * - POST /api/user/logout : 로그아웃
 */
@WebServlet("/api/user/*")
public class UserAPI extends HttpServlet {
    // HttpServlet 을 상속하여 HTTP 요청을 처리하는 UserAPI 클래스.

    private final Gson gson = new Gson();
    // JSON 직렬화/역직렬화를 위한 Gson 인스턴스.

    private final UserService userService = new UserService();
    // 비즈니스 로직(회원가입, 로그인 등)을 담당하는 UserService.

    /**
     * 요청 바디를 JSON 으로 읽어서
     * - 성공 시: success=true, data 에 JsonObject 저장
     * - 실패 시: success=false, message 에 에러 메시지
     */
    private ServiceResult readJson(HttpServletRequest req) throws IOException {

        // 1. Content-Type 검사
        String contentType = req.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("application/json")) {
            return ServiceResult.fail("Content-Type 은 application/json 이어야 합니다.");
        }

        try {
            JsonElement elem = JsonParser.parseReader(req.getReader());

            // 2. body 비어 있음
            if (elem == null || elem.isJsonNull()) {
                return ServiceResult.fail("요청 body 가 비어 있습니다.");
            }

            // 3. JSON 객체가 아님 (배열/값 등)
            if (!elem.isJsonObject()) {
                return ServiceResult.fail("JSON 객체 형식의 body 가 필요합니다. (예: {\"id\":\"user\"})");
            }

            // 4. 성공 → data 에 JsonObject 넣어서 반환
            return ServiceResult.ok(elem.getAsJsonObject());

        } catch (JsonParseException e) {
            // 5. JSON 문법 에러
            return ServiceResult.fail("잘못된 JSON 형식입니다.");
        }
    }

    // ===== CORS 설정 메서드 =====
    private void setCors(HttpServletResponse resp) {
        // 브라우저에서 다른 Origin 에서 호출할 수 있도록 CORS 허용.
        resp.setHeader("Access-Control-Allow-Origin", "*");
        // 모든 Origin 허용 (*).

        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        // 요청에 포함될 수 있는 헤더 중 Content-Type 을 허용.

        resp.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        // 허용할 HTTP 메서드 목록 지정.
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        // CORS preflight 요청(OPTIONS 메서드)을 처리.
        setCors(resp);
        // CORS 허용 헤더 설정.

        resp.setStatus(204);
        // 응답 본문 없는 성공 응답 204 No Content.
    }

    // ===== 공통 응답 JSON 출력 메서드들 =====

    private void writeJson(HttpServletResponse resp, int status, ServiceResult body)
            throws IOException {
        // HTTP 상태코드와 ServiceResult 구조로 JSON 응답을 내려주는 공통 메서드.
        resp.setStatus(status);
        // HTTP 상태 코드 설정.

        resp.setContentType("application/json; charset=UTF-8");
        // 응답 Content-Type 설정.

        resp.getWriter().write(gson.toJson(body));
        // Gson으로 JSON 문자열로 변환하여 클라이언트에 전송.
    }

    private void ok(HttpServletResponse resp, ServiceResult body) throws IOException {
        // 200 OK + ServiceResult 그대로 응답.
        writeJson(resp, 200, body);
    }

    private void created(HttpServletResponse resp, ServiceResult body) throws IOException {
        // 201 Created + ServiceResult 그대로 응답.
        writeJson(resp, 201, body);
    }

    private void badRequest(HttpServletResponse resp, String msg) throws IOException {
        // 400 Bad Request + 실패 응답.
        writeJson(resp, 400, ServiceResult.fail(msg));
    }

    private void unauthorized(HttpServletResponse resp, String msg) throws IOException {
        // 401 Unauthorized + 실패 응답.
        writeJson(resp, 401, ServiceResult.fail(msg));
    }

    private void notFound(HttpServletResponse resp, String msg) throws IOException {
        // 404 Not Found + 실패 응답.
        writeJson(resp, 404, ServiceResult.fail(msg));
    }

    private void serverError(HttpServletResponse resp, String msg) throws IOException {
        // 500 Internal Server Error + 실패 응답.
        writeJson(resp, 500, ServiceResult.fail(msg));
    }

    // ===== POST (회원가입, 로그인, 로그아웃 공통 처리) =====
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // POST /api/user/* 요청 처리 (회원가입, 로그인, 로그아웃).
        setCors(resp);
        // CORS 허용 헤더 설정.

        resp.setContentType("application/json; charset=UTF-8");
        // 응답 Content-Type 을 JSON + UTF-8 로 설정.

        try {
            String path = req.getPathInfo();
            // /api/user/* 에서 * 부분만 가져옴.
            // 예: /api/user/register → "/register"
            // /api/user/login → "/login"
            // /api/user/logout → "/logout"

            if (path == null) {
                // 경로 정보가 없는 경우 잘못된 요청으로 처리.
                badRequest(resp, "요청 경로를 확인하세요.");
                return;
            }

            // 1) 로그아웃은 body 없이 처리 (JSON 파싱 X)
            if ("/logout".equals(path)) {
                // 세션이 존재할 때만 가져오기 (새로 만들지 않음)
                HttpSession session = req.getSession(false);
                if (session != null) {
                    session.invalidate(); // 세션 완전 종료
                }

                // 로그아웃 성공 응답
                ok(resp, ServiceResult.ok("로그아웃 되었습니다."));
                return;
            }

            // 로그아웃이 아니라면 JSON body 파싱
            ServiceResult jr = readJson(req);
            if (!jr.success) {
                badRequest(resp, jr.message);
                return;
            }
            JsonObject json = (JsonObject) jr.data;

            switch (path) {
                case "/register": {
                    // 회원가입 처리.

                    if (!json.has("id") || !json.has("password") || !json.has("email")) {
                        // 필수 필드 유무 검사.
                        badRequest(resp, "필수 필드(id, password, email)가 없습니다.");
                        return;
                    }

                    String id = json.get("id").getAsString();
                    String password = json.get("password").getAsString();
                    String email = json.get("email").getAsString();

                    ServiceResult r = userService.register(id, password, email);

                    if (r.success) {
                        // 성공 시 201 Created + ServiceResult 전체 전송
                        HttpSession session = req.getSession(); // 여기서만 세션 생성/사용
                        session.setAttribute("id", id);
                        created(resp, r);
                    } else {
                        // 실패 시 400 Bad Request + message 사용.
                        badRequest(resp, r.message);
                    }
                    break;
                }

                case "/login": {
                    // 로그인 처리.

                    if (!json.has("id") || !json.has("password")) {
                        badRequest(resp, "필수 필드(id, password)가 없습니다.");
                        return;
                    }

                    String id = json.get("id").getAsString();
                    String password = json.get("password").getAsString();

                    ServiceResult r = userService.login(id, password);

                    if (r.success) {
                        // 로그인 성공: 200 OK + ServiceResult 그대로 응답.
                        HttpSession session = req.getSession(); // 여기서 세션 생성/사용
                        session.setAttribute("id", id);
                        ok(resp, r);
                    } else {
                        // 로그인 실패: 401 Unauthorized 로 응답.
                        unauthorized(resp, r.message);
                    }
                    break;
                }

                default:
                    // 정의되지 않은 경로로 요청 시.
                    notFound(resp, "지원하지 않는 경로입니다.");
            }

        } catch (Exception e) {
            // 예외 발생 시 서버 오류 처리.
            e.printStackTrace();
            serverError(resp, "서버 오류: " + e.getMessage());
        }
    }
}
