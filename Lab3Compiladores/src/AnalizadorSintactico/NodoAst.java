package AnalizadorSintactico;

import java.util.ArrayList;
import java.util.List;

public class NodoAst {

    //region ATRIBUTOS
    private static int contadorGlobal = 1;
    private final String id;
    private final String etiqueta;
    private final List<NodoAst> hijos;
    //endregion

    //region CONSTRUCTOR
    /*
       Crea un nodo del arbol AST
       @param etiqueta Texto que representa el nodo
    */
    public NodoAst(String etiqueta) {
        this.id = "n" + contadorGlobal++;
        this.etiqueta = etiqueta;
        this.hijos = new ArrayList<>();
    }
    //endregion

    //region METODOS
    /*
       Reinicia el contador de nodos
    */
    public static void reiniciarContador() {
        contadorGlobal = 1;
    }

    /*
       Agrega un hijo al nodo actual
       @param hijo Nodo hijo
    */
    public void agregarHijo(NodoAst hijo) {
        hijos.add(hijo);
    }

    public String getId() {
        return id;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public List<NodoAst> getHijos() {
        return hijos;
    }
    //endregion
}