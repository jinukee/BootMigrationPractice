<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
    <jsp:param name="pageTitle" value="${post.title}"/>
</jsp:include>

<div class="post-detail">
    <h1 class="post-detail-title">${post.title}</h1>
    <div class="post-detail-meta">
        <span class="author">${post.authorNickname}</span>
        <span class="separator">·</span>
        <span>${post.createdAt.toLocalDate()}</span>
        <c:if test="${post.updatedAt != post.createdAt}">
            <span class="separator">·</span>
            <span>수정됨</span>
        </c:if>
    </div>
    <div class="post-detail-content">${post.content}</div>

    <%-- 작성자 본인에게만 수정/삭제 버튼 표시 --%>
    <c:if test="${not empty sessionScope.loginUser && sessionScope.loginUser.id == post.userId}">
        <div class="post-detail-actions">
            <a href="${pageContext.request.contextPath}/post/edit.do?id=${post.id}"
               class="btn btn-secondary btn-sm">수정</a>
            <form action="${pageContext.request.contextPath}/post/delete.do" method="post"
                  onsubmit="return confirm('정말 삭제하시겠습니까?');" style="display:inline;">
                <input type="hidden" name="id" value="${post.id}">
                <button type="submit" class="btn btn-danger btn-sm">삭제</button>
            </form>
        </div>
    </c:if>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
