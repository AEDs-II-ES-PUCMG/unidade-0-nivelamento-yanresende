public class ProdutoNaoPerecivel extends Produto {

    public ProdutoNaoPerecivel(String descricao, double precoCusto, double margemLucro) {
        super(descricao, precoCusto, margemLucro);
    }

    public ProdutoNaoPerecivel(String descricao, double precoCusto) {
        super(descricao, precoCusto);

    }

    @Override
    public double valorDeVenda() {
        return precoCusto * (1 + margemLucro);
    }

    /**
     * Gera uma linha de texto a partir dos dados do produto. Preço e margem de
     * lucro vão formatados com 2 casas
     * decimais.
     * 
     * @return Uma string no formato "1; descrição;preçoDeCusto;margemDeLucro"
     */
    @Override
    public String gerarDadosTexto() {
        String precoFormatado = String.format("%.2f", precoCusto).replace(",", ".");
        String margemFormatada = String.format("%.2f", margemLucro).replace(",", ".");
        return String.format("1;%s;%s;%s", descricao, precoFormatado, margemFormatada);
    }

}
