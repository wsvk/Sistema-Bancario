<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div id="meus-investimentos-screen" class="screen">
    <div class="container">
        <div class="header">
            <h1>Meus Investimentos</h1>
            <div class="buttons">
                <a href="conta?id=${conta.getId()}" class="btn btn-back">Voltar</a>
            </div>
        </div>
        
        <div style="background: white; border-radius: 12px; padding: 30px; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);">
            <c:if test="${empty investimentos}">
                <div style="color: #6b7280; margin: 30px 0; text-align: center; font-size: 16px;">
                    Nenhum investimento encontrado.
                </div>
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
        </div>
    </div>
</div>
