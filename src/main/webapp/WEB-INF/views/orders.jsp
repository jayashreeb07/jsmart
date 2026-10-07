<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html><html><head><title>Orders - JS Mart</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css"/></head><body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<main class="wrap">
<h1>Orders</h1>
<c:if test="${param.success != null}"><p class="ok">Order #<c:out value="${param.success}"/> placed!</p></c:if>
<c:forEach var="o" items="${orders}">
<div class="card"><b>Order #<c:out value="${o.id}"/></b> — <c:out value="${o.status}"/> — ₹<c:out value="${o.totalAmount}"/>
<ul><c:forEach var="i" items="${o.items}"><li><c:out value="${i.productName}"/> × <c:out value="${i.quantity}"/></li></c:forEach></ul>
</div>
</c:forEach>
<c:if test="${empty orders}"><p>No orders yet.</p></c:if>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>
