<%-- 
    Document   : Health
    Created on : Jun 28, 2025, 10:36:53 PM
    Author     : mila
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <%= request.getAttribute("status") %>
        <p>Status do Banco: <%= request.getAttribute("db_status") %></p>
    </body>
</html>
