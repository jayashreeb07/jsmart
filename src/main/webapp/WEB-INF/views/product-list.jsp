<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html><html><head><title>Products - JS Mart</title>
<meta name="viewport" content="width=device-width, initial-scale=1"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css"/>
<script src="${pageContext.request.contextPath}/assets/js/wishlist.js" defer></script></head><body>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<main class="wrap shopwrap">
<div class="shop">
  <aside class="filters">
    <h3>Filters</h3>
    <form method="get" action="${pageContext.request.contextPath}/products">
      <input type="hidden" name="q" value="<c:out value='${param.q}'/>"/>
      <input type="hidden" name="sort" value="<c:out value='${param.sort}'/>"/>
      <h4>Category</h4>
      <label class="check"><input type="radio" name="category" value="" ${empty param.category ? 'checked' : ''}/> All</label>
      <label class="check"><input type="radio" name="category" value="Mobiles &amp; Electronics" ${param.category=='Mobiles & Electronics' ? 'checked' : ''}/> Mobiles &amp; Electronics</label>
      <label class="check"><input type="radio" name="category" value="Fashion" ${param.category=='Fashion' ? 'checked' : ''}/> Fashion</label>
      <label class="check"><input type="radio" name="category" value="Footwear" ${param.category=='Footwear' ? 'checked' : ''}/> Footwear</label>
      <label class="check"><input type="radio" name="category" value="Home &amp; Kitchen" ${param.category=='Home & Kitchen' ? 'checked' : ''}/> Home &amp; Kitchen</label>
      <label class="check"><input type="radio" name="category" value="Beauty &amp; Personal Care" ${param.category=='Beauty & Personal Care' ? 'checked' : ''}/> Beauty &amp; Personal Care</label>
      <label class="check"><input type="radio" name="category" value="Bags, Watches &amp; Accessories" ${param.category=='Bags, Watches & Accessories' ? 'checked' : ''}/> Bags, Watches &amp; Accessories</label>
      <label class="check"><input type="radio" name="category" value="Books &amp; Stationery" ${param.category=='Books & Stationery' ? 'checked' : ''}/> Books &amp; Stationery</label>
      <label class="check"><input type="radio" name="category" value="Toys &amp; Kids" ${param.category=='Toys & Kids' ? 'checked' : ''}/> Toys &amp; Kids</label>
      <label class="check"><input type="radio" name="category" value="Sports &amp; Fitness" ${param.category=='Sports & Fitness' ? 'checked' : ''}/> Sports &amp; Fitness</label>
      <h4>Price (₹)</h4>
      <div class="pricerow">
        <input name="minPrice" type="number" min="0" placeholder="Min" value="<c:out value='${param.minPrice}'/>"/>
        <input name="maxPrice" type="number" min="0" placeholder="Max" value="<c:out value='${param.maxPrice}'/>"/>
      </div>
      <button class="btn wide" type="submit">Apply filters</button>
      <a class="clearlink" href="${pageContext.request.contextPath}/products">Clear all</a>
    </form>
  </aside>
  <section class="listing">
    <div class="toolbar">
      <span class="muted">${fn:length(products)} product(s)
        <c:if test="${not empty param.category}"> in <b><c:out value="${param.category}"/></b></c:if>
        <c:if test="${not empty param.q}"> for "<b><c:out value="${param.q}"/></b>"</c:if>
      </span>
      <form method="get" action="${pageContext.request.contextPath}/products" class="sortform">
        <input type="hidden" name="q" value="<c:out value='${param.q}'/>"/>
        <input type="hidden" name="category" value="<c:out value='${param.category}'/>"/>
        <input type="hidden" name="minPrice" value="<c:out value='${param.minPrice}'/>"/>
        <input type="hidden" name="maxPrice" value="<c:out value='${param.maxPrice}'/>"/>
        <label>Sort:
          <select name="sort" onchange="this.form.submit()">
            <option value="" ${empty param.sort ? 'selected' : ''}>Newest</option>
            <option value="price_asc" ${param.sort=='price_asc' ? 'selected' : ''}>Price: Low to High</option>
            <option value="price_desc" ${param.sort=='price_desc' ? 'selected' : ''}>Price: High to Low</option>
            <option value="rating" ${param.sort=='rating' ? 'selected' : ''}>Top Rated</option>
          </select>
        </label>
      </form>
    </div>
    <div class="grid products" id="productGrid">
      <c:forEach var="p" items="${products}">
        <div class="pcard">
          <button class="wish" data-wish="${p.id}" title="Add to wishlist">♥</button>
          <a class="pimg" href="${pageContext.request.contextPath}/product?id=${p.id}">
            <c:choose>
              <c:when test="${not empty p.imageUrl}">
                <img src="<c:out value='${p.imageUrl}'/>" alt="<c:out value='${p.name}'/>" loading="lazy"
                     onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/assets/img/placeholder.svg'"/>
              </c:when>
              <c:otherwise>
                <img src="${pageContext.request.contextPath}/assets/img/placeholder.svg" alt="No image" loading="lazy"/>
              </c:otherwise>
            </c:choose>
          </a>
          <div class="pbody">
            <span class="chip"><c:out value="${p.category}"/></span>
            <a class="pname" href="${pageContext.request.contextPath}/product?id=${p.id}"><c:out value="${p.name}"/></a>
            <div class="prating">⭐ <c:out value="${p.avgRating}"/> <span class="muted">(<c:out value="${p.reviewCount}"/>)</span></div>
            <div class="pprice">₹<c:out value="${p.price}"/></div>
            <div class="pstock">
              <c:choose>
                <c:when test="${p.stockQty <= 0}"><span class="badge out">Out of stock</span></c:when>
                <c:when test="${p.stockQty <= 10}"><span class="badge low">Only <c:out value="${p.stockQty}"/> left</span></c:when>
                <c:otherwise><span class="badge in">In stock</span></c:otherwise>
              </c:choose>
            </div>
            <div class="pactions">
              <form method="post" action="${pageContext.request.contextPath}/api/v1/cart">
                <input type="hidden" name="productId" value="${p.id}"/>
                <input type="hidden" name="quantity" value="1"/>
                <button class="btn" type="submit" ${p.stockQty <= 0 ? 'disabled' : ''}>Add to Cart</button>
              </form>
              <a class="linkbtn" href="${pageContext.request.contextPath}/product?id=${p.id}">View Details</a>
            </div>
          </div>
        </div>
      </c:forEach>
    </div>
    <c:if test="${empty products}"><p>No products found. Try clearing the filters.</p></c:if>
  </section>
</div>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>
