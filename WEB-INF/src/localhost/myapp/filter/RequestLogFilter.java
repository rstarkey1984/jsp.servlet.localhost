package localhost.myapp.filter; // 필터 클래스가 속한 패키지 선언

import jakarta.servlet.*; // Filter, FilterChain, ServletRequest 등 기본 서블릿 인터페이스
import jakarta.servlet.annotation.WebFilter; // @WebFilter 어노테이션 사용을 위한 import
import jakarta.servlet.http.*; // HttpServletRequest, Cookie 클래스 사용
import java.io.IOException; // IOException 예외
import java.util.*; // Enumeration, Arrays 등 유틸 클래스

@WebFilter("/*") // 모든 요청 URL( /* )에 대해 이 필터가 실행되도록 설정
public class RequestLogFilter implements Filter { // Filter 인터페이스 구현 클래스 정의 시작

    @Override
    public void init(FilterConfig filterConfig) { // 필터 초기화 시 실행되는 메서드
        // 초기화할 내용이 없어서 비워둠
    }

    @Override
    public void doFilter(ServletRequest request, // 클라이언트 요청 객체 (HttpServletRequest의 부모 타입)
            ServletResponse response, // 클라이언트 응답 객체 (HttpServletResponse의 부모 타입)
            FilterChain chain) // 다음 필터 또는 서블릿으로 넘기는 체인 객체
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request; // ServletRequest를 HttpServletRequest로 다운캐스팅

        System.out.println("\n========== REQUEST DEBUG =========="); // 요청 디버그 로그 시작 출력
        System.out.println("URI: " + req.getRequestURI()); // 요청된 URI 출력
        System.out.println("Method: " + req.getMethod()); // 요청 메서드(GET/POST 등) 출력

        // -------------------- Parameters 출력 --------------------
        System.out.println("\n[Parameters]"); // 파라미터 섹션 제목 출력
        req.getParameterMap().forEach( // request.getParameterMap() → 모든 파라미터(key/value) 조회
                (k, v) -> System.out.println("  " + k + " = " + Arrays.toString(v)) // k: 이름, v: 값 배열 형태 출력
        );

        // // -------------------- Headers 출력 --------------------
        // System.out.println("\n[Headers]"); // 헤더 섹션 제목 출력
        // Enumeration<String> headerNames = req.getHeaderNames(); // 모든 헤더 이름을 가져오는
        // Enumeration 객체
        // while (headerNames.hasMoreElements()) { // 헤더가 더 있을 때까지 반복
        // String name = headerNames.nextElement(); // 헤더 이름 하나 가져오기
        // System.out.println(" " + name + ": " + req.getHeader(name)); // 헤더 이름과 값을 출력
        // }

        // // -------------------- Cookies 출력 --------------------
        // System.out.println("\n[Cookies]"); // 쿠키 섹션 제목 출력
        // Cookie[] cookies = req.getCookies(); // 요청에 포함된 모든 쿠키 가져오기
        // if (cookies != null) { // 쿠키가 존재할 경우
        // for (Cookie c : cookies) { // 모든 쿠키 반복
        // System.out.println(" " + c.getName() + ": " + c.getValue()); // 쿠키 이름 = 값 출력
        // }
        // } else { // 쿠키가 없을 경우
        // System.out.println(" (no cookies)"); // "쿠키 없음" 출력
        // }

        // System.out.println("===================================\n"); // 로그 구분선 출력

        // -------------------- 필터 체인 계속 진행 --------------------
        chain.doFilter(request, response); // 다음 필터 또는 최종 서블릿으로 요청/응답 전달
    }

    @Override
    public void destroy() { // 필터 종료 시 실행(리소스 정리용)
        // 정리할 내용이 없어서 비워둠
    }
}