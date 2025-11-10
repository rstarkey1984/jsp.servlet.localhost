<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- 
    ✨ JSP 초기 스크립틀릿: 요청값을 받고 request/session에 저장
    - 가능하면 JSTL/EL만 사용하는 것이 좋지만, 연습용으로 최소한의 스크립틀릿만 유지
--%>
<%
    // ⚙ POST 방식일 때 한글 인코딩 처리 (필터에서 해주는 게 더 좋음)
    request.setCharacterEncoding("UTF-8");

    // ✅ 1) 폼 데이터 받기 (request 파라미터)
    String name = request.getParameter("name");
    String color = request.getParameter("color");
    String[] hobbies = request.getParameterValues("hobby");

    // ✅ 2) request Scope에 저장 → 이번 요청(req)에서만 사용
    if (name   != null) request.setAttribute("name",   name);
    if (color  != null) request.setAttribute("color",  color);
    if (hobbies!= null) request.setAttribute("hobbies", hobbies);

    // ✅ 3) session Scope에 저장 → 브라우저가 유지되는 동안 저장
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
<p>폼에 입력하면 <b>JSTL + EL</b>로 출력 (스크립틀릿 없이)</p>

<!-- ✅ 입력폼: 기존 입력값이 있으면 request → 없으면 session 값 출력 -->
<div class="card">
    <form method="post">
        <div class="row">
            이름:
            <input type="text" name="name"
                   value="${param.name != null ? param.name : sessionScope.sessName}">
        </div>
        <div class="row">
            좋아하는 색:
            <input type="text" name="color" placeholder="blue"
                   value="${param.color != null ? param.color : sessionScope.sessColor}">
        </div>
        <div class="row">
            취미:
            <!-- 체크박스는 JSTL로 체크 상태 유지하려면 추가 작업 필요 -->
            <label><input type="checkbox" name="hobby" value="game"> 게임</label>
            <label><input type="checkbox" name="hobby" value="music"> 음악</label>
            <label><input type="checkbox" name="hobby" value="movie"> 영화</label>
        </div>
        <button type="submit">저장</button>
    </form>
</div>

<!-- ✅ name 값이 request 또는 session에 하나라도 있으면 출력 영역 보이게 -->
<c:if test="${not empty name or not empty sessionScope.sessName}">

    <!-- 🔹 Request 영역 값 출력 -->
    <div class="card">
        <h2>✅ 이번 요청(request) 값</h2>
        <p><b>name:</b> ${name}</p>
        <p><b>color:</b> ${color}</p>
        <p><b>hobby:</b>
            <c:choose>
                <c:when test="${not empty hobbies}">
                    <!-- 배열 출력 -->
                    <c:forEach var="h" items="${hobbies}">
                        <span class="pill">${h}</span>
                    </c:forEach>
                </c:when>
                <c:otherwise>없음</c:otherwise>
            </c:choose>
        </p>
    </div>

    <!-- 🔹 Session 영역 값 출력 -->
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
