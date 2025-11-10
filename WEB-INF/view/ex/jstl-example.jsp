<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- ✨ 컨트롤러 역할 (스클립틀릿 최소화 + EL/JSTL 출력) --%>
<%
    request.setCharacterEncoding("UTF-8"); // POST 한글 처리

    // 폼 데이터 받기
    String name = request.getParameter("name");
    String color = request.getParameter("color");
    String[] hobbies = request.getParameterValues("hobby");

    // request는 이번 요청에서만 사용
    if (name   != null) request.setAttribute("name",   name);
    if (color  != null) request.setAttribute("color",  color);
    if (hobbies!= null) request.setAttribute("hobbies", hobbies);

    // session은 브라우저 유지되는 동안 저장
    if (name != null && !name.isBlank())   session.setAttribute("sessName", name);
    if (color!= null && !color.isBlank())  session.setAttribute("sessColor", color);
    if (hobbies != null)                  session.setAttribute("sessHobbies", hobbies);
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>✨ JSTL Profile Demo</title>
    <style>
        body { font-family: 'Segoe UI', sans-serif; margin: 30px; color: #333; }
        .card { border: 1px solid #ccc; border-radius: 12px; padding: 20px; margin-bottom: 20px; }
        .row  { margin-bottom: 10px; }
        .pill { background: #eee; padding: 4px 8px; border-radius: 999px; margin-right: 6px; display: inline-block;}
    </style>
</head>
<body>

<h1>⭐ JSTL Request / Session Demo</h1>
<p>폼에 값을 입력하면 <b>JSTL + EL</b>만으로 출력됩니다. (스크립틀릿 X)</p>

<div class="card">
    <form method="get">
        <div class="row">
            이름: <input type="text" name="name" value="${param.name != null ? param.name : sessionScope.sessName}">
        </div>
        <div class="row">
            좋아하는 색: <input type="text" name="color" placeholder="blue" value="${param.color != null ? param.color : sessionScope.sessColor}">
        </div>
        <div class="row">
            취미:
            <label><input type="checkbox" name="hobby" value="game"> 게임</label>
            <label><input type="checkbox" name="hobby" value="music"> 음악</label>
            <label><input type="checkbox" name="hobby" value="movie"> 영화</label>
        </div>
        <button type="submit">저장</button>
    </form>
</div>

<c:if test="${not empty name or not empty sessionScope.sessName}">
<div class="card">
    <h2>✅ 이번 요청(request) 값</h2>
    <p><b>name:</b> ${name}</p>
    <p><b>color:</b> ${color}</p>
    <p><b>hobby:</b>
        <c:choose>
            <c:when test="${not empty hobbies}">
                <c:forEach var="h" items="${hobbies}">
                    <span class="pill">${h}</span>
                </c:forEach>
            </c:when>
            <c:otherwise>없음</c:otherwise>
        </c:choose>
    </p>
</div>

<div class="card">
    <h2>📌 세션(session) 저장 값</h2>
    <p><b>sessName:</b> ${sessionScope.sessName}</p>
    <p><b>sessColor:</b> ${sessionScope.sessColor}</p>
    <p><b>sessHobby:</b>
        <c:forEach var="h" items="${sessionScope.sessHobbies}">
            <span class="pill">${h}</span>
        </c:forEach>
    </p>
</div>
</c:if>

</body>
</html>