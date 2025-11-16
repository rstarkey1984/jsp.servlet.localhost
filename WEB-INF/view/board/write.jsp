<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> <%@
taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <c:set var="pageTitle" value="글쓰기" />
    <%@ include file="/WEB-INF/view/layout/head.jsp" %>
  </head>
  <body>
    <%@ include file="/WEB-INF/view/layout/header.jsp" %>
    <div class="main-content">
      <div class="wrapper">
        <div class="card">
          <div class="title">새 글 쓰기</div>
          <div class="subtitle">게시글을 작성해 주세요.</div>

          <!-- Flash 에러 메시지 -->
          <c:if test="${not empty sessionScope.flash_error}">
            <div class="error-box">${sessionScope.flash_error}</div>
            <c:remove var="flash_error" scope="session" />
          </c:if>

          <form action="${pageContext.request.contextPath}/board" method="post">
            <input type="hidden" name="action" value="create" />

            <div class="form-group">
              <label for="title">제목</label>
              <input
                class="input"
                type="text"
                id="title"
                name="title"
                maxlength="45"
                placeholder="제목을 입력하세요"
              />
            </div>

            <div class="form-group">
              <label for="content">내용</label>
              <textarea
                id="content"
                name="content"
                rows="6"
                class="input"
                style="
                  resize: vertical;
                  font-family: inherit;
                  font-size: 0.9rem;
                "
                placeholder="내용을 입력하세요"
              ></textarea>
            </div>

            <button type="submit" class="btn">등록하기</button>
          </form>

          <div class="helper">
            <a
              href="${pageContext.request.contextPath}/board/list"
              class="button-link"
            >
              <button type="button" class="btn btn-secondary">
                목록으로 돌아가기
              </button>
            </a>
          </div>
        </div>
      </div>
    </div>

    <%@ include file="/WEB-INF/view/layout/footer.jsp" %>
  </body>
</html>
