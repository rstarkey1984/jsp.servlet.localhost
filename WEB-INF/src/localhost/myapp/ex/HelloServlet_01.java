package localhost.myapp.ex;

import jakarta.servlet.http.*; // 서블릿 관련 HttpServlet, HttpServletRequest, HttpServletResponse 포함
import java.io.IOException; // 입출력 작업 중 발생할 수 있는 예외 처리를 위해 필요한 클래스
import java.io.PrintWriter; // PrintWriter 클래스를 사용하기 위해 java.io 패키지에서 불러옴

public class HelloServlet_01 extends HttpServlet {

    // GET 요청이 들어왔을 때 실행되는 메서드 (예: 브라우저 주소창에서 접속했을 때)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {  // IOException은 클라이언트와의 입출력 과정에서 발생할 수 있는 예외

        // 클라이언트(브라우저)에게 응답 데이터를 출력하기 위한 문자 기반 출력 스트림 가져오기
        // resp.getWriter()는 HTTP 응답(Response)의 본문에 텍스트를 작성할 수 있는 PrintWriter 객체를 반환함
        PrintWriter out = resp.getWriter();

        // 응답 데이터를 HTML 형식으로 설정, 문자 인코딩은 UTF-8로 설정
        resp.setContentType("text/html; charset=UTF-8");

        // 클라이언트(브라우저)에게 HTML 내용 전송
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>CSS 적용 예제</title>");
        out.println("<style>");
        out.println("html { color-scheme: light dark; }");
        out.println("body { width: 50em; margin: 0 auto;");
        out.println("font-family: Tahoma, Verdana, Arial, sans-serif; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>안녕, Servlet!</h1>");
        out.println("<h1>이 페이지는 web.xml에서 매핑되었습니다!</h1>");
        out.println("</body>");
        out.println("</html>");
    }
}