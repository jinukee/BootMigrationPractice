<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
    <jsp:param name="pageTitle" value="로그인"/>
</jsp:include>

<div class="form-container">
    <h1 class="form-title">로그인</h1>
    <div class="form-card">
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        <form action="${pageContext.request.contextPath}/user/login.do" method="post">
            <div class="form-group">
                <label for="email">이메일</label>
                <input type="email" id="email" name="email" placeholder="example@email.com" required>
            </div>
            <div class="form-group">
                <label for="password">비밀번호</label>
                <input type="password" id="password" name="password" placeholder="비밀번호를 입력하세요" required>
            </div>
            <button type="submit" class="btn btn-primary btn-block">로그인</button>
        </form>
    </div>
    <p class="form-footer">
        계정이 없으신가요? <a href="${pageContext.request.contextPath}/user/register.do">회원가입</a>
    </p>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
