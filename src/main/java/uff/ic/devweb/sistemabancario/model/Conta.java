package uff.ic.devweb.sistemabancario.model;

import java.math.BigDecimal;

public class Conta {
    private long id;
    private long idUsuario;
    private BigDecimal saldo;
    private String tipo;
    
    public Conta() {
        // Construtor vazio para criação via setters
    }
    
    public Conta(long id, long idUsuario, BigDecimal saldo, String tipo) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.saldo = saldo;
        this.tipo = tipo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    // Retorna o número formatado da conta (para exibição)
    public String getNumeroFormatado() {
        return String.format("%05d-%d", id, id % 10);
    }
    
    // Retorna o tipo formatado para exibição
    public String getTipoFormatado() {
        return tipo.equals("CORRENTE") ? "Conta Corrente" : "Conta Poupança";
    }
}
