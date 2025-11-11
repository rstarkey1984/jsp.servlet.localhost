package localhost.myapp.board;

import localhost.myapp.common.DB;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 게시판 CRUD.
 * - 목록 조회는 DESC 정렬 + LIMIT/OFFSET 로 간단 페이징 지원
 */
public class BoardDao {
    private final DataSource ds = DB.getDataSource();

    public List<Board> findAll(int page, int size) throws SQLException {
        int limit = Math.max(1, Math.min(size, 100));
        int offset = Math.max(0, (page - 1) * limit);
        String sql = "SELECT idx, title, content, reg_date FROM board ORDER BY idx DESC LIMIT ? OFFSET ?";
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                List<Board> list = new ArrayList<>();
                while (rs.next()) {
                    Board b = new Board();
                    b.idx = rs.getInt("idx");
                    b.title = rs.getString("title");
                    b.content = rs.getString("content");
                    b.regDate = rs.getString("reg_date");
                    list.add(b);
                }
                return list;
            }
        }
    }

    public Board findById(int idx) throws SQLException {
        String sql = "SELECT * FROM board WHERE idx=?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idx);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Board b = new Board();
                    b.idx = rs.getInt("idx");
                    b.title = rs.getString("title");
                    b.content = rs.getString("content");
                    b.regDate = rs.getString("reg_date");
                    return b;
                }
                return null;
            }
        }
    }

    public boolean insert(Board b) throws SQLException {
        String sql = "INSERT INTO board (title, content) VALUES (?, ?)";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, b.title);
            ps.setString(2, b.content);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean update(Board b) throws SQLException {
        String sql = "UPDATE board SET title=?, content=? WHERE idx=?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, b.title);
            ps.setString(2, b.content);
            ps.setInt(3, b.idx);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean delete(int idx) throws SQLException {
        String sql = "DELETE FROM board WHERE idx=?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idx);
            return ps.executeUpdate() == 1;
        }
    }
}