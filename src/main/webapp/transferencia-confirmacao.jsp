<%
    String contaOrigem = request.getParameter("contaOrigem");
    String contaDestino = request.getParameter("contaDestino");
    String valor = request.getParameter("valor");
%>
<div class="screen">
    <div class="container">
        <div class="header">
            <div>
                <h1>Transfer&ecirc;ncia Realizada</h1>
                <p style="color: #6b7280; margin-top: 5px;">Os dados da transfer&ecirc;ncia est&atilde;o abaixo:</p>
            </div>
            <div class="buttons">
                <a href="menu" class="btn btn-back">Menu Principal</a>
                <a href="logout" class="btn btn-logout">Sair</a>
            </div>
        </div>
        <div class="card" style="margin: 0 auto; text-align: left;">
            <div style="margin-bottom: 18px;">
                <strong>Conta de Origem:</strong> <%= contaOrigem %>
            </div>
            <div style="margin-bottom: 18px;">
                <strong>Conta de Destino:</strong> <%= contaDestino %>
            </div>
            <div style="margin-bottom: 18px;">
                <strong>Valor Transferido:</strong> R$ <%= valor %>
            </div>
            <div style="margin-bottom: 18px; color: green; font-weight: bold;">
                Transfer&ecirc;ncia realizada com sucesso!
            </div>
            <a href="extrato?id=<%= contaOrigem %>" class="btn btn-primary" style="width: 100%;">Ver Extrato</a>
        </div>
    </div>
</div>
