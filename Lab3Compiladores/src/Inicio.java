import AnalizadorLexico.AnalizadorLexico;
import AnalizadorLexico.Token;
import AnalizadorSintactico.AnalizadorSintactico;
import AnalizadorSintactico.ResultadoAnalisis;
import AnalizadorSintactico.VisualizadorArbol;
import Archivo.Archivo;
import Semantico.EvaluadorPostFija;
import Semantico.TablaSimbolos;
import Semantico.Valor;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Inicio {

    //region ATRIBUTOS
    private final Scanner scanner;
    private final Archivo archivo;
    //endregion

    //region CONSTRUCTOR
    /*
       Inicializa el flujo principal del laboratorio
    */
    public Inicio() {
        this.scanner = new Scanner(System.in);
        this.archivo = new Archivo();
    }
    //endregion

    //region METODOS
    /*
       Ejecuta el laboratorio completo
    */
    public void iniciar() {
        System.out.println("-------------LABORATORIO TDS--------------");
        System.out.print("Ingrese la ruta del archivo .txt: ");
        String rutaEntrada = scanner.nextLine();

        try {
            String contenido = archivo.leerContenido(rutaEntrada);

            AnalizadorLexico lexer = new AnalizadorLexico(contenido);
            List<Token> tokens = lexer.analizar();

            AnalizadorSintactico sintactico = new AnalizadorSintactico();
            List<ResultadoAnalisis> resultados = sintactico.analizar(tokens);

            imprimirErroresLexer(lexer.getErrores());

            if (!sintactico.getErrores().isEmpty()) {
                imprimirErroresSintacticos(sintactico.getErrores());
                return;
            }

            Path carpetaSalida = archivo.obtenerCarpetaSalida(rutaEntrada);

            for (int i = 0; i < resultados.size(); i++) {
                ResultadoAnalisis resultado = resultados.get(i);

                System.out.println("\n-------------EXPRESION " + (i + 1) + "--------------");
                System.out.println("Entrada: " + resultado.getEntradaOriginal());
                System.out.println("Postfija: " + resultado.getPostfija());

                Path rutaDot = carpetaSalida.resolve("arbol_expr_" + (i + 1) + ".dot");
                archivo.escribirContenido(rutaDot, resultado.getDot());
                System.out.println("Archivo DOT generado: " + rutaDot.toAbsolutePath());

                manejarEvaluacion(resultado);
                manejarVisualizacion(resultado);
            }

        } catch (Exception e) {
            System.out.println("Error critico: " + e.getMessage());
        }
    }

    /*
       Muestra errores lexicos
       @param errores Lista de errores lexicos
    */
    private void imprimirErroresLexer(List<String> errores) {
        if (errores.isEmpty()) {
            return;
        }

        System.out.println("\n-------------ERRORES LEXICOS--------------");
        for (String error : errores) {
            System.out.println(error);
        }
    }

    /*
       Muestra errores sintacticos
       @param errores Lista de errores sintacticos
    */
    private void imprimirErroresSintacticos(List<String> errores) {
        System.out.println("\n-------------ERRORES SINTACTICOS--------------");
        for (String error : errores) {
            System.out.println(error);
        }
    }

    /*
       Permite evaluar la expresion si el usuario desea
       @param resultado Resultado generado por el parser
    */
    private void manejarEvaluacion(ResultadoAnalisis resultado) {
        System.out.print("Desea evaluar esta expresion? (s/n): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();

        if (!respuesta.equals("s")) {
            return;
        }

        TablaSimbolos tablaSimbolos = new TablaSimbolos();

        for (String variable : resultado.getVariables()) {
            System.out.print("Ingrese valor para " + variable + " (entero, true o false): ");
            String entradaValor = scanner.nextLine().trim();

            if (entradaValor.equals("true") || entradaValor.equals("false")) {
                tablaSimbolos.insertar(variable, Valor.crearBooleano(Boolean.parseBoolean(entradaValor)));
            } else {
                tablaSimbolos.insertar(variable, Valor.crearEntero(Integer.parseInt(entradaValor)));
            }
        }

        EvaluadorPostFija evaluador = new EvaluadorPostFija(tablaSimbolos);

        try {
            Valor valor = evaluador.evaluar(resultado.getTokensPostfija());
            System.out.println("Resultado evaluado: " + valor);
        } catch (Exception e) {
            System.out.println("Error al evaluar: " + e.getMessage());
        }
    }

    /*
       Permite visualizar el arbol en pantalla si el usuario desea
       @param resultado Resultado generado por el parser
    */
    private void manejarVisualizacion(ResultadoAnalisis resultado) {
        System.out.print("Desea ver el arbol en pantalla? (s/n): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();

        if (respuesta.equals("s")) {
            VisualizadorArbol visualizador = new VisualizadorArbol();
            visualizador.mostrar(resultado.getNodoRaiz());
        }
    }
    //endregion
}