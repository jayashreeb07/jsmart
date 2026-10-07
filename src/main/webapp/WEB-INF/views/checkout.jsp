<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html><html><head><title>Checkout - JS Mart</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css"/></head><body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<main class="wrap narrow">
<h1>Checkout (Mock Payment)</h1>
<p>Total: ₹<c:out value="${total}"/></p>
<c:if test="${not empty message}"><p class="err"><c:out value="${message}"/></p></c:if>
<form method="post" action="${pageContext.request.contextPath}/checkout">
<label><input type="checkbox" name="confirm" value="true" required/> I confirm mock payment (no real money)</label>
<button class="btn" type="submit">Place order</button>
</form>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>
