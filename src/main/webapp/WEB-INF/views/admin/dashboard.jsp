<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html><html><head><title>Admin - JS Mart</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css"/></head><body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<main class="wrap">
<h1>Admin Dashboard</h1>
<h2>Users</h2>
<c:forEach var="u" items="${users}"><div class="card"><c:out value="${u.email}"/> — <c:out value="${u.role}"/></div></c:forEach>
<h2>Orders</h2>
<c:forEach var="o" items="${orders}"><div class="card">Order #<c:out value="${o.id}"/> <c:out value="${o.status}"/> ₹<c:out value="${o.totalAmount}"/></div></c:forEach>
<h2>Listings</h2>
<c:forEach var="p" items="${products}"><div class="card"><c:out value="${p.name}"/> ₹<c:out value="${p.price}"/></div></c:forEach>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>
