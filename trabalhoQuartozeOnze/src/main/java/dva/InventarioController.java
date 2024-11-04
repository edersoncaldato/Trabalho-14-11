package dva;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventarioController {

        @FXML
        private TextField txtCodigo;

        @FXML
        private ListView<String> newListViewProduto;

        @FXML
        private Button btnCodigo;

        private List<Produto> produtos = Estoque.estoqueAtual();
        private ObservableList<String> produtosExibidos = FXCollections.observableArrayList();
        private Map<String, Double> saldoAnterior = new HashMap<>();
        private Map<String, Double> contagemTemporaria = new HashMap<>();
        private int contagemAtual = 0;

        @FXML
        public void initialize() {
                for (Produto produto : produtos) {
                        produto.setSaldo(0.0);
                        saldoAnterior.put(produto.getCodBarras(), produto.getSaldo());
                }
                atualizarListView();
                configurarTextField();
        }

        private void atualizarListView() {
                produtosExibidos.clear();
                for (Produto produto : produtos) {
                        produtosExibidos.add(produto.toString());
                }
                newListViewProduto.setItems(produtosExibidos);
        }

        private void configurarTextField() {
                txtCodigo.textProperty().addListener((observable, oldValue, newValue) -> {
                        if (!newValue.matches("\\d{0,3}")) {
                                txtCodigo.setText(oldValue);
                        }
                });
                txtCodigo.setOnAction(event -> processarCodigo());
        }

        private void processarCodigo() {
                String codigo = txtCodigo.getText();
                if (codigo.length() == 3) {
                        Produto produtoEncontrado = buscarProduto(codigo);

                        if (produtoEncontrado != null) {
                                produtoEncontrado.setSaldo(produtoEncontrado.getSaldo() + 1.0);
                                atualizarListView();
                        } else {
                                exibirErro("Produto não encontrado no estoque!");
                        }

                        txtCodigo.clear();
                } else {
                        exibirErro("Por favor, insira um código de 3 dígitos.");
                }
        }

        private Produto buscarProduto(String codigo) {
                for (Produto produto : produtos) {
                        if (produto.getCodBarras().equals(codigo)) {
                                return produto;
                        }
                }
                return null;
        }

        private void exibirErro(String mensagem) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText(mensagem);
                alert.showAndWait();
        }

        @FXML
        public void finalizarInventario(ActionEvent event) {
                Map<String, Double> saldoAtual = new HashMap<>();
                for (Produto produto : produtos) {
                        saldoAtual.put(produto.getCodBarras(), produto.getSaldo());
                }

                switch (contagemAtual) {
                        case 0 -> {
                                contagemTemporaria.putAll(saldoAtual);
                                if (saldoAtual.equals(saldoAnterior)) {
                                        encerrarInventario("Inventário finalizado com sucesso!");
                                } else {
                                        exibirSucesso("Divergência encontrada. Por favor, refaça a contagem.");
                                        contagemAtual++;
                                }
                        }
                        case 1 -> {
                                if (saldoAtual.equals(saldoAnterior)) {
                                        encerrarInventario("Inventário finalizado com sucesso!");
                                } else if (saldoAtual.equals(contagemTemporaria)) {
                                        exibirDivergencias(contagemTemporaria, saldoAnterior);
                                } else {
                                        contagemTemporaria.clear();
                                        contagemTemporaria.putAll(saldoAtual);
                                        exibirSucesso("Divergência encontrada novamente. Refazendo contagem.");
                                }
                        }
                }
        }

        private void exibirDivergencias(Map<String, Double> novaContagem, Map<String, Double> saldoSistema) {
                StringBuilder divergencias = new StringBuilder("Divergências encontradas:\n");

                for (Produto produto : produtos) {
                        String codBarras = produto.getCodBarras();
                        Double saldoNova = novaContagem.get(codBarras);
                        Double saldoAntiga = saldoSistema.get(codBarras);
                        if (!saldoNova.equals(saldoAntiga)) {
                                divergencias.append(produto.getDescricao()).append(": ")
                                        .append("Nova contagem: ").append(saldoNova)
                                        .append(", Saldo esperado: ").append(saldoAntiga)
                                        .append("\n");
                        }
                }

                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Divergências");
                alert.setHeaderText(null);
                alert.setContentText(divergencias.toString());
                alert.showAndWait();
        }

        private void encerrarInventario(String mensagem) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText(null);
                alert.setContentText(mensagem);
                alert.showAndWait();

                txtCodigo.clear();
                newListViewProduto.getItems().clear();
                btnCodigo.setDisable(true);
        }

        private void exibirSucesso(String mensagem) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText(null);
                alert.setContentText(mensagem);
                alert.showAndWait();
        }
}
