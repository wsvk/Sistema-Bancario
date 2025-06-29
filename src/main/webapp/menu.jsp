<%-- 
    Document   : menu
    Created on : Jun 29, 2025, 5:47:20 PM
    Author     : mila
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<div id="menu" class="screen">
    <div class="container">
        <div class="header">
            <h1>Olá, ${usuario.getNome()}!</h1>
            <a href="./logout" class="btn btn-logout">Sair</a>
        </div>

        <div class="balance-card">
            <h2>Saldo Total</h2>
            <div class="balance">R$ <span id="total-balance">(colocar somatorio de saldos)</span></div>
        </div>

        <div class="accounts-section">
            <h3>Minhas Contas</h3>
            <a href="TODO-link-pra-conta">
                <div class="account-card">
                    <div class="account-info">
                        <h4>Conta Corrente</h4>
                        <p>Conta: 12345-6</p>
                    </div>
                    <div class="account-balance">R$ 3.500,00</div>
                </div>
            </a>
            
            <a href="TODO-link-pra-conta">
                <div class="account-card">
                    <div class="account-info">
                        <h4>Poupança</h4>
                        <p>Conta: 78910-1</p>
                    </div>
                    <div class="account-balance">R$ 1.500,00</div>
                </div>
            </a>
        </div>
    </div>
</div>