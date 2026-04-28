package AnalizadorLexico;

import java.util.ArrayList;
import java.util.List;

public class AnalizadorLexico {

    //region ATRIBUTOS
    private final String contenido;
    private final List<String> errores;
    private int posicion;
    private int lineaActual;
    private int columnaActual;
    //endregion

    //region CONSTRUCTOR
    /*
       Inicializa el analizador lexico
       @param contenido Texto del archivo
    */
    public AnalizadorLexico(String contenido) {
        this.contenido = contenido;
        this.errores = new ArrayList<>();
        this.posicion = 0;
        this.lineaActual = 1;
        this.columnaActual = 1;
    }
    //endregion

    //region METODOS
    /*
       Analiza el contenido y genera tokens
       @return Lista de tokens
    */
    public List<Token> analizar() {
        List<Token> tokens = new ArrayList<>();

        while (posicion < contenido.length()) {
            char actual = contenido.charAt(posicion);

            if (actual == ' ' || actual == '\t') {
                avanzar();
                continue;
            }

            if (actual == '\n') {
                tokens.add(new Token(Token.TipoToken.NEWLINE, "\\n", lineaActual, columnaActual));
                avanzarLinea();
                continue;
            }

            if (actual == '\r') {
                avanzar();
                continue;
            }

            Token token = reconocerToken();

            if (token != null) {
                tokens.add(token);
            } else {
                errores.add("line " + lineaActual + ", col " + columnaActual + ": caracter no valido '" + actual + "'");
                tokens.add(new Token(Token.TipoToken.DESCONOCIDO, String.valueOf(actual), lineaActual, columnaActual));
                avanzar();
            }
        }

        tokens.add(new Token(Token.TipoToken.EOF, "$", lineaActual, columnaActual));
        return tokens;
    }

    /*
       Reconoce el siguiente token disponible
       @return Token reconocido
    */
    private Token reconocerToken() {
        String resto = contenido.substring(posicion);

        if (resto.startsWith("&&")) {
            return crearToken(Token.TipoToken.AND, "&&", 2);
        }

        if (resto.startsWith("||")) {
            return crearToken(Token.TipoToken.OR, "||", 2);
        }

        if (resto.startsWith("<=")) {
            return crearToken(Token.TipoToken.MEIGUAL, "<=", 2);
        }

        if (resto.startsWith(">=")) {
            return crearToken(Token.TipoToken.MAIGUAL, ">=", 2);
        }

        if (resto.startsWith("==")) {
            return crearToken(Token.TipoToken.ESIGUAL, "==", 2);
        }

        if (resto.startsWith("!=")) {
            return crearToken(Token.TipoToken.NOIGUAL, "!=", 2);
        }

        char actual = contenido.charAt(posicion);

        if (Character.isDigit(actual)) {
            return reconocerNumero();
        }

        if (Character.isLetter(actual) || actual == '_') {
            return reconocerIdentificador();
        }

        switch (actual) {
            case '+':
                return crearToken(Token.TipoToken.SUM, "+", 1);
            case '-':
                return crearToken(Token.TipoToken.REST, "-", 1);
            case '*':
                return crearToken(Token.TipoToken.MULT, "*", 1);
            case '/':
                return crearToken(Token.TipoToken.DIV, "/", 1);
            case '(':
                return crearToken(Token.TipoToken.PARENIZQ, "(", 1);
            case ')':
                return crearToken(Token.TipoToken.PARENDER, ")", 1);
            case '!':
                return crearToken(Token.TipoToken.NOT, "!", 1);
            case '<':
                return crearToken(Token.TipoToken.MENOR, "<", 1);
            case '>':
                return crearToken(Token.TipoToken.MAYOR, ">", 1);
            default:
                return null;
        }
    }

    /*
       Reconoce numeros enteros
       @return Token numerico
    */
    private Token reconocerNumero() {
        int inicio = posicion;
        int columna = columnaActual;

        while (posicion < contenido.length() && Character.isDigit(contenido.charAt(posicion))) {
            avanzar();
        }

        String lexema = contenido.substring(inicio, posicion);
        return new Token(Token.TipoToken.NUM, lexema, lineaActual, columna);
    }

    /*
       Reconoce identificadores y literales booleanos
       @return Token reconocido
    */
    private Token reconocerIdentificador() {
        int inicio = posicion;
        int columna = columnaActual;

        while (posicion < contenido.length()) {
            char actual = contenido.charAt(posicion);

            if (Character.isLetterOrDigit(actual) || actual == '_') {
                avanzar();
            } else {
                break;
            }
        }

        String lexema = contenido.substring(inicio, posicion);

        if (lexema.equals("true")) {
            return new Token(Token.TipoToken.TRUE, lexema, lineaActual, columna);
        }

        if (lexema.equals("false")) {
            return new Token(Token.TipoToken.FALSE, lexema, lineaActual, columna);
        }

        return new Token(Token.TipoToken.ID, lexema, lineaActual, columna);
    }

    /*
       Crea token y avanza posiciones
       @param tipo Tipo del token
       @param lexema Texto del token
       @param longitud Cantidad de caracteres
       @return Token creado
    */
    private Token crearToken(Token.TipoToken tipo, String lexema, int longitud) {
        Token token = new Token(tipo, lexema, lineaActual, columnaActual);

        for (int i = 0; i < longitud; i++) {
            avanzar();
        }

        return token;
    }

    /*
       Avanza una posicion
    */
    private void avanzar() {
        posicion++;
        columnaActual++;
    }

    /*
       Avanza a la siguiente linea
    */
    private void avanzarLinea() {
        posicion++;
        lineaActual++;
        columnaActual = 1;
    }

    public List<String> getErrores() {
        return errores;
    }
    //endregion
}