<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
    <jsp:param name="pageTitle" value="글 수정"/>
</jsp:include>

<div class="write-form">
    <div class="form-card">
        <form action="${pageContext.request.contextPath}/post/update.do" method="post">
            <input type="hidden" name="id" value="${post.id}">
            <div class="form-group">
                <input type="text" name="title" class="title-input"
                       value="${post.title}" placeholder="제목을 입력하세요" required>
            </div>
            <div class="form-group">
                <textarea name="content" placeholder="당신의 이야기를 적어보세요..." required>${post.content}</textarea>
            </div>
            <div style="display:flex; justify-content:flex-end; gap:var(--space-sm);">
                <a href="${pageContext.request.contextPath}/post/detail.do?id=${post.id}" class="btn btn-secondary">취소</a>
                <button type="submit" class="btn btn-dark">수정하기</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
