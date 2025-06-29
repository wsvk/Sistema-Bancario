<%-- 
    Document   : login
    Created on : Jun 29, 2025, 4:39:34 PM
    Author     : mila
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div id="login-screen" class="screen">
    <div class="container">
        <div class="card">
            <h2>Login</h2>
            <form method="post">
                <div class="form-group">
                    <label for="email">Email:</label>
                    <input type="email" id="email" name="email" required>
                </div>
                <div class="form-group">
                    <label for="password">Senha:</label>
                    <input type="password" id="password" name="senha" required>
                </div>
                
                <% if (request.getAttribute("erro") != null) { %>
                    <p class="msg-erro">${erro}</p>
                <% } %>
                
                <div class="buttons">
                    <input type="submit" class="btn btn-primary" value="Entrar" />
                    <button type="button" onclick="navigation.back()" class="btn btn-secondary">Voltar</button>
                </div>
            </form>
        </div>
    </div>
</div>