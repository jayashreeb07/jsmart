<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${empty sessionScope.successTitle}">
  <c:redirect url="/index.jsp"/>
</c:if>
<!DOCTYPE html><html><head><title><c:out value="${sessionScope.successTitle}"/> - JS Mart</title>
<meta name="viewport" content="width=device-width, initial-scale=1"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css"/></head><body>
<main class="successwrap">
  <div class="successcard">
    <div class="successicon">✓</div>
    <h1><c:out value="${sessionScope.successTitle}"/></h1>
    <p class="muted"><c:out value="${sessionScope.successMsg}"/></p>
    <p><a class="btn big" href="${pageContext.request.contextPath}<c:out value='${sessionScope.successLink}'/>"><c:out value="${sessionScope.successBtn}"/></a></p>
  </div>
</main>
<c:remove var="successTitle" scope="session"/>
<c:remove var="successMsg" scope="session"/>
<c:remove var="successBtn" scope="session"/>
<c:remove var="successLink" scope="session"/>
</body></html>
