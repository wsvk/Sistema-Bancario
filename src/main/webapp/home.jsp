<%-- 
    Document   : home
    Created on : Jun 29, 2025, 4:23:43 PM
    Author     : mila
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Object usuario = session.getAttribute("usuario");

    if (usuario != null) {
        response.sendRedirect("menu.jsp");
        return; // importante para evitar continuar renderizando a página atual
    }
%>

<div id="home-screen" class="screen">
    <div class="container">
        <div class="card">
            <h1>Sistema Bancário</h1>
            <div class="buttons">
                <a href="./login" class="btn btn-primary">Entrar</a>
                <a href="./cadastro" class="btn btn-secondary">Registrar</a>
            </div>
        </div>
    </div>
</div>