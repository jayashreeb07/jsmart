<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html><html><head><title><c:out value="${product.name}"/> - JS Mart</title>
<meta name="viewport" content="width=device-width, initial-scale=1"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css"/>
<script src="${pageContext.request.contextPath}/assets/js/wishlist.js" defer></script></head><body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<main class="wrap">
<div class="detail">
  <div class="dimg">
    <button class="wish" data-wish="${product.id}" title="Add to wishlist">♥</button>
    <c:choose>
      <c:when test="${not empty product.imageUrl}">
        <img src="<c:out value='${product.imageUrl}'/>" alt="<c:out value='${product.name}'/>"
             onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/assets/img/placeholder.svg'"/>
      </c:when>
      <c:otherwise>
        <img src="${pageContext.request.contextPath}/assets/img/placeholder.svg" alt="No image"/>
      </c:otherwise>
    </c:choose>
  </div>
  <div class="dinfo">
    <span class="chip"><c:out value="${product.category}"/></span>
    <h1><c:out value="${product.name}"/></h1>
    <p class="muted">Seller: <c:out value="${product.sellerName}"/></p>
    <div class="prating">⭐ <c:out value="${product.avgRating}"/> <span class="muted">(<c:out value="${product.reviewCount}"/> reviews)</span></div>
    <div class="pprice big">₹<c:out value="${product.price}"/></div>
    <p><c:out value="${product.description}"/></p>
    <div class="pstock">
      <c:choose>
        <c:when test="${product.stockQty <= 0}"><span class="badge out">Out of stock</span></c:when>
        <c:when test="${product.stockQty <= 10}"><span class="badge low">Only <c:out value="${product.stockQty}"/> left in stock</span></c:when>
        <c:otherwise><span class="badge in">In stock (<c:out value="${product.stockQty}"/> available)</span></c:otherwise>
      </c:choose>
    </div>
    <form method="post" action="${pageContext.request.contextPath}/api/v1/cart" class="dform">
      <input type="hidden" name="productId" value="${product.id}"/>
      <label>Qty <input type="number" name="quantity" value="1" min="1" max="${product.stockQty}"/></label>
      <button class="btn" type="submit" ${product.stockQty <= 0 ? 'disabled' : ''}>Add to Cart</button>
    </form>
  </div>
</div>
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
