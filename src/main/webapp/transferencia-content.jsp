<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%
    String mensagem = (String) request.getAttribute("mensagem");
    String contaId = request.getParameter("id") != null ? request.getParameter("id") : "";
%>
<div class="screen">
    <div class="container">
        <div class="header">
            <div>
                <h1>Transferência</h1>
                <p style="color: #6b7280; margin-top: 5px;">Preencha os dados para transferir entre contas</p>
            </div>
            <div class="buttons">
                <a href="menu" class="btn btn-back">Menu Principal</a>
                <a href="logout" class="btn btn-logout">Sair</a>
            </div>
        </div>

        <div class="card" style="margin: 0 auto;">
            <form action="transferencia" method="post">
                <input type="hidden" name="contaOrigem" value="<%= contaId %>">
                <div style="margin-bottom: 18px; text-align: left;">
                    <label for="contaDestino">Número da Conta Destino:</label><br>
                    <input type="text" id="contaDestino" name="contaDestino" class="input" required style="width: 100%; padding: 8px; margin-top: 4px;">
                </div>
                <div style="margin-bottom: 18px; text-align: left;">
                    <label for="valor">Valor da Transferência:</label><br>
                    <input type="number" id="valor" name="valor" step="0.01" min="0.01" class="input" required style="width: 100%; padding: 8px; margin-top: 4px;">
                </div>
                <button type="submit" class="btn btn-primary" style="width: 100%;">Transferir</button>
            </form>
            <% if (mensagem != null) { %>
                <div style="color:red; margin-top: 16px; text-align: center; font-weight: bold;"> <%= mensagem %> </div>
            <% } %>
        </div>
    </div>
</div>
