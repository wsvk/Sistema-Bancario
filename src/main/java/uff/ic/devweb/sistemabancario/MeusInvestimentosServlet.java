package uff.ic.devweb.sistemabancario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import uff.ic.devweb.sistemabancario.model.Conta;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/meus-investimentos")
public class MeusInvestimentosServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Conta conta = (Conta) session.getAttribute("conta");
        if (conta == null) {
            resp.sendRedirect("login");
            return;
        }
        List<Map<String, Object>> investimentos = new ArrayList<>();
        try (Connection conn = DB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT tipo, valor, data FROM investimentos WHERE id_conta = ? ORDER BY data DESC");
            ps.setLong(1, conta.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> inv = new HashMap<>();
                inv.put("tipo", rs.getString("tipo"));
                inv.put("valor", rs.getBigDecimal("valor"));
                inv.put("data", rs.getTimestamp("data"));
                investimentos.add(inv);
            }
        } catch (Exception e) {
            req.setAttribute("erro", "Erro ao buscar investimentos.");
        }
        req.setAttribute("investimentos", investimentos);
        req.getRequestDispatcher("meus-investimentos.jsp").forward(req, resp);
    }
}
