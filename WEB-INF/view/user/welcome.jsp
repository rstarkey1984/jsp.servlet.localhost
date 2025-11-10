<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>회원가입 성공</title>
    <!-- css 태그 -->
    <style> 
        html { color-scheme: light dark; }
        body { width: 30em; margin: 0 auto;
        font-family: Tahoma, Verdana, Arial, sans-serif; }
    </style>
</head>
<body>
<div class="container">
    <h1>🎉 회원가입을 축하합니다!</h1>
    <p><b>${username}</b>님, 회원가입이 성공적으로 완료되었습니다.</p>

    <div class="info-box">
        <p><strong>이메일:</strong> <%=request.getAttribute("email")%></p>
        <p><strong>이름:</strong>  ${username}</p>
        <p><strong>나이:</strong>  ${age}</p>
    </div>

    <a href="/user/register" class="btn-home">다시하기</a>
</div>
</body>
</html>