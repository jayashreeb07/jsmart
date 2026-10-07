<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html><html><head><title>Register - JS Mart</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css"/></head><body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<main class="wrap narrow">
<h1>Register</h1>
<form method="post" action="${pageContext.request.contextPath}/api/v1/auth/register">
<label>Full name <input name="fullName" required/></label>
<label>Email <input name="email" type="email" required/></label>
<label>Password <input name="password" type="password" minlength="6" required/></label>
<label>Role <select name="role"><option value="BUYER">Buyer</option><option value="SELLER">Seller</option></select></label>
<button class="btn" type="submit">Register</button>
</form>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>
