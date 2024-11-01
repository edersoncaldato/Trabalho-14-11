package dva;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class ProdutosController implements Initializable {

    @FXML
    ListView listViewProdutos;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> produtos = FXCollections.observableArrayList();
        for(Produto produto : Estoque.estoqueAtual()){
            produtos.add(produto.toString());
        }
        listViewProdutos.setItems(produtos);
    }
}
