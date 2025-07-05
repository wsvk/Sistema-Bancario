<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div class="container">
    <h1>Meus Investimentos</h1>
    <c:if test="${empty investimentos}">
        <div style="color: #6b7280; margin: 30px 0;">Nenhum investimento encontrado.</div>
    </c:if>
    <c:if test="${not empty investimentos}">
        <table class="table">
            <thead>
                <tr>
                    <th>Tipo</th>
                    <th>Valor</th>
                    <th>Data</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="inv" items="${investimentos}">
                    <tr>
                        <td>${inv.tipo}</td>
                        <td>R$ <fmt:formatNumber value="${inv.valor}" type="currency" currencySymbol="" minFractionDigits="2" maxFractionDigits="2" /></td>
                        <td><fmt:formatDate value="${inv.data}" pattern="dd/MM/yyyy HH:mm" /></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
    <div style="margin-top: 20px;">
        <a href="conta" class="btn btn-back">Voltar para Conta</a>
    </div>
</div>
