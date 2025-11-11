package localhost.myapp.user;

import java.sql.SQLException;
import localhost.myapp.dto.ServiceResult;

/**
 * User 도메인의 비즈니스 규칙을 담당.
 * - 검증/중복확인/예외 처리 일관화
 * - C/U/D: ServiceResult 반환
 */
public class UserService {
    private final UserDao dao;

    public UserService() {
        this.dao = new UserDao();
    }

    // 테스트/주입용
    public UserService(UserDao dao) {
        this.dao = dao;
    }

    /** 회원가입 */
    public ServiceResult register(String id, String password, String email) {
        try {
            validateRegister(id, password, email);
            // 중복 체크
            if (dao.existsById(id) != null) {
                return fail("이미 존재하는 아이디입니다.");
            }

            User u = new User();
            u.id = id.trim();
            u.password = password;
            u.email = email.trim();

            boolean ok = dao.insert(u);
            return ok ? ok("회원가입 성공") : fail("회원가입 실패");
        } catch (IllegalArgumentException e) {
            return fail(e.getMessage());
        } catch (SQLException e) {
            return fail("데이터베이스 오류: " + e.getMessage());
        }
    }

    /** 로그인 */
    public ServiceResult login(String id, String password) {
        try {
            if (id == null || id.trim().isEmpty() || password == null || password.isEmpty()) {
                return fail("아이디/비밀번호를 입력해 주세요.");
            }
            boolean ok = dao.login(id.trim(), password);
            return ok ? ok("로그인 성공") : fail("로그인 실패");
        } catch (SQLException e) {
            return fail("데이터베이스 오류: " + e.getMessage());
        }
    }

    // --- 내부 유틸 ---

    private void validateRegister(String id, String password, String email) {
        if (id == null || id.trim().length() < 4) {
            throw new IllegalArgumentException("아이디는 4자 이상이어야 합니다.");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("비밀번호는 4자 이상이어야 합니다.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("올바른 이메일을 입력해 주세요.");
        }
    }

    private ServiceResult ok(String msg) {
        ServiceResult r = new ServiceResult();
        r.success = true;
        r.message = msg;
        return r;
    }

    private ServiceResult fail(String msg) {
        ServiceResult r = new ServiceResult();
        r.success = false;
        r.message = msg;
        return r;
    }
}