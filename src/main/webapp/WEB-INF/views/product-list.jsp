<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html><html><head><title>Products - JS Mart</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css"/></head><body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<main class="wrap">
<h1>Products</h1>
<div class="grid" id="productGrid">
<c:forEach var="p" items="${products}">
<div class="card">
<h3><c:out value="${p.name}"/></h3>
<p class="muted"><c:out value="${p.category}"/> · Seller: <c:out value="${p.sellerName}"/></p>
<p class="price">₹<c:out value="${p.price}"/></p>
<p>Stock: <c:out value="${p.stockQty}"/> · ⭐ <c:out value="${p.avgRating}"/> (<c:out value="${p.reviewCount}"/>)</p>
<p><a class="btn" href="${pageContext.request.contextPath}/product?id=${p.id}">Details</a></p>
</div>
</c:forEach>
</div>
<c:if test="${empty products}"><p>No products found.</p></c:if>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>
