# Adaptación de Casos de Prueba Sintácticos (`test/pruebas_sintactico/*.kt`)

Este documento detalla los cambios realizados a los archivos de prueba `.kt` proporcionados por el profesor para acoplarlos a las reglas léxicas y sintácticas implementadas en nuestro compilador **KotlinTec** (documentadas en `README.md`, `Lexico.java`, `Automata.java` y `SintacticoSemantico.java`).

---

## 1. Diferencias clave entre la gramática del profesor y nuestra implementación

1. **Declaración de variables (`DECLARACION`)**:
   - **Profesor**: Utiliza `var id: Tipo = expr;` y `val id: Tipo = expr;`.
   - **Nuestra gramática**: Utiliza exclusivamente la palabra reservada `let`:
     ```
     DECLARACION → let id : TIPO ASIGNACION ;
     ASIGNACION  → opasig EXPRESION | ε
     ```
     *(En nuestro analizador léxico `var` y `val` no son palabras reservadas, por lo que se reconocen como identificadores `id`).*

2. **Asignación de variables (`INSTRUCCION → id VERIFICACION_ID`)**:
   - **Profesor**: Utiliza `let id = expr;` para reasignar valores a una variable.
   - **Nuestra gramática**: `let` es exclusivo para declaraciones (siempre exige `: TIPO`), mientras que la asignación a una variable se escribe directamente con el identificador:
     ```
     INSTRUCCION     → id VERIFICACION_ID
     VERIFICACION_ID → ASIGNACION ; | INVOCACION ;
     ```
     Ejemplo: `edad = edad + 5 * 2;` (sin `let`).

3. **Literales de cadena (`literal`)**:
   - **Profesor**: Delimita cadenas con comillas dobles (`"Juan"`, `"Hola"`).
   - **Nuestro léxico (`Automata.java`, autómata 4)**: Reconoce literales de cadena únicamente entre **comillas simples** (`'Juan'`, `'Hola'`). Las comillas dobles `"` son ignoradas por el léxico.

4. **Clases sin funciones (`FUNCIONES`)**:
   - **Profesor**: Una clase vacía `class Ejemplo { }` produce error sintáctico.
   - **Nuestra gramática**: La producción `FUNCIONES → FUNCION FUNCIONES | ε` permite una clase con cero o más funciones (`class Ejemplo { }` es válido).

5. **Instrucción con identificador seguido de punto y coma (`id ;`)**:
   - **Profesor**: `mostrarMensaje ;` produce error sintáctico porque exige `=` o `()`.
   - **Nuestra gramática**: Como `VERIFICACION_ID → ASIGNACION ;` y `ASIGNACION → ε`, la secuencia `id ;` es aceptada por la producción anulable de `ASIGNACION`.

---

## 2. Detalle de cambios por archivo de prueba

### 2.1 Caso de prueba válido (`OKTest`)

#### `sintactico_OKTest_01.kt`
- **Objetivo**: Programa completo válido sin errores sintácticos (`0` errores esperados).
- **Cambios realizados**:
  - Se reemplazaron las palabras `var` y `val` en las declaraciones por `let` (`let edad: Int = 25;`, `let precio: Double = 10.5;`, `let limite: Int = 100;`, etc.).
  - Se eliminó `let` de las instrucciones de reasignación (`edad = edad + 5 * 2;`, `precio = (precio + 5.0) * 2.0;`, `resultado = resultado + 10;`, `resultado = resultado * 2;`).
  - Se cambiaron las comillas dobles `"Juan"` y `"Hola"` por comillas simples `'Juan'` y `'Hola'`.
- **Resultado**: `0` errores sintácticos.

---

### 2.2 Casos de prueba con error (`ErrTest`)

| Archivo | Contexto del error evaluado | Código original (Profesor) | Código adaptado (Nuestra gramática) | Primer error sintáctico reportado |
| :--- | :--- | :--- | :--- | :--- |
| **`sintactico_ErrTest_01.kt`** | Error en el cuerpo de la clase (`FUNCIONES` espera `'fun'` o `'}'`) | `class Ejemplo { }` *(válido en nuestra gramática por `FUNCIONES → ε`)* | `class Ejemplo { let edad: Int = 25; }` *(declaración fuera de función)* | `ERROR SINTACTICO: [FUNCIONES] Se esperaba 'fun' o '}'. No Linea 2` |
| **`sintactico_ErrTest_02.kt`** | Declaración de variable sin especificar `: TIPO` | `var edad = 25;` | `let edad = 25;` | `ERROR SINTACTICO: Se esperaba : se encontró =. Linea 4` |
| **`sintactico_ErrTest_03.kt`** | Declaración de variable sin la palabra reservada `let` | `limite : Int;` | *(Sin cambios)* `limite : Int;` | `ERROR SINTACTICO: [VERIFICACION_ID] Se esperaba 'opasig', ';' o '('. No Linea 4` |
| **`sintactico_ErrTest_04.kt`** | Asignación incompleta (falta la expresión después de `=`) | `let edad = ;` | `edad = ;` | `ERROR SINTACTICO: [EXPRESION] Se esperaba 'id', 'num', 'num.num', 'literal' o '('. No Linea 4` |
| **`sintactico_ErrTest_05.kt`** | Expresión aritmética con doble operador consecutivo (`* *`) | `let precio = (precio + 5.0) * * 2.0;` | `precio = (precio + 5.0) * * 2.0;` | `ERROR SINTACTICO: [TERMINO] Se esperaba 'id', 'num', 'num.num', 'literal' o '('. No Linea 4` |
| **`sintactico_ErrTest_06.kt`** | Invocación a función mal formada (falta paréntesis de apertura `(`) | `mostrarMensaje ;` *(aceptado por `ASIGNACION → ε`)* | `mostrarMensaje) ;` | `ERROR SINTACTICO: [VERIFICACION_ID] Se esperaba 'opasig', ';' o '('. No Linea 4` |
| **`sintactico_ErrTest_07.kt`** | Invocación a función con argumentos vacíos entre comas | `mostrarDatos(nombre, , );` | *(Sin cambios)* `mostrarDatos(nombre, , );` | `ERROR SINTACTICO: [ARGUMENTO] Se esperaba el inicio de una expresión. No Linea 4` |
| **`sintactico_ErrTest_08.kt`** | Sentencia `if / else` sin llaves `{ }` en sus bloques | `if (resultado > 50) let resultado = resultado + 10; else let resultado = resultado * 2;` | `if (resultado > 50) resultado = resultado + 10; else resultado = resultado * 2;` | `ERROR SINTACTICO: Se esperaba { se encontró resultado. Linea 5` |
| **`sintactico_ErrTest_09.kt`** | Sentencia `if` con condición vacía `if ()` | `if () { } else { }` | *(Sin cambios)* `if () { } else { }` | `ERROR SINTACTICO: [CONDICION] Se esperaba el inicio de una expresión. No Linea 4` |
| **`sintactico_ErrTest_10.kt`** | Sentencia `while` con condición sin paréntesis `()` | `while resultado < 200 { }` | *(Sin cambios)* `while resultado < 200 { }` | `ERROR SINTACTICO: Se esperaba ( se encontró resultado. Linea 4` |
| **`sintactico_ErrTest_11.kt`** | Declaración de función sin cuerpo `{ }` (terminada en `;`) | `fun sumar ( a : Int, b : Int ) : Int ;` | *(Sin cambios)* `fun sumar ( a : Int, b : Int ) : Int ;` | `ERROR SINTACTICO: Se esperaba { se encontró ;. Linea 3` |
| **`sintactico_ErrTest_12.kt`** | Parámetro de función con valor por defecto (`= 0.0`) | `fun calcularPrecio ( precio: Double = 0.0 ) { }` | *(Sin cambios)* `fun calcularPrecio ( precio: Double = 0.0 ) { }` | `ERROR SINTACTICO: [E_PARAMETRO] Se esperaba ',' o ')'. No Linea 3` |
| **`sintactico_ErrTest_13.kt`** | Palabra reservada `return` en el tipo de retorno de la función | `fun calcularPrecio () : return Double { }` | *(Sin cambios)* `fun calcularPrecio () : return Double { }` | `ERROR SINTACTICO: [TIPO] Se esperaba 'Int', 'Double' o 'String'. No Linea 2` |
| **`sintactico_ErrTest_14.kt`** | Sentencia `return` devolviendo un tipo (`Int`) en lugar de expresión | `return Int;` | *(Sin cambios)* `return Int;` | `ERROR SINTACTICO: [E_RETURN] Se esperaba una expresión o ';'. No Linea 4` |
| **`sintactico_ErrTest_15.kt`** | Instrucción fuera de cualquier declaración `class` | `x = 0 ;` | *(Sin cambios)* `x = 0 ;` | `ERROR SINTACTICO: [CLASE] Se esperaba 'class'. No Linea 1` |
