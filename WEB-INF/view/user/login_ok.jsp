<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>환영합니다!</title>
  <link rel="stylesheet" href="/assets/css/style.css">
</head>
<body>
  <div class="wrapper">
    <div class="card">
      <div class="title">로그인 성공 🎉</div>
      <div class="subtitle">
        안녕하세요, <strong>${sessionScope.id}</strong> 님! <br>
        오늘도 좋은 하루 보내세요 ☀️
      </div>

      <div class="form-group">
        <a href="/user/login"><button type="button">로그인으로 돌아가기</button></a>
      </div>

      <div class="form-group">
        <a href="/user/logout"><button type="button">로그아웃</button></a>
      </div>
    </div>
  </div>
</body>
</html>