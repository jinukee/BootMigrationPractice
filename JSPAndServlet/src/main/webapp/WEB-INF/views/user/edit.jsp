<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
    <jsp:param name="pageTitle" value="내 정보 수정"/>
</jsp:include>

<div class="form-container">
    <h1 class="form-title">내 정보 수정</h1>
    <div class="form-card">
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        <form action="${pageContext.request.contextPath}/user/update.do" method="post">
            <div class="form-group">
                <label for="email">이메일</label>
                <input type="email" id="email" value="${user.email}" disabled
                       style="background: var(--color-border-light); cursor: not-allowed;">
            </div>
            <div class="form-group">
                <label for="nickname">닉네임</label>
                <input type="text" id="nickname" name="nickname" value="${user.nickname}"
                       placeholder="새 닉네임을 입력하세요" required>
            </div>
            <div class="form-group">
                <label for="password">새 비밀번호</label>
                <input type="password" id="password" name="password"
                       placeholder="변경하지 않으려면 비워두세요">
            </div>
            <button type="submit" class="btn btn-primary btn-block">수정하기</button>
        </form>

        <!-- 회원탈퇴 영역 -->
        <div class="danger-zone">
            <p class="danger-zone-title">⚠ 위험 구역</p>
            <form action="${pageContext.request.contextPath}/user/delete.do" method="post"
                  onsubmit="return confirm('정말 탈퇴하시겠습니까? 작성한 모든 게시글이 삭제됩니다.');">
                <button type="submit" class="btn btn-danger btn-block">회원탈퇴</button>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
