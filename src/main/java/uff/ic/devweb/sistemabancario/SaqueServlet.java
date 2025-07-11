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
import java.sql.ResultSet;
import java.sql.SQLException;
import uff.ic.devweb.sistemabancario.model.Usuario;
/**
 *
 * @author Vinicius
 */

@WebServlet(name = "SaqueServlet", urlPatterns = "/saque")
public class SaqueServlet extends BaseServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contaId = request.getParameter("id");
        request.setAttribute("contaId", contaId);
        render(request, response, "saque.jsp");
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
                request.setAttribute("erro", "O valor do saque deve ser maior que zero.");
                render(request, response, "saque.jsp");
                return;
            }

            conn = getConn();
            conn.setAutoCommit(false);

            // Verificar saldo
            PreparedStatement psCheck = conn.prepareStatement("SELECT saldo FROM contas WHERE id = ? AND id_usuario = ?");
            psCheck.setLong(1, contaId);
            psCheck.setLong(2, usuario.getId());
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                double saldo = rs.getDouble("saldo");
                if (saldo < valor) {
                    conn.rollback();
                    request.setAttribute("erro", "Saldo insuficiente.");
                    render(request, response, "saque.jsp");
                    return;
                }
            } else {
                conn.rollback();
                request.setAttribute("erro", "Conta não encontrada ou não pertence ao usuário.");
                render(request, response, "saque.jsp");
                return;
            }

            // Atualizar saldo
            PreparedStatement psUpdate = conn.prepareStatement("UPDATE contas SET saldo = saldo - ? WHERE id = ?");
            psUpdate.setDouble(1, valor);
            psUpdate.setLong(2, contaId);
            int rows = psUpdate.executeUpdate();

            if (rows > 0) {
                // Inserir transação
                PreparedStatement psTransacao = conn.prepareStatement("INSERT INTO transacoes (conta_origem_id, tipo, valor) VALUES (?, 'SAQUE', ?)");
                psTransacao.setLong(1, contaId);
                psTransacao.setDouble(2, valor);
                psTransacao.executeUpdate();

                conn.commit();
                response.sendRedirect("conta?id=" + contaId);
            } else {
                conn.rollback();
                request.setAttribute("erro", "Erro ao realizar o saque.");
                render(request, response, "saque.jsp");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("erro", "Valor ou ID da conta inválido.");
            render(request, response, "saque.jsp");
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                // Log
            }
            request.setAttribute("erro", "Erro no banco de dados: " + e.getMessage());
            render(request, response, "saque.jsp");
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                // Log
            }
        }
    }
}
