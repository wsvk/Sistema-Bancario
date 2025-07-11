package uff.ic.devweb.sistemabancario;

import uff.ic.devweb.sistemabancario.model.Conta;
import uff.ic.devweb.sistemabancario.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/transferencia")
public class TransferenciaServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contaId = request.getParameter("id");
        request.setAttribute("contaId", contaId);
        request.getRequestDispatcher("transferencia.jsp").forward(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        String contaOrigemStr = request.getParameter("contaOrigem");
        String contaDestinoStr = request.getParameter("contaDestino");
        String valorStr = request.getParameter("valor");

        String mensagem = null;
        if (contaOrigemStr == null || contaDestinoStr == null || valorStr == null) {
            mensagem = "Preencha todos os campos.";
        } else {
            try {
                long contaOrigemId = Long.parseLong(contaOrigemStr);
                // Aceitar formato 00001-1 ou apenas número
                String contaDestinoInput = contaDestinoStr.trim();
                Long contaDestino = null;
                if (contaDestinoInput.matches("\\d{5}-\\d")) {
                    // Formato 00001-1: extrai o número antes do traço
                    contaDestino = Long.parseLong(contaDestinoInput.split("-")[0]);
                } else if (contaDestinoInput.matches("\\d+")) {
                    contaDestino = Long.parseLong(contaDestinoInput);
                }
                if (contaDestino == null) {
                    mensagem = "Formato do número da conta destino inválido.";
                } else {
                    BigDecimal valor = new BigDecimal(valorStr);
                    if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                        mensagem = "O valor deve ser positivo.";
                    } else {
                        Connection conn = getConn();
                        // Buscar conta de origem do usuário logado
                        PreparedStatement psOrigem = conn.prepareStatement("SELECT id, saldo FROM contas WHERE id = ? AND id_usuario = ?");
                        psOrigem.setLong(1, contaOrigemId);
                        psOrigem.setLong(2, usuario.getId());
                        ResultSet rsOrigem = psOrigem.executeQuery();
                        if (!rsOrigem.next()) {
                            mensagem = "Conta de origem não encontrada ou não pertence ao usuário.";
                        } else {
                            BigDecimal saldoOrigem = rsOrigem.getBigDecimal("saldo");
                            // Buscar conta destino
                            PreparedStatement psDestino = conn.prepareStatement("SELECT saldo FROM contas WHERE id = ?");
                            psDestino.setLong(1, contaDestino);
                            ResultSet rsDestino = psDestino.executeQuery();
                            if (!rsDestino.next()) {
                                mensagem = "Conta de destino não encontrada.";
                            } else if (contaOrigemId == contaDestino) {
                                mensagem = "Não é possível transferir para a mesma conta.";
                            } else if (saldoOrigem.compareTo(valor) < 0) {
                                mensagem = "Saldo insuficiente.";
                            } else {
                                // Iniciar transação
                                try {
                                    conn.setAutoCommit(false);
                                    // Subtrair do saldo da conta de origem
                                    PreparedStatement psUpdateOrigem = conn.prepareStatement("UPDATE contas SET saldo = saldo - ? WHERE id = ?");
                                    psUpdateOrigem.setBigDecimal(1, valor);
                                    psUpdateOrigem.setLong(2, contaOrigemId);
                                    psUpdateOrigem.executeUpdate();
                                    // Adicionar ao saldo da conta destino
                                    PreparedStatement psUpdateDestino = conn.prepareStatement("UPDATE contas SET saldo = saldo + ? WHERE id = ?");
                                    psUpdateDestino.setBigDecimal(1, valor);
                                    psUpdateDestino.setLong(2, contaDestino);
                                    psUpdateDestino.executeUpdate();
                                    // Registrar transação
                                    PreparedStatement psTransacao = conn.prepareStatement(
                                        "INSERT INTO transacoes (conta_origem_id, conta_destino_id, tipo, valor) VALUES (?, ?, 'TRANSFERENCIA', ?)"
                                    );
                                    psTransacao.setLong(1, contaOrigemId);
                                    psTransacao.setLong(2, contaDestino);
                                    psTransacao.setBigDecimal(3, valor);
                                    psTransacao.executeUpdate();
                                    conn.commit();
                                    // Redirecionar para página de confirmação após sucesso
                                    response.sendRedirect("transferencia-confirmacao-page.jsp?contaOrigem=" + contaOrigemId + "&contaDestino=" + contaDestino + "&valor=" + valor);
                                    return;
                                } catch (SQLException ex) {
                                    conn.rollback();
                                    mensagem = "Erro ao realizar transferência.";
                                } finally {
                                    conn.setAutoCommit(true);
                                }
                            }
                            rsDestino.close();
                            psDestino.close();
                        }
                        rsOrigem.close();
                        psOrigem.close();
                    }
                }
            } catch (NumberFormatException e) {
                mensagem = "Dados inválidos.";
            } catch (SQLException e) {
                mensagem = "Erro de banco de dados.";
            }
        }
        request.setAttribute("mensagem", mensagem);
        request.getRequestDispatcher("transferencia.jsp").forward(request, response);
    }
}
