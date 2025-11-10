package localhost.myapp.ex;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/test")
public class testServlet extends HttpServlet {

    // 생성자(Constructor)
    public testServlet() {
        System.out.println("생성자 호출됨: Servlet 객체 생성!");
    }

    @Override
    public void init() throws ServletException {
        System.out.println("init() 호출됨: 초기화 작업!");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, java.io.IOException {

        // 응답 데이터를 HTML 형식으로 설정, 문자 인코딩은 UTF-8로 설정
        resp.setContentType("text/html; charset=UTF-8");

        resp.getWriter().println("<h1 style='text-align:center'>Hello from Constructor Example!</h1>");        
        sayHello(resp, "홍길동");  // 일반 메서드 호출
    }

    // 사용자 정의 메서드
    public void sayHello(HttpServletResponse resp, String name) throws ServletException, java.io.IOException {
        resp.getWriter().println("<h1 style='text-align:center'>👋 안녕하세요, " + name + "님!</h1>");
    }

}