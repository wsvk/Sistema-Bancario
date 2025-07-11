<%-- 
    Document   : cadastro
    Created on : Jun 29, 2025, 4:41:47 PM
    Author     : mila
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<div id="cadastro-screen" class="screen">
    <div class="container">
        <div class="card">
            <h2>Cadastrar</h2>
            <form method="post">
                <div class="form-group">
                    <label for="reg-name">Nome:</label>
                    <input type="text" id="reg-name" name="nome" required>
                </div>
                <div class="form-group">
                    <label for="reg-email">Email:</label>
                    <input type="email" id="reg-email" name="email" required>
                </div>
                <div class="form-group">
                    <label for="reg-password">Senha:</label>
                    <input type="password" id="reg-password" name="senha" required>
                </div>
                <div class="buttons">
                    <input type="submit" class="btn btn-primary" value="Registrar">
                    <a href="index.jsp" class="btn btn-secondary">Voltar</a>
                </div>
            </form>
        </div>
    </div>
</div>