<%-- 
    Document   : index
    Created on : Jun 29, 2025, 4:21:22 PM
    Author     : mila
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    if (session.getAttribute("usuario") != null) {
        response.sendRedirect("./menu");
        return;
    }
    request.setAttribute("page", "home.jsp");
    RequestDispatcher dispatcher = request.getRequestDispatcher("layout.jsp");
    dispatcher.forward(request, response);
%>