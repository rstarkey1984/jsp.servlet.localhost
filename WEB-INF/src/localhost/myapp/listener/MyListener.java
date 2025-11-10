package localhost.myapp.listener;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.*;

@WebListener
public class MyListener implements ServletContextListener, HttpSessionListener, ServletRequestListener {

    // 웹 애플리케이션 시작/종료
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("🌐 애플리케이션 시작됨!");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("🌐 애플리케이션 종료됨!");
    }

    // 세션 생성/소멸
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        System.out.println("👤 세션 생성: " + se.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        System.out.println("👤 세션 소멸: " + se.getSession().getId());
    }

    // 요청(request) 시작/종료
    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        HttpServletRequest req = (HttpServletRequest) sre.getServletRequest();
        String url = req.getRequestURL().toString();     // 전체 URL
        //String uri = req.getRequestURI();                // URI만
        String query = req.getQueryString();             // 쿼리 파라미터
        String clientIp = req.getRemoteAddr();           // 요청 보낸 IP

        System.out.println("➡ 요청 들어옴: " + url +
                (query != null ? "?" + query : "") +
                " | IP: " + clientIp);
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        System.out.println("⬅ 요청 처리 완료");
    }
}
