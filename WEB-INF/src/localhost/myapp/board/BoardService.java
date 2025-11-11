package localhost.myapp.board;

import localhost.myapp.dto.ServiceResult;
import java.sql.SQLException;
import java.util.List;

/**
 * 비즈니스 규칙/검증을 담당하는 서비스 레이어.
 * - Controller(Servlet) ↔ Service ↔ DAO 구조
 * - Read: 원본 타입 반환(List<Board>, Board)
 * - Write(C/U/D): ServiceResult 반환(일관된 성공/실패 + 메시지)
 */
public class BoardService {
    private final BoardDao dao;

    public BoardService() {
        this.dao = new BoardDao();
    }

    // 테스트/주입용
    public BoardService(BoardDao dao) {
        this.dao = dao;
    }

    /** 목록 페이징 (Read는 데이터 그대로 반환) */
    public List<Board> list(int page, int size) throws SQLException {
        if (page < 1)
            page = 1;
        if (size < 1)
            size = 10;
        return dao.findAll(page, size);
    }

    /** 단건 조회 (없으면 null) */
    public Board get(int idx) throws SQLException {
        if (idx <= 0)
            return null;
        return dao.findById(idx);
    }

    /** 생성 (ServiceResult로 성공/실패 메시지 반환) */
    public ServiceResult create(String title, String content) {
        try {
            validate(title, content);
            Board b = new Board();
            b.title = title.trim();
            b.content = content.trim();

            boolean ok = dao.insert(b);
            return ok ? ok("게시글이 등록되었습니다.")
                    : fail("등록 실패");
        } catch (IllegalArgumentException e) {
            return fail(e.getMessage());
        } catch (SQLException e) {
            return fail("DB 오류: " + e.getMessage());
        }
    }

    /** 수정 */
    public ServiceResult update(int idx, String title, String content) {
        try {
            if (idx <= 0)
                return fail("잘못된 ID");
            validate(title, content);

            Board b = new Board();
            b.idx = idx;
            b.title = title.trim();
            b.content = content.trim();

            boolean ok = dao.update(b);
            return ok ? ok("수정되었습니다.")
                    : fail("수정 실패");
        } catch (IllegalArgumentException e) {
            return fail(e.getMessage());
        } catch (SQLException e) {
            return fail("DB 오류: " + e.getMessage());
        }
    }

    /** 삭제 */
    public ServiceResult delete(int idx) {
        try {
            if (idx <= 0)
                return fail("잘못된 ID");
            boolean ok = dao.delete(idx);
            return ok ? ok("삭제되었습니다.")
                    : fail("삭제 실패");
        } catch (SQLException e) {
            return fail("DB 오류: " + e.getMessage());
        }
    }

    /** 공통 검증 */
    private void validate(String title, String content) {
        if (title == null || content == null) {
            throw new IllegalArgumentException("title/content required");
        }
        String t = title.trim();
        String c = content.trim();
        if (t.isEmpty() || c.isEmpty()) {
            throw new IllegalArgumentException("title/content required");
        }
        if (t.length() > 200) {
            throw new IllegalArgumentException("title too long");
        }
    }

    /** 내부 헬퍼: 성공 응답 */
    private ServiceResult ok(String msg) {
        ServiceResult r = new ServiceResult();
        r.success = true;
        r.message = msg;
        return r;
    }

    /** 내부 헬퍼: 실패 응답 */
    private ServiceResult fail(String msg) {
        ServiceResult r = new ServiceResult();
        r.success = false;
        r.message = msg;
        return r;
    }
}