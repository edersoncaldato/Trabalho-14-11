package dva;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML
    private Button btnProdutos, btnInvetario;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnProdutos.setOnAction(event -> btnProdutosAction());
        btnInvetario.setOnAction(event -> btnInvetarioAction());
    }
    private void btnProdutosAction(){

    }
    private void btnInvetarioAction(){

    }
}