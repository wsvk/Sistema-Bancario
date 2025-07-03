package uff.ic.devweb.sistemabancario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uff.ic.devweb.sistemabancario.model.Conta;
import uff.ic.devweb.sistemabancario.model.Transacao;
import uff.ic.devweb.sistemabancario.model.Usuario;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "ContaServlet", urlPatterns = {"/conta"})
public class ContaServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar se o usuário está logado
        Usuario usuario = usuarioAtual(request);
        if (usuario == null) {
            response.sendRedirect("login");
            return;
        }
        
        // Verificar se foi passado o ID da conta
        String contaIdParam = request.getParameter("id");
        if (contaIdParam == null || contaIdParam.isEmpty()) {
            response.sendRedirect("menu");
            return;
        }
        
        try {
            long contaId = Long.parseLong(contaIdParam);
            
            // Buscar detalhes da conta
            Conta conta = buscarConta(contaId, usuario.getId());
            if (conta == null) {
                // Conta não encontrada ou não pertence ao usuário
                request.setAttribute("erro", "Conta não encontrada ou você não tem permissão para acessá-la");
                response.sendRedirect("menu");
                return;
            }
            
            // Buscar transações da conta
            List<Transacao> transacoes = buscarTransacoesDaConta(contaId);
            
            request.setAttribute("conta", conta);
            request.setAttribute("transacoes", transacoes);
            request.setAttribute("page", "conta.jsp");
            
            request.getRequestDispatcher("layout.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect("menu");
        }
    }
    
    private Conta buscarConta(long contaId, long usuarioId) {
        try (Connection conn = getConn();
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
            Logger.getLogger(ContaServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    private List<Transacao> buscarTransacoesDaConta(long contaId) {
        List<Transacao> transacoes = new ArrayList<>();
        
        try (Connection conn = getConn();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT t.id, t.conta_origem_id, t.conta_destino_id, t.tipo, t.valor, t.data, " +
                     "o.tipo as tipo_origem, d.tipo as tipo_destino, " +
                     "uo.nome as nome_origem, ud.nome as nome_destino " +
                     "FROM transacoes t " +
                     "LEFT JOIN contas o ON t.conta_origem_id = o.id " +
                     "LEFT JOIN contas d ON t.conta_destino_id = d.id " +
                     "LEFT JOIN usuarios uo ON o.id_usuario = uo.id " +
                     "LEFT JOIN usuarios ud ON d.id_usuario = ud.id " +
                     "WHERE t.conta_origem_id = ? OR t.conta_destino_id = ? " +
                     "ORDER BY t.data DESC")) {
            
            stmt.setLong(1, contaId);
            stmt.setLong(2, contaId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Transacao transacao = new Transacao();
                    transacao.setId(rs.getLong("id"));
                    
                    // Pode ser nulo em caso de depósito
                    if (rs.getObject("conta_origem_id") != null) {
                        transacao.setContaOrigemId(rs.getLong("conta_origem_id"));
                    }
                    
                    // Pode ser nulo em caso de saque
                    if (rs.getObject("conta_destino_id") != null) {
                        transacao.setContaDestinoId(rs.getLong("conta_destino_id"));
                    }
                    
                    transacao.setTipo(rs.getString("tipo"));
                    transacao.setValor(rs.getBigDecimal("valor"));
                    transacao.setData(rs.getTimestamp("data"));
                    
                    // Dados adicionais para exibição
                    transacao.setNomeContaOrigem(rs.getString("nome_origem"));
                    transacao.setNomeContaDestino(rs.getString("nome_destino"));
                    
                    // Números formatados para as contas
                    if (rs.getObject("conta_origem_id") != null) {
                        long id = rs.getLong("conta_origem_id");
                        transacao.setNumeroContaOrigem(String.format("%05d-%d", id, id % 10));
                    }
                    
                    if (rs.getObject("conta_destino_id") != null) {
                        long id = rs.getLong("conta_destino_id");
                        transacao.setNumeroContaDestino(String.format("%05d-%d", id, id % 10));
                    }
                    
                    transacoes.add(transacao);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ContaServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return transacoes;
    }
}