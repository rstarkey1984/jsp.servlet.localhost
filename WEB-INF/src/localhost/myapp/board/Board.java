package localhost.myapp.board;

/**
 * Board DTO(데이터 전달 객체)
 * - DB의 board 테이블 한 행(row)을 저장하는 모델 클래스
 * - JavaBean 규약 준수 (private 필드 + public getter/setter)
 * - JSP/EL에서 ${board.title} 형태로 getter 자동 호출
 */
public class Board {

    /** 게시글 번호 (Primary Key, DB의 idx 컬럼) */
    public int idx;

    /** 게시글 제목 (DB의 title 컬럼) */
    public String title;

    /** 게시글 내용 (DB의 content 컬럼) */
    public String content;

    /**
     * 게시글 등록일
     * - 자바 필드명: regDate
     * - DB 컬럼명: reg_date
     */
    public String regDate;

    /** 작성자 아이디 (DB의 fk_user_id 컬럼) */
    public String fk_user_id;

    /** 기본 생성자 (JavaBean 규약 준수) */
    public Board() {
    }

    // ---------------------- Getter / Setter ----------------------

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getFk_user_id() {
        return fk_user_id;
    }

    public void setFk_user_id(String fk_user_id) {
        this.fk_user_id = fk_user_id;
    }
}
