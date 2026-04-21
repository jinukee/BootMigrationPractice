<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
  공통 헤더 (include 방식).
  Spring Boot + Thymeleaf에서는 th:fragment로 정의하고 th:replace로 삽입한다.
--%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${param.pageTitle != null ? param.pageTitle : 'Blog'}</title>
    <link rel="preconnect" href="https://cdn.jsdelivr.net">
    <link href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/variable/pretendardvariable-dynamic-subset.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <header class="header">
        <div class="header-inner">
            <a href="${pageContext.request.contextPath}/post/list.do" class="header-logo">
                Blog<span class="logo-dot"></span>
            </a>
            <nav class="header-nav">
                <c:choose>
                    <c:when test="${not empty sessionScope.loginUser}">
                        <span class="user-info">${sessionScope.loginUser.nickname}님</span>
                        <a href="${pageContext.request.contextPath}/post/write.do" class="btn-write">새 글 작성</a>
                        <a href="${pageContext.request.contextPath}/user/edit.do">내 정보</a>
                        <form action="${pageContext.request.contextPath}/user/logout.do" method="get" style="display:inline;">
                            <button type="submit">로그아웃</button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/user/login.do">로그인</a>
                        <a href="${pageContext.request.contextPath}/user/register.do" class="btn-write">회원가입</a>
                    </c:otherwise>
                </c:choose>
            </nav>
        </div>
    </header>
    <main class="main-content">
