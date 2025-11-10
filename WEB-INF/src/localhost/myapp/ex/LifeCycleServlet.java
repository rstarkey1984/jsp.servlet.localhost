package localhost.myapp.ex;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
//import java.time.LocalDateTime;

@WebServlet(name = "LifeCycleServlet", urlPatterns = "/ex/life")
public class LifeCycleServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        System.out.println("🔵 [init] 서블릿 초기화 호출 - 인스턴스: " + thisIdentity());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        System.out.println("🟡 [service 시작] " + req.getMethod() + " 요청 - URI: " + req.getRequestURI());
        super.service(req, resp);
        System.out.println("🟡 [service 종료] " + req.getMethod() + " 요청 - URI: " + req.getRequestURI());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        System.out.println("🟢 [doGet] name=" + req.getParameter("name"));

        resp.setContentType("text/html; charset=UTF-8");
        resp.getWriter().println(makeHtml("GET 처리 완료", req));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        System.out.println("🟠 [doPost] name=" + req.getParameter("name"));

        resp.setContentType("text/html; charset=UTF-8");
        resp.getWriter().println(makeHtml("POST 처리 완료", req));
    }

    @Override
    public void destroy() {
        System.out.println("🔴 [destroy] 서블릿 종료 - 인스턴스: " + thisIdentity());
    }

    private String makeHtml(String title, HttpServletRequest req) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <title>Servlet Lifecycle Test</title>
            <!-- css 태그 -->
            <style> 
                html { color-scheme: light dark; }
                body { width: 35em; margin: 0 auto;
                font-family: Tahoma, Verdana, Arial, sans-serif; }
            </style>
        </head>
        <body>
            <h2>Servlet Lifecycle & GET/POST 테스트</h2>
            <p><b>결과:</b> %s</p>
            <p><b>요청 Method:</b> %s</p>
            <p><b>name 파라미터 값:</b> %s</p>
            <hr>
            <h3>GET 요청</h3>
            <form method="get" action="/ex/life">
                <input type="text" name="name" value="홍길동">
                <button type="submit">GET 전송</button>
            </form>
            <h3>POST 요청</h3>
            <form method="post" action="/ex/life">
                <input type="text" name="name" value="임꺽정">
                <button type="submit">POST 전송</button>
            </form>
        </body>
        </html>
        """.formatted(
                title,
                req.getMethod(),
                req.getParameter("name")
        );
    }

    private String thisIdentity() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this));
    }
}
