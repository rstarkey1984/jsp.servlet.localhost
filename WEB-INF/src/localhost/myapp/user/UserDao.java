package localhost.myapp.user;

import localhost.myapp.common.DB;

import javax.sql.DataSource;
import java.sql.*;

public class UserDao {
    private final DataSource ds = DB.getDataSource();

    public boolean insert(User u) throws SQLException {
        String sql = "INSERT INTO user (id, password, email) VALUES (?, sha2(?, 256), ?)";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.id);
            ps.setString(2, u.password);
            ps.setString(3, u.email);
            return ps.executeUpdate() == 1;
        }
    }

    public User findById(String id) throws SQLException {
        String sql = "SELECT * FROM user WHERE id=?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.idx = rs.getInt("idx");
                    u.id = rs.getString("id");
                    u.email = rs.getString("email");
                    u.regDate = rs.getString("reg_date");
                    return u;
                }
                return null;
            }
        }
    }

    public User existsById(String id) throws SQLException {
        String sql = "SELECT idx FROM user WHERE id=?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.idx = rs.getInt("idx");
                    return u;
                }
                return null;
            }
        }
    }

    public boolean login(String id, String password) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user WHERE id=? AND password=sha2(?, 256)";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) == 1;
            }
        }
    }
}