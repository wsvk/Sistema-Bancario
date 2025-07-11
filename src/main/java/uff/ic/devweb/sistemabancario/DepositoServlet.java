/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package uff.ic.devweb.sistemabancario;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import uff.ic.devweb.sistemabancario.model.Usuario;
/**
 *
 * @author Vinicius
 */

@WebServlet(name = "DepositoServlet", urlPatterns = "/deposito")
public class DepositoServlet extends BaseServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contaId = request.getParameter("id");
        request.setAttribute("contaId", contaId);
        render(request, response, "deposito.jsp");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Usuario usuario = usuarioAtual(request);
        if (usuario == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        String valorStr = request.getParameter("valor");
        String contaIdStr = request.getParameter("id");

        Connection conn = null;
        try {
            double valor = Double.parseDouble(valorStr);
            long contaId = Long.parseLong(contaIdStr);

            if (valor <= 0) {
                request.setAttribute("erro", "O valor do depósito deve ser maior que zero.");
                render(request, response, "deposito.jsp");
                return;
            }

            conn = getConn();
            conn.setAutoCommit(false); // Iniciar transação

            // Atualizar saldo da conta
            PreparedStatement stmtUpdate = conn.prepareStatement("UPDATE contas SET saldo = saldo + ? WHERE id = ? AND id_usuario = ?");
            stmtUpdate.setDouble(1, valor);
            stmtUpdate.setLong(2, contaId);
            stmtUpdate.setLong(3, usuario.getId());
            int rows = stmtUpdate.executeUpdate();

            if (rows > 0) {
                // Inserir transação
                PreparedStatement stmtTransacao = conn.prepareStatement("INSERT INTO transacoes (conta_destino_id, tipo, valor) VALUES (?, 'DEPOSITO', ?)");
                stmtTransacao.setLong(1, contaId);
                stmtTransacao.setDouble(2, valor);
                stmtTransacao.executeUpdate();

                conn.commit(); // Confirmar transação
                response.sendRedirect("conta?id=" + contaId);
            } else {
                conn.rollback();
                request.setAttribute("erro", "Erro ao realizar o depósito. Conta não encontrada ou não pertence ao usuário.");
                render(request, response, "deposito.jsp");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("erro", "Valor ou ID da conta inválido.");
            render(request, response, "deposito.jsp");
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                // Logar erro de rollback
            }
            request.setAttribute("erro", "Erro no banco de dados: " + e.getMessage());
            render(request, response, "deposito.jsp");
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                // Logar erro ao resetar autoCommit
            }
        }
    }
}
