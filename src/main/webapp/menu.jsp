<%-- 
    Document   : menu
    Created on : Jun 29, 2025, 5:47:20 PM
    Author     : mila
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div id="menu" class="screen">
    <div class="container">
        <div class="header">
            <h1>Olá, ${usuario.getNome()}!</h1>
            <a href="./logout" class="btn btn-logout">Sair</a>
        </div>

        <div class="balance-card">
            <h2>Saldo Total</h2>
            <div class="balance">R$ <fmt:formatNumber value="${saldoTotal}" type="currency" currencySymbol="" minFractionDigits="2" maxFractionDigits="2" /></div>
        </div>

        <div class="accounts-section">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                <h3>Minhas Contas</h3>
                <div style="display: flex; gap: 10px;">
                    <a href="./extrato" class="btn btn-secondary" style="padding: 8px 16px; font-size: 14px;">Extrato</a>
                    <a href="./criar-conta" class="btn btn-primary" style="padding: 8px 16px; font-size: 14px;">Nova Conta</a>
                </div>
            </div>
            
            <p style="color: #6b7280; font-size: 12px; margin-bottom: 10px;">
                Usuário ID: ${usuario.getId()} | 
                Contas encontradas: ${contas.size()}
            </p>
            
            <c:choose>
                <c:when test="${empty contas}">
                    <div style="text-align: center; padding: 30px; background: #f9fafb; border-radius: 8px; border: 1px dashed #d1d5db;">
                        <p style="color: #6b7280; margin-bottom: 15px;">Você ainda não possui contas cadastradas.</p>
                        <a href="./criar-conta" class="btn btn-primary">Criar Primeira Conta</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="conta" items="${contas}">
                        <a href="./conta?id=${conta.getId()}">
                            <div class="account-card">
                                <div class="account-info">
                                    <h4>${conta.getTipoFormatado()}</h4>
                                    <p>Conta: ${conta.getNumeroFormatado()}</p>
                                </div>
                                <div class="account-balance">R$ <fmt:formatNumber value="${conta.getSaldo()}" type="currency" currencySymbol="" minFractionDigits="2" maxFractionDigits="2" /></div>
                            </div>
                        </a>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>