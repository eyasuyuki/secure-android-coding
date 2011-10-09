<%@page pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>top Index</title>
</head>
<body>
<div>Login as: <a href=${logoutUrl} title="Logout">${email}</a></div>
<p>What do you eat?</p>
<form method="post" action="entry">
<div>
<b>Date:</b><input type="date" name="logDate" />
<select name="time">
<option value="Breakfast">Breakfast</option>
<option value="Lunch">Lunch</option>
<option value="Dinner">Dinner</option>
</select>
<b>Food:</b><input type="text" name="food" />
<b>Calorie:</b><input type="number" min="0" name="kcal" />KCal&nbsp;
<b>Salt:</b><input type="number" step="0.0001" name="salt" />g&nbsp;
<input type="submit" value="Add"/>
</div>
</form>
<table>
<tbody>
<tr><th>Log Date</th><th>Time</th><th>Food</th><th>Kcal</th><th>Salt</th></tr>
<c:forEach var="e" items="${foodLogList}">
<tr>
<td>${f:h(e.logDate)}</td>
<td>${f:h(e.time)}</td>
<td>${f:h(e.food)}</td>
<td>${f:h(e.kcal)}</td>
<td>${f:h(e.salt)}</td>
</tr>
</c:forEach>
</tbody>
</table>
</html>
