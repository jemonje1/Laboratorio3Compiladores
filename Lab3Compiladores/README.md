# Compilador - Laboratorio 3

Compilador implementado en Java que realiza análisis léxico, sintáctico y semántico de expresiones aritméticas, relacionales y booleanas. Genera notación postfija, árbol de sintaxis abstracta (AST) y evalúa expresiones.

hecho por alumnos: Sophia Alejandra Corea Perdomo (1185324) y Javier Enrique Monje Pérez (1260524)

## Descripción General

Este proyecto implementa un compilador completo con tres fases principales:

1. **Análisis Léxico**: Tokeniza la entrada
2. **Análisis Sintáctico**: Construye el AST y genera notación postfija
3. **Análisis Semántico**: Evalúa expresiones con valores de variables

Además, exporta el árbol sintáctico en formato DOT (Graphviz) y proporciona visualización gráfica interactiva.

---

## Compilación y Ejecución

### Paso 1: Compilar el Proyecto y ejecutarlo

### Paso 2: Ingresar la Ruta del Archivo

El programa solicitará que ingrese la ruta del archivo `.txt` que contiene la expresión:

```
-------------LABORATORIO TDS--------------
Ingrese la ruta del archivo .txt: ruta/del/archivo.txt
```

---

## Formato de Entrada

### Estructura del Archivo

El archivo de entrada debe contener **una o más expresiones**, separadas por **punto y coma** (`;`).

**Sintaxis válida:**
- Expresiones aritméticas: `a + b * (c - 2)`
- Expresiones relacionales: `x >= y - 1`
- Expresiones booleanas: `!(p && q) || r`
- Combinaciones: `(a + 5) * 2 >= b && true`

**Ejemplo de archivo entrada.txt:**
```
a + b * (c - 2);
!(p && q) || r;
(x + 2) * 3 >= y - 1
```

### Elementos Soportados

| Elemento | Tipo | Ejemplos |
|----------|------|----------|
| **Identificadores** | Variables | `a`, `b`, `x`, `variable1` |
| **Números** | Enteros | `2`, `10`, `-5` |
| **Literales Booleanos** | Constantes | `true`, `false` |
| **Operadores Aritméticos** | Binarios | `+`, `-`, `*`, `/` |
| **Operadores Unarios** | Prefijo | `-` (negación), `!` (negación booleana) |
| **Operadores Relacionales** | Binarios | `<`, `<=`, `>`, `>=`, `==`, `!=` |
| **Operadores Booleanos** | Binarios | `&&` (AND), `\|\|` (OR) |
| **Paréntesis** | Agrupación | `(`, `)` |

### Precedencia de Operadores

1. Paréntesis: `()`
2. Unarios: `-`, `!`
3. Multiplicación/División: `*`, `/`
4. Suma/Resta: `+`, `-`
5. Relacionales: `<`, `<=`, `>`, `>=`, `==`, `!=`
6. AND: `&&`
7. OR: `||`

---

## Formato de Salida

### Salida en Consola

Para cada expresión procesada, el programa muestra:

```
-------------EXPRESION 1--------------
Entrada: a + b * (c - 2)
Postfija: a b c 2 - * +
Archivo DOT generado: /ruta/salida/arbol_expr_1.dot

Desea evaluar esta expresion? (s/n): 
```

### 1. Expresión Procesada

Se muestra la entrada original y su representación en **notación postfija** (notación inversa polaca).

### 2. Archivo DOT

Se genera un archivo `.dot` en la misma carpeta que el archivo entrada:
- Nombre: `arbol_expr_1.dot`, `arbol_expr_2.dot`, etc.
- Formato: Compatible con Graphviz

Para visualizar el árbol:
```bash
dot -Tpng arbol_expr_1.dot -o arbol_expr_1.png
```

### 3. Evaluación Interactiva

Si el usuario ingresa `s`, se solicitan valores para las variables:

```
Desea evaluar esta expresion? (s/n): s
Ingrese valor para a (entero, true o false): 2
Ingrese valor para b (entero, true o false): 2
Ingrese valor para c (entero, true o false): 4
Resultado evaluado: 6
```

### 4. Visualización Gráfica

Si el usuario ingresa `s` para ver el árbol:

```
Desea ver el arbol en pantalla? (s/n): s
```

Se abre una ventana con el árbol sintáctico dibujado animadamente.

---

## Ejemplos de Uso

### Ejemplo 1: Expresión Aritmética

**Archivo:** `Caso1.txt`
```
a + b * (c - 2)
```

**Salida esperada:**
```
Entrada: a + b * (c - 2)
Postfija: a b c 2 - * +
```

**Evaluación con a=2, b=2, c=4:**
```
Resultado evaluado: 6
```

**Proceso:**
1. c - 2 = 4 - 2 = 2
2. b * 2 = 2 * 2 = 4
3. a + 4 = 2 + 4 = 6

---

### Ejemplo 2: Expresión Unaria

**Archivo:** `Caso2.txt`
```
-(x + 3) * y
```

**Salida esperada:**
```
Entrada: -(x + 3) * y
Postfija: x 3 + - y *
```

---

### Ejemplo 3: Expresión Booleana

**Archivo:** `Caso3.txt`
```
!(p && q) || r
```

**Salida esperada:**
```
Entrada: !(p && q) || r
Postfija: p q && ! r ||
```

**Evaluación con p=true, q=true, r=false:**
```
Resultado evaluado: false
```

---

### Ejemplo 4: Expresión Relacional

**Archivo:** `Caso4.txt`
```
(x + 2) * 3 >= y - 1
```

**Salida esperada:**
```
Entrada: (x + 2) * 3 >= y - 1
Postfija: x 2 + 3 * y 1 - >=
```

---

### Ejemplo 5: Expresión Mixta

**Archivo:** `Caso5.txt`
```
(a + 5) * 2 >= b && true
```

**Salida esperada:**
```
Entrada: (a + 5) * 2 >= b && true
Postfija: a 5 + 2 * b >= true &&
```

---

### Ejemplo 6: Expresión Negada

**Archivo:** `Caso6.txt`
```
!((x - 3) * 2 > y)
```

**Salida esperada:**
```
Entrada: !((x - 3) * 2 > y)
Postfija: x 3 - 2 * y > !
```

---

## Manejo de Errores

### Errores Léxicos

Se muestran si hay caracteres inválidos o tokens no reconocidos:

```
-------------ERRORES LEXICOS--------------
Error: Carácter inválido '@' en posición 5
```

### Errores Sintácticos

Se muestran si la expresión no cumple con la gramática:

```
-------------ERRORES SINTACTICOS--------------
Error: Se esperaba expresión válida
Error: Paréntesis sin cerrar
```

### Errores de Evaluación

Ocurren durante la evaluación:
- División por cero
- Variable no inicializada
- Tipos incompatibles

```
Error al evaluar: Variable 'x' no inicializada
Error al evaluar: División por cero
```

---

## Gramática Soportada

```
bexpr  → bterm bexpr'
bexpr' → || bterm bexpr' | ε

bterm  → bfactor bterm'
bterm' → && bfactor bterm' | ε

bfactor → ! bfactor | ( bexpr ) | rel | boollit

rel    → expr rop expr
rop    → < | <= | > | >= | == | !=

expr   → term expr'
expr'  → + term expr' | - term expr' | ε

term   → factor term'
term'  → * factor term' | / factor term' | ε

factor → ( expr ) | id | num | - factor

boollit → true | false
```

---

## Características Implementadas

- Análisis léxico con detección de errores  
- Parser descendente recursivo sin recursión a izquierda  
- Generación de notación postfija (RPN)  
- Construcción de árbol de sintaxis abstracta (AST)  
- Evaluación semántica con tabla de símbolos  
- Exportación a formato DOT para Graphviz  
- Visualización gráfica interactiva del AST  
- Manejo robusto de errores  

---

## Notas Técnicas

- La **notación postfija** facilita la evaluación mediante pilas
- El **AST** se construye simultáneamente con el análisis sintáctico
- Se elimina la **recursión a izquierda** para hacer el parser más eficiente
- La **precedencia de operadores** se mantiene mediante la jerarquía gramatical
- Se soportan tanto **tipos enteros como booleanos**

---

- **Sophia Alejandra Corea Perdomo** - Carné: 1185324
- **Javier Enrique Monje Pérez** - Carné: 1260524

Universidad Rafael Landívar - Ingeniería en Sistemas  
Compiladores, Sección 02
