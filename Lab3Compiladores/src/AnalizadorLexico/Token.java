package AnalizadorLexico;

public class Token {

    //region ENUM
    public enum TipoToken {
        ID, NUM,
        SUM, REST, MULT, DIV,
        PARENIZQ, PARENDER,
        AND, OR, NOT,
        MENOR, MEIGUAL, MAYOR, MAIGUAL, ESIGUAL, NOIGUAL,
        TRUE, FALSE,
        NEWLINE, EOF, DESCONOCIDO
    }
    //endregion

    //region ATRIBUTOS
    private final TipoToken tipo;
    private final String lexema;
    private final int linea;
    private final int columna;
    //endregion

    //region CONSTRUCTOR
    /*
       Crea un token lexico
       @param tipo Tipo del token
       @param lexema Texto encontrado
       @param linea Linea donde aparece
       @param columna Columna donde aparece
    */
    public Token(TipoToken tipo, String lexema, int linea, int columna) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.linea = linea;
        this.columna = columna;
    }
    //endregion

    //region METODOS
    public TipoToken getTipo() {
        return tipo;
    }

    public String getLexema() {
        return lexema;
    }

    public int getLinea() {
        return linea;
    }

    public int getColumna() {
        return columna;
    }

    @Override
    public String toString() {
        return "line " + linea + ", col " + columna + ": [" + tipo + "," + lexema + "]";
    }
    //endregion
}