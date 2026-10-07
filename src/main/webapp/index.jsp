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
<a class="card" href="${pageContext.request.contextPath}/products?category=Mobiles+%26+Electronics">Mobiles &amp; Electronics</a>
<a class="card" href="${pageContext.request.contextPath}/products?category=Fashion">Fashion</a>
<a class="card" href="${pageContext.request.contextPath}/products?category=Footwear">Footwear</a>
<a class="card" href="${pageContext.request.contextPath}/products?category=Home+%26+Kitchen">Home &amp; Kitchen</a>
<a class="card" href="${pageContext.request.contextPath}/products?category=Beauty+%26+Personal+Care">Beauty &amp; Personal Care</a>
<a class="card" href="${pageContext.request.contextPath}/products?category=Bags%2C+Watches+%26+Accessories">Bags, Watches &amp; Accessories</a>
<a class="card" href="${pageContext.request.contextPath}/products?category=Books+%26+Stationery">Books &amp; Stationery</a>
<a class="card" href="${pageContext.request.contextPath}/products?category=Toys+%26+Kids">Toys &amp; Kids</a>
<a class="card" href="${pageContext.request.contextPath}/products?category=Sports+%26+Fitness">Sports &amp; Fitness</a>
</div>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>
