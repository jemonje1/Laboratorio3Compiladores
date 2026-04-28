package AnalizadorSintactico;

public class ExportadorDot {

    //region METODOS
    /*
       Genera el texto DOT a partir de un AST
       @param raiz Nodo raiz del AST
       @return Texto DOT
    */
    public String generarDot(NodoAst raiz) {
        StringBuilder dot = new StringBuilder();

        dot.append("digraph AST {\n");
        dot.append("    node [shape=circle];\n");

        recorrerNodos(raiz, dot);
        recorrerRelaciones(raiz, dot);

        dot.append("}\n");

        return dot.toString();
    }

    /*
       Recorre los nodos para declararlos en DOT
       @param nodo Nodo actual
       @param dot Constructor de texto
    */
    private void recorrerNodos(NodoAst nodo, StringBuilder dot) {
        dot.append("    ")
                .append(nodo.getId())
                .append("[label=\"")
                .append(escapar(nodo.getEtiqueta()))
                .append("\"];\n");

        for (NodoAst hijo : nodo.getHijos()) {
            recorrerNodos(hijo, dot);
        }
    }

    /*
       Recorre relaciones padre hijo para DOT
       @param nodo Nodo actual
       @param dot Constructor de texto
    */
    private void recorrerRelaciones(NodoAst nodo, StringBuilder dot) {
        for (NodoAst hijo : nodo.getHijos()) {
            dot.append("    ")
                    .append(nodo.getId())
                    .append(" -> ")
                    .append(hijo.getId())
                    .append(";\n");

            recorrerRelaciones(hijo, dot);
        }
    }

    /*
       Escapa comillas para DOT
       @param texto Texto original
       @return Texto escapado
    */
    private String escapar(String texto) {
        return texto.replace("\"", "\\\"");
    }
    //endregion
}