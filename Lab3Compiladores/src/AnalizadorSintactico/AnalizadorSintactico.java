package AnalizadorSintactico;

import AnalizadorLexico.Token;

import java.util.ArrayList;
import java.util.List;

public class AnalizadorSintactico {

    //region ATRIBUTOS
    private final List<String> errores;
    //endregion

    //region CONSTRUCTOR
    /*
       Inicializa el analizador sintactico
    */
    public AnalizadorSintactico() {
        this.errores = new ArrayList<>();
    }
    //endregion

    //region METODOS
    /*
       Analiza todas las expresiones separadas por salto de linea
       @param tokens Lista completa de tokens
       @return Resultados por expresion
    */
    public List<ResultadoAnalisis> analizar(List<Token> tokens) {
        errores.clear();

        List<ResultadoAnalisis> resultados = new ArrayList<>();
        List<Token> tokensLinea = new ArrayList<>();
        StringBuilder entradaLinea = new StringBuilder();

        for (Token token : tokens) {
            if (token.getTipo() == Token.TipoToken.NEWLINE || token.getTipo() == Token.TipoToken.EOF) {
                if (!tokensLinea.isEmpty()) {
                    tokensLinea.add(new Token(Token.TipoToken.EOF, "$", token.getLinea(), token.getColumna()));

                    Parser parser = new Parser(tokensLinea);
                    ResultadoAnalisis resultado = parser.parsear(entradaLinea.toString().trim());

                    if (!parser.getErrores().isEmpty()) {
                        errores.addAll(parser.getErrores());
                    } else if (resultado != null) {
                        resultados.add(resultado);
                    }

                    tokensLinea.clear();
                    entradaLinea.setLength(0);
                }

                continue;
            }

            tokensLinea.add(token);
            entradaLinea.append(token.getLexema()).append(" ");
        }

        return resultados;
    }

    public List<String> getErrores() {
        return errores;
    }
    //endregion
}