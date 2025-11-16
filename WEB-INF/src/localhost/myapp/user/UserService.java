package localhost.myapp.user;

import java.sql.SQLException;
import localhost.myapp.dto.ServiceResult;

/**
 * User 도메인의 비즈니스 규칙(Service Layer)을 담당.
 *
 * ✔ Controller(Servlet) ↔ Service ↔ DAO 구조에서 "Service" 역할
 * - 파라미터 검증
 * - 중복 확인
 * - 예외 처리 일관화
 * - DAO 호출 결과를 ServiceResult로 감싸 일관된 응답 제공
 */
public class UserService {
    private final UserDao dao; // 데이터베이스 접근 객체(DAO)

    public UserService() {
        this.dao = new UserDao();
    }

    // 테스트용 또는 외부에서 DAO 주입 가능하도록 하는 생성자
    public UserService(UserDao dao) {
        this.dao = dao;
    }

    /**
     * -----------------------------
     * 🚀 회원가입 처리
     * - 입력값 검증
     * - 아이디 중복 체크
     * - DB insert
     * - ServiceResult 로 성공/실패 메시지 반환
     * ------------------------------
     */
    public ServiceResult register(String id, String password, String email) {
        try {
            // 1) 기본 형식 검증
            validateRegister(id, password, email);

            // 2) 아이디 중복 검사
            if (dao.existsById(id) != null) {
                return ServiceResult.fail("이미 존재하는 아이디입니다.");
            }

            // 3) User 객체 생성
            User u = new User();
            u.id = id.trim();
            u.password = password; // DAO에서 SHA2 해시 처리
            u.email = email.trim();

            // 4) DB 저장
            boolean ok = dao.insert(u);

            // 5) 결과 반환 (data 사용 안 하므로 메시지만)
            return ok
                    ? ServiceResult.ok("회원가입 성공")
                    : ServiceResult.fail("회원가입 실패");

        } catch (IllegalArgumentException e) {
            // validateRegister()에서 발생된 예외 처리
            return ServiceResult.fail(e.getMessage());

        } catch (SQLException e) {
            // DB 관련 예외 처리
            return ServiceResult.fail("데이터베이스 오류: " + e.getMessage());
        }
    }

    /**
     * -----------------------------
     * 🔐 로그인 처리
     * - 기본값 검증
     * - DAO.login(id, pw) 호출
     * - 성공/실패를 ServiceResult 로 반환
     * ------------------------------
     */
    public ServiceResult login(String id, String password) {
        try {
            // 필수 입력값 체크
            if (id == null || id.trim().isEmpty() ||
                    password == null || password.isEmpty()) {

                return ServiceResult.fail("아이디/비밀번호를 입력해 주세요.");
            }

            // DAO에서 비밀번호 SHA2 비교
            boolean ok = dao.login(id.trim(), password);

            // data 사용 안 하므로 메시지만 반환
            return ok
                    ? ServiceResult.ok("로그인 성공")
                    : ServiceResult.fail("로그인 실패");

        } catch (SQLException e) {
            return ServiceResult.fail("데이터베이스 오류: " + e.getMessage());
        }
    }

    /*
     * ========================================
     * 🔽 내부 유틸 메서드 (Service 내부용)
     * ========================================
     */

    /** 회원가입 입력값 검증 */
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
}
