<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div class="container">
    <h1>Investir</h1>
    <c:if test="${not empty erro}">
        <div style="color: red; margin-bottom: 10px;">${erro}</div>
    </c:if>
    <form action="investir" method="post">
        <div>
            <label for="tipo">Tipo de Investimento:</label>
            <select name="tipo" id="tipo" required>
                <option value="CDB">CDB (1.2% a.m.)</option>
                <option value="POUPANCA">Poupança (0.7% a.m.)</option>
                <option value="TESOURO">Tesouro Direto (1.0% a.m.)</option>
                <option value="FUNDOS">Fundos (1.5% a.m.)</option>
            </select>
        </div>
        <div>
            <label for="valor">Valor a investir:</label>
            <input type="number" step="0.01" min="0.01" max="${conta.getSaldo()}" name="valor" id="valor" required />
        </div>
        <div style="margin-top: 15px;">
            <button type="submit" class="btn">Investir</button>
            <a href="conta" class="btn btn-back">Cancelar</a>
        </div>
    </form>
    <div style="margin-top: 20px;">
        <strong>Saldo disponível:</strong> R$ <fmt:formatNumber value="${conta.getSaldo()}" type="currency" currencySymbol="" minFractionDigits="2" maxFractionDigits="2" />
    </div>
</div>
