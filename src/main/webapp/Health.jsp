<%-- 
    Document   : Health
    Created on : Jun 28, 2025, 10:36:53?PM
    Author     : mila
--%>

<div id="health-screen" class="screen active">
    <div class="container">
        <div class="card">
            <h1 style="margin-bottom:10px;">Status do sistema</h1>
            <p><b>Acesso ao servlet:</b> <%= request.getAttribute("status")%></p>
            <p><b>Status do Banco:</b> <%= request.getAttribute("db_status")%></p>
        </div>
    </div>
</div>