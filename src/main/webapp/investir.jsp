<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div id="investir-screen" class="screen">
    <div class="container">
        <div class="header">
            <h1>Investir</h1>
            <div class="buttons">
                <a href="conta?id=${conta.getId()}" class="btn btn-back">Voltar</a>
            </div>
        </div>
        
        <div class="card">
            <c:if test="${not empty erro}">
                <div class="msg-erro" style="margin-bottom: 20px;">${erro}</div>
            </c:if>
            
            <form action="investir?id=${conta.getId()}" method="post">
                <div class="form-group">
                    <label for="tipo">Tipo de Investimento:</label>
                    <select name="tipo" id="tipo" required>
                        <option value="CDB">CDB (1.2% a.m.)</option>
                        <option value="POUPANCA">Poupança (0.7% a.m.)</option>
                        <option value="TESOURO">Tesouro Direto (1.0% a.m.)</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="valor">Valor a investir:</label>
                    <input type="number" step="0.01" min="0.01" max="${conta.getSaldo()}" name="valor" id="valor" required />
                </div>
                
                <div class="buttons" style="margin-top: 20px;">
                    <button type="submit" class="btn btn-primary">Investir</button>
                </div>
            </form>
            
            <div style="margin-top: 20px; padding-top: 20px; border-top: 1px solid #e5e7eb;">
                <strong>Saldo disponível:</strong> R$ <fmt:formatNumber value="${conta.getSaldo()}" type="currency" currencySymbol="" minFractionDigits="2" maxFractionDigits="2" />
            </div>
        </div>
    </div>
</div>
