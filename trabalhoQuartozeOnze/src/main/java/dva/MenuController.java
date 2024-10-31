package dva;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
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
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Produtos.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Produtos");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void btnInvetarioAction(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Invetario.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Invetário");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}