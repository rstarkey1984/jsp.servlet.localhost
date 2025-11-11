<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>로그인!</title>
    <link rel="stylesheet" href="/assets/css/style.css">
</head>
<body>
<div class="wrapper">
    <div class="card">
        <div class="title">로그인</div>
        <div class="subtitle">계정 정보를 입력하여 로그인하세요.</div>

        <!-- 🔹 flash_error 메시지 표시 -->
        <%
            String flashError = (String) session.getAttribute("flash_error");
            if (flashError != null && !flashError.isEmpty()) {
        %>
            <div class="error-box"><%= flashError %></div>
        <%
                session.removeAttribute("flash_error");
            }

            String flashSuccess = (String) session.getAttribute("flash_success");
            if (flashSuccess != null && !flashSuccess.isEmpty()) {
        %>
            <div class="success-box"><%= flashSuccess %></div>
        <%
                session.removeAttribute("flash_success");
            }
        %>

        <form action="/user/login" method="post">
            <div class="form-group">
                <label for="id">아이디</label>
                <input class="input" type="text" id="id" name="id" placeholder="아이디를 입력하세요" required>
            </div>

            <div class="form-group">
                <label for="password">비밀번호</label>
                <input class="input" type="password" id="password" name="password" placeholder="비밀번호를 입력하세요" required>
            </div>

            <button type="submit">로그인</button>
        </form>

        <div class="helper">
            계정이 없으신가요? 
            <a href="/user/register">회원가입 페이지로 이동</a>
        </div>
    </div>
</div>
</body>
</html>