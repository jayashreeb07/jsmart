<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html><html><head><title>Track Order #<c:out value="${order.id}"/> - JS Mart</title>
<meta name="viewport" content="width=device-width, initial-scale=1"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css"/></head><body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<main class="wrap">
<p><a href="${pageContext.request.contextPath}/orders">← My Orders</a></p>
<h1>Track Order #<c:out value="${order.id}"/></h1>
<p class="muted">Placed on <c:out value="${order.createdAt}"/> · Total ₹<c:out value="${order.totalAmount}"/></p>
<p>Current status: <b><c:out value="${order.status}"/></b></p>
<c:choose>
  <c:when test="${order.status == 'CANCELLED'}">
    <ol class="track">
      <li class="done">✓ Order Placed</li>
      <li class="done">✓ Cancelled</li>
    </ol>
  </c:when>
  <c:otherwise>
    <ol class="track">
      <li class="done">✓ Order Placed</li>
      <li class="${order.status=='CONFIRMED'||order.status=='SHIPPED'||order.status=='DELIVERED' ? 'done' : ''}">
        <c:choose><c:when test="${order.status=='CONFIRMED'||order.status=='SHIPPED'||order.status=='DELIVERED'}">✓</c:when><c:otherwise>○</c:otherwise></c:choose>
        Order Confirmed</li>
      <li class="${order.status=='SHIPPED'||order.status=='DELIVERED' ? 'done' : ''}">
        <c:choose><c:when test="${order.status=='SHIPPED'||order.status=='DELIVERED'}">✓</c:when><c:otherwise>○</c:otherwise></c:choose>
        Shipped</li>
      <li class="${order.status=='DELIVERED' ? 'done' : ''}">
        <c:choose><c:when test="${order.status=='DELIVERED'}">✓</c:when><c:otherwise>○</c:otherwise></c:choose>
        Delivered</li>
    </ol>
  </c:otherwise>
</c:choose>
<h2>Items in this order</h2>
<c:forEach var="i" items="${order.items}">
<div class="card titem">
  <c:choose>
    <c:when test="${not empty i.productImage}">
      <img class="thumb" src="<c:out value='${i.productImage}'/>" alt="<c:out value='${i.productName}'/>" loading="lazy"
           onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/assets/img/placeholder.svg'"/>
    </c:when>
    <c:otherwise>
      <img class="thumb" src="${pageContext.request.contextPath}/assets/img/placeholder.svg" alt="No image"/>
    </c:otherwise>
  </c:choose>
  <div><b><c:out value="${i.productName}"/></b><br/>
  Qty <c:out value="${i.quantity}"/> × ₹<c:out value="${i.unitPrice}"/></div>
</div>
</c:forEach>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>
