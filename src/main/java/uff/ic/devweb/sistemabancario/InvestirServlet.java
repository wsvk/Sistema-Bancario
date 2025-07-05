package uff.ic.devweb.sistemabancario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import uff.ic.devweb.sistemabancario.model.Conta;
import uff.ic.devweb.sistemabancario.model.Usuario;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@WebServlet(name = "Investir", urlPatterns = {"/investir"})
public class InvestirServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Verificar se o usuário está logado
        Usuario usuario = usuarioAtual(req);
        if (usuario == null) {
            resp.sendRedirect("login");
            return;
        }
        
        // Tentar pegar o ID da conta da URL primeiro
        String contaIdParam = req.getParameter("id");
        Conta conta = null;
        
        if (contaIdParam != null && !contaIdParam.isEmpty()) {
            // Se ID foi passado, buscar conta do banco
            try {
                long contaId = Long.parseLong(contaIdParam);
                conta = buscarConta(contaId, usuario.getId());
            } catch (NumberFormatException e) {
                resp.sendRedirect("menu");
                return;
            }
        } else {
            // Se não foi passado ID, tentar pegar da sessão (fallback)
            HttpSession session = req.getSession(false);
            if (session != null) {
                conta = (Conta) session.getAttribute("conta");
            }
        }
        
        if (conta == null) {
            resp.sendRedirect("menu");
            return;
        }
        
        req.setAttribute("conta", conta);
        req.setAttribute("page", "investir.jsp");
        req.getRequestDispatcher("layout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Verificar se o usuário está logado
        Usuario usuario = usuarioAtual(req);
        if (usuario == null) {
            resp.sendRedirect("login");
            return;
        }
        
        // Buscar conta (pelo ID ou da sessão)
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
        String tipo = req.getParameter("tipo");
        String valorStr = req.getParameter("valor");
        BigDecimal valor;
        try {
            valor = new BigDecimal(valorStr);
        } catch (Exception e) {
            req.setAttribute("erro", "Valor inválido.");
            req.setAttribute("conta", conta);
            req.setAttribute("page", "investir.jsp");
            req.getRequestDispatcher("layout.jsp").forward(req, resp);
            return;
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0 || valor.compareTo(conta.getSaldo()) > 0) {
            req.setAttribute("erro", "Valor deve ser positivo e menor ou igual ao saldo disponível.");
            req.setAttribute("conta", conta);
            req.setAttribute("page", "investir.jsp");
            req.getRequestDispatcher("layout.jsp").forward(req, resp);
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
                // Atualizar saldo em sessão se existir
                HttpSession session = req.getSession(false);
                if (session != null) {
                    session.setAttribute("conta", conta);
                }
                resp.sendRedirect("conta?id=" + conta.getId());
            } catch (SQLException e) {
                conn.rollback();
                req.setAttribute("erro", "Erro ao registrar investimento.");
                req.setAttribute("conta", conta);
                req.setAttribute("page", "investir.jsp");
                req.getRequestDispatcher("layout.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            req.setAttribute("erro", "Erro de conexão com o banco de dados.");
            req.setAttribute("conta", conta);
            req.setAttribute("page", "investir.jsp");
            req.getRequestDispatcher("layout.jsp").forward(req, resp);
        }
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
