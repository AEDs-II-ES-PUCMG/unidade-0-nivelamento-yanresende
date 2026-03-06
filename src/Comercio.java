import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Comercio {
    /** Para inclusão de novos produtos no vetor */
    static final int MAX_NOVOS_PRODUTOS = 10;

    /**
     * Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto
     */
    static String nomeArquivoDados;

    /** Scanner para leitura do teclado */
    static Scanner teclado;

    /**
     * Vetor de produtos cadastrados. Sempre terá espaço para 10 novos produtos a
     * cada execução
     */
    static Produto[] produtosCadastrados;

    /** Quantidade produtos cadastrados atualmente no vetor */
    static int quantosProdutos;

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("AEDII COMÉRCIO DE COISINHAS");
        System.out.println("===========================");
    }

    /**
     * Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * Perceba que poderia haver uma melhor modularização com a criação de uma
     * classe Menu.
     * 
     * @return Um inteiro com a opção do usuário.
     */
    static int menu() {
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar e listar um produto");
        System.out.println("3 - Cadastrar novo produto");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        return Integer.parseInt(teclado.nextLine());
    }

    /**
     * Lê os dados de um arquivo texto e retorna um vetor de produtos. Arquivo no
     * formato
     * N (quantiade de produtos) <br/>
     * tipo; descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna um vetor vazio em
     * caso de problemas com o arquivo.
     * 
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os produtos carregados, ou vazio em caso de problemas de
     *         leitura.
     */
    static Produto[] lerProdutos(String nomeArquivoDados) {
        Produto[] vetorProdutos;
        Scanner arqDados = null;
        try {
            arqDados = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
            quantosProdutos = Integer.parseInt(arqDados.nextLine());
            vetorProdutos = new Produto[quantosProdutos + MAX_NOVOS_PRODUTOS];
            for (int i = 0; i < quantosProdutos; i++) {
                String linha = arqDados.nextLine();
                vetorProdutos[i] = Produto.criarDoTexto(linha);
            }

        } catch (IOException fne) {
            vetorProdutos = null;
        } finally {
            arqDados.close();
        }

        return vetorProdutos;
    }

    /** Lista todos os produtos cadastrados, numerados, um por linha */
    static void listarTodosOsProdutos() {
        cabecalho();
        System.out.println("\nPRODUTOS CADASTRADOS:");
        for (int i = 0; i < produtosCadastrados.length; i++) {
            if (produtosCadastrados[i] != null)
                System.out.println(String.format("%02d - %s", (i + 1), produtosCadastrados[i].toString()));
        }
    }

    /**
     * Localiza um produto no vetor de cadastrados, a partir do nome, e imprime seus
     * dados.
     * A busca não é sensível ao caso. Em caso de não encontrar o produto, imprime
     * mensagem padrão
     */
    static void localizarProdutos() {
        System.out.print("Digite o nome do produto para pesquisar: ");
        String nome = teclado.nextLine();
        boolean encontrou = false;
        for (int i = 0; i < quantosProdutos; i++) {
            if (produtosCadastrados[i] != null && produtosCadastrados[i].descricao.toLowerCase().contains(nome.toLowerCase())) {
                System.out.println("Produto encontrado: " + produtosCadastrados[i].toString());
                encontrou = true;
            }
        }
        if (!encontrou) {
            System.out.println("Produto não encontrado.");
        }
    }

    /**
     * Rotina de cadastro de um novo produto: pergunta ao usuário o tipo do produto,
     * lê os dados correspondentes,
     * cria o objeto adequado de acordo com o tipo, inclui no vetor. Este método
     * pode ser feito com um nível muito
     * melhor de modularização. As diversas fases da lógica poderiam ser
     * encapsuladas em outros métodos.
     * Uma sugestão de melhoria mais significativa poderia ser o uso de padrão
     * Factory Method para criação dos objetos.
     */
    static void cadastrarProduto() {
        if (quantosProdutos >= produtosCadastrados.length) {
            System.out.println("Capacidade de armazenamento cheia.");
            return;
        }

        System.out.println("--- Cadastro de Produto ---");
        System.out.println("1 - Produto Não Perecível");
        System.out.println("2 - Produto Perecível");
        System.out.print("Digite o tipo do produto: ");

        try {
            int tipo = Integer.parseInt(teclado.nextLine());

            System.out.print("Descrição: ");
            String descricao = teclado.nextLine();

            System.out.print("Preço de Custo: ");
            double preco = Double.parseDouble(teclado.nextLine().replace(",", "."));

            System.out.print("Margem de Lucro: ");
            double margem = Double.parseDouble(teclado.nextLine().replace(",", "."));

            Produto novoProduto;

            if (tipo == 1) {
                novoProduto = new ProdutoNaoPerecivel(descricao, preco, margem);
            } else if (tipo == 2) {
                System.out.print("Data de Validade (dd/MM/yyyy): ");
                String dataStr = teclado.nextLine();
                LocalDate validade = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                novoProduto = new ProdutoPerecivel(descricao, preco, margem, validade);
            } else {
                System.out.println("Tipo de produto inválido.");
                return;
            }

            produtosCadastrados[quantosProdutos] = novoProduto;
            quantosProdutos++;
            System.out.println("Produto cadastrado com sucesso!");

        } catch (Exception e) {
            System.out.println("Erro ao cadastrar produto: " + e.getMessage());
        }
    }

    /**
     * Salva os dados dos produtos cadastrados no arquivo csv informado. Sobrescreve
     * todo o conteúdo do arquivo.
     * 
     * @param nomeArquivo Nome do arquivo a ser gravado.
     */
    public static void salvarProdutos(String nomeArquivo) {
        try (FileWriter writer = new FileWriter(nomeArquivo, Charset.forName("UTF-8"))) {
            writer.write(quantosProdutos + "\n");
            for (int i = 0; i < quantosProdutos; i++) {
                if (produtosCadastrados[i] != null) {
                    writer.write(produtosCadastrados[i].gerarDadosTexto() + "\n");
                }
            }
            System.out.println("Dados salvos com sucesso.");
        } catch (IOException e) {
            System.out.println("Erro ao salvar os dados: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        teclado = new Scanner(System.in, Charset.forName("ISO-8859-2"));
        nomeArquivoDados = "dadosProdutos.csv";
        produtosCadastrados = lerProdutos(nomeArquivoDados);
        int opcao = -1;
        do {
            opcao = menu();
            switch (opcao) {
                case 1 -> listarTodosOsProdutos();
                case 2 -> localizarProdutos();
                case 3 -> cadastrarProduto();
            }
            pausa();
        } while (opcao != 0);

        salvarProdutos(nomeArquivoDados);
        teclado.close();
    }
}
