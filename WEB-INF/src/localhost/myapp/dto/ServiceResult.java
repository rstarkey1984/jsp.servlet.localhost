package localhost.myapp.dto;

/**
 * 공통 API 응답 DTO (제네릭 제거 버전)
 *
 * - 모든 API 응답을 동일한 형태로 통일
 * - success : 성공 여부
 * - message : 설명 메시지
 * - data : 실제 데이터 (Object)
 * - idx : 새로 생성된 리소스(PK) 번호 (게시글 생성 시 등)
 */
public class ServiceResult {

    /** 요청 성공 여부 */
    public boolean success;

    /** 성공 또는 실패 메시지 */
    public String message;

    /** 응답 데이터 (형식 제한 없음) */
    public Object data;

    /** 새로 생성된 리소스의 식별자 (예: 게시글 idx) */
    public Integer idx;

    /** 기본 생성자 */
    public ServiceResult() {
    }

    /** 전체 필드 설정 생성자 */
    public ServiceResult(boolean success, String message, Object data, Integer idx) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.idx = idx;
    }

    /** ✔ 성공 (메시지 + data 포함) */
    public static ServiceResult ok(String message, Object data) {
        return new ServiceResult(true, message, data, null);
    }

    /** ✔ 성공 (data만) */
    public static ServiceResult ok(Object data) {
        return new ServiceResult(true, null, data, null);
    }

    /** ✔ 성공 (메시지 + 생성된 idx 포함) */
    public static ServiceResult okWithId(String message, int idx) {
        return new ServiceResult(true, message, null, idx);
    }

    /** ✔ 성공 (메시지만) */
    public static ServiceResult ok(String message) {
        return new ServiceResult(true, message, null, null);
    }

    /** ❌ 실패 (메시지만) */
    public static ServiceResult fail(String message) {
        return new ServiceResult(false, message, null, null);
    }
}
