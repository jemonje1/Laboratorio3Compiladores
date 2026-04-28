package Semantico;

public class Valor {

    //region ENUM
    public enum TipoValor {
        ENTERO, BOOLEANO
    }
    //endregion

    //region ATRIBUTOS
    private final TipoValor tipo;
    private final Integer valorEntero;
    private final Boolean valorBooleano;
    //endregion

    //region CONSTRUCTOR
    /*
       Crea un valor semantico
       @param tipo Tipo del valor
       @param valorEntero Valor entero
       @param valorBooleano Valor booleano
    */
    private Valor(TipoValor tipo, Integer valorEntero, Boolean valorBooleano) {
        this.tipo = tipo;
        this.valorEntero = valorEntero;
        this.valorBooleano = valorBooleano;
    }
    //endregion

    //region METODOS
    /*
       Crea un valor entero
       @param valor Valor numerico
       @return Valor semantico entero
    */
    public static Valor crearEntero(int valor) {
        return new Valor(TipoValor.ENTERO, valor, null);
    }

    /*
       Crea un valor booleano
       @param valor Valor logico
       @return Valor semantico booleano
    */
    public static Valor crearBooleano(boolean valor) {
        return new Valor(TipoValor.BOOLEANO, null, valor);
    }

    public TipoValor getTipo() {
        return tipo;
    }

    public int getValorEntero() {
        if (tipo != TipoValor.ENTERO) {
            throw new RuntimeException("Se esperaba un valor entero");
        }

        return valorEntero;
    }

    public boolean getValorBooleano() {
        if (tipo != TipoValor.BOOLEANO) {
            throw new RuntimeException("Se esperaba un valor booleano");
        }

        return valorBooleano;
    }

    @Override
    public String toString() {
        if (tipo == TipoValor.ENTERO) {
            return String.valueOf(valorEntero);
        }

        return String.valueOf(valorBooleano);
    }
    //endregion
}