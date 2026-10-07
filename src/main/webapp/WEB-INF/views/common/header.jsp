<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header class="nav">
  <a class="brand" href="${pageContext.request.contextPath}/index.jsp">🛒 JS Mart</a>
  <form class="search" action="${pageContext.request.contextPath}/products" method="get">
    <input name="q" placeholder="Search products..." value="<c:out value='${param.q}'/>"/>
    <select name="category">
      <option value="">All</option>
      <option <c:if test="${param.category=='Electronics'}">selected</c:if>>Electronics</option>
      <option <c:if test="${param.category=='Fashion'}">selected</c:if>>Fashion</option>
      <option <c:if test="${param.category=='Home'}">selected</c:if>>Home</option>
      <option <c:if test="${param.category=='Books'}">selected</c:if>>Books</option>
    </select>
    <button type="submit">Search</button>
  </form>
  <nav>
    <a href="${pageContext.request.contextPath}/products">Browse</a>
    <a href="${pageContext.request.contextPath}/cart">Cart</a>
    <a href="${pageContext.request.contextPath}/orders">Orders</a>
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
