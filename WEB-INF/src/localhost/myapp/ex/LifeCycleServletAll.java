// package 선언: 이 클래스가 속한 패키지 이름을 지정합니다.
package localhost.myapp.ex; // 패키지 구조에 맞춰 소스 파일 경로도 ex/ 디렉터리여야 합니다.

// Servlet/Jakarta EE 관련 클래스들을 임포트합니다.
import jakarta.servlet.ServletException; // 서블릿 수명주기 메서드에서 던지는 예외 타입
import jakarta.servlet.ServletInputStream; // 요청 바디를 바이트 스트림으로 읽을 때 사용
import jakarta.servlet.http.Cookie; // HTTP 쿠키 표현 객체
import jakarta.servlet.http.HttpServlet; // 서블릿의 기본 추상 클래스
import jakarta.servlet.http.HttpServletRequest; // 요청 정보를 담는 객체
import jakarta.servlet.http.HttpServletResponse; // 응답을 구성하는 객체
import jakarta.servlet.http.HttpSession; // 세션 상태를 다루는 객체

// 자바 표준 라이브러리 임포트
import java.io.BufferedReader; // 문자 입력 버퍼링 리더
import java.io.IOException; // 입출력 예외
import java.io.PrintWriter; // 문자 출력 라이터
import java.nio.charset.StandardCharsets; // 표준 문자셋(UTF-8 등)
import java.time.LocalDateTime; // 현재 시각 표현(로그/표시용)
import java.util.Enumeration; // 열거형(헤더/로케일 등 순회)
import java.util.Locale; // 로케일 정보
import java.util.Map; // 파라미터 맵 등 키-값 구조
import java.util.StringJoiner; // 문자열 조인 유틸리티
import jakarta.servlet.annotation.*; // @WebServlet 같은 어노테이션 사용을 위해 필요

/**
 * LifeCycleServlet
 *
 * 목적:
 *  - Servlet 생명주기(init -> service -> doGet/doPost -> destroy) 흐름을 로그로 명확히 보여준다.
 *  - HttpServletRequest에 들어있는 각종 정보(요청라인, 헤더, 파라미터, 쿠키, 세션, 바디)를 한 페이지로 출력한다.
 *  - HttpServletResponse로 응답을 구성하는 방법(상태코드, 헤더, 쿠키, 본문, 리다이렉트, 에러)을 한 곳에서 실습한다.
 *
 * 테스트 팁(예시 URL은 컨텍스트 루트에 따라 조정):
 *  - GET 기본:              /life
 *  - 리다이렉트:            /life?action=redirect&to=/life
 *  - 에러 응답:             /life?action=error&code=403&msg=Forbidden
 *  - 쿠키 설정:             /life?setCookie=hello:world (다음 요청에서 쿠키 수신 확인)
 *  - POST JSON 바디 미리보기: curl -X POST -H "Content-Type: application/json" -d '{"a":1,"test":"한글"}' http://java.localhost/ex/life
 *
 * 주의:
 *  - 이 클래스는 @WebServlet 대신 web.xml로 매핑해도 되고, @WebServlet("/ex/life")를 붙여도 된다.
 *  - Tomcat 10+ (jakarta.* 네임스페이스) 기준.
 */
@WebServlet("/ex/life-all") // 어노테이션으로 URL 패턴을 연결하고 싶을 때 주석 해제
public class LifeCycleServletAll extends HttpServlet { // HttpServlet을 상속하여 서블릿 구현 시작

    /**
     * init()
     * - 서블릿 인스턴스가 생성된 직후 "최초 1회" 호출되는 초기화 메서드.
     * - DB 커넥션 풀 준비, 설정값 로드 등 초기 작업을 수행하기 좋다.
     */
    @Override // 부모 클래스의 메서드를 재정의함을 표시
    public void init() throws ServletException { // 초기화 로직을 구현
        System.out.println("[LifeCycleServlet] init() - " + LocalDateTime.now()); // 초기화 시점 로그 출력
    } // init() 끝

    /**
     * service()
     * - 모든 HTTP 요청을 가장 먼저 받는 메서드.
     * - 여기서 doGet(), doPost() 등으로 분기되며, super.service()가 그 역할을 수행한다.
     * - 로그를 남겨 요청 흐름(메서드/URI/시간)을 확인할 수 있게 한다.
     */
    @Override // 부모의 service()를 재정의
    protected void service(HttpServletRequest req, HttpServletResponse resp) // 요청/응답 객체를 받음
            throws ServletException, IOException { // 예외 전파

        System.out.println("[LifeCycleServlet] service() - " // 로그 프리픽스
                + req.getMethod() + " " + req.getRequestURI() // 요청 메서드와 URI 기록
                + " @ " + LocalDateTime.now()); // 현재 시간 기록

        // 중요: 부모의 service()를 호출해야 doGet/doPost가 정상적으로 실행된다.
        super.service(req, resp); // HTTP 메서드에 따라 doGet/doPost 등으로 위임
    } // service() 끝

    /**
     * doGet()
     * - 요청 정보(HttpServletRequest) 상세를 모아서 텍스트로 출력.
     * - 응답(HttpServletResponse) 구성(상태/헤더/쿠키/본문) 예시도 포함.
     * - 쿼리 파라미터로 특수 동작을 제공:
     *     ?action=redirect&to=/life     -> sendRedirect 테스트
     *     ?action=error&code=400&msg=.. -> sendError 테스트
     *     ?setCookie=foo:bar            -> 응답 쿠키 설정
     */
    @Override // GET 요청 처리 메서드 재정의
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) // GET 요청 진입점
            throws IOException { // 입출력 예외 전파

        // -------------------- (1) 응답 메타 구성 --------------------
        // 출력 인코딩 및 MIME 설정(Writer 사용 시 인코딩 일치 필수)
        resp.setCharacterEncoding("UTF-8"); // 응답 문자 인코딩을 UTF-8로 지정
        resp.setContentType("text/plain; charset=UTF-8"); // 텍스트/UTF-8 MIME 지정

        // 커스텀 헤더 추가 예시
        resp.setHeader("X-Example", "LifeCycleServlet"); // 사용자 정의 응답 헤더 설정

        // 쿼리로 쿠키 설정(?setCookie=key:value)
        String cookieParam = req.getParameter("setCookie"); // setCookie 파라미터 조회
        if (cookieParam != null && cookieParam.contains(":")) { // "키:값" 형태인지 검사
            String[] kv = cookieParam.split(":", 2); // 첫 콜론 기준으로 분리
            Cookie c = new Cookie(kv[0], kv[1]); // 쿠키 객체 생성
            c.setPath("/");          // 애플리케이션 전체 경로에서 유효
            c.setHttpOnly(true);     // JS 접근 차단(보안)
            // c.setSecure(true);    // HTTPS에서만 전송(환경에 맞게)
            c.setMaxAge(60 * 10);    // 10분 동안 유지
            resp.addCookie(c);       // 응답에 쿠키 추가
        } // 쿠키 설정 끝

        // -------------------- (2) 특수 동작: 리다이렉트/에러 --------------------
        String action = req.getParameter("action"); // action 파라미터로 동작 분기
        if ("redirect".equalsIgnoreCase(action)) { // redirect 요청인지 확인
            // 리다이렉트 URL이 없으면 기본은 /life로 보냄
            String to = req.getParameter("to"); // 목적지 URL 파라미터
            resp.sendRedirect(to != null ? to : req.getContextPath() + "/life"); // 리다이렉트 수행
            return; // 리다이렉트로 응답 완료되었으므로 메서드 종료
        } else if ("error".equalsIgnoreCase(action)) { // error 요청인지 확인
            // 에러 코드/메시지 전송 (예: 400, 403 등)
            int code = parseIntOrDefault(req.getParameter("code"), 400); // 코드 파싱(기본 400)
            String msg = req.getParameter("msg"); // 메시지(선택)
            if (msg != null) resp.sendError(code, msg); // 코드+메시지 전송
            else resp.sendError(code); // 코드만 전송
            return; // 에러 응답으로 종료
        } // 특수 동작 분기 끝

        // -------------------- (3) 요청 정보 수집 --------------------
        StringBuilder out = new StringBuilder(8_192); // 출력 버퍼(가변 문자열)

        out.append("=== Java Servlet Request/Response Deep-Dive ===\n\n"); // 헤더 타이틀
        out.append("# Time\n") // 섹션 제목
           .append("now: ").append(LocalDateTime.now()).append('\n') // 현재 시각
           .append('\n'); // 빈 줄
           
        // A. 요청 라인(Request Line) / 경로 관련 정보
        out.append("# Request Line\n"); // HTTP 요청 첫 줄(메서드, 경로, 프로토콜 정보)

        // 1) 요청 메서드(GET, POST 등)
        // - 클라이언트가 서버에 어떤 작업을 요청했는지 나타냄.
        out.append("method           : ").append(req.getMethod()).append('\n'); 

        // 2) 프로토콜 버전 (HTTP/1.1, HTTP/2 등)
        // - 브라우저와 서버가 사용하는 HTTP 버전 정보
        out.append("protocol         : ").append(req.getProtocol()).append('\n');     

        // 3) 스킴(scheme) - http 또는 https
        // - 보안 연결 여부를 알 수 있음 (https면 SSL/TLS 암호화 통신)
        out.append("scheme           : ").append(req.getScheme()).append('\n');

        // 4) requestURI - 도메인 제외 경로 부분만 반환
        //   예) http://localhost:8080/myapp/ex/life -> /myapp/ex/life
        out.append("requestURI       : ").append(req.getRequestURI()).append('\n');

        // 5) requestURL - 도메인 + 포트까지 포함된 전체 URL (StringBuffer 타입)
        //   예) http://localhost:8080/myapp/ex/life
        out.append("requestURL       : ").append(req.getRequestURL()).append('\n');

        // 6) queryString - ? 뒤에 오는 파라미터 문자열 원본
        //   예) ?name=kim&age=20 → "name=kim&age=20"
        out.append("queryString      : ").append(nullToEmpty(req.getQueryString())).append('\n');

        // 7) contextPath - 애플리케이션의 루트 경로(=프로젝트 이름)
        //   예) war 파일 이름이 myapp이면 "/myapp", ROOT일 경우 ""
        out.append("contextPath      : ").append(req.getContextPath()).append('\n');

        // 8) servletPath - web.xml 또는 @WebServlet에서 매핑된 경로
        //   예) @WebServlet("/ex/life") → "/ex/life"
        out.append("servletPath      : ").append(req.getServletPath()).append('\n');

        // 9) pathInfo - servletPath 뒤에 추가로 붙은 경로 정보 (REST API 등에서 사용)
        //   예) "/ex/user/10" 에서 "/ex/user/*" 매핑이면 pathInfo="/10"
        //   → null일 수 있으므로 nullToEmpty()로 처리
        out.append("pathInfo         : ").append(nullToEmpty(req.getPathInfo())).append('\n');

        // 10) pathTranslated - pathInfo가 실제 서버(파일 시스템) 경로로 변환된 값
        //   예) /var/www/.../life/10 이런 절대경로가 반환될 수 있음
        out.append("pathTranslated   : ").append(nullToEmpty(req.getPathTranslated())).append('\n');
        out.append('\n'); // 보기 좋게 줄 띄우기

        // B. 서버(로컬) / 클라이언트 측 정보
        out.append("# Endpoints\n"); // 네트워크 관련 정보 섹션

        // 1) serverName - 현재 요청을 처리 중인 서버의 도메인 또는 IP
        //   예) localhost, 192.168.0.10, www.example.com
        out.append("serverName       : ").append(req.getServerName()).append('\n');

        // 2) serverPort - 서버가 요청을 받은 포트 번호
        //   예) 기본값 80(HTTP), 443(HTTPS), 톰캣 기본 8080
        out.append("serverPort       : ").append(req.getServerPort()).append('\n');

        // 3) localAddr - 서버가 실제로 바인딩된 IP 주소
        //   예) 127.0.0.1 또는 192.168.x.x
        out.append("localAddr        : ").append(req.getLocalAddr()).append('\n');

        // 4) localName - localAddr에 해당하는 호스트 이름 (DNS 역조회 결과)
        out.append("localName        : ").append(req.getLocalName()).append('\n');

        // 5) localPort - 서버가 실제로 요청을 수신한 포트 (클라이언트에서 들어온 포트)
        out.append("localPort        : ").append(req.getLocalPort()).append('\n');

        // 6) remoteAddr - 클라이언트(요청 보낸 사람)의 IP
        //   예) 127.0.0.1 (로컬), 또는 실제 사용자 IP
        out.append("remoteAddr       : ").append(req.getRemoteAddr()).append('\n');

        // 7) remoteHost - 클라이언트 IP를 호스트 이름으로 변환한 값 (DNS reverse lookup)
        //   주로 속도가 느릴 수 있어 remoteAddr을 더 많이 사용
        out.append("remoteHost       : ").append(req.getRemoteHost()).append('\n');

        // 8) remotePort - 클라이언트가 요청을 보낸 소켓의 포트 번호
        //   예) 사용자가 54321 포트로 요청을 보내왔을 수 있음
        out.append("remotePort       : ").append(req.getRemotePort()).append('\n');

        out.append('\n'); // 섹션 정리용 빈 줄

        // C. 헤더들
        out.append("# Headers\n"); // 섹션 제목
        Enumeration<String> headerNames = req.getHeaderNames(); // 헤더 이름 열거
        while (headerNames.hasMoreElements()) { // 모든 헤더 순회
            String h = headerNames.nextElement(); // 헤더 이름
            out.append(h).append(": ").append(req.getHeader(h)).append('\n'); // "이름: 값" 형식 출력
        }
        out.append('\n'); // 빈 줄

        // D. 콘텐츠/로케일/인코딩
        out.append("# Content/Locale\n"); // 섹션 제목
        out.append("contentType      : ").append(nullToEmpty(req.getContentType())).append('\n'); // 콘텐츠 타입
        out.append("contentLength    : ").append(req.getContentLength()).append('\n'); // 길이(-1 가능)
        out.append("characterEncoding: ").append(nullToEmpty(req.getCharacterEncoding())).append('\n'); // 요청 인코딩
        out.append("Locales (Accept-Language): "); // 클라이언트가 선호하는 로케일 목록
        StringJoiner locJoiner = new StringJoiner(", "); // 로케일들을 콤마로 조인
        Enumeration<Locale> locales = req.getLocales(); // 로케일 열거
        while (locales.hasMoreElements()) locJoiner.add(locales.nextElement().toLanguageTag()); // 태그 형태로 추가
        out.append(locJoiner.length() == 0 ? "(none)" : locJoiner.toString()).append('\n'); // 출력
        out.append('\n'); // 빈 줄

        // E. 파라미터(쿼리/폼) — 다중값 포함
        out.append("# Parameters\n"); // 섹션 제목
        Map<String, String[]> pmap = req.getParameterMap(); // 파라미터 맵 조회
        if (pmap.isEmpty()) { // 파라미터가 없는 경우
            out.append("(no parameters)\n"); // 없음 표시
        } else { // 있는 경우
            for (Map.Entry<String, String[]> e : pmap.entrySet()) { // 각 항목 순회
                out.append(e.getKey()).append(" = "); // 키 출력
                StringJoiner sj = new StringJoiner(", "); // 값들을 조인
                for (String v : e.getValue()) sj.add(v); // 배열 값 추가
                out.append('[').append(sj.toString()).append(']').append('\n'); // [v1, v2] 형식
            }
        }
        out.append('\n'); // 빈 줄

        // F. 쿠키
        out.append("# Cookies\n"); // 섹션 제목
        Cookie[] cookies = req.getCookies(); // 요청에 포함된 쿠키 배열
        if (cookies == null || cookies.length == 0) { // 쿠키 없으면
            out.append("(no cookies)\n"); // 없음 표시
        } else { // 쿠키가 있으면
            for (Cookie c : cookies) { // 각 쿠키 순회
                out.append(c.getName()).append('=').append(c.getValue()) // 이름=값
                   .append(" ; Path=").append(c.getPath()) // 경로
                   .append(" ; HttpOnly=").append(c.isHttpOnly()) // HttpOnly 표시
                   .append(" ; MaxAge=").append(c.getMaxAge()) // 만료(초)
                   .append('\n'); // 줄바꿈
            }
        }
        out.append('\n'); // 빈 줄

        // G. 세션
        out.append("# Session\n"); // 섹션 제목
        // true: 세션이 없으면 새로 생성. false: 없으면 null 반환.
        HttpSession session = req.getSession(true); // 세션을 가져오거나 새로 만듦

        // 방문 카운터 (세션 유지 예시)
        Integer visit = (Integer) session.getAttribute("sess.visit"); // 방문 수 조회
        if (visit == null) visit = 0; // 초기값 설정
        session.setAttribute("sess.visit", ++visit); // 1 증가시켜 저장

        out.append("sessionId        : ").append(session.getId()).append('\n'); // 세션 ID
        out.append("isNew            : ").append(session.isNew()).append('\n');        // 신규 생성 여부
        out.append("creationTime     : ").append(session.getCreationTime()).append('\n');        // 생성 시각(ms)
        out.append("lastAccessedTime : ").append(session.getLastAccessedTime()).append('\n'); // 최근 접근 시각(ms)
        out.append("maxInactiveInterval(s): ").append(session.getMaxInactiveInterval()).append('\n');        // 유휴 타임아웃(초)
        // 샘플 세션 속성 저장/조회
        if (session.getAttribute("visitedAt") == null) { // 처음 방문이면
            session.setAttribute("visitedAt", LocalDateTime.now().toString()); // 방문 시각 저장
        }
        out.append("attr.visitedAt   : ").append(String.valueOf(session.getAttribute("visitedAt"))).append('\n'); // 저장값 표시
        out.append("sess.visit       : ").append(session.getAttribute("sess.visit")).append('\n'); // 카운터 표시
        out.append('\n'); // 빈 줄

        // H. 바디 미리보기 (GET은 보통 바디가 없지만 예외 처리)
        out.append("# Body (first 2048 bytes if present)\n"); // 섹션 제목
        if (req.getContentLength() > 0 || req.getHeader("Transfer-Encoding") != null) { // 바디 존재 판단
            // 텍스트/바이너리 모두 대응하는 헬퍼 사용
            out.append(readBodyPreview(req)).append('\n'); // 헬퍼 메서드로 일부 미리보기
        } else { // 바디가 없을 때
            out.append("(no body)\n"); // 없음 표시
        }
        out.append('\n'); // 빈 줄

        // -------------------- (4) 응답 본문/상태 전송 --------------------
        resp.setStatus(HttpServletResponse.SC_OK); // 200 상태 코드 설정

        // 텍스트 본문은 Writer로 전송(인코딩 주의)
        try (PrintWriter pw = resp.getWriter()) { // 자동 close 되는 PrintWriter 획득
            pw.print(out.toString()); // 앞서 구성한 본문 출력
            pw.println("# Response Tips"); // 추가 안내 섹션
            pw.println("- resp.setStatus(int), setHeader(String,String), addCookie(Cookie)"); // 상태/헤더/쿠키 안내
            pw.println("- resp.sendRedirect(url) for redirect"); // 리다이렉트 안내
            pw.println("- resp.sendError(code[, message]) for error"); // 에러 응답 안내
            pw.println("- resp.getWriter() for text, resp.getOutputStream() for binary"); // 텍스트/바이너리 구분
            pw.flush(); // 버퍼 강제 플러시
        } // try-with-resources 블록 종료(Writer 닫힘)

        // 참고) 바이너리 응답 예시(주석)
        // resp.setContentType("application/octet-stream"); // 바이너리 MIME 예시
        // try (ServletOutputStream os = resp.getOutputStream()) { // 바이트 출력 스트림
        //     os.write(new byte[]{1,2,3,4}); // 바이트 전송
        // }
    } // doGet() 끝

    /**
     * doPost()
     * - POST 요청에서 파라미터와 바디를 어떻게 다루는지 보여준다.
     * - 컨테이너가 파라미터를 파싱해주는 경우(req.getParameter*)와
     *   Raw Body를 직접 읽는 경우(req.getReader()/getInputStream())의 차이를 함께 출력.
     */
    @Override // POST 요청 처리 메서드 재정의
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) // POST 진입점
            throws IOException { // 입출력 예외 전파

        // 응답 인코딩/MIME 및 샘플 헤더
        resp.setCharacterEncoding("UTF-8"); // 응답 인코딩 지정
        resp.setContentType("text/plain; charset=UTF-8"); // 텍스트/UTF-8 지정
        resp.setHeader("X-Example", "LifeCycleServlet-POST"); // 커스텀 헤더

        StringBuilder out = new StringBuilder(4_096); // 출력 버퍼
        out.append("=== POST Received ===\n"); // 제목
        out.append("contentType      : ").append(nullToEmpty(req.getContentType())).append('\n'); // 요청 콘텐츠 타입
        out.append("characterEncoding: ").append(nullToEmpty(req.getCharacterEncoding())).append('\n'); // 요청 인코딩
        out.append("contentLength    : ").append(req.getContentLength()).append("\n\n"); // 길이

        // 컨테이너가 파싱해준 파라미터(폼/쿼리)
        out.append("# Parameters (parsed by container)\n"); // 섹션 제목
        Map<String, String[]> pmap = req.getParameterMap(); // 파라미터 맵
        if (pmap.isEmpty()) out.append("(no parameters)\n"); // 없으면 표시
        else { // 있으면 순회
            for (Map.Entry<String, String[]> e : pmap.entrySet()) { // 항목 순회
                out.append(e.getKey()).append(" = "); // 키
                StringJoiner sj = new StringJoiner(", "); // 값 조인
                for (String v : e.getValue()) sj.add(v); // 값 추가
                out.append('[').append(sj.toString()).append(']').append('\n'); // 출력
            }
        }
        out.append('\n'); // 빈 줄

        // Raw Body 미리보기(최대 4096바이트)
        out.append("# Raw Body (first 4096 bytes)\n"); // 섹션 제목
        out.append(readBodyPreview(req, 4096)).append('\n'); // 본문 미리보기

        try (PrintWriter pw = resp.getWriter()) { // Writer 획득(자동 close)
            pw.print(out.toString()); // 결과 출력
        } // try-with-resources 종료
    } // doPost() 끝

    /**
     * destroy()
     * - 서블릿이 내려갈 때(컨테이너 종료/언로드) 1회 호출.
     * - 열려있는 리소스 정리, 로그 마무리 등에 사용.
     */
    @Override // 서블릿 소멸 훅
    public void destroy() { // 종료 시 호출
        System.out.println("[LifeCycleServlet] destroy() - " + LocalDateTime.now()); // 소멸 시점 로그
    } // destroy() 끝

    // ==================== 유틸 메서드 ====================

    /** 바디 미리보기(텍스트 우선, 실패 시 바이너리로 헥사 출력) */
    private static String readBodyPreview(HttpServletRequest req) throws IOException { // 오버로드: 기본 2048바이트
        return readBodyPreview(req, 2048); // 기본 길이로 委任
    } // readBodyPreview(req) 끝

    /** 바디 미리보기(최대 maxBytes 바이트) */
    private static String readBodyPreview(HttpServletRequest req, int maxBytes) throws IOException { // 바디 일부 읽기
        // 요청 인코딩이 지정되지 않았다면 UTF-8로 설정(일반 텍스트 처리 대비)
        req.setCharacterEncoding(req.getCharacterEncoding() != null
                ? req.getCharacterEncoding()
                : StandardCharsets.UTF_8.name()); // 인코딩 보정

        // 1) 문자 스트림으로 시도
        StringBuilder sb = new StringBuilder(); // 텍스트 버퍼
        try (BufferedReader br = req.getReader()) { // 문자 리더 얻기(컨테이너가 인코딩 적용)
            int total = 0; // 누적 바이트(문자 기준이지만 대략)
            char[] buf = new char[512]; // 임시 버퍼
            int n; // 읽은 개수
            while ((n = br.read(buf)) != -1 && total < maxBytes) { // 끝/한도 전까지 반복
                int toAppend = Math.min(n, maxBytes - total); // 남은 한도만큼만 추가
                sb.append(buf, 0, toAppend); // 버퍼에서 복사
                total += toAppend; // 누적 갱신
            }
            // 더 읽을 게 있다면 생략 표기
            if (br.read() != -1) sb.append("\n...[truncated]"); // 한도 초과 표시
            return sb.toString(); // 텍스트 결과 반환
        } catch (IllegalStateException e) { // getReader() 사용 불가(이미 사용/바이너리 등)
            // getReader()가 이미 사용되었거나(사양 위반) 바이너리라면 InputStream으로 시도
            StringBuilder hex = new StringBuilder(); // 헥사 문자열 버퍼
            try (ServletInputStream is = req.getInputStream()) { // 바이트 스트림 얻기
                int total = 0; // 누적 바이트 수
                byte[] b = new byte[512]; // 임시 바이트 버퍼
                int n; // 읽은 바이트 수
                while ((n = is.read(b)) != -1 && total < maxBytes) { // 끝/한도 전까지 반복
                    int toAppend = Math.min(n, maxBytes - total); // 남은 한도 산출
                    for (int i = 0; i < toAppend; i++) { // 바이트별 반복
                        hex.append(String.format("%02x ", b[i])); // 두 자리 헥사로 추가
                    }
                    total += toAppend; // 누적 갱신
                }
                if (is.read() != -1) hex.append("...[truncated]"); // 더 남았으면 생략 표시
            }
            return "(binary) " + hex.toString(); // 바이너리 표기와 함께 반환
        }
    } // readBodyPreview(req,int) 끝

    /** null 문자열을 빈 문자열로 안전 변환 */
    private static String nullToEmpty(String s) { // NPE 예방 유틸
        return (s == null) ? "" : s; // null이면 빈 문자열 반환
    } // nullToEmpty 끝

    /** 안전한 정수 파싱(실패 시 기본값 반환) */
    private static int parseIntOrDefault(String s, int def) { // 숫자 파싱 도우미
        try { return Integer.parseInt(s); } catch (Exception e) { return def; } // 실패 시 기본값
    } // parseIntOrDefault 끝
} // 클래스 LifeCycleServlet 끝
