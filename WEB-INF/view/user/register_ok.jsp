<%@ page contentType="text/html; charset=UTF-8" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>가입 완료!</title>
    <link rel="stylesheet" href="/assets/css/style.css">
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
<div class="wrapper">
    <div class="card">
        <div class="title">가입 완료 🎉</div>
        <div class="subtitle">
            <b>${sessionScope.id}</b> 님, 환영합니다!<br>
            이제 로그인하시면 서비스를 이용하실 수 있어요.
        </div>

        <button type="button" onclick="location.href='/user/login'">로그인 하러 가기</button>
    </div>
</div>
</body>
</html>