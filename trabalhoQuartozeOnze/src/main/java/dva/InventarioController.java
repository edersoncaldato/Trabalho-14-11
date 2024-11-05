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

//Define a classe
public class InventarioController {

        //Declara variáveis associadas aos componentes da interface (textfield, listview e button)
        //O @FXML indica que eles estão ligados ao arquivo .fxml
        @FXML
        private TextField txtCodigo;

        @FXML
        private ListView<String> newListViewProduto;

        @FXML
        private Button btnCodigo;

        // Carrega a lista de produtos do estoque
        private List<Produto> produtos = Estoque.estoqueAtual();
        //Lista observável de produtos que sera mpstrada na listView
        private ObservableList<String> produtosExibidos = FXCollections.observableArrayList();
        // Saldo inicial do sistema
        private Map<String, Double> saldoAnterior = new HashMap<>();
        //Primeira contagem que é guardada para as futuras comparações
        private Map<String, Double> primeiraContagem = new HashMap<>();
        //Variável para rastear o número da contagem atual
        private int contagemAtual = 0; // Rastreia a contagem atual

        //método inicialize, executado após carregar a interface
        @FXML
        public void initialize() {
                //inicializa o saldo dos produtos e atualiza a exibição
                for (Produto produto : produtos) {
                        saldoAnterior.put(produto.getCodBarras(), produto.getSaldo()); //armazena o saldo inicial
                        produto.setSaldo(0.0); //zera o saldo do produto para o inventário
                }
                atualizarListView();
                configurarTextField();
        }

        //método para limpar a lista obsevável e a preenche com os produtos atualizados, após isso, exibe esses produtos na list view
        private void atualizarListView() {
                produtosExibidos.clear();
                for (Produto produto : produtos) {
                        produtosExibidos.add(produto.toString());
                }
                newListViewProduto.setItems(produtosExibidos); //liga a ListView com os produtos a serem exibidos
        }

        //método para configurar o campo txtCodigo para aceitar apenas três digitos e executa "processarCodigo()" ao pressionar a tecla Enter
        private void configurarTextField() {
                //restringe o txtField para permitir apenas até 3 digitos
                txtCodigo.textProperty().addListener((observable, oldValue, newValue) -> {
                        if (!newValue.matches("\\d{0,3}")) {
                                txtCodigo.setText(oldValue);
                        }
                });
                //ação para quando o usuário pressiona Enter no txtField
                txtCodigo.setOnAction(event -> processarCodigo());
        }

        //método para oberto o código do campop "txtCodigo" e verificar se o código tem 3 digitos.
        //Se sim, irá buscar o produto que corresponde a ele e é acrescentado 1 em seu saldo
        //Atualiza o listView e limpa o campo txtCodigo
        private void processarCodigo() {
                String codigo = txtCodigo.getText();
                if (codigo.length() == 3) {
                        Produto produtoEncontrado = buscarProduto(codigo);

                        if (produtoEncontrado != null) {
                                //incrementa o saldo do produto e atualiza a exibição na listView
                                produtoEncontrado.setSaldo(produtoEncontrado.getSaldo() + 1.0); // Adiciona 1 ao saldo
                                atualizarListView();
                        } else {
                                exibirErro("Produto não encontrado no estoque!");
                        }

                        txtCodigo.clear(); //limpa o campo após a verificação
                } else {
                        exibirErro("Por favor, insira um código de 3 dígitos.");
                }
        }

        //Método que percorre pela lista "produtos" para encontrar o produto pelo código de 3 digitos
        //Se encontrado, retorna o produto, e se não, retorna null
        private Produto buscarProduto(String codigo) {
                for (Produto produto : produtos) {
                        if (produto.getCodBarras().equals(codigo)) {
                                return produto;
                        }
                }
                return null;
        }

        //exibe um alerta de erro
        private void exibirErro(String mensagem) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText(mensagem);
                alert.showAndWait();
        }

        //método que calcula o saldo atual de cada produto
        //Se for a primeira contagem, comprara com o saldo do sistema, caso tenha alguma diferença,os dados são armazdenados para uma futura comparação
        //Se não for a primeira contagem, chama "novaContagem()"
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
                                //se diferente, zera a lista e pede uma nova contagem
                                exibirDivergencias(saldoAtual);
                                resetarInventario();
                                primeiraContagem.putAll(saldoAtual); //armazena a primeira contagem para comparações futuras
                                exibirSucesso("Diferenças encontradas. Por favor, refaça a contagem.");
                                contagemAtual++;
                        }
                } else {
                        novaContagem(event);
                }
        }

        //método que gerencia as contagens seguintes ao comparar com o saldo do sistema, o saldo que se espera ter no estoque
        @FXML
        private void novaContagem(ActionEvent event) {
                Map<String, Double> saldoNovaContagem = new HashMap<>();
                for (Produto produto : produtos) {
                        saldoNovaContagem.put(produto.getCodBarras(), produto.getSaldo());
                }

                if (contagemAtual == 1) { //segunda contagem
                        if (compararContagensComSistema(saldoNovaContagem)) {
                                exibirSucesso("Inventário finalizado com sucesso após segunda contagem!");
                                encerrarInventario();
                        } else if (saldoNovaContagem.equals(primeiraContagem)) {
                                salvarDivergencias(saldoNovaContagem);
                        } else {
                                exibirSucesso("Diferenças encontradas novamente. Iniciando terceira contagem.");
                                resetarInventario();
                                primeiraContagem.clear(); // Limpa a primeira contagem, pois ela não é mais relevante
                                contagemAtual++;
                        }
                } else if (contagemAtual == 2) { //terceira contagem
                        if (compararContagensComSistema(saldoNovaContagem)) {
                                exibirSucesso("Inventário finalizado com sucesso após terceira contagem!");
                                encerrarInventario();
                        } else {
                                salvarDivergencias(saldoNovaContagem); //finaliza com divergência se a terceira contagem for diferente do sistema
                        }
                }
        }

        //método que compara os saldos atuais com o saldo inicial
        private boolean compararContagensComSistema(Map<String, Double> saldoAtual) {
                return saldoAtual.equals(saldoAnterior);
        }

        //método que registra as diferenças encontradas
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

                encerrarInventario(); //finaliza o inventário após salvar as divergências
        }

        //método que finaliza o inventário e reseta os dados
        private void encerrarInventario() {
                txtCodigo.clear();
                newListViewProduto.getItems().clear();
                btnCodigo.setDisable(true);
                contagemAtual = 0;
                saldoAnterior.clear();
                primeiraContagem.clear();
        }

        //método que zera os saldos dos produtos
        private void resetarInventario() {
                for (Produto produto : produtos) {
                        produto.setSaldo(0.0); //reseta o saldo dos produtos
                }
                atualizarListView(); //atualiza a listView
        }

        //exibe uma mensagem de suceeso
        private void exibirSucesso(String mensagem) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText(null);
                alert.setContentText(mensagem);
                alert.showAndWait();
        }

        //exibe a mensagem informando que há divergências
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
