package localhost.myapp.user;

/**
 * User DTO(데이터 전달 객체)
 * - DB의 user 테이블 한 행(row)을 저장하는 모델 클래스
 * - JavaBean 규약 준수 (private 필드 + public getter/setter)
 * - JSP/EL에서 ${user.id}처럼 getter가 자동 호출됨
 */
public class User {

    /** 고유 번호 (Primary Key, DB의 idx 컬럼) */
    public int idx;

    /** 사용자 아이디 (DB의 id 컬럼) */
    public String id;

    /** 비밀번호 (해시된 문자열, DB의 password 컬럼) */
    public String password;

    /** 이메일 주소 (DB의 email 컬럼) */
    public String email;

    /**
     * 가입일시
     * - 자바 필드명: regDate
     * - DB 컬럼명: reg_date
     */
    public String regDate;

    /** 기본 생성자 (JavaBean 규약) */
    public User() {
    }

    // ---------------------- Getter / Setter ----------------------

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }
}
