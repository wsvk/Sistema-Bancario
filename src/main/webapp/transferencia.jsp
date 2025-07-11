<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String mensagem = (String) request.getAttribute("mensagem");
    String contaId = request.getParameter("id") != null ? request.getParameter("id") : "";
    request.setAttribute("page", "transferencia-content.jsp");
%>
<jsp:include page="layout.jsp" />
