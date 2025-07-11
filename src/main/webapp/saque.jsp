<%-- 
    Document   : saque
    Created on : 11 de jul. de 2025, 02:04:48
    Author     : Vinicius
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div class="screen">
    <div class="container">
        <div class="card">
            <h2 style="color: #1e3a8a; margin-bottom: 20px;">Depósito</h2>
            <% if (request.getAttribute("erro") != null) { %>
                <p style="color: #dc2626; margin-bottom: 20px;"><%= request.getAttribute("erro") %></p>
            <% } %>
            <% if (request.getAttribute("mensagem") != null) { %>
                <p style="color: #059669; margin-bottom: 20px;"><%= request.getAttribute("mensagem") %></p>
            <% } %>
            <form method="post" action="saque">
                <input type="hidden" name="id" value="${contaId}" />
                <div class="form-group">
                    <label for="valor">Digite o valor do saque:</label>
                    <input type="number" step="0.01" name="valor" id="valor" placeholder="Ex: 100.00" required />
                </div>
                <div class="buttons">
                    <button type="submit" class="btn btn-primary">Realizar saque</button>
                    <a href="conta?id=${contaId}" class="btn btn-back">Voltar</a>
                </div>
            </form>
        </div>
    </div>
</div>