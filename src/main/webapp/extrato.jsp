<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="styles.css">
        <title>Extrato - Sistema Bancario</title>
    </head>
    <body>
        <div id="extrato-screen" class="screen">
            <div class="container">
                <div class="header">
                    <div>
                        <h1>Extrato de Transações</h1>
                        <p style="color: #6b7280; margin-top: 5px;">${usuario.getNome()}</p>
                    </div>
                    <div class="buttons">
                        <a href="./menu" class="btn btn-back">Menu Principal</a>
                        <a href="./logout" class="btn btn-logout">Sair</a>
                    </div>
                </div>

                <!-- Filtros -->
                <div class="filter-section">
                    <form method="get" action="extrato" class="filter-form">
                        <div class="filter-row">
                            <div class="filter-group">
                                <label for="conta">Conta:</label>
                                <select id="conta" name="conta">
                                    <option value="">Todas as contas</option>
                                    <c:forEach var="conta" items="${contas}">
                                        <option value="${conta.getId()}" 
                                                <c:if test="${contaSelecionada == conta.getId().toString()}">selected</c:if>>
                                            ${conta.getTipoFormatado()} - ${conta.getNumeroFormatado()}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            
                            <div class="filter-group">
                                <label for="tipo">Tipo:</label>
                                <select id="tipo" name="tipo">
                                    <option value="TODOS" <c:if test="${tipoSelecionado == 'TODOS' || empty tipoSelecionado}">selected</c:if>>Todos</option>
                                    <option value="DEPOSITO" <c:if test="${tipoSelecionado == 'DEPOSITO'}">selected</c:if>>Depósito</option>
                                    <option value="SAQUE" <c:if test="${tipoSelecionado == 'SAQUE'}">selected</c:if>>Saque</option>
                                    <option value="TRANSFERENCIA" <c:if test="${tipoSelecionado == 'TRANSFERENCIA'}">selected</c:if>>Transferência</option>
                                </select>
                            </div>
                        </div>
                        
                        <div class="filter-row">
                            <div class="filter-group">
                                <label for="dataInicio">Data Início:</label>
                                <input type="date" id="dataInicio" name="dataInicio" value="${dataInicio}">
                            </div>
                            
                            <div class="filter-group">
                                <label for="dataFim">Data Fim:</label>
                                <input type="date" id="dataFim" name="dataFim" value="${dataFim}">
                            </div>
                        </div>
                        
                        <div class="filter-actions">
                            <button type="submit" class="btn btn-primary">Filtrar</button>
                            <a href="extrato" class="btn btn-secondary">Limpar Filtros</a>
                        </div>
                    </form>
                </div>

                <!-- Lista de Transações -->
                <div class="transactions-section">
                    <div class="section-header">
                        <h3>Histórico de Transações</h3>
                        <p class="transaction-count">
                            <c:choose>
                                <c:when test="${transacoes.size() == 1}">1 transação encontrada</c:when>
                                <c:otherwise>${transacoes.size()} transações encontradas</c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                    
                    <c:choose>
                        <c:when test="${empty transacoes}">
                            <div class="empty-state">
                                <div class="empty-icon">📋</div>
                                <h4>Nenhuma transação encontrada</h4>
                                <p>Não há transações que correspondam aos filtros aplicados.</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="transactions-list">
                                <c:forEach var="transacao" items="${transacoes}">
                                    <div class="transaction-item">
                                        <div class="transaction-icon">
                                            <c:choose>
                                                <c:when test="${transacao.getTipo() == 'DEPOSITO'}">💰</c:when>
                                                <c:when test="${transacao.getTipo() == 'SAQUE'}">💸</c:when>
                                                <c:when test="${transacao.getTipo() == 'TRANSFERENCIA'}">↔️</c:when>
                                                <c:otherwise>💳</c:otherwise>
                                            </c:choose>
                                        </div>
                                        
                                        <div class="transaction-details">
                                            <div class="transaction-header">
                                                <h4 class="transaction-type">
                                                    <c:choose>
                                                        <c:when test="${transacao.getTipo() == 'DEPOSITO'}">Depósito</c:when>
                                                        <c:when test="${transacao.getTipo() == 'SAQUE'}">Saque</c:when>
                                                        <c:when test="${transacao.getTipo() == 'TRANSFERENCIA'}">Transferência</c:when>
                                                        <c:otherwise>${transacao.getTipo()}</c:otherwise>
                                                    </c:choose>
                                                </h4>
                                                <div class="transaction-amount 
                                                    <c:choose>
                                                        <c:when test="${transacao.getTipo() == 'DEPOSITO'}">positive</c:when>
                                                        <c:when test="${transacao.getTipo() == 'SAQUE'}">negative</c:when>
                                                        <c:when test="${transacao.getTipo() == 'TRANSFERENCIA'}">neutral</c:when>
                                                        <c:otherwise>neutral</c:otherwise>
                                                    </c:choose>">
                                                    <c:choose>
                                                        <c:when test="${transacao.getTipo() == 'DEPOSITO'}">+</c:when>
                                                        <c:when test="${transacao.getTipo() == 'SAQUE'}">-</c:when>
                                                        <c:otherwise></c:otherwise>
                                                    </c:choose>R$ <fmt:formatNumber value="${transacao.getValor()}" type="currency" currencySymbol="" minFractionDigits="2" maxFractionDigits="2" />
                                                </div>
                                            </div>
                                            
                                            <div class="transaction-info">
                                                <div class="transaction-date">
                                                    <fmt:formatDate value="${transacao.getData()}" pattern="dd/MM/yyyy 'às' HH:mm:ss" />
                                                </div>
                                                
                                                <c:if test="${transacao.getTipo() == 'TRANSFERENCIA'}">
                                                    <div class="transaction-parties">
                                                        <c:if test="${not empty transacao.getNomeContaOrigem()}">
                                                            <span class="party-info">
                                                                <strong>De:</strong> ${transacao.getNomeContaOrigem()} 
                                                                (${transacao.getNumeroContaOrigem()})
                                                            </span>
                                                        </c:if>
                                                        <c:if test="${not empty transacao.getNomeContaDestino()}">
                                                            <span class="party-info">
                                                                <strong>Para:</strong> ${transacao.getNomeContaDestino()} 
                                                                (${transacao.getNumeroContaDestino()})
                                                            </span>
                                                        </c:if>
                                                    </div>
                                                </c:if>
                                                
                                                <c:if test="${transacao.getTipo() == 'DEPOSITO' && not empty transacao.getNumeroContaDestino()}">
                                                    <div class="transaction-account">
                                                        <strong>Conta:</strong> ${transacao.getNumeroContaDestino()}
                                                    </div>
                                                </c:if>
                                                
                                                <c:if test="${transacao.getTipo() == 'SAQUE' && not empty transacao.getNumeroContaOrigem()}">
                                                    <div class="transaction-account">
                                                        <strong>Conta:</strong> ${transacao.getNumeroContaOrigem()}
                                                    </div>
                                                </c:if>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </body>
</html>
