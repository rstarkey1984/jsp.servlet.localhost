package localhost.myapp.api;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import localhost.myapp.board.Board;
import localhost.myapp.board.BoardService;
import localhost.myapp.dto.ServiceResult;

/**
 * /api/board/*
 *
 * - GET /api/board : 게시글 목록 (page, size)
 * - GET /api/board/{idx} : 게시글 상세
 * - POST /api/board : 게시글 작성
 * - PUT /api/board/{idx} : 게시글 수정 (본인 글만)
 * - DELETE /api/board/{idx} : 게시글 삭제 (본인 글만)
 *
 * 모든 응답은 ServiceResult JSON 구조를 사용한다.
 */
@WebServlet("/api/board/*")
public class BoardAPI extends HttpServlet {

    /** 게시판 비즈니스 로직 */
    private final BoardService service = new BoardService();

    /** JSON 변환기 */
    private final Gson gson = new Gson();

    /**
     * 요청 바디를 JSON으로 읽고, 성공/실패를 ServiceResult로 감싸서 반환
     * - 성공 시: success=true, data에 JsonObject 저장
     * - 실패 시: success=false, message에 오류 설명
     */
    private ServiceResult readJson(HttpServletRequest req) throws IOException {

        // 1. Content-Type 검사
        String contentType = req.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("application/json")) {
            return ServiceResult.fail("Content-Type 은 application/json 이어야 합니다.");
            // 호출한 쪽에서 jr.success 보고 400으로 응답할 것
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

            // 4. 성공 → data에 JsonObject 넣어서 반환
            JsonObject obj = elem.getAsJsonObject();
            return ServiceResult.ok(obj);

        } catch (JsonParseException e) {
            // 5. JSON 문법 에러
            return ServiceResult.fail("잘못된 JSON 형식입니다.");
        }
    }

    /** CORS 헤더 설정 */
    private void setCors(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
    }

    /** OPTIONS 요청(CORS preflight) 처리 */
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setCors(resp);
        resp.setStatus(204); // No Content
    }

    // ===== 응답 공통 처리 메서드들 =====

    /** 상태코드 + JSON 응답 출력 */
    private void writeJson(HttpServletResponse resp, int status, ServiceResult body)
            throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write(gson.toJson(body));
    }

    /** 200 OK + data 응답 */
    private void ok(HttpServletResponse resp, Object data) throws IOException {
        writeJson(resp, 200, ServiceResult.ok(data));
    }

    /** 201 Created 응답 */
    private void created(HttpServletResponse resp, ServiceResult result) throws IOException {
        writeJson(resp, 201, result);
    }

    /** 400 Bad Request */
    private void badRequest(HttpServletResponse resp, String msg) throws IOException {
        writeJson(resp, 400, ServiceResult.fail(msg));
    }

    /** 404 Not Found */
    private void notFound(HttpServletResponse resp, String msg) throws IOException {
        writeJson(resp, 404, ServiceResult.fail(msg));
    }

    /** 500 Internal Server Error */
    private void serverError(HttpServletResponse resp, String msg) throws IOException {
        writeJson(resp, 500, ServiceResult.fail(msg));
    }

    // ============================================================
    // GET (목록/단건 조회)
    // ============================================================

    /**
     * GET /api/board → 게시글 목록
     * GET /api/board/{idx} → 게시글 상세
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);

        try {
            String path = req.getPathInfo(); // "/3" or null

            // ===== 목록 조회 =====
            if (path == null || "/".equals(path)) {
                int page = parseInt(req.getParameter("page"), 1);
                int size = parseInt(req.getParameter("size"), 10);

                List<Board> list = service.list(page, size);
                ok(resp, list); // List<Board>를 data로 감싸서 응답
                return;
            }

            // ===== 단일 조회 =====
            int idx = Integer.parseInt(path.substring(1));
            Board b = service.get(idx);

            if (b == null) {
                notFound(resp, "게시글을 찾을 수 없습니다.");
                return;
            }

            ok(resp, b);

        } catch (Exception e) {
            e.printStackTrace();
            serverError(resp, "서버 오류: " + e.getMessage());
        }
    }

    // ============================================================
    // POST (게시글 생성)
    // ============================================================

    /**
     * POST /api/board
     * Body: { title, content }
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);

        try {
            ServiceResult jr = readJson(req);
            if (!jr.success) {
                badRequest(resp, jr.message);
                return;
            }

            JsonObject json = (JsonObject) jr.data;

            // 필수값 검증
            if (json == null || !json.has("title") || !json.has("content")) {
                badRequest(resp, "필수 필드(title, content)가 없습니다.");
                return;
            }

            HttpSession session = req.getSession(); // 로그인 세션
            String fk_user_id = (String) session.getAttribute("id"); // 작성자 ID

            String title = json.get("title").getAsString();
            String content = json.get("content").getAsString();

            // 게시글 생성 (ServiceResult.idx 에 새 idx 들어감)
            ServiceResult r = service.create(title, content, fk_user_id);

            if (r.success) {
                created(resp, r); // status 201 + body: ServiceResult( message + idx )
            } else {
                badRequest(resp, r.message);
            }

        } catch (Exception e) {
            e.printStackTrace();
            serverError(resp, "서버 오류: " + e.getMessage());
        }
    }

    // ============================================================
    // PUT (게시글 수정)
    // ============================================================

    /**
     * PUT /api/board/{idx}
     * Body: { title, content }
     * - 본인 게시글만 수정 가능
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);

        try {
            String path = req.getPathInfo();

            if (path == null || path.length() < 2) {
                badRequest(resp, "잘못된 요청 경로입니다.");
                return;
            }

            int idx = Integer.parseInt(path.substring(1));
            ServiceResult jr = readJson(req);
            if (!jr.success) {
                badRequest(resp, jr.message);
                return;
            }
            JsonObject json = (JsonObject) jr.data;

            if (json == null || !json.has("title") || !json.has("content")) {
                badRequest(resp, "필수 필드(title, content)가 없습니다.");
                return;
            }

            HttpSession session = req.getSession();
            String fk_user_id = (String) session.getAttribute("id");

            String title = json.get("title").getAsString();
            String content = json.get("content").getAsString();

            // 수정 로직 (본인 여부는 service.update 내부에서 검사)
            ServiceResult r = service.update(idx, title, content, fk_user_id);

            if (r.success) {
                writeJson(resp, 200, r); // 메시지: "수정되었습니다", data: null
            } else {
                badRequest(resp, r.message);
            }

        } catch (Exception e) {
            e.printStackTrace();
            serverError(resp, "서버 오류: " + e.getMessage());
        }
    }

    // ============================================================
    // DELETE (게시글 삭제)
    // ============================================================

    /**
     * DELETE /api/board/{idx}
     * - 본인 게시글만 삭제 가능
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);

        try {
            String path = req.getPathInfo();

            if (path == null || path.length() < 2) {
                badRequest(resp, "잘못된 요청 경로입니다.");
                return;
            }

            HttpSession session = req.getSession();
            String fk_user_id = (String) session.getAttribute("id");

            int idx = Integer.parseInt(path.substring(1));

            // 삭제 로직 (본인 여부는 service.delete 내부에서 검사)
            ServiceResult r = service.delete(idx, fk_user_id);

            if (r.success) {
                writeJson(resp, 200, r);
            } else {
                badRequest(resp, r.message);
            }

        } catch (Exception e) {
            e.printStackTrace();
            serverError(resp, "서버 오류: " + e.getMessage());
        }
    }

    /** 문자열 숫자 파싱 유틸 (파싱 실패 시 기본값 반환) */
    private int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception ignore) {
            return def;
        }
    }
}
