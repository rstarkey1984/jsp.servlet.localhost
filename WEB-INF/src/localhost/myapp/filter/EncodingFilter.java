package localhost.myapp.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter("/*") // 모든 요청에 필터 적용
public class EncodingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // System.out.println("EncodingFilter 초기화");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        // 1) 요청(request)에 UTF-8 인코딩 설정
        req.setCharacterEncoding("UTF-8");
        // System.out.println("EncodingFilter: 요청 인코딩 UTF-8 설정");

        // 2) 다음 필터 또는 서블릿으로 요청 전달
        chain.doFilter(req, resp);

        // 3) 응답(response) 후처리 (필요하면)
        // System.out.println("EncodingFilter: 응답 처리 후 단계");
    }

    @Override
    public void destroy() {
        // System.out.println("EncodingFilter 종료");
    }

}