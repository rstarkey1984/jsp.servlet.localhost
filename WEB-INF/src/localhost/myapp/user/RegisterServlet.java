package localhost.myapp.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/user/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //req.setCharacterEncoding("UTF-8");

        // 현재 요청/응답을 그대로 유지한 채 서버 내부에서 register.jsp로 포워딩
        req.getRequestDispatcher("/WEB-INF/view/user/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //req.setCharacterEncoding("UTF-8");

        String email = req.getParameter("email");
        String username = req.getParameter("username");
        String age = req.getParameter("age");        

        // (DB 저장 로직 가능) - 지금은 단순히 값만 JSP로 전달
        // ...
        // ...
        // ...
        // (DB 저장 로직 끝)

        req.setAttribute("email", email);
        req.setAttribute("username", username);
        req.setAttribute("age", age);

        // 현재 요청/응답을 그대로 유지한 채 서버 내부에서 register.jsp로 포워딩
        req.getRequestDispatcher("/WEB-INF/view/user/welcome.jsp").forward(req, resp);
    }
}