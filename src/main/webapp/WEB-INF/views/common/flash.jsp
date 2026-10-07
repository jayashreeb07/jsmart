<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${not empty sessionScope.flashOk}">
  <div class="flash ok"><c:out value="${sessionScope.flashOk}"/></div>
  <c:remove var="flashOk" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.flashErr}">
  <div class="flash err"><c:out value="${sessionScope.flashErr}"/></div>
  <c:remove var="flashErr" scope="session"/>
</c:if>
