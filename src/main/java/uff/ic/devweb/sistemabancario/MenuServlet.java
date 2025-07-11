package uff.ic.devweb.sistemabancario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uff.ic.devweb.sistemabancario.model.Usuario;
import uff.ic.devweb.sistemabancario.model.Conta;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import uff.ic.devweb.sistemabancario.model.Conta;
import uff.ic.devweb.sistemabancario.model.Usuario;

/**
 *
 * @author mila
 */
@WebServlet(name = "MenuServlet", urlPatterns = {"/menu"})
public class MenuServlet extends BaseServlet {
    
    private static final Logger LOGGER = Logger.getLogger(MenuServlet.class.getName());

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario usuario = usuarioAtual(request);
        if (usuario == null) {
            response.sendRedirect(".");
            return;
        }
        
        // Buscar contas do usuário
        List<Conta> contas = new ArrayList<>();
        BigDecimal saldoTotal = new BigDecimal(0);
        try {
            Connection conn = getConn();
            PreparedStatement stmt = conn.prepareStatement("SELECT id, saldo, tipo FROM contas WHERE id_usuario = ?");
            stmt.setLong(1, usuario.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Conta conta = new Conta();
                conta.setId(rs.getLong("id"));
                conta.setSaldo(rs.getBigDecimal("saldo"));
                conta.setTipo(rs.getString("tipo"));
                contas.add(conta);
                saldoTotal = saldoTotal.add(rs.getBigDecimal("saldo"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar contas do usuário", e);
        }
        
        request.setAttribute("usuario", usuario);
        request.setAttribute("contas", contas);
        request.setAttribute("saldoTotal", saldoTotal);
        
        render(request, response, "menu.jsp");
    }
    
    private List<Conta> buscarContasDoUsuario(long idUsuario) {
        List<Conta> contas = new ArrayList<>();
        
        try {
            Connection conn = getConn();
            String sql = "SELECT id, id_usuario, saldo, tipo FROM contas WHERE id_usuario = ? ORDER BY id";
            
            LOGGER.info("Executando SQL: " + sql + " para usuário ID: " + idUsuario);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, idUsuario);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Conta conta = new Conta();
                        conta.setId(rs.getLong("id"));
                        conta.setIdUsuario(rs.getLong("id_usuario"));
                        conta.setSaldo(rs.getBigDecimal("saldo"));
                        conta.setTipo(rs.getString("tipo"));
                        
                        LOGGER.info("Conta encontrada: ID=" + conta.getId() + 
                                ", Tipo=" + conta.getTipo() + 
                                ", Saldo=" + conta.getSaldo());
                        
                        contas.add(conta);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar contas do usuário", e);
        }
        
        return contas;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

