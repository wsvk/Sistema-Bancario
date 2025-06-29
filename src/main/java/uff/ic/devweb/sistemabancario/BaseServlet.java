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
import java.util.logging.Level;
import java.util.logging.Logger;
import uff.ic.devweb.sistemabancario.model.Usuario;

/**
 *
 * @author mila
 */
public class BaseServlet extends HttpServlet {

    private DB db;

    @Override
    public void init() throws ServletException {
        db = new DB();
    }

    @Override
    public void destroy() {
        db.destroy();
    }

    protected Connection getConn() {
        return db.get();
    }

    protected void render(HttpServletRequest request, HttpServletResponse response, String page) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        request.setAttribute("page", page);
        request.getRequestDispatcher("layout.jsp").forward(request, response);
    }

    protected Usuario usuarioAtual(HttpServletRequest request) {
        return (Usuario) request.getSession().getAttribute("usuario");
    }
}
