<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>회원가입!</title>
    <link rel="stylesheet" href="/assets/css/style.css">
</head>
<body>
<div class="wrapper">
    <div class="card">
        <div class="title">회원가입</div>
        <div class="subtitle">서비스 이용을 위해 정보를 입력해주세요.</div>

        <!-- 🔹 flash_error 메시지 표시 -->
        <%
            String flashError = (String) session.getAttribute("flash_error");
            if (flashError != null && !flashError.isEmpty()) {
        %>
            <div class="error-box"><%= flashError %></div>
        <%
                // 메시지 1회만 표시되도록 세션에서 삭제
                session.removeAttribute("flash_error");
            }
        %>

        <form action="/user/register" method="post">
            <div class="form-group">
                <label for="id">아이디</label>
                <input class="input" type="text" id="id" name="id" placeholder="예) smartstudent01" required>
            </div>

            <div class="form-group">
                <label for="password">비밀번호</label>
                <input class="input" type="password" id="password" name="password" placeholder="영문+숫자 조합 권장" required>
            </div>

            <div class="form-group">
                <label for="email">이메일</label>
                <input class="input" type="email" id="email" name="email" placeholder="you@example.com" required>
            </div>

            <button type="submit">가입하기</button>
        </form>

        <div class="helper">
            이미 계정이 있으신가요? <a href="/user/login">로그인 페이지로 이동</a>
        </div>
    </div>
</div>
</body>
</html>