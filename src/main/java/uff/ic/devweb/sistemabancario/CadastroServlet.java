/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package uff.ic.devweb.sistemabancario;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.UUID;
import uff.ic.devweb.sistemabancario.model.Usuario;

/**
 *
 * @author mila
 */
@WebServlet(name = "CadastroServlet", urlPatterns = {"/cadastro"})
public class CadastroServlet extends BaseServlet {

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
        render(request, response, "cadastro.jsp");
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
        String nome, email, senha;

        nome = request.getParameter("nome");
        email = request.getParameter("email");
        senha = request.getParameter("senha");

        Connection conn = getConn();

        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO usuarios(nome, email, senha) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, senha);

            long id = 0;
            if (stmt.executeUpdate() == 0) {
                request.setAttribute("erro", "falha ao inserir no banco");
                render(request, response, "cadastro.jsp");
                return;
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    id = rs.getLong(1);
                }
            }

            // nao muito seguro, mas vai funcionar
            request.getSession().setAttribute("usuario", new Usuario(
                    id,
                    nome,
                    email,
                    "<omitido>"
            ));
            response.sendRedirect("./menu");
        } catch (SQLException ex) {
            request.setAttribute("erro", ex.getMessage());
            Logger.getLogger(CadastroServlet.class.getName()).log(Level.SEVERE, null, ex);
            render(request, response, "cadastro.jsp");
        }
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
