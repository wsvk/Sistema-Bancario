package uff.ic.devweb.sistemabancario;

import uff.ic.devweb.sistemabancario.model.Conta;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

@WebServlet("/investir")
public class InvestirServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Conta conta = (Conta) session.getAttribute("conta");
        if (conta == null) {
            resp.sendRedirect("login");
            return;
        }
        req.setAttribute("conta", conta);
        req.getRequestDispatcher("investir.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Conta conta = (Conta) session.getAttribute("conta");
        if (conta == null) {
            resp.sendRedirect("login");
            return;
        }
        String tipo = req.getParameter("tipo");
        String valorStr = req.getParameter("valor");
        BigDecimal valor;
        try {
            valor = new BigDecimal(valorStr);
        } catch (Exception e) {
            req.setAttribute("erro", "Valor inválido.");
            req.setAttribute("conta", conta);
            req.getRequestDispatcher("investir.jsp").forward(req, resp);
            return;
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0 || valor.compareTo(conta.getSaldo()) > 0) {
            req.setAttribute("erro", "Valor deve ser positivo e menor ou igual ao saldo disponível.");
            req.setAttribute("conta", conta);
            req.getRequestDispatcher("investir.jsp").forward(req, resp);
            return;
        }
        // Debitar valor da conta
        try (Connection conn = DB.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement ps1 = conn.prepareStatement("UPDATE contas SET saldo = saldo - ? WHERE id = ?");
                ps1.setBigDecimal(1, valor);
                ps1.setLong(2, conta.getId());
                ps1.executeUpdate();
                PreparedStatement ps2 = conn.prepareStatement("INSERT INTO investimentos (id_conta, tipo, valor, data) VALUES (?, ?, ?, ?)");
                ps2.setLong(1, conta.getId());
                ps2.setString(2, tipo);
                ps2.setBigDecimal(3, valor);
                ps2.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                ps2.executeUpdate();
                conn.commit();
                // Atualizar saldo em sessão
                conta.setSaldo(conta.getSaldo().subtract(valor));
                session.setAttribute("conta", conta);
                resp.sendRedirect("conta?msg=investimento_sucesso");
            } catch (SQLException e) {
                conn.rollback();
                req.setAttribute("erro", "Erro ao registrar investimento.");
                req.setAttribute("conta", conta);
                req.getRequestDispatcher("investir.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            req.setAttribute("erro", "Erro de conexão com o banco de dados.");
            req.setAttribute("conta", conta);
            req.getRequestDispatcher("investir.jsp").forward(req, resp);
        }
    }
}
