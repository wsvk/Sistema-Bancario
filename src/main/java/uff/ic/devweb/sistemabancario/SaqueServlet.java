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
        try {
            double valor = Double.parseDouble(valorStr);
            if (valor <= 0) {
                request.setAttribute("erro", "O valor do saque deve ser maior que zero.");
                render(request, response, "saque.jsp");
                return;
            }
            Connection conn = getConn();
            PreparedStatement checkSaldo = conn.prepareStatement("SELECT saldo FROM contas WHERE id_usuario = ?");
            checkSaldo.setLong(1, usuario.getId());
            ResultSet rs = checkSaldo.executeQuery();
            while (rs.next()){
                if (valor > rs.getDouble("saldo")){
                    request.setAttribute("erro", "O valor do saque nao pode ser maior do que o valor contido na conta.");
                    render(request, response, "saque.jsp");
                    return;
                }
            }
            PreparedStatement stmt1 = conn.prepareStatement("UPDATE contas SET saldo = saldo - ? WHERE id_usuario = ?");
            stmt1.setDouble(1, valor);
            stmt1.setLong(2, usuario.getId());
            int rows = stmt1.executeUpdate();
            if (rows > 0) {
                PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO transacoes (conta_origem_id, conta_destino_id, tipo, valor)"
                        + "VALUES (?, NULL, 'SAQUE', ?)");
                stmt2.setLong(1, usuario.getId());
                stmt2.setDouble(2, valor);
                stmt2.executeUpdate();
                request.setAttribute("mensagem", "Depósito realizado com sucesso!");
                response.sendRedirect("conta?id=" + usuario.getId());
                return;
            } else {
                request.setAttribute("erro", "Erro ao realizar o depósito. Conta não encontrada.");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("erro", "Valor inválido para depósito.");
        } catch (SQLException e) {
            request.setAttribute("erro", "Erro no banco de dados: " + e.getMessage());
        }
        render(request, response, "deposito.jsp");
    }
}
