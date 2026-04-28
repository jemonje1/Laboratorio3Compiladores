package Semantico;

import java.util.List;
import java.util.Stack;

public class EvaluadorPostFija {

    //region ATRIBUTOS
    private final TablaSimbolos tablaSimbolos;
    //endregion

    //region CONSTRUCTOR
    /*
       Inicializa el evaluador con tabla de simbolos
       @param tablaSimbolos Tabla de variables inicializadas
    */
    public EvaluadorPostFija(TablaSimbolos tablaSimbolos) {
        this.tablaSimbolos = tablaSimbolos;
    }
    //endregion

    //region METODOS
    /*
       Evalua una expresion postfija
       @param tokens Tokens postfija
       @return Valor resultante
    */
    public Valor evaluar(List<String> tokens) {
        Stack<Valor> pila = new Stack<>();
        return evaluarRecursivo(tokens, 0, pila);
    }

    /*
       Evalua recursivamente la expresion postfija
       @param tokens Tokens postfija
       @param indice Posicion actual
       @param pila Pila de valores
       @return Valor resultante
    */
    private Valor evaluarRecursivo(List<String> tokens, int indice, Stack<Valor> pila) {
        if (indice >= tokens.size()) {
            if (pila.size() != 1) {
                throw new RuntimeException("Expresion postfija invalida");
            }

            return pila.pop();
        }

        String token = tokens.get(indice);

        if (esOperadorBinario(token)) {
            if (pila.size() < 2) {
                throw new RuntimeException("Faltan operandos para el operador: " + token);
            }

            Valor valor2 = pila.pop();
            Valor valor1 = pila.pop();
            Valor resultado = aplicarOperacionBinaria(token, valor1, valor2);
            pila.push(resultado);

        } else if (esOperadorUnario(token)) {
            if (pila.isEmpty()) {
                throw new RuntimeException("Falta operando para el operador: " + token);
            }

            Valor valor = pila.pop();
            Valor resultado = aplicarOperacionUnaria(token, valor);
            pila.push(resultado);

        } else {
            pila.push(convertirTokenAValor(token));
        }

        return evaluarRecursivo(tokens, indice + 1, pila);
    }

    /*
       Convierte un token a valor
       @param token Token recibido
       @return Valor convertido
    */
    private Valor convertirTokenAValor(String token) {
        if (token.equals("true")) {
            return Valor.crearBooleano(true);
        }

        if (token.equals("false")) {
            return Valor.crearBooleano(false);
        }

        if (esNumero(token)) {
            return Valor.crearEntero(Integer.parseInt(token));
        }

        return tablaSimbolos.obtener(token);
    }

    /*
       Verifica si un token es numero
       @param token Token recibido
       @return true si es numero
    */
    private boolean esNumero(String token) {
        return token.matches("[0-9]+");
    }

    /*
       Verifica si es operador binario
       @param token Token recibido
       @return true si es operador binario
    */
    private boolean esOperadorBinario(String token) {
        return token.equals("+")
                || token.equals("-")
                || token.equals("*")
                || token.equals("/")
                || token.equals("<")
                || token.equals("<=")
                || token.equals(">")
                || token.equals(">=")
                || token.equals("==")
                || token.equals("!=")
                || token.equals("&&")
                || token.equals("||");
    }

    /*
       Verifica si es operador unario
       @param token Token recibido
       @return true si es operador unario
    */
    private boolean esOperadorUnario(String token) {
        return token.equals("!")
                || token.equals("neg");
    }

    /*
       Aplica una operacion binaria
       @param operador Operador recibido
       @param valor1 Primer valor
       @param valor2 Segundo valor
       @return Resultado de la operacion
    */
    private Valor aplicarOperacionBinaria(String operador, Valor valor1, Valor valor2) {
        switch (operador) {
            case "+":
                return Valor.crearEntero(valor1.getValorEntero() + valor2.getValorEntero());

            case "-":
                return Valor.crearEntero(valor1.getValorEntero() - valor2.getValorEntero());

            case "*":
                return Valor.crearEntero(valor1.getValorEntero() * valor2.getValorEntero());

            case "/":
                if (valor2.getValorEntero() == 0) {
                    throw new RuntimeException("Division por cero");
                }
                return Valor.crearEntero(valor1.getValorEntero() / valor2.getValorEntero());

            case "<":
                return Valor.crearBooleano(valor1.getValorEntero() < valor2.getValorEntero());

            case "<=":
                return Valor.crearBooleano(valor1.getValorEntero() <= valor2.getValorEntero());

            case ">":
                return Valor.crearBooleano(valor1.getValorEntero() > valor2.getValorEntero());

            case ">=":
                return Valor.crearBooleano(valor1.getValorEntero() >= valor2.getValorEntero());

            case "==":
                if (valor1.getTipo() == Valor.TipoValor.ENTERO && valor2.getTipo() == Valor.TipoValor.ENTERO) {
                    return Valor.crearBooleano(valor1.getValorEntero() == valor2.getValorEntero());
                }

                if (valor1.getTipo() == Valor.TipoValor.BOOLEANO && valor2.getTipo() == Valor.TipoValor.BOOLEANO) {
                    return Valor.crearBooleano(valor1.getValorBooleano() == valor2.getValorBooleano());
                }

                throw new RuntimeException("No se puede comparar == entre tipos diferentes");

            case "!=":
                if (valor1.getTipo() == Valor.TipoValor.ENTERO && valor2.getTipo() == Valor.TipoValor.ENTERO) {
                    return Valor.crearBooleano(valor1.getValorEntero() != valor2.getValorEntero());
                }

                if (valor1.getTipo() == Valor.TipoValor.BOOLEANO && valor2.getTipo() == Valor.TipoValor.BOOLEANO) {
                    return Valor.crearBooleano(valor1.getValorBooleano() != valor2.getValorBooleano());
                }

                throw new RuntimeException("No se puede comparar != entre tipos diferentes");

            case "&&":
                return Valor.crearBooleano(valor1.getValorBooleano() && valor2.getValorBooleano());

            case "||":
                return Valor.crearBooleano(valor1.getValorBooleano() || valor2.getValorBooleano());

            default:
                throw new RuntimeException("Operador no reconocido: " + operador);
        }
    }

    /*
       Aplica una operacion unaria
       @param operador Operador recibido
       @param valor Valor recibido
       @return Resultado de la operacion
    */
    private Valor aplicarOperacionUnaria(String operador, Valor valor) {
        switch (operador) {
            case "!":
                return Valor.crearBooleano(!valor.getValorBooleano());

            case "neg":
                return Valor.crearEntero(-valor.getValorEntero());

            default:
                throw new RuntimeException("Operador unario no reconocido: " + operador);
        }
    }
    //endregion
}