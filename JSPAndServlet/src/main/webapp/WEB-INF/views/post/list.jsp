<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
    <jsp:param name="pageTitle" value="Blog"/>
</jsp:include>

<%--
  게시글 목록 페이지.
  JSTL의 <c:forEach>로 리스트를 순회하여 렌더링한다.
  Spring Boot + Thymeleaf에서는 th:each 속성으로 동일하게 구현한다.
--%>
<div class="post-list">
    <c:choose>
        <c:when test="${empty postList}">
            <div class="empty-state">
                <div class="empty-state-icon">📝</div>
                <p class="empty-state-text">아직 작성된 글이 없습니다</p>
                <p class="empty-state-sub">첫 번째 글을 작성해보세요!</p>
            </div>
        </c:when>
        <c:otherwise>
            <c:forEach var="post" items="${postList}">
                <a href="${pageContext.request.contextPath}/post/detail.do?id=${post.id}" class="post-card">
                    <h2 class="post-card-title">${post.title}</h2>
                    <p class="post-card-content">${post.content}</p>
                    <div class="post-card-meta">
                        <span>${post.authorNickname}</span>
                        <span class="separator">·</span>
                        <span>${post.createdAt.toLocalDate()}</span>
                    </div>
                </a>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
