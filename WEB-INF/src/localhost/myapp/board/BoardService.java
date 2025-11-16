package localhost.myapp.board;

import localhost.myapp.dto.ServiceResult;
import java.sql.SQLException;
import java.util.List;

public class BoardService {

    private final BoardDao dao;

    public BoardService() {
        this.dao = new BoardDao();
    }

    public BoardService(BoardDao dao) {
        this.dao = dao;
    }

    /** 목록 페이징 (Read는 그대로 반환) */
    public List<Board> list(int page, int size) throws SQLException {
        if (page < 1)
            page = 1;
        if (size < 1)
            size = 10;
        return dao.findAll(page, size);
    }

    /** 전체 개수 */
    public int count() throws SQLException {
        return dao.countAll();
    }

    /** 단건 조회 (없으면 null) */
    public Board get(int idx) throws SQLException {
        if (idx <= 0)
            return null;
        return dao.findById(idx);
    }

    /**
     * 생성 : 성공 시 새 idx 가 ServiceResult.idx 에 들어감
     */
    public ServiceResult create(String title, String content, String fk_user_id) {
        try {
            validate(title, content);

            Board b = new Board();
            b.title = title.trim();
            b.content = content.trim();
            b.fk_user_id = fk_user_id;

            Integer newId = dao.insert(b);

            if (newId == null) {
                return ServiceResult.fail("게시글 등록에 실패했습니다.");
            }

            // ✔ idx 필드에 새로 생성된 PK 저장
            return ServiceResult.okWithId("게시글이 등록되었습니다.", newId);

        } catch (IllegalArgumentException e) {
            return ServiceResult.fail(e.getMessage());

        } catch (SQLException e) {
            return ServiceResult.fail("데이터베이스 오류: " + e.getMessage());
        }
    }

    /**
     * 수정
     */
    public ServiceResult update(int idx, String title, String content, String fk_user_id) {
        try {
            if (idx <= 0) {
                return ServiceResult.fail("잘못된 게시글 번호입니다.");
            }

            // 1) 기존 게시글 조회
            Board b_exists = get(idx); // idx 로 게시물 정보 가져오기

            // 게시물이 없으면
            if (b_exists == null) {
                return ServiceResult.fail("게시물이 존재하지 않습니다.");
            }

            // 2) 권한 체크
            // - fk_user_id 컬럼이 null이면 누구나 수정 가능
            // - null이 아니면 세션에서 전달받은 fk_user_id와 같을 때만 가능
            if (!canModify(b_exists, fk_user_id)) {
                return ServiceResult.fail("본인 게시글만 수정할 수 있습니다.");
            }

            validate(title, content);

            Board b = new Board();
            b.idx = idx;
            b.title = title.trim();
            b.content = content.trim();

            boolean ok = dao.update(b);

            if (!ok) {
                return ServiceResult.fail("게시글 수정에 실패했습니다.");
            }

            // 수정은 별도 data, idx 필요 없으니 메시지만
            return ServiceResult.ok("게시글이 수정되었습니다.");

        } catch (IllegalArgumentException e) {
            return ServiceResult.fail(e.getMessage());

        } catch (SQLException e) {
            return ServiceResult.fail("데이터베이스 오류: " + e.getMessage());
        }
    }

    /**
     * 삭제
     */
    public ServiceResult delete(int idx, String fk_user_id) {
        try {
            if (idx <= 0) {
                return ServiceResult.fail("잘못된 게시글 번호입니다.");
            }

            // 1) 기존 게시글 조회
            Board b_exists = get(idx); // idx 로 게시물 정보 가져오기

            // 게시물이 없으면
            if (b_exists == null) {
                return ServiceResult.fail("게시물이 존재하지 않습니다.");
            }

            // 2) 권한 체크
            // - fk_user_id 컬럼이 null이면 누구나 수정 가능
            // - null이 아니면 세션에서 전달받은 fk_user_id와 같을 때만 가능
            if (!canModify(b_exists, fk_user_id)) {
                return ServiceResult.fail("본인 게시글만 삭제할 수 있습니다.");
            }

            boolean ok = dao.delete(idx);

            if (!ok) {
                return ServiceResult.fail("게시글 삭제에 실패했습니다.");
            }

            // 삭제도 메시지만
            return ServiceResult.ok("게시글이 삭제되었습니다.");

        } catch (SQLException e) {
            return ServiceResult.fail("데이터베이스 오류: " + e.getMessage());
        }
    }

    /** 공통 검증 */
    private void validate(String title, String content) {
        if (title == null || content == null) {
            throw new IllegalArgumentException("제목과 내용을 입력해야 합니다.");
        }

        String t = title.trim();
        String c = content.trim();

        if (t.isEmpty() || c.isEmpty()) {
            throw new IllegalArgumentException("제목과 내용을 입력해야 합니다.");
        }

        if (t.length() > 45) {
            throw new IllegalArgumentException("제목은 45자 이하로 입력해주세요.");
        }
    }

    /**
     * 게시글 수정/삭제 권한 체크
     * - DB fk_user_id == null → 누구나 가능 (true)
     * - DB fk_user_id != null → 세션 fk_user_id와 같을 때만 가능
     */
    private boolean canModify(Board b, String fk_user_id) {

        // 소유자가 없는 글 (fk_user_id가 null) → 아무나 수정/삭제 가능
        if (b.fk_user_id == null) {
            return true;
        }

        // 소유자가 있는 글인데, 세션에 사용자 정보가 없다 → 권한 없음
        if (fk_user_id == null) {
            return false;
        }

        // 둘 다 있을 때는 동일한지 비교
        return b.fk_user_id.equals(fk_user_id);
    }

}
