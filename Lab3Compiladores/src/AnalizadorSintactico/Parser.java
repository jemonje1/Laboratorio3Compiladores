package AnalizadorSintactico;

import AnalizadorLexico.Token;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Parser {

    //region CLASE INTERNA
    private static class ResultadoNodo {
        List<String> postfija;
        NodoAst nodo;

        ResultadoNodo(List<String> postfija, NodoAst nodo) {
            this.postfija = postfija;
            this.nodo = nodo;
        }
    }
    //endregion

    //region ATRIBUTOS
    private final List<Token> tokens;
    private final List<String> errores;
    private final Set<String> variables;
    private int posicion;
    //endregion

    //region CONSTRUCTOR
    /*
       Inicializa el parser con la lista de tokens
       @param tokens Lista de tokens de una expresion
    */
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.errores = new ArrayList<>();
        this.variables = new LinkedHashSet<>();
        this.posicion = 0;
    }
    //endregion

    //region METODOS PRINCIPALES
    /*
       Analiza una expresion completa
       @return Resultado del analisis
    */
    public ResultadoAnalisis parsear(String entradaOriginal) {
        NodoAst.reiniciarContador();

        ResultadoNodo resultado = analizarEntrada();

        if (!esFin()) {
            Token token = obtenerActual();
            errores.add("line " + token.getLinea() + ", col " + token.getColumna()
                    + ": token inesperado '" + token.getLexema() + "'");
        }

        if (!errores.isEmpty()) {
            return null;
        }

        String postfija = String.join(" ", resultado.postfija);
        String dot = new ExportadorDot().generarDot(resultado.nodo);

        return new ResultadoAnalisis(
                entradaOriginal,
                postfija,
                resultado.postfija,
                resultado.nodo,
                dot,
                variables
        );
    }

    /*
       Punto inicial del parser
       @return Resultado del nodo inicial
    */
    private ResultadoNodo analizarEntrada() {
        return analizarBexpr();
    }
    //endregion

    //region GRAMATICA BOOLEANA
    /*
       bexpr -> bterm bexpr'
       @return Resultado de bexpr
    */
    private ResultadoNodo analizarBexpr() {
        ResultadoNodo izquierdo = analizarBterm();
        return analizarBexprPadre(izquierdo);
    }

    /*
       bexpr' -> || bterm bexpr' | epsilon
       @param izquierdo Resultado acumulado
       @return Resultado actualizado
    */
    private ResultadoNodo analizarBexprPadre(ResultadoNodo izquierdo) {
        while (coincidir(Token.TipoToken.OR)) {
            ResultadoNodo derecho = analizarBterm();

            List<String> postfija = combinarPostfija(izquierdo.postfija, derecho.postfija, "||");
            NodoAst nodo = crearNodoBinario("||", izquierdo.nodo, derecho.nodo);

            izquierdo = new ResultadoNodo(postfija, nodo);
        }

        return izquierdo;
    }

    /*
       bterm -> bfactor bterm'
       @return Resultado de bterm
    */
    private ResultadoNodo analizarBterm() {
        ResultadoNodo izquierdo = analizarBfactor();
        return analizarBtermPadre(izquierdo);
    }

    /*
       bterm' -> && bfactor bterm' | epsilon
       @param izquierdo Resultado acumulado
       @return Resultado actualizado
    */
    private ResultadoNodo analizarBtermPadre(ResultadoNodo izquierdo) {
        while (coincidir(Token.TipoToken.AND)) {
            ResultadoNodo derecho = analizarBfactor();

            List<String> postfija = combinarPostfija(izquierdo.postfija, derecho.postfija, "&&");
            NodoAst nodo = crearNodoBinario("&&", izquierdo.nodo, derecho.nodo);

            izquierdo = new ResultadoNodo(postfija, nodo);
        }

        return izquierdo;
    }

    /*
       bfactor -> ! bfactor | ( bexpr ) | rel | boollit
       @return Resultado de bfactor
    */
    private ResultadoNodo analizarBfactor() {
        if (coincidir(Token.TipoToken.NOT)) {
            ResultadoNodo valor = analizarBfactor();

            List<String> postfija = new ArrayList<>(valor.postfija);
            postfija.add("!");

            NodoAst nodo = new NodoAst("!");
            nodo.agregarHijo(valor.nodo);

            return new ResultadoNodo(postfija, nodo);
        }

        if (coincidir(Token.TipoToken.TRUE)) {
            NodoAst nodo = new NodoAst("true");
            return new ResultadoNodo(List.of("true"), nodo);
        }

        if (coincidir(Token.TipoToken.FALSE)) {
            NodoAst nodo = new NodoAst("false");
            return new ResultadoNodo(List.of("false"), nodo);
        }

        if (verificar(Token.TipoToken.PARENIZQ) && contieneBooleanoHastaParentesis()) {
            avanzar();
            ResultadoNodo resultado = analizarBexpr();
            consumir(Token.TipoToken.PARENDER, "Se esperaba ')'");
            return resultado;
        }

        return analizarRel();
    }

    /*
       rel -> expr rop expr | expr
       @return Resultado de relacion o expresion aritmetica
    */
    private ResultadoNodo analizarRel() {
        ResultadoNodo izquierdo = analizarExpr();

        if (esOperadorRelacional(obtenerActual().getTipo())) {
            String operador = obtenerActual().getLexema();
            avanzar();

            ResultadoNodo derecho = analizarExpr();

            List<String> postfija = combinarPostfija(izquierdo.postfija, derecho.postfija, operador);
            NodoAst nodo = crearNodoBinario(operador, izquierdo.nodo, derecho.nodo);

            return new ResultadoNodo(postfija, nodo);
        }

        return izquierdo;
    }
    //endregion

    //region GRAMATICA ARITMETICA
    /*
       expr -> term expr'
       @return Resultado de expr
    */
    private ResultadoNodo analizarExpr() {
        ResultadoNodo izquierdo = analizarTerm();
        return analizarExprPadre(izquierdo);
    }

    /*
       expr' -> + term expr' | - term expr' | epsilon
       @param izquierdo Resultado acumulado
       @return Resultado actualizado
    */
    private ResultadoNodo analizarExprPadre(ResultadoNodo izquierdo) {
        while (verificar(Token.TipoToken.SUM) || verificar(Token.TipoToken.REST)) {
            String operador = obtenerActual().getLexema();
            avanzar();

            ResultadoNodo derecho = analizarTerm();

            List<String> postfija = combinarPostfija(izquierdo.postfija, derecho.postfija, operador);
            NodoAst nodo = crearNodoBinario(operador, izquierdo.nodo, derecho.nodo);

            izquierdo = new ResultadoNodo(postfija, nodo);
        }

        return izquierdo;
    }

    /*
       term -> factor term'
       @return Resultado de term
    */
    private ResultadoNodo analizarTerm() {
        ResultadoNodo izquierdo = analizarFactor();
        return analizarTermPadre(izquierdo);
    }

    /*
       term' -> * factor term' | / factor term' | epsilon
       @param izquierdo Resultado acumulado
       @return Resultado actualizado
    */
    private ResultadoNodo analizarTermPadre(ResultadoNodo izquierdo) {
        while (verificar(Token.TipoToken.MULT) || verificar(Token.TipoToken.DIV)) {
            String operador = obtenerActual().getLexema();
            avanzar();

            ResultadoNodo derecho = analizarFactor();

            List<String> postfija = combinarPostfija(izquierdo.postfija, derecho.postfija, operador);
            NodoAst nodo = crearNodoBinario(operador, izquierdo.nodo, derecho.nodo);

            izquierdo = new ResultadoNodo(postfija, nodo);
        }

        return izquierdo;
    }

    /*
       factor -> ( expr ) | id | num | - factor
       @return Resultado de factor
    */
    private ResultadoNodo analizarFactor() {
        if (coincidir(Token.TipoToken.REST)) {
            ResultadoNodo valor = analizarFactor();

            List<String> postfija = new ArrayList<>(valor.postfija);
            postfija.add("neg");

            NodoAst nodo = new NodoAst("neg");
            nodo.agregarHijo(valor.nodo);

            return new ResultadoNodo(postfija, nodo);
        }

        if (coincidir(Token.TipoToken.PARENIZQ)) {
            ResultadoNodo resultado = analizarExpr();
            consumir(Token.TipoToken.PARENDER, "Se esperaba ')'");
            return resultado;
        }

        if (coincidir(Token.TipoToken.NUM)) {
            Token anterior = obtenerAnterior();
            NodoAst nodo = new NodoAst(anterior.getLexema());
            return new ResultadoNodo(List.of(anterior.getLexema()), nodo);
        }

        if (coincidir(Token.TipoToken.ID)) {
            Token anterior = obtenerAnterior();
            variables.add(anterior.getLexema());

            NodoAst nodo = new NodoAst(anterior.getLexema());
            return new ResultadoNodo(List.of(anterior.getLexema()), nodo);
        }

        Token token = obtenerActual();
        errores.add("line " + token.getLinea() + ", col " + token.getColumna()
                + ": se esperaba factor, pero se encontro '" + token.getLexema() + "'");

        NodoAst nodoError = new NodoAst("error");
        avanzar();
        return new ResultadoNodo(List.of("error"), nodoError);
    }
    //endregion

    //region UTILIDADES
    /*
       Combina postfijas con operador
       @param izquierda Postfija izquierda
       @param derecha Postfija derecha
       @param operador Operador a agregar
       @return Lista postfija combinada
    */
    private List<String> combinarPostfija(List<String> izquierda, List<String> derecha, String operador) {
        List<String> resultado = new ArrayList<>();
        resultado.addAll(izquierda);
        resultado.addAll(derecha);
        resultado.add(operador);
        return resultado;
    }

    /*
       Crea nodo binario para AST
       @param operador Operador del nodo
       @param izquierdo Nodo izquierdo
       @param derecho Nodo derecho
       @return Nodo creado
    */
    private NodoAst crearNodoBinario(String operador, NodoAst izquierdo, NodoAst derecho) {
        NodoAst nodo = new NodoAst(operador);
        nodo.agregarHijo(izquierdo);
        nodo.agregarHijo(derecho);
        return nodo;
    }

    /*
       Verifica si hay booleanos dentro de un parentesis
       @return true si contiene booleano
    */
    private boolean contieneBooleanoHastaParentesis() {
        int nivel = 0;

        for (int i = posicion; i < tokens.size(); i++) {
            Token.TipoToken tipo = tokens.get(i).getTipo();

            if (tipo == Token.TipoToken.PARENIZQ) {
                nivel++;
            } else if (tipo == Token.TipoToken.PARENDER) {
                nivel--;
                if (nivel == 0) {
                    return false;
                }
            }

            if (tipo == Token.TipoToken.AND || tipo == Token.TipoToken.OR
                    || tipo == Token.TipoToken.NOT || tipo == Token.TipoToken.TRUE
                    || tipo == Token.TipoToken.FALSE || esOperadorRelacional(tipo)) {
                return true;
            }
        }

        return false;
    }

    /*
       Verifica operador relacional
       @param tipo Tipo de token
       @return true si es relacional
    */
    private boolean esOperadorRelacional(Token.TipoToken tipo) {
        return tipo == Token.TipoToken.MENOR
                || tipo == Token.TipoToken.MEIGUAL
                || tipo == Token.TipoToken.MAYOR
                || tipo == Token.TipoToken.MAIGUAL
                || tipo == Token.TipoToken.ESIGUAL
                || tipo == Token.TipoToken.NOIGUAL;
    }

    /*
       Consume token esperado
       @param tipo Tipo esperado
       @param mensaje Mensaje de error
    */
    private void consumir(Token.TipoToken tipo, String mensaje) {
        if (verificar(tipo)) {
            avanzar();
            return;
        }

        Token token = obtenerActual();
        errores.add("line " + token.getLinea() + ", col " + token.getColumna() + ": " + mensaje);
    }

    /*
       Coincide con un token y avanza
       @param tipo Tipo esperado
       @return true si coincide
    */
    private boolean coincidir(Token.TipoToken tipo) {
        if (verificar(tipo)) {
            avanzar();
            return true;
        }

        return false;
    }

    private boolean verificar(Token.TipoToken tipo) {
        if (esFin()) {
            return false;
        }

        return obtenerActual().getTipo() == tipo;
    }

    private Token avanzar() {
        if (!esFin()) {
            posicion++;
        }

        return obtenerAnterior();
    }

    private boolean esFin() {
        return obtenerActual().getTipo() == Token.TipoToken.EOF
                || obtenerActual().getTipo() == Token.TipoToken.NEWLINE;
    }

    private Token obtenerActual() {
        return tokens.get(posicion);
    }

    private Token obtenerAnterior() {
        return tokens.get(posicion - 1);
    }

    public List<String> getErrores() {
        return errores;
    }
    //endregion
}