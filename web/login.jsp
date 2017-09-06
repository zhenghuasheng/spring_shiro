<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<html>
<!-- 这是login.jsp中的 -->
<body>
<form action="${pageContext.request.contextPath }/user/login.do" method="post">
    username:<input type="text" name="username"/><br>
    password:<input type="password" name="password"/><br>
    <input type="submit" value="登陆">${error}
</form>
</body>
</html>