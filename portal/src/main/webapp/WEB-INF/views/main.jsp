<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Insert title here</title>
</head>
<body>
	<div>
		<c:choose>
			<c:when test="${empty sessionScope.user }">
				<a href="${ctx }/loginForm" >请登录</a>
			</c:when>
			<c:otherwise>
				欢迎，${sessionScope.user['name'] }。<a href="${ctx }/logout" >退出</a>
			</c:otherwise>
		</c:choose>
		
	</div>
</body>
</html>