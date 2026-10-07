<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html><html><head><title>Cart - JS Mart</title>
<meta name="viewport" content="width=device-width, initial-scale=1"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css"/>
<script src="${pageContext.request.contextPath}/assets/js/app.js" defer></script></head><body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<main class="wrap">
<h1>Your Cart</h1>
<c:forEach var="i" items="${items}">
<div class="card titem">
<c:choose>
<c:when test="${not empty i.imageUrl}">
<img class="thumb" src="<c:out value='${i.imageUrl}'/>" alt="<c:out value='${i.productName}'/>" loading="lazy"
     onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/assets/img/placeholder.svg'"/>
</c:when>
<c:otherwise>
<img class="thumb" src="${pageContext.request.contextPath}/assets/img/placeholder.svg" alt="No image"/>
</c:otherwise>
</c:choose>
<div class="grow"><b><c:out value="${i.productName}"/></b><br/>₹<c:out value="${i.unitPrice}"/> each
<form class="qtyform" method="post" action="${pageContext.request.contextPath}/cart">
<input type="hidden" name="productId" value="${i.productId}"/>
<label>Qty <input type="number" name="quantity" value="${i.quantity}" min="1" max="${i.stockQty}"/></label>
<button type="submit">Update</button>
</form>
</div>
<div><b>₹<c:out value="${i.unitPrice * i.quantity}"/></b><br/><button data-remove="${i.productId}">Remove</button></div>
</div>
</c:forEach>
<c:if test="${empty items}"><p>Cart is empty.</p></c:if>
<h3>Total: ₹<c:out value="${total}"/></h3>
<p><a class="btn" href="${pageContext.request.contextPath}/checkout">Proceed to Checkout</a></p>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>
