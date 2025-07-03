package uff.ic.devweb.sistemabancario.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transacao {
    private long id;
    private Long contaOrigemId;
    private Long contaDestinoId;
    private String tipo;
    private BigDecimal valor;
    private Timestamp data;
    
    // Campos adicionais para exibição
    private String nomeContaOrigem;
    private String nomeContaDestino;
    private String numeroContaOrigem;
    private String numeroContaDestino;
    
    public Transacao() {
        // Construtor vazio
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getContaOrigemId() {
        return contaOrigemId;
    }

    public void setContaOrigemId(Long contaOrigemId) {
        this.contaOrigemId = contaOrigemId;
    }

    public Long getContaDestinoId() {
        return contaDestinoId;
    }

    public void setContaDestinoId(Long contaDestinoId) {
        this.contaDestinoId = contaDestinoId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Timestamp getData() {
        return data;
    }

    public void setData(Timestamp data) {
        this.data = data;
    }

    public String getNomeContaOrigem() {
        return nomeContaOrigem;
    }

    public void setNomeContaOrigem(String nomeContaOrigem) {
        this.nomeContaOrigem = nomeContaOrigem;
    }

    public String getNomeContaDestino() {
        return nomeContaDestino;
    }

    public void setNomeContaDestino(String nomeContaDestino) {
        this.nomeContaDestino = nomeContaDestino;
    }

    public String getNumeroContaOrigem() {
        return numeroContaOrigem;
    }

    public void setNumeroContaOrigem(String numeroContaOrigem) {
        this.numeroContaOrigem = numeroContaOrigem;
    }

    public String getNumeroContaDestino() {
        return numeroContaDestino;
    }

    public void setNumeroContaDestino(String numeroContaDestino) {
        this.numeroContaDestino = numeroContaDestino;
    }
    
    // Verifica se a transação é um crédito para a conta especificada
    public boolean isCredito(long contaId) {
        return (contaDestinoId != null && contaDestinoId == contaId) || 
               (tipo.equals("DEPOSITO") && contaDestinoId != null && contaDestinoId == contaId);
    }
    
    // Verifica se a transação é um débito para a conta especificada
    public boolean isDebito(long contaId) {
        return (contaOrigemId != null && contaOrigemId == contaId) && 
               (tipo.equals("TRANSFERENCIA") || tipo.equals("SAQUE"));
    }
    
    // Retorna uma descrição amigável da transação
    public String getDescricao(long contaAtualId) {
        switch (tipo) {
            case "DEPOSITO":
                return "Depósito em conta";
            case "SAQUE":
                return "Saque";
            case "TRANSFERENCIA":
                if (contaOrigemId == contaAtualId) {
                    return "Transferência enviada para " + 
                           (nomeContaDestino != null ? nomeContaDestino : "Conta " + numeroContaDestino);
                } else {
                    return "Transferência recebida de " + 
                           (nomeContaOrigem != null ? nomeContaOrigem : "Conta " + numeroContaOrigem);
                }
            default:
                return tipo;
        }
    }
}
