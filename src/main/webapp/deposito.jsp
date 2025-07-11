<%-- 
    Document   : deposito
    Created on : 10 de jul. de 2025, 03:28:06
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
            <form method="post" action="deposito">
                <input type="hidden" name="id" value="${contaId}" />
                <div class="form-group">
                    <label for="valor">Digite o valor do depósito:</label>
                    <input type="number" step="0.01" name="valor" id="valor" placeholder="Ex: 100.00" required />
                </div>
                <div class="buttons">
                    <button type="submit" class="btn btn-primary">Realizar depósito</button>
                    <a href="conta?id=${conta.getId()}" class="btn btn-back">Voltar</a>
                </div>
            </form>
        </div>
    </div>
</div>
