package localhost.myapp.ex;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import localhost.myapp.common.DB;

@WebServlet("/ex/jndi")
public class JNDI extends HttpServlet {

    private final DataSource ds = DB.getDataSource(); // DB.getDataSource()로 커넥션 풀 객체 생성

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try (Connection con = ds.getConnection();) {

            System.out.println("DataSource 연결 성공!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}