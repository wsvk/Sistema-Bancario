<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div id="conta-screen" class="screen">
    <div class="container">
        <div class="header">
            <div>
                <h1>${conta.getTipoFormatado()}</h1>
                <p style="color: #6b7280; margin-top: 5px;">Conta: ${conta.getNumeroFormatado()}</p>
            </div>
            <div class="buttons">
                <a href="./menu" class="btn btn-back">Voltar</a>
                <a href="./logout" class="btn btn-logout">Sair</a>
            </div>
        </div>

        <div class="balance-card">
            <h2>Saldo Atual</h2>
            <div class="balance">R$ <fmt:formatNumber value="${conta.getSaldo()}" type="currency" currencySymbol="" minFractionDigits="2" maxFractionDigits="2" /></div>
        </div>

        <div style="margin: 30px 0;">
            <div class="operations">
                <a href="#" class="operation-btn" onclick="alert('Funcionalidade em desenvolvimento!'); return false;">
                    <span class="icon">💰</span>
                    <span>Depositar</span>
                </a>
                <a href="#" class="operation-btn" onclick="alert('Funcionalidade em desenvolvimento!'); return false;">
                    <span class="icon">💸</span>
                    <span>Sacar</span>
                </a>
                <a href="./transferencia?id=${conta.getId()}" class="operation-btn">
                    <span class="icon">↗️</span>
                    <span>Transferir</span>
                </a>
                <div style="display: flex; gap: 10px;">
                    
                </div>
               <a href="./investir?id=${conta.getId()}" class="operation-btn">>
                    <span class="icon">📊</span>
                    <span>Investir</span>
                </a>
                <a href="./meus-investimentos?id=${conta.getId()}" class="operation-btn">
                    <span class="icon">📈</span>
                    <span>Meus Investimentos</span>
                </a>
            </div>
        </div>

        <div class="statement-list">
            <h3 style="margin-bottom: 20px;">Extrato</h3>

            <c:choose>
                <c:when test="${empty transacoes}">
                    <div style="text-align: center; padding: 30px; background: #f9fafb; border-radius: 8px; border: 1px dashed #d1d5db;">
                        <p style="color: #6b7280;">Nenhuma transação registrada.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="transacao" items="${transacoes}">
                        <div class="statement-item">
                            <div class="statement-info">
                                <div class="statement-description">${transacao.getDescricao(conta.getId())}</div>
                                <div class="statement-date"><fmt:formatDate value="${transacao.getData()}" pattern="dd/MM/yyyy HH:mm" /></div>
                            </div>
                            <c:choose>
                                <c:when test="${transacao.isCredito(conta.getId())}">
                                    <div class="statement-amount positive">+R$ <fmt:formatNumber value="${transacao.getValor()}" type="currency" currencySymbol="" minFractionDigits="2" maxFractionDigits="2" /></div>
                                </c:when>
                                <c:when test="${transacao.isDebito(conta.getId())}">
                                    <div class="statement-amount negative">-R$ <fmt:formatNumber value="${transacao.getValor()}" type="currency" currencySymbol="" minFractionDigits="2" maxFractionDigits="2" /></div>
                                </c:when>
                                <c:otherwise>
                                    <div class="statement-amount">R$ <fmt:formatNumber value="${transacao.getValor()}" type="currency" currencySymbol="" minFractionDigits="2" maxFractionDigits="2" /></div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>
