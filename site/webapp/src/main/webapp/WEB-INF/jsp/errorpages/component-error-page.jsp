<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<%--@elvariable id="errorComponentWindow" type="org.hippoecm.hst.core.container.HstComponentWindow"--%>

<c:if test="${not empty errorComponentWindow.componentExceptions}">
  <ul>
    <c:forEach var="componentException" items="${errorComponentWindow.componentExceptions}">
      <li>
        <pre>${fn:escapeXml(componentException.message)}</pre>
      </li>
    </c:forEach>
  </ul>
</c:if>
