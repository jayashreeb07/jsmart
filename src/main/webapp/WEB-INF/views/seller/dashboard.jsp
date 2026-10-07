<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html><html><head><title>Seller Dashboard - JS Mart</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css"/></head><body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<main class="wrap">
<h1>Seller Dashboard</h1>
<h2>Add product</h2>
<form method="post" action="${pageContext.request.contextPath}/seller/products">
<label>Name <input name="name" required/></label>
<label>Description <textarea name="description"></textarea></label>
<label>Price <input name="price" type="number" step="0.01" min="0" required/></label>
<label>Stock <input name="stockQty" type="number" min="0" required/></label>
<label>Category <input name="category"/></label>
<label>Image URL <input name="imageUrl"/></label>
<button class="btn" type="submit">Create</button>
</form>
<h2>My listings</h2>
<c:forEach var="p" items="${products}">
<div class="card"><b><c:out value="${p.name}"/></b> ₹<c:out value="${p.price}"/> stock <c:out value="${p.stockQty}"/></div>
</c:forEach>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>
