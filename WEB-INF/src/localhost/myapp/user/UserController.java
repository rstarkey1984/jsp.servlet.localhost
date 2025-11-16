package localhost.myapp.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import localhost.myapp.dto.ServiceResult;

import java.io.IOException;

/**
 * UserController
 *
 * - URL 패턴: /user/*
 * 예) /user/login, /user/register, /user/logout 등
 * - 역할: 로그인/로그아웃/회원가입 처리(Controller)
 * - GET → 화면 이동 (JSP forward)
 * - POST → 실제 처리(login, register)
 *
 * Controller 흐름
 * 1) 클라이언트 요청
 * 2) pathInfo 로 세부 경로 확인
 * 3) 필요한 JSP 또는 서비스 호출
 * 4) 결과에 따라 redirect 또는 forward
 */
@WebServlet("/user/*")
public class UserController extends HttpServlet {

    // 사용자 관련 비즈니스 로직을 담당하는 서비스
    private final UserService service = new UserService();

    /**
     * pathInfo 정규화 함수
     * - null 또는 "" → "/" 로 변경
     * - 마지막에 "/" 가 있으면 제거 (단, "/" 자체는 그대로 유지)
     * 예)
     * "/login/" → "/login"
     * null → "/"
     */
    private String normPath(HttpServletRequest req) {
        String p = req.getPathInfo();
        if (p == null || p.isEmpty())
            return "/";
        if (p.length() > 1 && p.endsWith("/"))
            return p.substring(0, p.length() - 1);
        return p;
    }

    /**
     * GET 요청 처리
     * - 보통 화면 이동 담당
     * - /login → login.jsp
     * - /logout → 세션 종료 후 로그인 페이지로 redirect
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String path = normPath(req); // 정리된 경로값
        System.out.println(path); // 디버깅용 출력

        switch (path) {

            // 로그인 화면
            case "/login":
                req.getRequestDispatcher("/WEB-INF/view/user/login.jsp")
                        .forward(req, resp);
                break;

            // 로그인 성공 화면
            case "/login_ok":
                req.getRequestDispatcher("/WEB-INF/view/user/login_ok.jsp")
                        .forward(req, resp);
                break;

            // 회원가입 화면
            case "/register":
                req.getRequestDispatcher("/WEB-INF/view/user/register.jsp")
                        .forward(req, resp);
                break;

            // 회원가입 성공 화면
            case "/register_ok":
                req.getRequestDispatcher("/WEB-INF/view/user/register_ok.jsp")
                        .forward(req, resp);
                break;

            // 로그아웃 처리
            case "/logout":
                HttpSession session = req.getSession(false);
                if (session != null) {
                    session.invalidate(); // 세션 완전 종료
                }
                // 다시 로그인 화면으로
                resp.sendRedirect(req.getContextPath() + "/user/login");
                break;

            // 기본 URL → /user/ → 로그인 페이지로 보냄
            case "/":
                resp.sendRedirect(req.getContextPath() + "/user/login");
                break;

            // 정의되지 않은 URL
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "페이지를 찾을 수 없습니다.");
        }
    }

    /**
     * POST 요청 처리
     * - 실제 이동이 아닌 "데이터 처리(login, register)" 담당
     * - 성공 → 성공 페이지 redirect
     * - 실패 → flash 메시지 저장 후 다시 원래 페이지로 redirect
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String path = normPath(req);
        HttpSession session = req.getSession();

        // 공통 파라미터
        String id = req.getParameter("id");
        String password = req.getParameter("password");

        switch (path) {

            /** -------------------- 로그인 처리 -------------------- */
            case "/login":
                try {
                    ServiceResult r = service.login(id, password);

                    if (r.success) {
                        // 로그인 성공 → 세션에 id 저장
                        session.setAttribute("id", id);
                        resp.sendRedirect(req.getContextPath() + "/user/login_ok");
                    } else {
                        // 실패 메시지를 flash 로 전달
                        session.setAttribute("flash_error", r.message);
                        resp.sendRedirect(req.getContextPath() + "/user/login");
                    }

                } catch (Exception e) {
                    log("login failed", e);
                    session.setAttribute("flash_error", "서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
                    resp.sendRedirect(req.getContextPath() + "/user/login");
                }
                break;

            /** -------------------- 회원가입 처리 -------------------- */
            case "/register":

                String email = req.getParameter("email");

                try {
                    // 제네릭 타입 맞추기: ServiceResult<Void>
                    ServiceResult r = service.register(id, password, email);

                    if (r.success) {
                        // 회원가입 성공 → 자동 로그인 비슷하게 세션에 id 저장
                        session.setAttribute("id", id);
                        resp.sendRedirect(req.getContextPath() + "/user/register_ok");
                    } else {
                        session.setAttribute("flash_error", r.message);
                        resp.sendRedirect(req.getContextPath() + "/user/register");
                    }

                } catch (Exception e) {
                    log("register failed", e);
                    session.setAttribute("flash_error", "서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
                    resp.sendRedirect(req.getContextPath() + "/user/register");
                }
                break;

            /** -------------------- 기타 잘못된 POST 요청 -------------------- */
            default:
                System.out.println("잘못된 요청입니다");
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 요청입니다.");
        }

    }
}
