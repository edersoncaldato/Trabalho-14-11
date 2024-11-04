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

        private List<Produto> produtos = Estoque.estoqueAtual(); // Carrega a lista de produtos do estoque
        private ObservableList<String> produtosExibidos = FXCollections.observableArrayList();
        private Map<String, Double> saldoAnterior = new HashMap<>(); // Saldo inicial do sistema
        private Map<String, Double> primeiraContagem = new HashMap<>();
        private int contagemAtual = 0; // Rastreia a contagem atual

        @FXML
        public void initialize() {
                // Inicializa saldo dos produtos e atualiza a exibição
                for (Produto produto : produtos) {
                        saldoAnterior.put(produto.getCodBarras(), produto.getSaldo()); // Armazena saldo inicial
                        produto.setSaldo(0.0); // Zera o saldo do produto para o inventário
                }
                atualizarListView();
                configurarTextField();
        }

        private void atualizarListView() {
                produtosExibidos.clear();
                for (Produto produto : produtos) {
                        produtosExibidos.add(produto.toString());
                }
                newListViewProduto.setItems(produtosExibidos); // Liga a ListView com os produtos a serem exibidos
        }

        private void configurarTextField() {
                // Restringe o TextField para permitir apenas até 3 números
                txtCodigo.textProperty().addListener((observable, oldValue, newValue) -> {
                        if (!newValue.matches("\\d{0,3}")) {
                                txtCodigo.setText(oldValue);
                        }
                });
                // Ação para quando o usuário pressiona Enter no TextField
                txtCodigo.setOnAction(event -> processarCodigo());
        }

        private void processarCodigo() {
                String codigo = txtCodigo.getText();
                if (codigo.length() == 3) {
                        Produto produtoEncontrado = buscarProduto(codigo);

                        if (produtoEncontrado != null) {
                                // Incrementa o saldo do produto e atualiza a exibição na ListView
                                produtoEncontrado.setSaldo(produtoEncontrado.getSaldo() + 1.0); // Adiciona 1.0 ao saldo
                                atualizarListView();
                        } else {
                                exibirErro("Produto não encontrado no estoque!");
                        }

                        txtCodigo.clear(); // Limpa o campo após a verificação
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

                if (contagemAtual == 0) {
                        if (compararContagensComSistema(saldoAtual)) {
                                exibirSucesso("Inventário finalizado com sucesso!");
                                encerrarInventario();
                        } else {
                                // Se diferente, zera a lista e pede uma nova contagem
                                exibirDivergencias(saldoAtual);
                                resetarInventario();
                                primeiraContagem.putAll(saldoAtual); // Armazena a primeira contagem para comparações futuras
                                exibirSucesso("Diferenças encontradas. Por favor, refaça a contagem.");
                                contagemAtual++;
                        }
                } else {
                        novaContagem(event);
                }
        }

        @FXML
        private void novaContagem(ActionEvent event) {
                Map<String, Double> saldoNovaContagem = new HashMap<>();
                for (Produto produto : produtos) {
                        saldoNovaContagem.put(produto.getCodBarras(), produto.getSaldo());
                }

                if (compararContagensComSistema(saldoNovaContagem)) {
                        exibirSucesso("Inventário finalizado com sucesso após segunda contagem!");
                        encerrarInventario();
                } else if (saldoNovaContagem.equals(primeiraContagem)) {
                        // Se a nova contagem é igual à primeira, salva as diferenças
                        salvarDivergencias(saldoNovaContagem);
                } else {
                        // Se diferente da primeira contagem, pede para refazer novamente
                        exibirSucesso("Diferenças encontradas novamente. Iniciando nova contagem.");
                        resetarInventario();
                        primeiraContagem.clear();
                        primeiraContagem.putAll(saldoNovaContagem);
                }
        }

        private boolean compararContagensComSistema(Map<String, Double> saldoAtual) {
                return saldoAtual.equals(saldoAnterior);
        }

        private void salvarDivergencias(Map<String, Double> saldoNovaContagem) {
                StringBuilder divergencias = new StringBuilder("Diferenças finais encontradas:\n");
                for (Produto produto : produtos) {
                        String codBarras = produto.getCodBarras();
                        if (!saldoNovaContagem.get(codBarras).equals(saldoAnterior.get(codBarras))) {
                                divergencias.append(produto.getDescricao()).append(": ")
                                        .append("Nova contagem: ").append(saldoNovaContagem.get(codBarras))
                                        .append(", Saldo sistema: ").append(saldoAnterior.get(codBarras))
                                        .append("\n");
                        }
                }

                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Divergências Finais");
                alert.setHeaderText(null);
                alert.setContentText(divergencias.toString());
                alert.showAndWait();

                encerrarInventario(); // Finaliza o inventário após salvar as divergências
        }

        private void encerrarInventario() {
                txtCodigo.clear();
                newListViewProduto.getItems().clear();
                btnCodigo.setDisable(true);
                contagemAtual = 0;
                saldoAnterior.clear();
                primeiraContagem.clear();
        }

        private void resetarInventario() {
                for (Produto produto : produtos) {
                        produto.setSaldo(0.0); // Reseta saldo
                }
                atualizarListView(); // Atualiza ListView
        }

        private void exibirSucesso(String mensagem) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText(null);
                alert.setContentText(mensagem);
                alert.showAndWait();
        }

        private void exibirDivergencias(Map<String, Double> saldoAtual) {
                StringBuilder divergencias = new StringBuilder("Divergências encontradas:\n");
                boolean encontrouDivergencia = false;

                for (Produto produto : produtos) {
                        String codBarras = produto.getCodBarras();
                        if (!saldoAtual.get(codBarras).equals(saldoAnterior.get(codBarras))) {
                                divergencias.append(produto.getDescricao()).append(": ")
                                        .append("Saldo atual: ").append(saldoAtual.get(codBarras))
                                        .append(", Saldo esperado: ").append(saldoAnterior.get(codBarras))
                                        .append("\n");
                                encontrouDivergencia = true;
                        }
                }

                if (!encontrouDivergencia) {
                        divergencias.append("Nenhuma divergência encontrada.");
                }

                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Divergências");
                alert.setHeaderText(null);
                alert.setContentText(divergencias.toString());
                alert.showAndWait();
        }
}
