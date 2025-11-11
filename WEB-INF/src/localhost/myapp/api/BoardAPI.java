package localhost.myapp.api;

import localhost.myapp.board.Board;
import localhost.myapp.board.BoardService;
import localhost.myapp.dto.ServiceResult;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

/**
 * /api/board/* REST 엔드포인트
 * - GET /api/board?page=1&size=10 : 목록
 * - GET /api/board/{idx}          : 단건 조회
 * - POST /api/board               : 생성
 * - PUT /api/board/{idx}          : 수정
 * - DELETE /api/board/{idx}       : 삭제
 */
@WebServlet("/api/board/*")
public class BoardAPI extends HttpServlet {
    private final BoardService service = new BoardService();
    private final Gson gson = new Gson();

    private JsonObject readJson(HttpServletRequest req) throws IOException {
        return gson.fromJson(req.getReader(), JsonObject.class);
    }

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

    /** 목록 / 단건 조회 */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        resp.setContentType("application/json; charset=UTF-8");
        try {
            String path = req.getPathInfo();

            if (path == null || "/".equals(path)) {
                int page = parseInt(req.getParameter("page"), 1);
                int size = parseInt(req.getParameter("size"), 10);
                List<Board> list = service.list(page, size);
                resp.getWriter().write(gson.toJson(list));
                return;
            }

            int idx = Integer.parseInt(path.substring(1));
            Board b = service.get(idx);
            if (b == null) {
                writeJson(resp, 404, false, "게시글을 찾을 수 없습니다.");
                return;
            }
            resp.getWriter().write(gson.toJson(b));
        } catch (Exception e) {
            e.printStackTrace();
            writeJson(resp, 500, false, "서버 오류: " + e.getMessage());
        }
    }

    /** 게시글 생성 */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        resp.setContentType("application/json; charset=UTF-8");
        try {
            JsonObject json = readJson(req);
            if (json == null || !json.has("title") || !json.has("content")) {
                writeJson(resp, 400, false, "잘못된 요청 형식입니다.");
                return;
            }

            String title = json.get("title").getAsString();
            String content = json.get("content").getAsString();

            ServiceResult r = service.create(title, content);
            resp.setStatus(r.success ? 201 : 400);
            resp.getWriter().write(gson.toJson(r));
        } catch (Exception e) {
            e.printStackTrace();
            writeJson(resp, 500, false, "서버 오류: " + e.getMessage());
        }
    }

    /** 게시글 수정 */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        resp.setContentType("application/json; charset=UTF-8");
        try {
            String path = req.getPathInfo();
            if (path == null || path.length() < 2) {
                writeJson(resp, 400, false, "잘못된 요청 경로입니다.");
                return;
            }

            int idx = Integer.parseInt(path.substring(1));
            JsonObject json = readJson(req);
            if (json == null || !json.has("title") || !json.has("content")) {
                writeJson(resp, 400, false, "잘못된 요청 형식입니다.");
                return;
            }

            String title = json.get("title").getAsString();
            String content = json.get("content").getAsString();

            ServiceResult r = service.update(idx, title, content);
            resp.setStatus(r.success ? 200 : 400);
            resp.getWriter().write(gson.toJson(r));
        } catch (Exception e) {
            e.printStackTrace();
            writeJson(resp, 500, false, "서버 오류: " + e.getMessage());
        }
    }

    /** 게시글 삭제 */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        resp.setContentType("application/json; charset=UTF-8");
        try {
            String path = req.getPathInfo();
            if (path == null || path.length() < 2) {
                writeJson(resp, 400, false, "잘못된 요청 경로입니다.");
                return;
            }

            int idx = Integer.parseInt(path.substring(1));
            ServiceResult r = service.delete(idx);
            resp.setStatus(r.success ? 200 : 400);
            resp.getWriter().write(gson.toJson(r));
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

    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception ignore) { return def; }
    }
}
