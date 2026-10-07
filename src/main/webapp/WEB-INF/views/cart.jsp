<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html><html><head><title>Cart - JS Mart</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css"/>
<script src="${pageContext.request.contextPath}/assets/js/app.js" defer></script></head><body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<main class="wrap">
<h1>Your Cart</h1>
<c:forEach var="i" items="${items}">
<div class="card"><b><c:out value="${i.productName}"/></b> — ₹<c:out value="${i.unitPrice}"/> × <c:out value="${i.quantity}"/>
<button data-remove="${i.productId}">Remove</button></div>
</c:forEach>
<c:if test="${empty items}"><p>Cart is empty.</p></c:if>
<h3>Total: ₹<c:out value="${total}"/></h3>
<p><a class="btn" href="${pageContext.request.contextPath}/checkout">Checkout</a></p>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>
