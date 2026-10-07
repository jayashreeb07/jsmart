<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header class="nav">
  <a class="brand" href="${pageContext.request.contextPath}/index.jsp">🛒 JS Mart</a>
  <form class="search" action="${pageContext.request.contextPath}/products" method="get">
    <input name="q" placeholder="Search for products, brands and more..." value="<c:out value='${param.q}'/>"/>
    <select name="category">
      <option value="">All Categories</option>
      <option <c:if test="${param.category=='Mobiles & Electronics'}">selected</c:if>>Mobiles &amp; Electronics</option>
      <option <c:if test="${param.category=='Fashion'}">selected</c:if>>Fashion</option>
      <option <c:if test="${param.category=='Footwear'}">selected</c:if>>Footwear</option>
      <option <c:if test="${param.category=='Home & Kitchen'}">selected</c:if>>Home &amp; Kitchen</option>
      <option <c:if test="${param.category=='Beauty & Personal Care'}">selected</c:if>>Beauty &amp; Personal Care</option>
      <option <c:if test="${param.category=='Bags, Watches & Accessories'}">selected</c:if>>Bags, Watches &amp; Accessories</option>
      <option <c:if test="${param.category=='Books & Stationery'}">selected</c:if>>Books &amp; Stationery</option>
      <option <c:if test="${param.category=='Toys & Kids'}">selected</c:if>>Toys &amp; Kids</option>
      <option <c:if test="${param.category=='Sports & Fitness'}">selected</c:if>>Sports &amp; Fitness</option>
    </select>
    <button type="submit">Search</button>
  </form>
  <nav>
    <a href="${pageContext.request.contextPath}/products">Browse</a>
    <a href="${pageContext.request.contextPath}/cart">Cart</a>
    <a href="${pageContext.request.contextPath}/orders">My Orders</a>
    <c:choose>
      <c:when test="${empty sessionScope.user}">
        <a href="${pageContext.request.contextPath}/login.jsp">Login</a>
        <a href="${pageContext.request.contextPath}/register.jsp">Register</a>
      </c:when>
      <c:otherwise>
        <span>Hello, <c:out value="${sessionScope.user.fullName}"/> (<c:out value="${sessionScope.user.role}"/>)</span>
        <c:if test="${sessionScope.user.role == 'SELLER'}"><a href="${pageContext.request.contextPath}/seller/dashboard">Seller</a></c:if>
        <c:if test="${sessionScope.user.role == 'ADMIN'}"><a href="${pageContext.request.contextPath}/admin/dashboard">Admin</a></c:if>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
      </c:otherwise>
    </c:choose>
  </nav>
</header>
<nav class="catnav">
  <a href="${pageContext.request.contextPath}/products?category=Mobiles+%26+Electronics">Mobiles &amp; Electronics</a>
  <a href="${pageContext.request.contextPath}/products?category=Fashion">Fashion</a>
  <a href="${pageContext.request.contextPath}/products?category=Footwear">Footwear</a>
  <a href="${pageContext.request.contextPath}/products?category=Home+%26+Kitchen">Home &amp; Kitchen</a>
  <a href="${pageContext.request.contextPath}/products?category=Beauty+%26+Personal+Care">Beauty</a>
  <a href="${pageContext.request.contextPath}/products?category=Bags%2C+Watches+%26+Accessories">Bags &amp; Accessories</a>
  <a href="${pageContext.request.contextPath}/products?category=Books+%26+Stationery">Books</a>
  <a href="${pageContext.request.contextPath}/products?category=Toys+%26+Kids">Toys &amp; Kids</a>
  <a href="${pageContext.request.contextPath}/products?category=Sports+%26+Fitness">Sports</a>
</nav>
