package localhost.myapp.board;

import localhost.myapp.dto.ServiceResult;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * BoardController
 *
 * 라우팅 규칙 (URL 구조)
 * - GET
 * /board/list → 목록 페이지
 * /board/detail → 상세 페이지
 * /board/write → 글쓰기 페이지
 * /board/edit → 수정 페이지
 *
 * - POST
 * action=create → 게시글 생성
 * action=update → 게시글 수정
 * action=delete → 게시글 삭제
 *
 * Controller 역할:
 * - 사용자 요청 파악 (pathInfo, action)
 * - 필요한 Service 호출
 * - JSP로 forward 또는 redirect
 */
@WebServlet("/board/*")
public class BoardController extends HttpServlet {

    private BoardService service;

    /** 서블릿 초기화 시 서비스 객체 생성 */
    @Override
    public void init() throws ServletException {
        this.service = new BoardService();
    }

    /**
     * GET 요청 처리 (화면 이동)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // /board/list → pathInfo = /list
        String path = req.getPathInfo();

        // /board/ → 기본 URL이면 list로 이동
        if (path == null || path.equals("/")) {
            path = "/list";
        }

        try {
            switch (path) {
                case "/list":
                    list(req, resp);
                    break;
                case "/detail":
                    detail(req, resp);
                    break;
                case "/write":
                    showWriteForm(req, resp);
                    break;
                case "/edit":
                    showEditForm(req, resp);
                    break;
                default:
                    // 정의되지 않은 URL
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    /**
     * POST 요청 처리 (실제 작업)
     * action 값으로 구분:
     * - create
     * - update
     * - delete
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");

        if (action == null || action.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        switch (action) {
            case "create":
                create(req, resp);
                break;
            case "update":
                update(req, resp);
                break;
            case "delete":
                delete(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /*
     * ===========================================================
     * ============== GET: View 화면 관련 =================
     * ===========================================================
     */

    /** 게시판 목록 페이지 */
    private void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, SQLException {

        // 페이지 파라미터 기본값
        int page = parseInt(req.getParameter("page"), 1);
        int size = parseInt(req.getParameter("size"), 10);

        // 전체 게시글 개수
        int totalCount = service.count();
        int totalPages = (int) Math.ceil(totalCount / (double) size);

        if (totalPages == 0)
            totalPages = 1;
        if (page > totalPages)
            page = totalPages;

        // DB에서 현재 페이지 목록 가져오기
        List<Board> list = service.list(page, size);

        // 블록 페이징 계산 (5페이지씩)
        int blockSize = 5;
        int currentBlock = (page - 1) / blockSize;
        int startPage = currentBlock * blockSize + 1;
        int endPage = Math.min(startPage + blockSize - 1, totalPages);

        // JSP에서 사용할 데이터 전달
        req.setAttribute("list", list);
        req.setAttribute("page", page);
        req.setAttribute("size", size);
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("startPage", startPage);
        req.setAttribute("endPage", endPage);

        req.getRequestDispatcher("/WEB-INF/view/board/list.jsp")
                .forward(req, resp);
    }

    /** 게시글 상세 페이지 */
    private void detail(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, SQLException {

        int idx = parseInt(req.getParameter("idx"), 0);

        // idx 검증 실패 → 목록으로
        if (idx <= 0) {
            resp.sendRedirect(req.getContextPath() + "/board/list");
            return;
        }

        Board board = service.get(idx);

        // 게시글 존재하지 않으면 목록으로
        if (board == null) {
            resp.sendRedirect(req.getContextPath() + "/board/list");
            return;
        }

        req.setAttribute("board", board);
        req.getRequestDispatcher("/WEB-INF/view/board/detail.jsp")
                .forward(req, resp);
    }

    /** 글쓰기 폼 (빈 폼만 보여줌) */
    private void showWriteForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.getRequestDispatcher("/WEB-INF/view/board/write.jsp")
                .forward(req, resp);
    }

    /** 수정 폼 (기존 데이터 불러오기) */
    private void showEditForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, SQLException {

        int idx = parseInt(req.getParameter("idx"), 0);

        if (idx <= 0) {
            resp.sendRedirect(req.getContextPath() + "/board/list");
            return;
        }

        Board board = service.get(idx);

        if (board == null) {
            resp.sendRedirect(req.getContextPath() + "/board/list");
            return;
        }

        req.setAttribute("board", board);
        req.getRequestDispatcher("/WEB-INF/view/board/edit.jsp")
                .forward(req, resp);
    }

    /*
     * ===========================================================
     * ============== POST: Create/Update/Delete ============
     * ===========================================================
     */

    /** 게시글 생성 */
    private void create(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession();

        String title = req.getParameter("title");
        String content = req.getParameter("content");
        String fk_user_id = (String) session.getAttribute("id");

        ServiceResult result = service.create(title, content, fk_user_id);

        String ctx = req.getContextPath();

        if (result.success) {
            // 성공 메시지 flash로 전달
            session.setAttribute("flash_success", result.message);
            resp.sendRedirect(ctx + "/board/list");
        } else {
            session.setAttribute("flash_error", result.message);
            resp.sendRedirect(ctx + "/board/write");
        }
    }

    /** 게시글 수정 */
    private void update(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession();

        int idx = parseInt(req.getParameter("idx"), 0);
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        String fk_user_id = (String) session.getAttribute("id");

        ServiceResult result = service.update(idx, title, content, fk_user_id);

        String ctx = req.getContextPath();

        if (result.success) {
            session.setAttribute("flash_success", result.message);
            resp.sendRedirect(ctx + "/board/detail?idx=" + idx);
        } else {
            session.setAttribute("flash_error", result.message);
            resp.sendRedirect(ctx + "/board/edit?idx=" + idx);
        }
    }

    /** 게시글 삭제 */
    private void delete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession();
        String fk_user_id = (String) session.getAttribute("id");

        int idx = parseInt(req.getParameter("idx"), 0);
        ServiceResult result = service.delete(idx, fk_user_id);

        String ctx = req.getContextPath();

        if (result.success)
            session.setAttribute("flash_success", result.message);
        else
            session.setAttribute("flash_error", result.message);

        resp.sendRedirect(ctx + "/board/list");
    }

    /** 숫자 파싱 (예외 발생 → 기본값 반환) */
    private int parseInt(String s, int defaultValue) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
