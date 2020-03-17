<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<html>
<body>
<shiro:hasRole name="admin">
    <h1>admin login</h1>
</shiro:hasRole>
欢迎你${user.username }
<a href="${pageContext.request.contextPath }/user/logout">退出</a>
<a href="/user/admin">user-admin</a>
</body>
</html>
