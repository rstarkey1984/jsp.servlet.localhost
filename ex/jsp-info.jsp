<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%

// 방문 카운터 (세션 유지 예시)
Integer visit = (Integer) session.getAttribute("sess.visit");
if (visit == null) visit = 0;
session.setAttribute("sess.visit", ++visit);

%>
<!DOCTYPE html>
<html>
<head>
    <title>JSP 내장 객체 정보 출력</title>
    <style>
        body { font-family: Arial, sans-serif; width: 70%; margin: 20px auto; }
        h2 { color: #4A90E2; border-bottom: 2px solid #ddd; padding-bottom: 5px; }
        table { width: 100%; border-collapse: collapse; margin-bottom: 30px; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
        th { background-color: #f5f5f5; }
    </style>
</head>
<body>

<h2>1. request 객체 (HttpServletRequest)</h2>
<table>
    <tr><th>설명</th><th>값</th></tr>
    <tr><td>요청 URL</td><td><%= request.getRequestURL() %></td></tr>
    <tr><td>요청 URI</td><td><%= request.getRequestURI() %></td></tr>
    <tr><td>요청방식(Method)</td><td><%= request.getMethod() %></td></tr>
    <tr><td>클라이언트 IP</td><td><%= request.getRemoteAddr() %></td></tr>
    <tr><td>프로토콜</td><td><%= request.getProtocol() %></td></tr>
    <tr><td>쿼리스트링</td><td><%= request.getQueryString() %></td></tr>
</table>

<h2>2. response 객체 (HttpServletResponse)</h2>
<table>
    <tr><th>설명</th><th>값</th></tr>
    <tr><td>Content Type</td><td><%= response.getContentType() %></td></tr>
</table>

<h2>3. session 객체 (HttpSession)</h2>
<table>
    <tr><th>설명</th><th>값</th></tr>
    <tr><td>세션 ID</td><td><%= session.getId() %></td></tr>
    <tr><td>세션 종료시간</td><td><%= session.getMaxInactiveInterval() %></td></tr>
    <tr><td>생성 시간</td><td><%= new java.util.Date(session.getCreationTime()) %></td></tr>
    <tr><td>최근 접근 시간</td><td><%= new java.util.Date(session.getLastAccessedTime()) %></td></tr>
    <tr><td>방문 카운터</td><td><%= session.getAttribute("sess.visit") %></td></tr>
</table>

<h2>4. application 객체 (ServletContext)</h2>
<table>
    <tr><th>설명</th><th>값</th></tr>
    <tr><td>컨텍스트 경로</td><td><%= application.getContextPath() %></td></tr>
    <tr><td>서버 정보</td><td><%= application.getServerInfo() %></td></tr>
</table>

<h2>5. out 객체 (JspWriter)</h2>
<table>
    <tr><th>설명</th><th>값</th></tr>
    <tr><td>버퍼 크기</td><td><%= out.getBufferSize() %></td></tr>
    <tr><td>남은 버퍼</td><td><%= out.getRemaining() %></td></tr>
</table>

<h2>6. pageContext 객체 (PageContext)</h2>
<table>
    <tr><th>설명</th><th>값</th></tr>
    <tr><td>세션 객체</td><td><%= pageContext.getSession() %></td></tr>
    <tr><td>application 객체</td><td><%= pageContext.getServletContext() %></td></tr>
</table>

<h2>7. config 객체 (ServletConfig)</h2>
<table>
    <tr><th>설명</th><th>값</th></tr>
    <tr><td>서블릿 이름</td><td><%= config.getServletName() %></td></tr>
</table>

<h2>8. page 객체 (this, JSP 자체)</h2>
<table>
    <tr><th>설명</th><th>값</th></tr>
    <tr><td>JSP가 변환된 실제 클래스</td><td><%= this.getClass() %></td></tr>
</table>

<h2>9. exception 객체 (Throwable) — 오류 페이지에서만 사용 가능</h2>
<%
    if (exception != null) {
%>
    <table>
        <tr><th>예외 메시지</th><td><%= exception.getMessage() %></td></tr>
    </table>
<%
    } else {
%>
    <p>예외 없음 (정상 페이지입니다)</p>
<%
    }
%>

</body>
</html>
