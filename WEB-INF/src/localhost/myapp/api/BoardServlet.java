package localhost.myapp.api;

import localhost.myapp.dao.BoardDao;
import localhost.myapp.model.Board;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

/**
 * /api/board/* REST 스타일 엔드포인트
 * - GET /api/board?page=1&size=10 : 목록 페이징
 * - GET /api/board/{idx}          : 단건 조회
 * - POST /api/board               : 생성
 * - PUT /api/board/{idx}          : 수정
 * - DELETE /api/board/{idx}       : 삭제
 */
@WebServlet("/api/board/*")
public class BoardServlet extends HttpServlet {
    private final BoardDao dao = new BoardDao();
    private final Gson gson = new Gson();

    private JsonObject readJson(HttpServletRequest req) throws IOException {
        return gson.fromJson(req.getReader(), JsonObject.class);
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        resp.setContentType("application/json; charset=UTF-8");
        try {
            String path = req.getPathInfo();
            if (path == null || "/".equals(path)) {
                // 페이징 파라미터 처리
                int page = parseInt(req.getParameter("page"), 1);
                int size = parseInt(req.getParameter("size"), 10);
                List<Board> list = dao.findAll(page, size);
                resp.getWriter().write(gson.toJson(list));
            } else {
                int idx = Integer.parseInt(path.substring(1));
                Board b = dao.findById(idx);
                if (b == null) {
                    resp.setStatus(404);
                    resp.getWriter().write("{\"error\":\"not_found\"}");
                    return;
                }
                resp.getWriter().write(gson.toJson(b));
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write(jsonError(e));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        resp.setContentType("application/json; charset=UTF-8");
        try {
            JsonObject json = readJson(req);
            // 간단 검증
            if (json == null || !json.has("title") || !json.has("content")) {
                resp.setStatus(400);
                resp.getWriter().write("{\"error\":\"invalid_body\"}");
                return;
            }
            Board b = new Board();
            b.title = json.get("title").getAsString();
            b.content = json.get("content").getAsString();
            dao.insert(b);
            resp.setStatus(201);
            resp.getWriter().write("{\"message\":\"created\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write(jsonError(e));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        resp.setContentType("application/json; charset=UTF-8");
        try {
            String path = req.getPathInfo();
            if (path == null || path.length() < 2) { resp.setStatus(400); return; }

            int idx = Integer.parseInt(path.substring(1));
            JsonObject json = readJson(req);
            if (json == null || !json.has("title") || !json.has("content")) {
                resp.setStatus(400);
                resp.getWriter().write("{\"error\":\"invalid_body\"}");
                return;
            }

            Board b = new Board();
            b.idx = idx;
            b.title = json.get("title").getAsString();
            b.content = json.get("content").getAsString();

            boolean ok = dao.update(b);
            if (!ok) { resp.setStatus(404); resp.getWriter().write("{\"error\":\"not_found\"}"); return; }

            resp.getWriter().write("{\"message\":\"updated\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write(jsonError(e));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        resp.setContentType("application/json; charset=UTF-8");
        try {
            String path = req.getPathInfo();
            if (path == null || path.length() < 2) { resp.setStatus(400); return; }
            int idx = Integer.parseInt(path.substring(1));

            boolean ok = dao.delete(idx);
            if (!ok) { resp.setStatus(404); resp.getWriter().write("{\"error\":\"not_found\"}"); return; }

            resp.getWriter().write("{\"message\":\"deleted\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write(jsonError(e));
        }
    }

    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception ignore) { return def; }
    }

    private String jsonError(Exception e) {
        // 실서비스에선 스택트레이스 노출 금지. 로그로만 남기고, 사용자에겐 일반 메시지 전달.
        return "{\"error\":\"" + e.getClass().getSimpleName() + "\",\"message\":\"" + e.getMessage() + "\"}";
    }
}