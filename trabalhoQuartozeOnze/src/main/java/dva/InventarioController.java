package dva;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;

import java.util.ArrayList;
import java.util.List;

public class InventarioController {

        @FXML
        private TextField txtCodigo;

        @FXML
        private ListView<String> newListViewProduto;

        @FXML
        private Button btnCodigo;

        private List<Produto> produtos = Estoque.estoqueAtual();  // Carrega os produtos do estoque
        private List<String> produtosExibidos = new ArrayList<>();
        private List<Produto> saldoAnterior = new ArrayList<>();  // Saldo inicial
        private List<Produto> primeiraContagem = new ArrayList<>();
        private int contagemAtual = 0;

        @FXML
        public void initialize() {
                for (Produto produto : produtos) {
                        saldoAnterior.add(new Produto(produto.getCodBarras(), produto.getDescricao(), produto.getSaldo()));
                        produto.setSaldo(0.0);
                }
                atualizarVisulizacaoDaLista();
                txtCodigo.textProperty().addListener((observable, valorAntigo, novoValor) -> {
                        if (!novoValor.matches("\\d*")) {
                                txtCodigo.setText(valorAntigo);
                        }
                });
                txtCodigo.setOnAction(event -> processarCodigo());
        }

        private void atualizarVisulizacaoDaLista() {
                produtosExibidos.clear();
                for (Produto produto : produtos) {
                        produtosExibidos.add(produto.toString());
                }
                // Atualiza a ListView com a nova lista de produtos
                newListViewProduto.getItems().clear();
                newListViewProduto.getItems().addAll(produtosExibidos);
        }


        private void processarCodigo() {
                String codigo = txtCodigo.getText();
                if (!codigo.isEmpty()) {
                        Produto produtoEncontrado = buscarProduto(codigo);

                        if (produtoEncontrado != null) {
                                produtoEncontrado.setSaldo(produtoEncontrado.getSaldo() + 1.0);
                                atualizarVisulizacaoDaLista();
                        } else {
                                exibirMensagem("Produto não encontrado no estoque!");
                        }

                        txtCodigo.clear();
                } else {
                        exibirMensagem("Por favor, insira um código de barras.");
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

        @FXML
        public void finalizarInventario(ActionEvent event) {
                List<Produto> saldoAtual = obterSaldoAtual();

                if (contagemAtual == 0) {
                        if (compararContagens(saldoAtual, saldoAnterior)) {
                                exibirMensagem("Inventário finalizado com sucesso!");
                                encerrarInventario();
                        } else {
                                exibirDivergencias(saldoAtual);
                                resetarInventario();
                                primeiraContagem.addAll(saldoAtual);
                                exibirMensagem("Diferenças encontradas.");
                                contagemAtual++;
                        }
                } else {
                        novaContagem(event);
                }
        }

        private List<Produto> obterSaldoAtual() {
                List<Produto> saldoAtual = new ArrayList<>();
                for (Produto produto : produtos) {
                        saldoAtual.add(new Produto(produto.getCodBarras(), produto.getDescricao(), produto.getSaldo()));
                }
                return saldoAtual;
        }

        @FXML
        private void novaContagem(ActionEvent event) {
                List<Produto> saldoNovaContagem = obterSaldoAtual();

                if (contagemAtual == 1) {
                        if (compararContagens(saldoNovaContagem, saldoAnterior)) {
                                exibirMensagem("Inventário finalizado com sucesso!");
                                encerrarInventario();
                        } else if (compararContagens(saldoNovaContagem, primeiraContagem)) {
                                salvarDivergencias(saldoNovaContagem);
                        } else {
                                exibirMensagem("Diferenças encontradas novamente.");
                                resetarInventario();
                                primeiraContagem.clear();
                                contagemAtual++;
                        }
                } else if (contagemAtual == 2) {
                        if (compararContagens(saldoNovaContagem, saldoAnterior)) {
                                exibirMensagem("Inventário finalizado com sucesso!");
                                encerrarInventario();
                        } else {
                                salvarDivergencias(saldoNovaContagem);
                        }
                }
        }

        private boolean compararContagens(List<Produto> contagem1, List<Produto> contagem2) {
                if (contagem1.size() != contagem2.size()) return false;
                for (int i = 0; i < contagem1.size(); i++) {
                        Produto p1 = contagem1.get(i);
                        Produto p2 = contagem2.get(i);
                        if (!p1.getCodBarras().equals(p2.getCodBarras()) || p1.getSaldo() != p2.getSaldo()) {
                                return false;
                        }
                }
                return true;
        }

        private void salvarDivergencias(List<Produto> saldoNovaContagem) {
                String divergencias = "Diferenças finais encontradas:\n";
                for (Produto saldoProduto : saldoNovaContagem) {
                        Produto saldoInicial = buscarSaldo(saldoProduto.getCodBarras(), saldoAnterior);
                        if (saldoInicial != null && saldoProduto.getSaldo() != saldoInicial.getSaldo()) {
                                divergencias += saldoProduto.getCodBarras() + ": " +
                                        "Nova contagem: " + saldoProduto.getSaldo() +
                                        ", Saldo sistema: " + saldoInicial.getSaldo() + "\n";
                        }
                }

                exibirMensagem(divergencias);
                encerrarInventario();
        }

        private Produto buscarSaldo(String codBarras, List<Produto> saldoLista) {
                for (Produto saldoProduto : saldoLista) {
                        if (saldoProduto.getCodBarras().equals(codBarras)) {
                                return saldoProduto;
                        }
                }
                return null;
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
                        produto.setSaldo(0.0);
                }
                atualizarVisulizacaoDaLista();
        }

        private void exibirMensagem(String mensagem) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION); // AlertType padrão
                alert.setTitle("Mensagem");
                alert.setHeaderText(null);
                alert.setContentText(mensagem);
                alert.showAndWait();
        }

        private void exibirDivergencias(List<Produto> saldoAtual) {
                String divergencias = "Divergências encontradas:\n";
                boolean encontrouDivergencia = false;

                for (Produto saldoProduto : saldoAtual) {
                        Produto saldoInicial = buscarSaldo(saldoProduto.getCodBarras(), saldoAnterior);
                        if (saldoInicial != null && saldoProduto.getSaldo() != saldoInicial.getSaldo()) {
                                divergencias += saldoProduto.getCodBarras() + ": " +
                                        "Saldo atual: " + saldoProduto.getSaldo() +
                                        ", Saldo esperado: " + saldoInicial.getSaldo() + "\n";
                                encontrouDivergencia = true;
                        }
                }

                if (!encontrouDivergencia) {
                        divergencias += "Nenhuma divergência encontrada.";
                }

                exibirMensagem(divergencias);
        }
}
