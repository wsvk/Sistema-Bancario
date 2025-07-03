package uff.ic.devweb.sistemabancario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uff.ic.devweb.sistemabancario.model.Usuario;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "CriarContaServlet", urlPatterns = {"/criar-conta"})
public class CriarContaServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar se o usuário está logado
        Usuario usuario = usuarioAtual(request);
        if (usuario == null) {
            response.sendRedirect("login");
            return;
        }
        
        request.setAttribute("page", "criar-conta.jsp");
        request.getRequestDispatcher("layout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar se o usuário está logado
        Usuario usuario = usuarioAtual(request);
        if (usuario == null) {
            response.sendRedirect("login");
            return;
        }
        
        String tipo = request.getParameter("tipo");
        String saldoInicialStr = request.getParameter("saldoInicial");
        
        // Validação básica
        if (tipo == null || tipo.isEmpty() || (!tipo.equals("CORRENTE") && !tipo.equals("POUPANCA"))) {
            request.setAttribute("erro", "Tipo de conta inválido");
            request.setAttribute("page", "criar-conta.jsp");
            request.getRequestDispatcher("layout.jsp").forward(request, response);
            return;
        }
        
        // Converter saldo inicial (opcional)
        BigDecimal saldoInicial = BigDecimal.ZERO;
        if (saldoInicialStr != null && !saldoInicialStr.isEmpty()) {
            try {
                saldoInicial = new BigDecimal(saldoInicialStr);
                if (saldoInicial.compareTo(BigDecimal.ZERO) < 0) {
                    request.setAttribute("erro", "O saldo inicial não pode ser negativo");
                    request.setAttribute("page", "criar-conta.jsp");
                    request.getRequestDispatcher("layout.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("erro", "Valor de saldo inicial inválido");
                request.setAttribute("page", "criar-conta.jsp");
                request.getRequestDispatcher("layout.jsp").forward(request, response);
                return;
            }
        }
        
        try {
            Connection conn = getConn();
            
            // Iniciar transação
            conn.setAutoCommit(false);
            
            try {
                // 1. Criar a conta
                long contaId;
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO contas (id_usuario, saldo, tipo) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    
                    pstmt.setLong(1, usuario.getId());
                    pstmt.setBigDecimal(2, saldoInicial);
                    pstmt.setString(3, tipo);
                    
                    pstmt.executeUpdate();
                    
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            contaId = rs.getLong(1);
                        } else {
                            throw new SQLException("Falha ao obter ID da conta criada");
                        }
                    }
                }
                
                // 2. Se houver saldo inicial, registrar depósito
                if (saldoInicial.compareTo(BigDecimal.ZERO) > 0) {
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT INTO transacoes (conta_origem_id, conta_destino_id, tipo, valor) VALUES (NULL, ?, 'DEPOSITO', ?)")) {
                        
                        pstmt.setLong(1, contaId);
                        pstmt.setBigDecimal(2, saldoInicial);
                        
                        pstmt.executeUpdate();
                    }
                }
                
                // Confirmar transação
                conn.commit();
                
                request.setAttribute("sucesso", "Conta criada com sucesso!");
                
            } catch (SQLException e) {
                // Reverter transação em caso de erro
                conn.rollback();
                throw e;
            } finally {
                // Restaurar configuração padrão
                conn.setAutoCommit(true);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(CriarContaServlet.class.getName()).log(Level.SEVERE, null, ex);
            request.setAttribute("erro", "Erro ao criar conta: " + ex.getMessage());
        }
        
        request.setAttribute("page", "criar-conta.jsp");
        request.getRequestDispatcher("layout.jsp").forward(request, response);
    }
}
