<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- 시작 페이지: 게시글 목록으로 리다이렉트 --%>
<% response.sendRedirect(request.getContextPath() + "/post/list.do"); %>
