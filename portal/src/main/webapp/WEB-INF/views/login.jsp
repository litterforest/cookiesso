<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>用户登录</title>
</head>
<body>
	<div>
		<form action="${ctx }/doLogin" method="post" >
			<input type="hidden" name="redirectUrl" value="${redirectUrl }" >
			<table>
				<tr>
					<td>用户名：</td>
					<td><input name="username" ></td>
				</tr>
				<tr>
					<td>密&nbsp;&nbsp;码：</td>
					<td><input type="password" name="password" ></td>
				</tr>
				<tr>
					<td colspan="2" >${msg }<input type="submit" value="登录" ></td>
				</tr>
			</table>
		</form>
	</div>
</body>
</html>