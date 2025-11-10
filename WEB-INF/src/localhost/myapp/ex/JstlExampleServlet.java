package localhost.myapp.ex;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/jstl/example")
public class JstlExampleServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("name", "홍길동");
        req.setAttribute("age", 25);

        List<String> list = List.of("사과", "바나나", "포도");
        req.setAttribute("fruits", list);

        //getServletContext().getRequestDispatcher("/WEB-INF/view/ex/jstl-example.jsp").forward(req, resp);
        req.getRequestDispatcher("/WEB-INF/view/ex/jstl-example.jsp").forward(req, resp);
    }
}