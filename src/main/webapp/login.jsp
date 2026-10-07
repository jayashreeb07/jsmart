<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html><html><head><title>Login - JS Mart</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css"/></head><body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<main class="wrap narrow">
<h1>Login</h1>
<c:if test="${param.registered == '1'}"><p class="ok">Registered! Please login.</p></c:if>
<form method="post" action="${pageContext.request.contextPath}/api/v1/auth/login">
<label>Email <input name="email" type="email" required/></label>
<label>Password <input name="password" type="password" required/></label>
<button class="btn" type="submit">Login</button>
</form>
<p>Demo: buyer1@jsmart.local / Buyer@123 · seller1@jsmart.local / Seller@123 · admin@jsmart.local / Admin@123</p>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>
