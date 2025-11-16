package localhost.myapp.ex;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ex/jdbc")
public class JDBC extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String url = "jdbc:mysql://localhost:3306/test";
        String user = "test";
        String pass = "test1234";

        try (Connection con = DriverManager.getConnection(url, user, pass)) {
            System.out.println("연결 성공!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}