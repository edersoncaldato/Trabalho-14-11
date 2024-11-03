package dva;
import java.util.ArrayList;
import java.util.List;

public class Estoque {
    static List<Produto> estoqueAtual() {
        List<Produto> produtos = new ArrayList<>();
        produtos.add(new Produto("123", "SAPATO", 2));
        produtos.add(new Produto("234", "CHINELOS", 3));
        produtos.add(new Produto("456", "BOTA", 4));
        produtos.add(new Produto("567", "TENIS", 0));
        produtos.add(new Produto("678", "MEIA", 1));
        return produtos;
    }
    static List<Produto> estoqueNovo() {
        List<Produto> produtos = new ArrayList<>();
        produtos.add(new Produto("123", "SAPATO", 0));
        produtos.add(new Produto("234", "CHINELOS", 0));
        produtos.add(new Produto("456", "BOTA", 0));
        produtos.add(new Produto("567", "TENIS", 0));
        produtos.add(new Produto("678", "MEIA", 0));
        return produtos;
    }
}
