package localhost.myapp.ex;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet(name = "DemoServlet", urlPatterns = "/ex/demo")
public class DemoServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        System.out.printf("[%s] init() 호출 - 인스턴스:%s%n",
                LocalDateTime.now(), thisIdentity());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        System.out.printf("[%s] service() 시작 - 스레드:%s, 메서드:%s, URI:%s%n",
                LocalDateTime.now(), Thread.currentThread().getName(),
                req.getMethod(), req.getRequestURI());
        super.service(req, resp);
        System.out.printf("[%s] service() 종료 - 스레드:%s, 메서드:%s, URI:%s%n",
                LocalDateTime.now(), Thread.currentThread().getName(),
                req.getMethod(), req.getRequestURI());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        System.out.printf("[%s] doGet() - 파라미터 name=%s%n",
                LocalDateTime.now(), req.getParameter("name"));
        resp.setContentType("text/html; charset=UTF-8");
        resp.getWriter().println("<h1>GET OK</h1>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        System.out.printf("[%s] doPost() - 파라미터 name=%s%n",
                LocalDateTime.now(), req.getParameter("name"));
        resp.setContentType("text/html; charset=UTF-8");
        resp.getWriter().println("<h1>POST OK</h1>");
    }

    @Override
    public void destroy() {
        System.out.printf("[%s] destroy() 호출 - 인스턴스:%s%n",
                LocalDateTime.now(), thisIdentity());
    }

    private String thisIdentity() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this));
    }
}
