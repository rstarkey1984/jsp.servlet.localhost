<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html> <!-- 브라우저가 최신 웹 표준에 맞춰 작동하도록 사용함 -->
<html>
<head> <!-- HTML 문서의 정보를 담는 부분으로, 웹 페이지 자체에 표시되지는 않습니다. -->
    <title>GET POST 요청 예제</title> <!-- 페이지 제목 --> 
    
    <!-- css 태그 -->
    <style> 
        html { color-scheme: light dark; }
        body { width: 35em; margin: 0 auto;
        font-family: Tahoma, Verdana, Arial, sans-serif; }
    </style>

</head>
<body>
<h2>Servlet Lifecycle Demo</h2>

<form action="<%=request.getContextPath()%>/ex/demo" method="get">
  <input name="name" value="홍길동">
  <button type="submit">GET 호출</button>
</form>

<form action="<%=request.getContextPath()%>/ex//demo" method="post" style="margin-top:12px;">
  <input name="name" value="임꺽정">
  <button type="submit">POST 호출</button>
</form>

<p>
  <a href="<%=request.getContextPath()%>/ex//demo?name=이몽룡">GET 링크로 호출</a>
</p>
</body>