<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%  
    // ================================  
    // 🟡 Scriptlet 영역 (JSP 내 Java 코드 실행 영역)  
    // ================================

    // request 객체로부터 "name"이라는 파라미터 값을 가져옴 (?name=값)
    String name = request.getParameter("name");

    // name 값이 없거나 공백이면 기본값 "손님"으로 설정
    if (name == null || name.trim().equals("")) {
        name = "손님";
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Hello JSP</title>
</head>
<body style="text-align:center;">

    <!-- JSP 표현식(Expression): <%= %>를 사용하여 변수 값 출력 -->
    <h1>Hello, <%= name %> 님!</h1>

    <!-- 사용자에게 이름을 입력받는 HTML 폼 -->
    <!-- GET 방식으로 요청하면 URL에 ?name=입력값 형태로 전달됨 -->
    <form method="get">
        <input type="text" name="name" placeholder="이름을 입력하세요">
        <button type="submit">전송</button>
    </form>

</body>
</html>