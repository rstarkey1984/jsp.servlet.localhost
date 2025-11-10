<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>회원가입 페이지</title>
    <!-- css 태그 -->
    <style> 
        html { color-scheme: light dark; }
        body { width: 30em; margin: 0 auto;
        font-family: Tahoma, Verdana, Arial, sans-serif; }
    </style>
</head>
<body>
    <h2>회원 정보 입력</h2>
    <form action="/user/register" method="post">
        <p>이메일: <input type="text" name="email"></p>
        <p>이름: <input type="text" name="username"></p>
        <p>나이: <input type="text" name="age"></p>
        <p><button type="submit">등록하기</button></p>
    </form>
</body>
</html>