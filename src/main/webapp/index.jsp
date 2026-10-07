<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html><html><head><title>JS Mart</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css"/></head><body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<main class="wrap">
<h1>Welcome to JS Mart</h1>
<p>Demo e-commerce: browse, cart, mock checkout, reviews, chatbot.</p>
<p><a class="btn" href="${pageContext.request.contextPath}/products">Browse products</a></p>
<h2>Shop by category</h2>
<div class="grid">
<a class="card" href="${pageContext.request.contextPath}/products?category=Mobiles">Mobiles</a>
<a class="card" href="${pageContext.request.contextPath}/products?category=Laptops">Laptops</a>
<a class="card" href="${pageContext.request.contextPath}/products?category=Headphones">Headphones</a>
<a class="card" href="${pageContext.request.contextPath}/products?category=Tablets">Tablets</a>
<a class="card" href="${pageContext.request.contextPath}/products?category=Accessories">Accessories</a>
</div>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>
