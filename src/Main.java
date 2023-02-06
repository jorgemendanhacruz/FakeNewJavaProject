import java.util.Scanner;
import java.io.*;



public class Main {


    //numero de variaveis a escrever no ficheiro csv//
    static final int NUMERO_VARIAVEIS_APRESENTAR = 5; //1-dias 2-S 3-I 4-R 5-N//

    //Numero de letras gregas-4//
    static final int NUMERO_VARIAVEIS_PROPAG = 4; //1-beta 2-gama 3-ro 4-alfa//

    //constantes para passar por parametro para usar na mensagem de validaçao
    static final String MENSAGEM1 = "opção";
    static final String MENSAGEM2 = "valor";

    // Os "P" Significam Posição// temos as posiçoes para as letras gregas e as posiçoes do S,I,R,N no array
    static final int P_BETA = 0;
    static final int P_GAMA = 1;
    static final int P_RO = 2;
    static final int P_ALFA = 3;
    static final int P_S = 0;
    static final int P_I = 1;
    static final int P_R = 2;
    static final int P_N = 3;

    //posições argumentos//
    static final int P_EulerOuKutta = 2;
    static final int P_PASSO = 4;
    static final int P_POPULACAO = 6;
    static final int P_DIAS = 8;


    //quantidade variaveis SIR
    static final int NUMERO_VAR_SIR = 3;

    //constante para escolher nome do ficheiro, modo nao interativo

    //valores iniciais modelo SIR
    static final int INFETADOS_INICIAL = 1;
    static final int RECUPERADOS_INICIAL = 0;

    //ficheiros cujo nome se mantem
    static final String FILE_IN = "parametros.csv";
    static final String COMP_LADO = "Comparar_ladoalado.png"; //Compara os gráficos das duas primeiras origens lado a lado
    static final String COMP_SOBRE = "Comparar_sobrepostos.png"; //Compara os gráficos das duas primeiras origens sobrepostos


    //scanners ler ficheiros e ler do teclado
    static Scanner ler = new Scanner(System.in);
    static Runtime rt = Runtime.getRuntime();


    public static void main(String[] args) throws IOException, InterruptedException {
        switch (args.length) {
            case 0:
                //caso não haja parametros o programa é executado no modo interativo
                UtilizarModoInterativo(0);

                break;
            case 9:
                //caso sejam inseridos 9 parametros o programa é executado no modo não interativo
                UtilizarModoNaoInterativo(args);

                break;
            default:
                //então caso o utilizador se engane nos argumentos o programa corre em modo interativo para não estourar//
                UtilizarModoInterativo(1);
        }
    }


    //chama o modo iterativo 
    public static void UtilizarModoInterativo(int errArgs) throws IOException, InterruptedException {
        boolean erro,flag;
        String[][] FicheirosCsv;
        String filecompare1, filecompare2, graph;
        LimparEcra();

        // como é no terminal não da para usar o do/while
        //então caso o utilizador se engane nos argumentos o programa corre em modo interativo para não estourar//
        if(errArgs==1){
            System.out.println("Argumentos inválidos, o programa irá correr em modo interativo");
            System.out.println();
        }


        erro = false;
        MostrarTituloMenu("Menu Principal");
        System.out.println("Escolha uma opção: ");
        System.out.println("[1] - Adicionar tabela/gráfico");
        System.out.println("[2] - Comparar tabelas existentes");
        System.out.println("[3] - Sair");
        do {
            int opcao = Validar(MENSAGEM1);
            ler.nextLine();
            switch (opcao) {
                case 1:
                    LimparEcra();
                    MostrarTituloMenu("Adicionar tabela/gráfico");
                    String[] nomes = GuardarNomeFicheiro(FILE_IN); //mostra as linhas do ficheiro de origem e passa as mesmas para um array
                    String nome = EscolherOrigem(nomes); //recebe o nome da linha escolhida
                    String[] arrayParametros = GuardarParametros(FILE_IN, nome); //coloca os dados da linha num array
                    TrocarVirgulasPorPontos(arrayParametros);

                    //preenchimento das variaveis com os valores do array
                    double[] arrayLetrasG = new double[NUMERO_VARIAVEIS_APRESENTAR]; //letras G de gregas//
                    PreencherArrayG(arrayParametros, arrayLetrasG);

                    //perguntas ao utilizador sobre população,dias e passo
                    int populacao = VerificarPopulacao();
                    int dias = VerificarDias();
                    double passo = VerificarPasso();

                    //metodo para selecionar os metodos de euler ou rugge kutta
                    EscolherUmMetodo(arrayLetrasG[P_BETA], arrayLetrasG[P_GAMA], arrayLetrasG[P_RO], arrayLetrasG[P_ALFA], populacao, dias, passo);
                    break;

                case 2:
                    FicheirosCsv = GuardarFicheirosExistentes(ContarFicheirosExistentes()); //recebe um array com os ficheiros csv criados pelo utilizador e mostra os mesmo no ecrã
                    String plotName;

                    //Verifica se existem ficheiros suficientes para comparar
                    if (FicheirosCsv.length >= 2) {
                        //caso haja dois ficheiros criados pelo utilizador faz as seguintes instruções
                        filecompare1 = CompararFicheiros(FicheirosCsv, 1); //pergunta qual é o primeiro ficheiro
                        filecompare2 = CompararFicheiros(FicheirosCsv, 2);//pergunta qual é o segundo ficheiro

                        //pergunta ao utilizador como quer a comparação dos gráficos

                            System.out.println("Pretende obter a comparação com os gráficos como?");
                            System.out.println("1 - lado a lado");
                            System.out.println("2 - sobrepostos");

                        //validação da opção    
                        do {
                            flag=false;
                            opcao = Validar(MENSAGEM1);
                            if(opcao < 0 || opcao > 2){
                                flag=true;
                                System.out.println("Opção inválida");

                            }
                            System.out.println();

                        } while (flag);

                        plotName=ValidarCriacaoFile("png");//pergunta o nome que o utilizador quer dar ao ficheiro de saida e valida o mesmo
                        graph = GnuplotCompare(filecompare1, filecompare2, opcao, plotName);//cria um png com os dois gráficos, tendo em conta a forma escolhida e retorna o nome do ficheiro criado
                        GuardarFicheiro(graph); //pergunta se quer guardar o ficheiroc criado

                        UtilizarModoInterativo(0);//retorna para o modo interativo

                    } else {
                        //caso não existam dois ficheiros criados pelo o utilizador não deixa comparar e mostra a seguinte mensagem
                        System.out.println("Não existem pelo menos dois ficheiros para comparar");
                        System.out.println();
                        System.out.println("(prima o enter para continuar)");

                        ler.nextLine();
                        UtilizarModoInterativo(0);
                    }
                    break;

                case 3:
                    erro = false;
                    break;

                default:
                    System.out.println("opção inválida");
                    erro = true;
                    break;
            }
        } while (erro);

    }

    private static void PreencherArrayG(String[] array, double[] arrayDouble) {

        for (int i = 1; i < array.length; i++) {
            arrayDouble[i - 1] = Double.parseDouble(array[i]);
        }
    }

    //Mostra ao utilizador um menu para o utilizador escolher um metodo e com as variaveis passadas por parametro executa esse metodo
    public static void EscolherUmMetodo(double beta, double gama, double ro, double alfa, int populacao, int dias, double passo) throws IOException, InterruptedException {

        boolean erro;

        LimparEcra();
        MostrarTituloMenu("Selecione o método");
        System.out.println("Escolha uma opção:");
        System.out.println("[1] - Obter os resultados com o método de Euler");
        System.out.println("[2] - Obter os resultados com o método de Runge-Kutta");
        System.out.println("[3] - Voltar para trás");
        System.out.println("[4] - Sair");
        do {
            int opcao = Validar("opcao");
            ler.nextLine();
            switch (opcao) {
                case 1:
                    LimparEcra();
                    SelecionarOpcaoEuler(beta, gama, ro, alfa, populacao, dias, passo);
                    erro = false;
                    break;
                case 2:
                    LimparEcra();
                    SelecionarOpcaoRungeKutta(beta, gama, ro, alfa, populacao, dias, passo);
                    erro = false;
                    break;
                case 3:
                    UtilizarModoInterativo(0);
                    erro = false;
                    break;
                case 4:
                    erro = false;
                    break;
                default:
                    System.out.println("opção inválida");
                    erro = true;
                    break;
            }

        } while (erro);


    }

    //chama a função para criar a tabela do RungeKutta
    private static void SelecionarOpcaoRungeKutta(double beta, double gama, double ro, double alfa, int populacao, int dias, double passo) throws IOException, InterruptedException {
        //dias = valorDeDiasParaMetodos(dias);
        double[][] arrayS_I_T = new double[(int) (dias / passo) + 1][3];
        PreencherComParametros(arrayS_I_T, populacao - 1, INFETADOS_INICIAL, RECUPERADOS_INICIAL);
        CalcularPorRungeKutta(arrayS_I_T, beta, gama, ro, alfa, passo, dias);
        double[][] arrayColocarNoFicheiro = VerificarValoresParaDias(arrayS_I_T, passo, dias);
        MostrarTituloMenu("Método Runge Kutta");
        VizualizarMenu(arrayColocarNoFicheiro, beta, gama, ro, alfa, populacao, dias, passo);
    }

    //chama a função para criar a tabela do Euler
    public static void SelecionarOpcaoEuler(double beta, double gama, double ro, double alfa, int populacao, int dias, double passo) throws IOException, InterruptedException {
        //dias = valorDeDiasParaMetodos(dias);
        double[][] arrayS_I_T = new double[(int) (dias / passo) + 1][3];
        PreencherComParametros(arrayS_I_T, populacao - 1, INFETADOS_INICIAL, RECUPERADOS_INICIAL);
        CalcularPorEuler(arrayS_I_T, beta, gama, ro, alfa, passo, dias);
        double[][] arrayColocarNoFicheiro = VerificarValoresParaDias(arrayS_I_T, passo, dias);
        MostrarTituloMenu("Método Euler");
        VizualizarMenu(arrayColocarNoFicheiro, beta, gama, ro, alfa, populacao, dias, passo);
    }

    //cria o menu para criar as tabelas e gráficos para os metodos
    public static void VizualizarMenu(double[][] arrayColocarNoFicheiro, double beta, double gama, double ro, double alfa, int populacao, int dias, double passo) throws IOException, InterruptedException {
        boolean erro;
        String file, graph;
        do {
            System.out.println("Escolha uma opção:");
            System.out.println("[1] - Gerar tabela");
            System.out.println("[2] - Gerar tabela e gráfico");
            System.out.println("[3] - Voltar atrás");
            System.out.println("[4] - Sair");
            int opcao = Validar(MENSAGEM1);


            switch (opcao) {

                case 1:
                    System.out.println();
                    file = ValidarCriacaoFile("csv");
                    EscreverNoFicheiro(arrayColocarNoFicheiro, file);
                    GuardarFicheiro(file);

                    erro = false;
                    UtilizarModoInterativo(0);
                    break;

                case 2:
                    System.out.println();

                    file = ValidarCriacaoFile("csv");
                    EscreverNoFicheiro(arrayColocarNoFicheiro, file);
                    graph = Gnuplot(file);
                    GuardarFicheiro(file);
                    GuardarFicheiro(graph);

                    erro = false;
                    UtilizarModoInterativo(0);
                    break;

                case 3:
                    EscolherUmMetodo(beta, gama, ro, alfa, populacao, dias, passo);
                    erro = false;
                    break;
                case 4:
                    erro = false;
                    break;
                default:
                    System.out.println("opção inválida");
                    erro = true;
                    break;
            }

        } while (erro);
    }

    //pergunta o passo
    private static double VerificarPasso() {
        double passo;
        boolean erro;
        int passarPassoMaiorQueUm=100000;
        do {
            erro = false;
            System.out.println("Qual o passo para o qual quer gerar os dados");
            passo = ler.nextDouble();
            if (passo <= 0 || passo > 1 || passarPassoMaiorQueUm % (passo * passarPassoMaiorQueUm) != 0) {  /*criei a variavel passar maior que um porque como o passo é double a divisao nunca dá inteiro
                                                                                                            exceto se multiplicarmos até ser maior que um, utilizei um valor alto que à partida chegará*/
                System.out.println("Valor inválido, tente novamente");
                erro = true;
            }

        } while (erro);

        System.out.println();
        return passo;
    }

    //pergunta a dimensão da população
    private static int VerificarPopulacao() {
        int populacao;
        boolean erro;
        do {
            erro = false;
            System.out.println("Qual o tamanho da população para o qual quer gerar os dados");
            populacao = Validar(MENSAGEM2);
            if (populacao <= 0) {
                erro = true;
                System.out.println("Tamanho da população errada, tente novamente");
            }

        } while (erro);

        System.out.println();

        return populacao;
    }

    //pergunta a dimensão dos dias
    private static int VerificarDias() {
        int dias;
        boolean erro;
        do {
            erro = false;
            System.out.println("Qual o numero de dias para o qual quer gerar os dados");
            dias = Validar(MENSAGEM2);
            if (dias <= 0) {
                erro = true;
                System.out.println("Número de dias errado, tente novamente");
            }

        } while (erro);

        System.out.println();

        return dias;
    }

    //Mostra as linhas do do ficheiro de origem(Dina/Ruca) e devolve um array com esses dados
    private static String[] GuardarNomeFicheiro(String file) throws FileNotFoundException {
        System.out.println("Digite o número do nome que deseja escolher");
        Scanner in = new Scanner(new File(file));
        String[] array_valores;
        String[] nomes = new String[15];
        int i = 0;

        while (in.hasNext()) {
            array_valores = in.nextLine().split(";");
            if (array_valores[0] != null) {
                nomes[i] = array_valores[0];
                i++;

            }
        }

        return nomes;
    }

    //retorna um array com o nome da linha do ficheiro de origem escolhido pelo utilizador
    public static String EscolherOrigem(String[] nomes) {
        boolean erro;
        int i = 1;
        int opcao;
        while (nomes[i] != null) {

            System.out.println(i + "-" + nomes[i]);
            i++;
        }
        do {
            erro = false;
            opcao = Validar(MENSAGEM1);
            ler.nextLine();
            if (opcao <= 0 || opcao >= i) {
                erro = true;
                System.out.println("Valor inválido tente novamente");
            }
        } while (erro);
        System.out.println();

        return nomes[opcao];
    }

    //chama o modo não interativo
    public static void UtilizarModoNaoInterativo(String[] args) throws IOException, InterruptedException {

        Scanner in = new Scanner(new File(args[0]));
        String[] array_valores;
        String nome="",file="",file1="",file2="";
        int j=0;
        in.nextLine();
        while (in.hasNext()) {
            array_valores = in.nextLine().split(";");
            nome = array_valores[0];
            String[] arrayParametros = GuardarParametros(args[0], nome);
            TrocarVirgulasPorPontos(arrayParametros);
            double passo = Double.parseDouble(args[P_PASSO]);
            int tamanhoPopulacao = Integer.parseInt(args[P_POPULACAO]);
            int dias = Integer.parseInt(args[P_DIAS]);           //passar para um array??//
            double[] arrayParamPropag = new double[NUMERO_VARIAVEIS_PROPAG];
            PreencherArrayPropag(arrayParametros, arrayParamPropag);
            double[][] arrayS_I_T = new double[(int) (dias / passo) + 1][NUMERO_VAR_SIR]; //30 -> numero dias // 3 -> S I T
            int populacaoInicial = tamanhoPopulacao - 1;
            PreencherComParametros(arrayS_I_T, populacaoInicial, INFETADOS_INICIAL, RECUPERADOS_INICIAL);


            //passar o passo para string e remover o ponto para assim poder adicionar ao titulo
            String passoS= String.valueOf(passo);
            passoS=passoS.replace(".", "");

            int opcao = Integer.parseInt(args[P_EulerOuKutta]); //opcao( 1-Euler ou 2-Kutta)
            switch (opcao) {
                case 1:

                    CalcularPorEuler(arrayS_I_T, arrayParamPropag[P_BETA], arrayParamPropag[P_GAMA], arrayParamPropag[P_RO], arrayParamPropag[P_ALFA], passo, dias);
                    file=GerarResultadosModoNaoInterativo(nome,arrayS_I_T,args,dias,passo,tamanhoPopulacao);
                    Gnuplot(file);
                    break;

                case 2:

                    CalcularPorRungeKutta(arrayS_I_T, arrayParamPropag[P_BETA], arrayParamPropag[P_GAMA], arrayParamPropag[P_RO], arrayParamPropag[P_ALFA], passo, dias);
                    file=GerarResultadosModoNaoInterativo(nome,arrayS_I_T,args,dias,passo,tamanhoPopulacao);
                    Gnuplot(file);
                    break;
            }

            if(j==0){
                file1=file;
            }else if(j==1){
                file2=file;
            }

            j++;
        }

        if(j>=2){
            GnuplotCompare(file1,file2,1,COMP_LADO);
            GnuplotCompare(file1,file2,2,COMP_SOBRE);

        }



    }


    public static String GerarResultadosModoNaoInterativo(String nome,double [][] matriz,String[] args, int dias,double passo,int populacao) throws IOException, InterruptedException {
        String file;
        double[][] arrayColocarNoFicheiro = VerificarValoresParaDias(matriz, passo, dias);
        file = nome + "m" + args[2] + "p" + passo + "t" + populacao + "d" + dias + ".csv";
        EscreverNoFicheiro(arrayColocarNoFicheiro, file);
        return file;

    }


    private static double[] PreencherArrayPropag(String[] arrayParametros, double[] arrayParametrosDouble) {

        arrayParametrosDouble[P_BETA] = Double.parseDouble(arrayParametros[P_BETA + 1]);
        arrayParametrosDouble[P_GAMA] = Double.parseDouble(arrayParametros[P_GAMA + 1]);
        arrayParametrosDouble[P_RO] = Double.parseDouble(arrayParametros[P_RO + 1]);
        arrayParametrosDouble[P_ALFA] = Double.parseDouble(arrayParametros[P_ALFA + 1]);

        return arrayParametrosDouble;
    }


    public static void PreencherComParametros(double[][] arrayS_i_t, int suscetiveis, int infetados,
                                              int recuperados) {
        arrayS_i_t[0][P_S] = suscetiveis;
        arrayS_i_t[0][P_I] = infetados;
        arrayS_i_t[0][P_R] = recuperados;

    }

    //guardar parametros do ficheiro de origem tendo em conta o nome da linha escolhida//
    public static String[] GuardarParametros(String file, String nome) throws FileNotFoundException {
        Scanner in = new Scanner(new File(file));
        boolean flag = false;
        in.nextLine();
        String[] array;

        do {
            array = in.nextLine().split(";");
            if (array[0].equals(nome)) {
                flag = true;
            }
        } while (in.hasNext() && !flag);

        in.close();

        return array;
    }

    //troca as virgulas do array por pontos
    public static void TrocarVirgulasPorPontos(String[] array) {
        for (int i = 0; i < NUMERO_VARIAVEIS_APRESENTAR; i++) {
            array[i] = array[i].replaceAll(",", ".");
        }
    }

    //da return do resultado dos suscetiveis
    public static double CalcularfuncS(double S, double I, double beta) {
        // −β.S.I //
        return (-beta * S * I);
    }

    //da return do resultado dos infetados
    public static double CalcularfuncI(double S, double I, double R, double beta, double gama, double ro, double alfa) {
        //ρ.β.S.I − γ.I + α.R//
        return (ro * beta * S * I - gama * I + alfa * R);
    }

    //da return do resultado dos recuperados
    public static double CalcularfuncR(double S, double I, double R, double beta, double gama, double ro, double alfa) {
        //γ.I − α.R + (1 − ρ).β.S.I//
        return (gama * I - alfa * R + (1 - ro) * beta * S * I);
    }

    //Método de Runge-Kutta de 4ª ordem
    public static void CalcularPorRungeKutta(double[][] arrayS_I_T, double beta, double gama, double ro, double alfa, double passo, int numeroDias) {

        double[][] arrayK1 = new double[(int) (numeroDias/passo)][NUMERO_VAR_SIR];
        double[][] arrayK2 = new double[(int) (numeroDias/passo)][NUMERO_VAR_SIR];
        double[][] arrayK3 = new double[(int) (numeroDias/passo)][NUMERO_VAR_SIR];
        double[][] arrayK4 = new double[(int) (numeroDias/passo)][NUMERO_VAR_SIR];
        double[][] arrayK = new double[(int) (numeroDias/passo)][NUMERO_VAR_SIR];


        double[] arrayS = new double[(int) (numeroDias/passo)];
        double[] arrayI = new double[(int) (numeroDias/passo)];
        double[] arrayR = new double[(int) (numeroDias/passo)];


        arrayS[0] = arrayS_I_T[0][P_S];
        arrayI[0] = INFETADOS_INICIAL;
        arrayR[0] = RECUPERADOS_INICIAL;

        for (int i = 0; i < (numeroDias)/passo-1; i++) {
//K1
            arrayK1[i][0] = CalcularfuncS(arrayS[i], arrayI[i], beta) * passo;
            arrayK1[i][1] = CalcularfuncI(arrayS[i], arrayI[i], arrayR[i], beta, gama, ro, alfa) * passo;
            arrayK1[i][2] = CalcularfuncR(arrayS[i], arrayI[i], arrayR[i], beta, gama, ro, alfa) * passo;

//K2
            arrayK2[i][0] = CalcularfuncS((arrayS[i] + arrayK1[i][0] / 2), (arrayI[i] + arrayK1[i][1] / 2), beta) * passo;
            arrayK2[i][1] = CalcularfuncI((arrayS[i] + arrayK1[i][0] / 2), (arrayI[i] + arrayK1[i][1] / 2), (arrayR[i] + arrayK1[i][2] / 2), beta, gama, ro, alfa) * passo;
            arrayK2[i][2] = CalcularfuncR((arrayS[i] + arrayK1[i][0] / 2), (arrayI[i] + arrayK1[i][1] / 2), (arrayR[i] + arrayK1[i][2] / 2), beta, gama, ro, alfa) * passo;

//K3
            arrayK3[i][0] = CalcularfuncS((arrayS[i] + arrayK2[i][0] / 2), (arrayI[i] + arrayK2[i][1] / 2), beta) * passo;
            arrayK3[i][1] = CalcularfuncI((arrayS[i] + arrayK2[i][0] / 2), (arrayI[i] + arrayK2[i][1] / 2), (arrayR[i] + arrayK2[i][2] / 2), beta, gama, ro, alfa) * passo;
            arrayK3[i][2] = CalcularfuncR((arrayS[i] + arrayK2[i][0] / 2), (arrayI[i] + arrayK2[i][1] / 2), (arrayR[i] + arrayK2[i][2] / 2), beta, gama, ro, alfa) * passo;

//K4
            arrayK4[i][0] = CalcularfuncS((arrayS[i] + arrayK3[i][0]), (arrayI[i] + arrayK3[i][1]), beta) * passo;
            arrayK4[i][1] = CalcularfuncI((arrayS[i] + arrayK3[i][0]), (arrayI[i] + arrayK3[i][1]), (arrayR[i] + arrayK3[i][2]), beta, gama, ro, alfa) * passo;
            arrayK4[i][2] = CalcularfuncR((arrayS[i] + arrayK3[i][0]), (arrayI[i] + arrayK3[i][1]), (arrayR[i] + arrayK3[i][2]), beta, gama, ro, alfa) * passo;

//K
            arrayK[i][0] = CalcularK(arrayK1[i][0], arrayK2[i][0], arrayK3[i][0], arrayK4[i][0]);
            arrayS[i + 1] = arrayS[i] + arrayK[i][0];
            arrayK[i][1] = CalcularK(arrayK1[i][1], arrayK2[i][1], arrayK3[i][1], arrayK4[i][1]);
            arrayI[i + 1] = arrayI[i] + arrayK[i][1];
            arrayK[i][2] = CalcularK(arrayK1[i][2], arrayK2[i][2], arrayK3[i][2], arrayK4[i][2]);
            arrayR[i + 1] = arrayR[i] + arrayK[i][2];


            arrayS_I_T[i+1][P_S] = arrayS[i + 1];
            arrayS_I_T[i+1][P_I] = arrayI[i + 1];
            arrayS_I_T[i+1][P_R] = arrayR[i + 1];

        }

    }

    public static double CalcularK(double K1, double K2, double K3, double K4) {
        //(K1+2K2+2K3+K4)/6
        return (K1 + 2 * K2 + 2 * K3 + K4) / 6;
    }

    //metodo de Euler //
    public static void CalcularPorEuler(double[][] arrayS_I_T, double beta, double gama, double ro, double alfa, double passo, int numeroDias) {

        int i = 0;
        double valorS, valor_S_seguinte;
        double valorI, valor_I_seguinte;
        double valorR, valor_R_seguinte;

        do {
            valorS = arrayS_I_T[i][P_S];
            valorI = arrayS_I_T[i][P_I];
            valorR = arrayS_I_T[i][P_R];

            valor_S_seguinte = valorS + passo * CalcularfuncS(valorS, valorI, beta);
            valor_I_seguinte = valorI + passo * CalcularfuncI(valorS, valorI, valorR, beta, gama, ro, alfa);
            valor_R_seguinte = valorR + passo * CalcularfuncR(valorS, valorI, valorR, beta, gama, ro, alfa);


            arrayS_I_T[i + 1][P_S] = valor_S_seguinte;
            arrayS_I_T[i + 1][P_I] = valor_I_seguinte;
            arrayS_I_T[i + 1][P_R] = valor_R_seguinte;
            i++;

        } while (i < numeroDias / passo);

    }

    // perguntar se o passo tem de ser divisor de 1//
    public static double[][] VerificarValoresParaDias(double[][] arrayS_I_T, double passo, int dias) {

        double[][] array = new double[dias][4];    //1coluna--dias  2coluna--valoresS 3coluna--valoresI 4coluna--valoresR//
        int intervalos = (int) (1 / passo);
        int intervalos2;
        for (int i = 0; i < dias; i++) {
            intervalos2 = intervalos * i;
            array[i][P_S] = arrayS_I_T[intervalos2][0];
            array[i][P_I] = arrayS_I_T[intervalos2][1];
            array[i][P_R] = arrayS_I_T[intervalos2][2];
            array[i][P_N] = arrayS_I_T[intervalos2][0] + arrayS_I_T[intervalos2][1] + arrayS_I_T[intervalos2][2];
        }
        return array;
    }

    //Escreve no ficheiro csv os valores do metodo
    public static void EscreverNoFicheiro(double[][] arrayColocarNoFicheiro, String file) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(file);
        for (int i = 0; i < arrayColocarNoFicheiro.length; i++) {
            printWriter.print(i + ";");
            for (int j = 0; j < arrayColocarNoFicheiro[i].length; j++) {
                printWriter.print(arrayColocarNoFicheiro[i][j]);
                if (j != arrayColocarNoFicheiro[i].length - 1)
                    printWriter.print(";");
            }
            printWriter.println();
        }

        printWriter.close();
    }


    //recebe o nome do ficheiro por parametro e cria um gráfico para o mesmo
    public static String Gnuplot(String filename) throws IOException, InterruptedException {

        // nome do ficheiro auxiliar que para a criação do gráfico
        PrintWriter out = new PrintWriter("" + filename + ".gp");

        //titulo do ficheiro que o gnuplot vai gerar
        String titulo = filename.replaceAll("_", " ");

        // texto a ser inserido no ficheiro auxiliar para poder criar o gráfico, tendo em conta as definições impostas por nos (como guardar em png, titulo, representação das linhas, separador de coluna)
        out.println("reset");
        out.println("set terminal pngcairo  size 1024,768");
        out.println("set output '" + filename + ".png'");
        out.println("set title " + "\"" + titulo + "\"");
        out.println("set xlabel " + "\"Dias\"");
        out.println("set ylabel " + "\"Pessoas\"");
        out.println("set datafile separator ';'");
        out.println("plot " + "\"" + filename + "\" u 1:2 w l t \"suscetiveis\", \"\" u 1:3 w l t \"infetados\", \"\" u 1:4 w l t \"recuperados\"");
        out.close();

        // Comando para chamar o terminal e executar o gnuplot tendo em conta o ficheiro auxiliar
        Process gnuplot = rt.exec("gnuplot -p " + filename + ".gp"); 

        gnuplot.waitFor(); //Só avança após o gnuplot ter sido executado

        //apagar ficheiro auxiliar
        File auxfile = new File(filename + ".gp");
        auxfile.delete();

        filename = filename + ".png";
        return filename; //return do nome do ficheiro criado
    }

    //Recebe os dois ficheiros a comparar e a forma (lado a lado || sobreposto) e cria um png conforme a forma escolhida pelo o utilizador.
    public static String GnuplotCompare(String file1, String file2, int forma,String name) throws IOException, InterruptedException {

        String filename = name; //nome de saida

        // nome do ficheiro auxiliar que para a criação do gráfico
        PrintWriter out = new PrintWriter( filename + ".gp");
        
        //titulo do ficheiro que o gnuplot vai gerar
        String titulo = filename.replaceAll("_", " ");
        titulo = titulo.replaceAll(".png", " ");

        // texto a ser inserido no ficheiro auxiliar para poder criar o gráfico, tendo em conta as definições impostas por nos (como guardar em jpeg, titulo, representação das linhas, separador de coluna)
        if (forma == 1) { //Caso seja 1, cria um ficheiro png com os gráficos lado a lado
            out.println("reset");
            out.println("set terminal pngcairo size 1024,768");
            out.println("set output '" + filename);
            out.println("set datafile separator ';'");
            out.println("set multiplot layout 1, 2 ;");
            out.println("set title " + "\"" + file1.replaceAll("_", " ") + "\"");
            out.println("set xlabel " + "\"Dias\"");
            out.println("set ylabel " + "\"Pessoas\"");
            out.println("plot " + "\"" + file1 + "\" u 1:2 w l t \"suscetiveis\", \"\" u 1:3 w l t \"infetados\", \"\" u 1:4 w l t \"recuperados\"");
            out.println("set title " + "\"" + file2.replaceAll("_", " ") + "\"");
            out.println("set xlabel " + "\"Dias\"");
            out.println("set ylabel " + "\"Pessoas\"");
            out.println("plot " + "\"" + file2 + "\" u 1:2 w l t \"suscetiveis\", \"\" u 1:3 w l t \"infetados\", \"\" u 1:4 w l t \"recuperados\"");
            out.println("unset multiplot");
            out.close();
        } else { //Caso seja 2,cria um ficheiro png com os gráficos sobrepostos, um com as linhas a tracejado e o outro não 
            out.println("reset");
            out.println("set terminal pngcairo size 1024,768");
            out.println("set output '" + filename);
            out.println("set title " + "\"" + titulo + "\"");
            out.println("set xlabel " + "\"Dias\"");
            out.println("set ylabel " + "\"Pessoas\"");
            out.println("set datafile separator ';'");
            out.println("plot " + "\"" + file1 + "\" u 1:2 w l t \"suscetiveis(" + file1 + ")\", \"\" u 1:3 w l t \"infetados(" + file1 + ")\", \"\" u 1:4 w l t \"recuperados(" + file1 + ")\", \"" + file2 + "\" u 1:2 w l dt 2 lt 7 lw 2 t \"suscetiveis(" + file2 + ")\", \"\" u 1:3 w l dt 2 lt 4 lw 2 t \"infetados(" + file2 + ")\", \"\" u 1:4 w l lt 2 lw 2 dt 2 t \"recuperados(" + file2 + ")\"");
            out.close();

        }

        // Comando para chamar o terminal e executar o gnuplot tendo em conta o ficheiro auxiliar
        Process gnuplot = rt.exec("gnuplot -p " + filename + ".gp"); //execução do gnuplot com as instruções
        gnuplot.waitFor(); //Só avança após o gnuplot ter sido executado
        //apagar ficheiro auxiliar
        File auxfile = new File(filename + ".gp");
        auxfile.delete();

        return filename; // return do ficheiro criado
    }

    //Pergunta qual é o nome que o utilizador quer dar ao ficheiro que vai criar e verifica se o nome é vazio ou já existe e caso o nome seja valido retorna o mesmo
    public static String ValidarCriacaoFile(String extensao) throws FileNotFoundException {
       //guarda o nome dos ficheiros e diretorios que estao na pasta do programa e coloca-os num array
        File folder = new File("./"); 
        File[] listOfFiles = folder.listFiles();
        
        boolean flag;
        String FicheiroName, ficheiro, filename, fileaux;
        ler.nextLine();

        //Validação do nome inserido pelo utilizador
        do {
            flag = false;

            System.out.println("Qual o nome que quer dar ao ficheiro que irá guardar os valores"); //pergunta o nome do ficheiro ao utilizador
            ficheiro = ler.nextLine();

            //retira os espaços inseridos no nome que o utilizador inseriu e troca por "_", depois insere a extensão que foi passada por parametro
            FicheiroName = ficheiro.replace(' ', '_');
            fileaux = FicheiroName + "." + extensao;

            // Verifica se já existe um ficheiro com o nome que foi inserido pelo utilizador
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    filename = listOfFiles[i].getName();
                    if (fileaux.equals(filename)) {
                        System.out.println("ficheiro já existente");
                        flag = true;
                        break;
                    }
                }
            }

            // verifica se o utilizador inseriu um nome vazio
            if (FicheiroName.isBlank()) {
                System.out.println("ficheiro não pode ter um nome vazio");
                flag = true;
            }
        } while (flag);

        FicheiroName = (FicheiroName + "." + extensao);
        return FicheiroName; //retorna o nome introduzido pelo utilizador caso seja válido
    }

    //Pergunta ao utilizador se pretende guardar o ficheiro csv e png, caso não queira apaga o mesmo
    public static void GuardarFicheiro(String file) throws IOException {
        boolean erro;
        File fileVer = new File(file);
        LimparEcra();
        MostrarTituloMenu("Guardar");
        do {
            int opcao;
            System.out.println("Pretende Guardar o Ficheiro " + file + "?");
            System.out.println("[1] - Sim");
            System.out.println("[2] - Não");
            System.out.print("opção: ");
            opcao = ler.nextInt();
            switch (opcao) {
                case 1:

                    System.out.println("Guardado com sucesso");
                    System.out.println();
                    System.out.println("(prima o enter para continuar)");

                    ler.nextLine();
                    ler.nextLine();

                    erro = false;
                    break;


                case 2:

                    boolean result = fileVer.delete();
                    if (result) {
                        System.out.println("Ficheiro " + file + " removido com sucesso");
                        System.out.println();
                        System.out.println("(prima o enter para continuar)");

                        ler.nextLine();
                        ler.nextLine();

                    } else {
                        System.out.println("Remoção falhada");
                        System.out.println();
                        System.out.println("(prima o enter para continuar)");

                        ler.nextLine();
                        ler.nextLine();


                    }
                    erro = false;

                    break;


                default:
                    erro = true;
                    System.out.println("Valor inválido");
                    break;

            }

        }
        while (erro);
    }

    // Retorna um array com os ficheiros csv criados pelo utilizador, todas as tabelas anteriormente criadas
    public static String[][] GuardarFicheirosExistentes(int numfiles) throws IOException {
        //guarda o nome dos ficheiros e diretorios que estao na pasta do programa e coloca-os num array
        File folder = new File("./");
        File[] listOfFiles = folder.listFiles();

        String filename = "";
        int cont = 1;

        LimparEcra(); //limpa a consola
        MostrarTituloMenu("Comparar tabelas existentes");
        System.out.println("Lista de ficheiros existentes:");


        String[][] arrayFicheirosCsv = new String[numfiles][2];//cria uma matriz com o numero do programa e o nome do programa criados pelo utilizador

        //Lista os ficheiros criados pelo utilizador, lista-os associados a um numero, tendo o utilizador que escolher o numero respetivo
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                filename = listOfFiles[i].toString();
                filename = filename.replace("./", "");
                filename = filename.replace(".\\", "");
                if (filename.endsWith(".csv") && !filename.equals(FILE_IN)) {
                    System.out.println(cont + " - " + filename); //lista os ficheiros
                    //Guarda o numero e o nome numa matriz
                    arrayFicheirosCsv[cont - 1][0] = String.valueOf(cont);
                    arrayFicheirosCsv[cont - 1][1] = filename;

                    cont++;

                }
            }
        }

        System.out.println();
        return arrayFicheirosCsv; //retorna uma matriz com os ficheiros e o numero associado ao mesmo
    }

    public static int ContarFicheirosExistentes() throws IOException {
        //guarda o nome dos ficheiros e diretorios que estao na pasta do programa e coloca-os num array
        File folder = new File("./");
        File[] listOfFiles = folder.listFiles();

        String filename = "";
        int numFiles = 0;

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                filename = listOfFiles[i].toString();
                filename = filename.replace("./", "");
                filename = filename.replace(".\\", "");
                if (filename.endsWith(".csv") && !filename.equals(FILE_IN)) {
                    numFiles++; //caso o ficheiro termine em .csv e nao seja igual ao ficheiro csv dos parametros incremeta um


                }
            }
        }
        return numFiles;
    }

    // Lê o numero correspondente ao nome dos ficheiros introduzidos pelo o utilizador, os ficheiros que vão ser comparados. Retorna o nome do ficheiro lido
    public static String CompararFicheiros(String[][] ficheirosExistentes, int ordem) {
        String ficheiro = "";
        int opcao, numfiles = ficheirosExistentes.length;
        boolean flag;

        do {
            flag=false;
            switch (ordem) {
                case 1:
                    System.out.println("Numero correspondente ao primeiro ficheiro:");
                    break;
                case 2:
                    System.out.println("Numero correspondente ao segundo ficheiro:");
                    break;
                default:
                    break;
            }
            opcao = ler.nextInt();
            if(opcao <= 0 || opcao > numfiles){
                flag=true;
                System.out.println("Opção inválida");
            }

            System.out.println();
        } while (flag);


        for (int i = 0; i < numfiles; i++) {
            if (ficheirosExistentes[i][0].equals(String.valueOf(opcao))) {
                ficheiro = ficheirosExistentes[i][1];
            }
        }
        return ficheiro;
    }

    //fazer metodo verificaçao String e numeros n inteiros
    public static int Validar(String mensagem) {
        String opcao;

        int opcaoValidada = 1;
        boolean erro;
        do {
            System.out.print(mensagem + ": ");
            opcao = ler.next();
            erro = false;
            try {
                opcaoValidada = Integer.parseInt(opcao);
            } catch (NumberFormatException e) {
                System.out.println("O valor inserido não é um inteiro, tente novamente!!");
                System.out.println();
                erro = true;
            }

        } while (erro);

        return opcaoValidada;
    }

    public static void LimparEcra(){
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void MostrarTituloMenu(String titulo) {
        String barra = "-------------------------------";
        System.out.println(barra);
        int result = (barra.length() - titulo.length()) / 2;


        for (int i = 0; i < result; i++) {
            System.out.print(" ");

        }
        System.out.println(titulo);
        System.out.println(barra);


    }

}




