package Semantico;

import java.util.HashMap;
import java.util.Map;

public class TablaSimbolos {

    //region ATRIBUTOS
    private final Map<String, Valor> simbolos;
    //endregion

    //region CONSTRUCTOR
    /*
       Inicializa la tabla de simbolos con HashMap
    */
    public TablaSimbolos() {
        this.simbolos = new HashMap<>();
    }
    //endregion

    //region METODOS
    /*
       Inserta o actualiza una variable
       @param nombre Nombre de la variable
       @param valor Valor asignado
    */
    public void insertar(String nombre, Valor valor) {
        simbolos.put(nombre, valor);
    }

    /*
       Verifica si una variable existe
       @param nombre Nombre de la variable
       @return true si existe
    */
    public boolean existe(String nombre) {
        return simbolos.containsKey(nombre);
    }

    /*
       Obtiene el valor de una variable
       @param nombre Nombre de la variable
       @return Valor almacenado
    */
    public Valor obtener(String nombre) {
        if (!existe(nombre)) {
            throw new RuntimeException("Variable no inicializada: " + nombre);
        }

        return simbolos.get(nombre);
    }
    //endregion
}