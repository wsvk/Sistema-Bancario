package uff.ic.devweb.sistemabancario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import uff.ic.devweb.sistemabancario.model.Conta;
import uff.ic.devweb.sistemabancario.model.Usuario;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "Meus-Investimentos", urlPatterns = {"/meus-investimentos"})
public class MeusInvestimentosServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Verificar se o usuário está logado
        Usuario usuario = usuarioAtual(req);
        if (usuario == null) {
            resp.sendRedirect("login");
            return;
        }
        
        // Buscar conta pelo ID ou da sessão
        String contaIdParam = req.getParameter("id");
        Conta conta = null;
        
        if (contaIdParam != null && !contaIdParam.isEmpty()) {
            try {
                long contaId = Long.parseLong(contaIdParam);
                conta = buscarConta(contaId, usuario.getId());
            } catch (NumberFormatException e) {
                resp.sendRedirect("menu");
                return;
            }
        } else {
            HttpSession session = req.getSession(false);
            if (session != null) {
                conta = (Conta) session.getAttribute("conta");
            }
        }
        
        if (conta == null) {
            resp.sendRedirect("menu");
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
        req.setAttribute("page", "meus-investimentos.jsp");
        req.getRequestDispatcher("layout.jsp").forward(req, resp);
    }
    
    private Conta buscarConta(long contaId, long usuarioId) {
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT id, id_usuario, saldo, tipo FROM contas WHERE id = ? AND id_usuario = ?")) {
            
            stmt.setLong(1, contaId);
            stmt.setLong(2, usuarioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Conta conta = new Conta();
                    conta.setId(rs.getLong("id"));
                    conta.setIdUsuario(rs.getLong("id_usuario"));
                    conta.setSaldo(rs.getBigDecimal("saldo"));
                    conta.setTipo(rs.getString("tipo"));
                    return conta;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
}
