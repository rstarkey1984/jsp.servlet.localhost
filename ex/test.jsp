<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.time.LocalTime" %>

<%-- 
    JSP 선언문 (Declaration)
    - JSP가 서블릿 클래스로 변환될 때 멤버 변수/메소드로 들어감
--%>
<%! 
    int visitCount = 0;

    public String greetingMessage(String name) {
        return "Hello, " + name + "!";
    }
%>

<%
    // 스크립틀릿 (Scriptlet) — Java 코드 작성 가능
    visitCount++;

    String name = request.getParameter("name");
    if (name == null || name.trim().equals("")) {
        name = "Guest";
    }

    // 현재 시간
    LocalTime time = LocalTime.now();
%>

<!DOCTYPE html>
<html>
<head>
    <title>JSP 문법 예제</title>
</head>
<body>
    <h2>JSP 기본 문법 (JSTL 없이)</h2>

    <p><strong>1. 표현식(Expression):</strong>  
      이름: <%= name %></p>

    <p><strong>2. 선언문 함수 결과:</strong>  
      <%= greetingMessage(name) %></p>

    <p><strong>3. 현재 시간 (import 사용):</strong>  
      <%= time %></p>

    <p><strong>4. 방문 횟수 (전역 변수):</strong>  
      <%= visitCount %> 번째 방문입니다.</p>

    <%-- EL(Expression Language) 사용 --%>
    <p><strong>5. EL 사용:</strong></p>
    <% request.setAttribute("userName", name); %>
    <ul>
        <li>request에 저장된 이름 → ${userName}</li>
        <li>요청 파라미터 name → ${param.name}</li>
        <li>빈 값 또는 null인지 체크 → ${empty param.name}</li>
    </ul>

    <%-- 입력 폼 (name 파라미터 전달용) --%>
    <form method="get" action="test.jsp">
        <input type="text" name="name" placeholder="이름 입력">
        <button type="submit">전송</button>
    </form>
</body>
</html>