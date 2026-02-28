import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProdutoPerecivel extends Produto {
    private double DESCONTO = 0.25;
    private int PRAZO_DESCONTO = 7;
    private LocalDate dataDeValidade;

    public ProdutoPerecivel(String desc, double precoCusto, double margemLucro, LocalDate validade) {
        super(desc, precoCusto, margemLucro);
        if (validade.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data de validade inválida: anterior a data atual.");
        }
        this.dataDeValidade = validade;
    }

    public double valorVenda() {
        LocalDate hoje = LocalDate.now();
        if (dataDeValidade.isBefore(hoje)) {
            throw new IllegalArgumentException("Produto vencido não pode ser vendido.");
        }

        double valorVenda = super.valorDeVenda();

        if (!dataDeValidade.isAfter(hoje.plusDays(PRAZO_DESCONTO))) {
            valorVenda -= valorVenda * DESCONTO;
        }

        return valorVenda;
    }

    @Override
    public String toString() {
        return super.toString() + " - Data de Validade: " + dataDeValidade;
    }

    /**
     * Gera uma linha de texto a partir dos dados do produto. Preço e margem de
     * lucro vão formatados com 2 casas
     * decimais.
     * Data de validade vai no formato dd/mm/aaaa
     * 
     * @return Uma string no formato "2;
     *         descrição;preçoDeCusto;margemDeLucro;dataDeValidade"
     */
    @Override
    public String gerarDadosTexto() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String precoFormatado = String.format("%.2f", precoCusto).replace(",", ".");
        String margemFormatada = String.format("%.2f", margemLucro).replace(",", ".");
        String dataFomatada = formato.format(dataDeValidade);
        return String.format("2;%s;%s;%s;%s", descricao, precoFormatado, margemFormatada, dataFomatada);
    }

}
