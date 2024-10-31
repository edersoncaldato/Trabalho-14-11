package dva;

public class Produto {
    private String codBarras;
    private String descricao;
    private double saldo;

    public Produto(String codBarras, String descricao, double saldo) {
        this.codBarras = codBarras;
        this.descricao = descricao;
        this.saldo = saldo;
    }

    public String getCodBarras() {
        return codBarras;
    }

    public void setCodBarras(String codBarras) {
        this.codBarras = codBarras;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
}
