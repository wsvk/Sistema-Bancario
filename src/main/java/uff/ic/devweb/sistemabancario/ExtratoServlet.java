package uff.ic.devweb.sistemabancario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uff.ic.devweb.sistemabancario.model.Conta;
import uff.ic.devweb.sistemabancario.model.Transacao;
import uff.ic.devweb.sistemabancario.model.Usuario;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "ExtratoServlet", urlPatterns = {"/extrato"})
public class ExtratoServlet extends BaseServlet {

    private static final Logger LOGGER = Logger.getLogger(ExtratoServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verificar se o usuário está logado
        Usuario usuario = usuarioAtual(request);
        if (usuario == null) {
            response.sendRedirect("login");
            return;
        }
        
        // Buscar todas as contas do usuário
        List<Conta> contas = buscarContasDoUsuario(usuario.getId());
        
        // Parâmetros de filtro
        String contaIdParam = request.getParameter("conta");
        String dataInicioParam = request.getParameter("dataInicio");
        String dataFimParam = request.getParameter("dataFim");
        String tipoParam = request.getParameter("tipo");
        
        // Buscar transações com filtros
        List<Transacao> transacoes = buscarTransacoesComFiltros(
            usuario.getId(), contaIdParam, dataInicioParam, dataFimParam, tipoParam
        );
        
        request.setAttribute("usuario", usuario);
        request.setAttribute("contas", contas);
        request.setAttribute("transacoes", transacoes);
        request.setAttribute("contaSelecionada", contaIdParam);
        request.setAttribute("dataInicio", dataInicioParam);
        request.setAttribute("dataFim", dataFimParam);
        request.setAttribute("tipoSelecionado", tipoParam);
        
        render(request, response, "extrato.jsp");
    }
    
    private List<Conta> buscarContasDoUsuario(long idUsuario) {
        List<Conta> contas = new ArrayList<>();
        
        try (Connection conn = getConn();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT id, id_usuario, saldo, tipo FROM contas WHERE id_usuario = ? ORDER BY id")) {
            
            stmt.setLong(1, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Conta conta = new Conta();
                    conta.setId(rs.getLong("id"));
                    conta.setIdUsuario(rs.getLong("id_usuario"));
                    conta.setSaldo(rs.getBigDecimal("saldo"));
                    conta.setTipo(rs.getString("tipo"));
                    contas.add(conta);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar contas para extrato", ex);
        }
        
        return contas;
    }
    
    private List<Transacao> buscarTransacoesComFiltros(long usuarioId, String contaId, 
            String dataInicio, String dataFim, String tipo) {
        List<Transacao> transacoes = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder(
            "SELECT t.id, t.conta_origem_id, t.conta_destino_id, t.tipo, t.valor, t.data, " +
            "o.tipo as tipo_origem, d.tipo as tipo_destino, " +
            "uo.nome as nome_origem, ud.nome as nome_destino " +
            "FROM transacoes t " +
            "LEFT JOIN contas o ON t.conta_origem_id = o.id " +
            "LEFT JOIN contas d ON t.conta_destino_id = d.id " +
            "LEFT JOIN usuarios uo ON o.id_usuario = uo.id " +
            "LEFT JOIN usuarios ud ON d.id_usuario = ud.id " +
            "WHERE (o.id_usuario = ? OR d.id_usuario = ?)"
        );
        
        List<Object> params = new ArrayList<>();
        params.add(usuarioId);
        params.add(usuarioId);
        
        // Filtro por conta específica
        if (contaId != null && !contaId.isEmpty()) {
            try {
                long idConta = Long.parseLong(contaId);
                sql.append(" AND (t.conta_origem_id = ? OR t.conta_destino_id = ?)");
                params.add(idConta);
                params.add(idConta);
            } catch (NumberFormatException e) {
                LOGGER.warning("ID de conta inválido: " + contaId);
            }
        }
        
        // Filtro por data de início
        if (dataInicio != null && !dataInicio.isEmpty()) {
            sql.append(" AND t.data >= ?");
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date data = sdf.parse(dataInicio);
                params.add(new java.sql.Timestamp(data.getTime()));
            } catch (ParseException e) {
                LOGGER.warning("Data de início inválida: " + dataInicio);
            }
        }
        
        // Filtro por data fim
        if (dataFim != null && !dataFim.isEmpty()) {
            sql.append(" AND t.data <= ?");
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date data = sdf.parse(dataFim + " 23:59:59");
                params.add(new java.sql.Timestamp(data.getTime()));
            } catch (ParseException e) {
                LOGGER.warning("Data fim inválida: " + dataFim);
            }
        }
        
        // Filtro por tipo
        if (tipo != null && !tipo.isEmpty() && !tipo.equals("TODOS")) {
            sql.append(" AND t.tipo = ?");
            params.add(tipo);
        }
        
        sql.append(" ORDER BY t.data DESC");
        
        try (Connection conn = getConn();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            // Definir parâmetros
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Transacao transacao = new Transacao();
                    transacao.setId(rs.getLong("id"));
                    
                    if (rs.getObject("conta_origem_id") != null) {
                        transacao.setContaOrigemId(rs.getLong("conta_origem_id"));
                    }
                    
                    if (rs.getObject("conta_destino_id") != null) {
                        transacao.setContaDestinoId(rs.getLong("conta_destino_id"));
                    }
                    
                    transacao.setTipo(rs.getString("tipo"));
                    transacao.setValor(rs.getBigDecimal("valor"));
                    transacao.setData(rs.getTimestamp("data"));
                    
                    transacao.setNomeContaOrigem(rs.getString("nome_origem"));
                    transacao.setNomeContaDestino(rs.getString("nome_destino"));
                    
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
            LOGGER.log(Level.SEVERE, "Erro ao buscar transações para extrato", ex);
        }
        
        return transacoes;
    }
}
