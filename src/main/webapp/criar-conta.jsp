<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Criar Nova Conta - Sistema Bancário</title>
    <link rel="stylesheet" href="styles.css?v=<%= System.currentTimeMillis() %>">
    <style>
        /* Inline CSS to ensure styling is applied */
        body.criar-conta-page {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
            margin: 0;
        }
        
        .form-container {
            background: white;
            border-radius: 15px;
            padding: 40px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
            max-width: 500px;
            width: 100%;
            position: relative;
            overflow: hidden;
        }
        
        .form-container::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, #3b82f6, #1e40af);
        }
        
        .form-header h1 {
            color: #1e3a8a;
            margin-bottom: 12px;
            font-size: 28px;
            font-weight: 600;
            text-align: center;
        }
        
        .form-header p {
            color: #64748b;
            font-size: 15px;
            line-height: 1.5;
            text-align: center;
            margin-bottom: 35px;
        }
        
        .input-group {
            margin-bottom: 25px;
            text-align: left;
        }
        
        .input-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #374151;
            font-size: 14px;
        }
        
        .input-group select,
        .input-group input {
            width: 100%;
            padding: 15px 16px;
            border: 2px solid #e5e7eb;
            border-radius: 8px;
            font-size: 16px;
            transition: all 0.3s ease;
            background-color: #fafafa;
            box-sizing: border-box;
        }
        
        .input-group select:focus,
        .input-group input:focus {
            outline: none;
            border-color: #3b82f6;
            background-color: white;
            box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
            transform: translateY(-1px);
        }
        
        .input-group small {
            display: block;
            margin-top: 8px;
            color: #6b7280;
            font-size: 13px;
            font-style: italic;
        }
        
        .form-actions {
            display: flex;
            gap: 15px;
            justify-content: center;
            margin-top: 35px;
            flex-wrap: wrap;
        }
        
        .btn {
            min-width: 140px;
            padding: 14px 24px;
            font-size: 16px;
            font-weight: 600;
            border-radius: 8px;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-block;
            text-align: center;
            border: none;
            cursor: pointer;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #3b82f6, #1d4ed8);
            color: white;
            box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4);
        }
        
        .btn-secondary {
            background: #f8fafc;
            border: 2px solid #e2e8f0;
            color: #475569;
        }
        
        .btn-secondary:hover {
            background: #e2e8f0;
            transform: translateY(-1px);
        }
        
        .alert {
            padding: 16px 20px;
            border-radius: 10px;
            margin-bottom: 25px;
            font-weight: 500;
            font-size: 15px;
            border-left: 4px solid;
        }
        
        .alert-error {
            background-color: #fef2f2;
            color: #dc2626;
            border-left-color: #dc2626;
            border: 1px solid #fecaca;
        }
        
        .alert-success {
            background-color: #f0fdf4;
            color: #16a34a;
            border-left-color: #16a34a;
            border: 1px solid #bbf7d0;
        }
    </style>
</head>
<body class="criar-conta-page">
    <div class="criar-conta-container">
        <div class="form-container">
            <div class="form-header">
                <h1>Criar Nova Conta</h1>
                <p>Preencha os dados para criar sua nova conta bancária</p>
            </div>

            <% if (request.getAttribute("erro") != null) { %>
                <div class="alert alert-error">
                    <%= request.getAttribute("erro") %>
                </div>
            <% } %>

            <% if (request.getAttribute("sucesso") != null) { %>
                <div class="alert alert-success">
                    <%= request.getAttribute("sucesso") %>
                </div>
            <% } %>

            <form method="post" action="criar-conta" class="form">
                <div class="input-group">
                    <label for="tipo">Tipo de Conta *</label>
                    <select id="tipo" name="tipo" required>
                        <option value="">Selecione o tipo de conta</option>
                        <option value="CORRENTE" <%= "CORRENTE".equals(request.getParameter("tipo")) ? "selected" : "" %>>
                            Conta Corrente
                        </option>
                        <option value="POUPANCA" <%= "POUPANCA".equals(request.getParameter("tipo")) ? "selected" : "" %>>
                            Poupança
                        </option>
                    </select>
                </div>

                <div class="input-group money-input">
                    <label for="saldoInicial">Saldo Inicial (opcional)</label>
                    <input 
                        type="number" 
                        id="saldoInicial" 
                        name="saldoInicial" 
                        step="0.01" 
                        min="0"
                        placeholder="0,00"
                        value="<%= request.getParameter("saldoInicial") != null ? request.getParameter("saldoInicial") : "" %>"
                    >
                    <small>Deixe em branco para criar com saldo zero</small>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">Criar Conta</button>
                    <a href="menu" class="btn btn-secondary">Voltar ao Menu</a>
                </div>
            </form>
        </div>
    </div>

    <script>
        // Formatar input de valor monetário
        document.getElementById('saldoInicial').addEventListener('input', function(e) {
            let value = e.target.value;
            // Remove caracteres não numéricos exceto ponto e vírgula
            value = value.replace(/[^0-9.,]/g, '');
            // Substitui vírgula por ponto
            value = value.replace(',', '.');
            e.target.value = value;
        });
    </script>
</body>
</html>
