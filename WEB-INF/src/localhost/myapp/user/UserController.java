package localhost.myapp.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import localhost.myapp.dto.ServiceResult;

import java.io.IOException;

@WebServlet("/user/*")
public class UserController extends HttpServlet {

    private final UserService service = new UserService();

    // pathInfo 정규화: null -> "/", 끝 슬래시 제거(루트 "/"는 유지)
    private String normPath(HttpServletRequest req) {
        String p = req.getPathInfo();
        if (p == null || p.isEmpty()) return "/";
        if (p.length() > 1 && p.endsWith("/")) return p.substring(0, p.length() - 1);
        return p;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String path = normPath(req);     

        System.out.println(path);

        switch (path) {
            case "/login":
                req.getRequestDispatcher("/WEB-INF/view/user/login.jsp").forward(req, resp);
                break;
            case "/login_ok":
                req.getRequestDispatcher("/WEB-INF/view/user/login_ok.jsp").forward(req, resp);
                break;
            case "/register":
                req.getRequestDispatcher("/WEB-INF/view/user/register.jsp").forward(req, resp);
                break;
            case "/register_ok":
                req.getRequestDispatcher("/WEB-INF/view/user/register_ok.jsp").forward(req, resp);
                break;
            case "/logout":
                HttpSession session = req.getSession(false);
                if (session != null) {
                    session.invalidate();  // 세션 완전 종료
                }
                resp.sendRedirect(req.getContextPath() + "/user/login");
                break;
            case "/":
                // 기본 페이지가 필요하면 여기서 redirect
                resp.sendRedirect(req.getContextPath() + "/user/login");
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "페이지를 찾을 수 없습니다.");
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String path = normPath(req);
        HttpSession session = req.getSession();

        String id = req.getParameter("id");
        String password = req.getParameter("password");

        switch (path) {
            case "/login":

                try {

                    ServiceResult r = service.login(id, password);

                    if (r.success) {
                        session.setAttribute("id", id);
                        resp.sendRedirect(req.getContextPath() + "/user/login_ok");
                    } else {
                        session.setAttribute("flash_error", r.message);
                        resp.sendRedirect(req.getContextPath() + "/user/login");
                    }

                } catch (Exception e) {
                    log("register failed", e);
                    session.setAttribute("flash_error", "서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
                    resp.sendRedirect(req.getContextPath() + "/user/login"); 
                }

                break;

            case "/register":

                String email = req.getParameter("email");                

                try {

                    ServiceResult r = service.register(id, password, email);

                    if (r.success) {
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

            default:
                System.out.println("잘못된 요청입니다");
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 요청입니다.");
        }

    }

    
}