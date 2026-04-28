package AnalizadorSintactico;

import java.util.List;
import java.util.Set;

public class ResultadoAnalisis {

    //region ATRIBUTOS
    private final String entradaOriginal;
    private final String postfija;
    private final List<String> tokensPostfija;
    private final NodoAst nodoRaiz;
    private final String dot;
    private final Set<String> variables;
    //endregion

    //region CONSTRUCTOR
    /*
       Guarda el resultado final de una expresion
       @param entradaOriginal Expresion original
       @param postfija Traduccion postfija
       @param tokensPostfija Tokens de la postfija
       @param nodoRaiz Nodo raiz del AST
       @param dot Representacion DOT del AST
       @param variables Variables encontradas
    */
    public ResultadoAnalisis(String entradaOriginal, String postfija, List<String> tokensPostfija,
                             NodoAst nodoRaiz, String dot, Set<String> variables) {
        this.entradaOriginal = entradaOriginal;
        this.postfija = postfija;
        this.tokensPostfija = tokensPostfija;
        this.nodoRaiz = nodoRaiz;
        this.dot = dot;
        this.variables = variables;
    }
    //endregion

    //region METODOS
    public String getEntradaOriginal() {
        return entradaOriginal;
    }

    public String getPostfija() {
        return postfija;
    }

    public List<String> getTokensPostfija() {
        return tokensPostfija;
    }

    public NodoAst getNodoRaiz() {
        return nodoRaiz;
    }

    public String getDot() {
        return dot;
    }

    public Set<String> getVariables() {
        return variables;
    }
    //endregion
}