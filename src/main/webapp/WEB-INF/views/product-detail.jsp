<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html><html><head><title><c:out value="${product.name}"/> - JS Mart</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css"/></head><body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<main class="wrap">
<h1><c:out value="${product.name}"/></h1>
<p class="muted"><c:out value="${product.category}"/> · Seller: <c:out value="${product.sellerName}"/></p>
<p class="price">₹<c:out value="${product.price}"/></p>
<p><c:out value="${product.description}"/></p>
<p>Stock: <c:out value="${product.stockQty}"/> · ⭐ <c:out value="${product.avgRating}"/> (<c:out value="${product.reviewCount}"/> reviews)</p>
<form method="post" action="${pageContext.request.contextPath}/api/v1/cart">
<input type="hidden" name="productId" value="${product.id}"/>
<label>Qty <input type="number" name="quantity" value="1" min="1" max="${product.stockQty}"/></label>
<button class="btn" type="submit">Add to cart</button>
</form>
<h2>Reviews</h2>
<c:forEach var="r" items="${reviews}">
<div class="card"><b><c:out value="${r.userName}"/></b> ⭐<c:out value="${r.rating}"/>
<p><c:out value="${r.comment}"/></p></div>
</c:forEach>
<c:if test="${empty reviews}"><p>No reviews yet.</p></c:if>
<h3>Write a review (delivered orders only)</h3>
<form method="post" action="${pageContext.request.contextPath}/api/v1/reviews">
<input type="hidden" name="productId" value="${product.id}"/>
<label>Rating (1-5) <input type="number" name="rating" min="1" max="5" required/></label>
<label>Comment <textarea name="comment" maxlength="2000"></textarea></label>
<button class="btn" type="submit">Submit review</button>
</form>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>
