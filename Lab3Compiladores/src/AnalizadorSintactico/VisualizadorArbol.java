package AnalizadorSintactico;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisualizadorArbol {

    //region METODOS
    /*
       Muestra el arbol AST como dibujo animado
       @param raiz Nodo raiz del AST
    */
    public void mostrar(NodoAst raiz) {
        JFrame ventana = new JFrame("Visualizacion del arbol AST");
        PanelArbol panel = new PanelArbol(raiz);

        ventana.add(panel);
        ventana.setSize(900, 650);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);

        panel.iniciarAnimacion();
    }
    //endregion

    //region CLASE INTERNA
    private static class PanelArbol extends JPanel {

        //region ATRIBUTOS
        private final NodoAst raiz;
        private final Map<NodoAst, PosicionNodo> posiciones;
        private final List<NodoAst> ordenAnimacion;
        private int cantidadVisible;
        private int contadorX;
        private final int radioNodo;
        private final int separacionX;
        private final int separacionY;
        //endregion

        //region CONSTRUCTOR
        /*
           Inicializa el panel de dibujo
           @param raiz Nodo raiz del AST
        */
        public PanelArbol(NodoAst raiz) {
            this.raiz = raiz;
            this.posiciones = new HashMap<>();
            this.ordenAnimacion = new ArrayList<>();
            this.cantidadVisible = 0;
            this.contadorX = 0;
            this.radioNodo = 28;
            this.separacionX = 80;
            this.separacionY = 95;

            setBackground(Color.WHITE);

            calcularPosiciones();
            generarOrdenAnimacion(raiz);
        }
        //endregion

        //region METODOS
        /*
           Inicia la animacion del arbol
        */
        public void iniciarAnimacion() {
            Timer timer = new Timer(450, evento -> {
                cantidadVisible++;
                repaint();

                if (cantidadVisible >= ordenAnimacion.size()) {
                    ((Timer) evento.getSource()).stop();
                }
            });

            timer.start();
        }

        /*
           Calcula posiciones de cada nodo
        */
        private void calcularPosiciones() {
            contadorX = 1;
            asignarPosiciones(raiz, 0);

            int anchoUsado = contadorX * separacionX;
            int desplazamiento = Math.max(60, (900 - anchoUsado) / 2);

            for (PosicionNodo posicion : posiciones.values()) {
                posicion.x += desplazamiento;
                posicion.y += 60;
            }
        }

        /*
           Asigna posiciones recursivamente
           @param nodo Nodo actual
           @param nivel Nivel del nodo
           @return Posicion x calculada
        */
        private int asignarPosiciones(NodoAst nodo, int nivel) {
            if (nodo.getHijos().isEmpty()) {
                int x = contadorX * separacionX;
                int y = nivel * separacionY;
                posiciones.put(nodo, new PosicionNodo(x, y));
                contadorX++;
                return x;
            }

            int sumaX = 0;

            for (NodoAst hijo : nodo.getHijos()) {
                sumaX += asignarPosiciones(hijo, nivel + 1);
            }

            int x = sumaX / nodo.getHijos().size();
            int y = nivel * separacionY;

            posiciones.put(nodo, new PosicionNodo(x, y));
            return x;
        }

        /*
           Genera el orden de aparicion de los nodos
           @param nodo Nodo actual
        */
        private void generarOrdenAnimacion(NodoAst nodo) {
            ordenAnimacion.add(nodo);

            for (NodoAst hijo : nodo.getHijos()) {
                generarOrdenAnimacion(hijo);
            }
        }

        /*
           Dibuja el arbol en pantalla
           @param g Graficos del panel
        */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D graficos = (Graphics2D) g;
            graficos.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graficos.setFont(new Font("Arial", Font.BOLD, 15));

            dibujarRelaciones(graficos);
            dibujarNodos(graficos);
        }

        /*
           Dibuja las lineas entre nodos visibles
           @param graficos Graficos del panel
        */
        private void dibujarRelaciones(Graphics2D graficos) {
            graficos.setColor(Color.DARK_GRAY);
            graficos.setStroke(new BasicStroke(2));

            for (int i = 0; i < cantidadVisible && i < ordenAnimacion.size(); i++) {
                NodoAst nodo = ordenAnimacion.get(i);
                PosicionNodo posicionPadre = posiciones.get(nodo);

                for (NodoAst hijo : nodo.getHijos()) {
                    if (!esVisible(hijo)) {
                        continue;
                    }

                    PosicionNodo posicionHijo = posiciones.get(hijo);

                    graficos.drawLine(
                            posicionPadre.x,
                            posicionPadre.y,
                            posicionHijo.x,
                            posicionHijo.y
                    );
                }
            }
        }

        /*
           Dibuja los nodos visibles
           @param graficos Graficos del panel
        */
        private void dibujarNodos(Graphics2D graficos) {
            for (int i = 0; i < cantidadVisible && i < ordenAnimacion.size(); i++) {
                NodoAst nodo = ordenAnimacion.get(i);
                PosicionNodo posicion = posiciones.get(nodo);

                graficos.setColor(new Color(220, 238, 255));
                graficos.fillOval(
                        posicion.x - radioNodo,
                        posicion.y - radioNodo,
                        radioNodo * 2,
                        radioNodo * 2
                );

                graficos.setColor(new Color(40, 90, 140));
                graficos.setStroke(new BasicStroke(2));
                graficos.drawOval(
                        posicion.x - radioNodo,
                        posicion.y - radioNodo,
                        radioNodo * 2,
                        radioNodo * 2
                );

                graficos.setColor(Color.BLACK);
                dibujarTextoCentrado(graficos, nodo.getEtiqueta(), posicion.x, posicion.y);
            }
        }

        /*
           Verifica si un nodo ya debe mostrarse
           @param nodo Nodo a verificar
           @return true si ya es visible
        */
        private boolean esVisible(NodoAst nodo) {
            int indice = ordenAnimacion.indexOf(nodo);
            return indice >= 0 && indice < cantidadVisible;
        }

        /*
           Dibuja texto centrado dentro del nodo
           @param graficos Graficos del panel
           @param texto Texto a dibujar
           @param x Posicion x
           @param y Posicion y
        */
        private void dibujarTextoCentrado(Graphics2D graficos, String texto, int x, int y) {
            FontMetrics metricas = graficos.getFontMetrics();
            int ancho = metricas.stringWidth(texto);
            int alto = metricas.getAscent();

            graficos.drawString(texto, x - ancho / 2, y + alto / 4);
        }
        //endregion
    }
    //endregion

    //region CLASE INTERNA
    private static class PosicionNodo {

        //region ATRIBUTOS
        int x;
        int y;
        //endregion

        //region CONSTRUCTOR
        /*
           Guarda una posicion del nodo
           @param x Posicion horizontal
           @param y Posicion vertical
        */
        public PosicionNodo(int x, int y) {
            this.x = x;
            this.y = y;
        }
        //endregion
    }
    //endregion
}