# \# KotlinTec — Reglas léxicas y sintácticas (guía para agente verificador)

# 

# Fuente: \*LA2-U1P02 Analizador Sintáctico de Lenguaje Kotlin (rev1a)\* — gramática "Rev. Tyreans", `SintacticoSemantico.java` y `Lexico.java`.

# 

# \*\*Cómo usar este documento.\*\* Sirve para dos tipos de chequeo:

# \- \*\*A. Programas fuente KotlinTec\*\*: ¿el programa es léxica y sintácticamente válido?

# \- \*\*B. Implementación\*\* (`Lexico.java`, `SintacticoSemantico.java`): ¿cumple la gramática y los requisitos de la práctica?

# 

# Cada regla se marca con su origen: \*\*\[DOC]\*\* está explícita en el documento, \*\*\[CÓDIGO]\*\* se deduce del código Java, \*\*\[INFERIDO]\*\* es una deducción mía (el documento no incluye la clase `Automata`, así que los detalles finos del léxico no se pueden confirmar).

# 

# \---

# 

# \## 1. Visión general del pipeline

# 

# 1\. `Lexico.Analiza(texto)` recorre el texto, convierte en tokens y llena la \*\*tabla de símbolos (TS)\*\* y el \*\*buffer de entrada (BE)\*\*. Cada entrada del BE tiene: `complex` (el token), `lexema`, `entrada` (índice en TS) y `numLinea`.

# 2\. `SintacticoSemantico.analizar()` toma `preAnalisis = be.preAnalisis.complex` y llama a `CLASE()` (símbolo inicial).

# 3\. Es un \*\*parser descendente predictivo recursivo LL(1)\*\*: una función por no terminal (31) más `emparejar(token)`. Un solo token de lookahead (`preAnalisis`), sin backtracking.

# 4\. Al terminar `CLASE()`, el siguiente token debe ser `$` (fin de archivo).

# 

# \---

# 

# \## 2. Reglas léxicas

# 

# \### 2.1 Palabras reservadas \[CÓDIGO]

# Exactamente 10, sensibles a mayúsculas:

# 

# `class` `fun` `let` `if` `else` `while` `return` `Int` `Double` `String`

# 

# \- El léxico primero reconoce una palabra como `id`; si su lexema está en la lista, \*\*el token pasa a ser el propio lexema\*\* (`complex = "class"`, `"fun"`, etc.) y \*\*no entra a la tabla de símbolos\*\*.

# \- `Int`, `Double`, `String` van con \*\*mayúscula inicial\*\*. `int`, `string` serían identificadores comunes.

# \- No existen `val`, `var`, `for`, `true`, `false`, `null`, `when`, `break`, etc. Serían `id`.

# 

# \### 2.2 Tabla de tokens

# 

# | Token (`complex`) | Qué reconoce | Autómata | ¿Entra a TS? |

# |---|---|---|---|

# | \*(delimitador)\* | espacios, tabs, saltos de línea; se descartan | 0 | — |

# | `opmult` | operador multiplicativo (`\*`, `/`) \[INFERIDO] | 1 | No |

# | `opsuma` | operador aditivo (`+`) \[INFERIDO] | 2 | No |

# | `id` | identificador (típicamente letra seguida de letras/dígitos) \[INFERIDO] | 3 | \*\*Sí\*\* |

# | `literal` | cadena entre comillas | 4 | \*\*Sí\*\* |

# | `signo` | signo (`-`) \[INFERIDO] | 5 | No |

# | `opasig` | asignación (`=`, y también `:=`) | 6, 13 y 11/12 si lexema = `=` | No |

# | `num.num` | constante real (3 autómatas: Reales1/2/3) | 7, 8, 9 | \*\*Sí\*\* |

# | `num` | constante entera | 10 | \*\*Sí\*\* |

# | `oprel` | relacionales `<` `>` `<=` `>=` `==` `!=` | 11, 12 (lexema ≠ `=`) | No |

# | `punto` | punto/puntuación; el token queda igual al lexema | 14 | No |

# | `caresp` | carácter especial; el token queda igual al lexema (`{ } ( ) , ; : \\| \&`…) \[INFERIDO] | 15 | No |

# | `caracter` | constante carácter entre comillas simples, ej. `'\&'` | 16 | \*\*Sí\*\* |

# 

# \### 2.3 Reglas de funcionamiento

# \- \*\*Prioridad por orden de autómata\*\* \[CÓDIGO]: se prueban en orden 0→16 y gana el primero que reconoce. Importa en casos como `5.0` (real antes que entero) o `=`/`==`.

# \- \*\*`=` y `==`\*\* \[CÓDIGO]: `=` es `opasig`; `==`, `!=`, `<`, `>`, `<=`, `>=` son `oprel`. `:=` es `opasig`.

# \- \*\*Los tokens de puntuación se usan por su lexema en el parser.\*\* El parser compara contra `"{"`, `"}"`, `"("`, `")"`, `","`, `";"`, `":"`, `"|"`, `"\&"`. Un cambio en la tabla `caresp`/`punto` rompe el parser.

# \- \*\*Los operadores del parser son tokens abstractos\*\*: `opsuma`, `signo`, `opmult`, `oprel`, `opasig`. Los lexemas `+ - \* / < > …` nunca llegan al parser como tokens.

# \- \*\*Número de línea\*\*: cada token guarda la línea del fuente (`\_noLineas\[]`), que se usa en los mensajes de error.

# \- \*\*Límite\*\*: `MAXTOKENS = 500`. Con más de 500 tokens el arreglo se desborda (`ArrayIndexOutOfBounds`).

# \- \*\*Caracteres no reconocidos\*\* \[CÓDIGO]: si ningún autómata los reconoce, el léxico hace `\_i++` y \*\*los descarta en silencio, sin error léxico\*\*.

# \- \*\*Al final del archivo\*\* el BE debe terminar con el token `$`; el parser lo usa como centinela.

# 

# \### 2.4 Checklist léxico para un programa fuente

# \- \[ ] Las palabras reservadas están escritas exactamente (`Int`, no `int`).

# \- \[ ] Los tipos son solo `Int`, `Double`, `String`.

# \- \[ ] Los identificadores no coinciden con una palabra reservada.

# \- \[ ] Los reales tienen forma `num.num` (ej. `10.5`, `5.0`); el entero no lleva punto.

# \- \[ ] Las cadenas están correctamente delimitadas (ver discrepancia D5).

# \- \[ ] No hay caracteres fuera del alfabeto del lenguaje (el léxico los ignora sin avisar).

# \- \[ ] No supera 500 tokens.

# 

# \---

# 

# \## 3. Gramática (LL(1)) \[DOC]

# 

# `ε` = vacío. Terminales entre comillas son tokens/lexemas tal como los ve el parser.

# 

# ```

# CLASE                → class id { FUNCIONES }

# FUNCIONES            → FUNCION FUNCIONES | ε

# FUNCION              → fun id ( PARAMETROS ) TIPO\_RETORNO { INSTRUCCIONES }

# PARAMETROS           → PARAMETRO E\_PARAMETRO | ε

# E\_PARAMETRO          → , PARAMETRO E\_PARAMETRO | ε

# PARAMETRO            → id : TIPO

# TIPO\_RETORNO         → : TIPO | ε

# INSTRUCCIONES        → INSTRUCCION INSTRUCCIONES | ε

# INSTRUCCION          → DECLARACION | id VERIFICACION\_ID | IF | WHILE | RETURN

# IF                   → if ( CONDICION ) { INSTRUCCIONES } E\_IF

# E\_IF                 → else E\_IF\_PRIMA | ε

# E\_IF\_PRIMA           → { INSTRUCCIONES } | IF

# WHILE                → while ( CONDICION ) { INSTRUCCIONES }

# CONDICION            → COMPARACION E\_CONDICION

# E\_CONDICION          → UNION\_LOGICA COMPARACION E\_CONDICION | ε      (UNION\_LOGICA = | , \&)

# COMPARACION          → EXPRESION E\_COMPARACION

# E\_COMPARACION        → OPERADOR\_LOGICO EXPRESION | ε                (OPERADOR\_LOGICO = oprel)

# DECLARACION          → let id : TIPO ASIGNACION ;

# ASIGNACION           → opasig EXPRESION | ε

# VERIFICACION\_ID      → ASIGNACION ; | INVOCACION ;

# INVOCACION           → ( ARGUMENTOS )

# RETURN               → return E\_RETURN ;

# E\_RETURN             → EXPRESION | ε

# ARGUMENTOS           → ARGUMENTO E\_ARGUMENTOS | ε

# E\_ARGUMENTOS         → , ARGUMENTO E\_ARGUMENTOS | ε

# ARGUMENTO            → EXPRESION

# EXPRESION            → TERMINO E\_EXPRESION

# E\_EXPRESION          → OPERADOR\_ARITMETICO TERMINO E\_EXPRESION | ε   (opsuma | signo | opmult)

# TERMINO              → id VERIFICACION\_TERMINO | num | num.num | literal | ( EXPRESION )

# VERIFICACION\_TERMINO → ε | INVOCACION

# TIPO                 → Int | Double | String

# ```

# 

# \### 3.1 Tabla de decisión del parser (token de lookahead → producción) \[CÓDIGO]

# 

# `EXPR\_INI` = `{ id, num, num.num, literal, ( }`. `FIN\_EXPR` = `{ oprel, |, \&, ), ;, "," }`.

# 

# | No terminal | Produce la regla con… | Produce ε con… | Si no, error |

# |---|---|---|---|

# | CLASE | `class` | — | `Se esperaba 'class'` |

# | FUNCIONES | `fun` | `}` | `'fun' o '}'` |

# | FUNCION | `fun` | — | `'fun'` |

# | PARAMETROS | `id` | `)` | `'id' o ')'` |

# | E\_PARAMETRO | `,` | `)` | `',' o ')'` |

# | PARAMETRO | `id` | — | `'id'` |

# | TIPO\_RETORNO | `:` | `{` | `':' o '{'` |

# | INSTRUCCIONES | `let id if while return` | `}` | `instrucción o '}'` |

# | INSTRUCCION | `let`→DECLARACION; `id`→id VERIF\_ID; `if`; `while`; `return` | — | lista de 5 |

# | IF | `if` | — | `'if'` |

# | E\_IF | `else` | `let id if while return }` | `'else' o fin de bloque` |

# | E\_IF\_PRIMA | `{` o `if` | — | `'{' o 'if'` |

# | WHILE | `while` | — | `'while'` |

# | CONDICION | `EXPR\_INI` | — | `inicio de expresión` |

# | E\_CONDICION | `\\|` o `\&` | `)` | `'\\|', '\&' o ')'` |

# | COMPARACION | `EXPR\_INI` | — | `inicio de expresión` |

# | E\_COMPARACION | `oprel` | `\\| \& )` | `operador relacional, '\\|', '\&' o ')'` |

# | DECLARACION | `let` | — | `'let'` |

# | ASIGNACION | `opasig` | `;` | `'opasig' o ';'` |

# | VERIFICACION\_ID | `opasig` o `;`→ASIGNACION `;`; `(`→INVOCACION `;` | — | `'opasig', ';' o '('` |

# | INVOCACION | `(` | — | `'('` |

# | RETURN | `return` | — | `'return'` |

# | E\_RETURN | `EXPR\_INI` | `;` | `expresión o ';'` |

# | ARGUMENTOS | `EXPR\_INI` | `)` | `expresión o ')'` |

# | E\_ARGUMENTOS | `,` | `)` | `',' o ')'` |

# | ARGUMENTO | `EXPR\_INI` | — | `inicio de expresión` |

# | EXPRESION | `EXPR\_INI` | — | `id, num, num.num, literal o '('` |

# | E\_EXPRESION | `opsuma`, `signo`, `opmult` | `FIN\_EXPR` | `operador aritmético o fin de expresión` |

# | TERMINO | `id`, `num`, `num.num`, `literal`, `(` | — | `id, num, num.num, literal o '('` |

# | VERIFICACION\_TERMINO | `(`→INVOCACION | `opsuma signo opmult oprel \\| \& ) ; ,` | `'(' o fin del término` |

# | TIPO | `Int`, `Double`, `String` | — | `'Int', 'Double' o 'String'` |

# 

# \### 3.2 Qué acepta y qué rechaza la gramática (consecuencias prácticas)

# 

# \*\*Acepta:\*\*

# \- Un archivo = \*\*una sola clase\*\* con \*\*cero o más funciones\*\*; no hay variables ni código fuera de las funciones.

# \- Parámetros `nombre: Tipo` separados por comas; tipo de retorno opcional `: Tipo`.

# \- Instrucciones: `let x: Tipo;`, `let x: Tipo = expr;`, `x = expr;`, `x;`, `f(args);`, `if`, `while`, `return;`, `return expr;`.

# \- `if (...) {...}`, `if (...) {...} else {...}`, `if (...) {...} else if (...) {...}` (el `else if` se reduce a `else` + `IF`).

# \- Llamadas a funciones dentro de expresiones: `x = sumar(a, b);`.

# \- Condiciones con `|` y `\&` entre comparaciones: `(a > 1 \& b < 2 | c == 3)`.

# \- Una condición sin operador relacional: `if (x) {...}`.

# 

# \*\*Rechaza:\*\*

# \- `;` obligatorio al cerrar declaración, asignación, invocación y `return`. Los `}` de bloques no llevan `;`.

# \- Las llaves `{ }` son \*\*obligatorias\*\* en `if`, `else`, `while` y funciones. No hay cuerpo de una sola línea.

# \- `else` solo puede seguir a un `if`.

# \- Una comparación admite \*\*un solo\*\* operador relacional: `a < b < c` es error.

# \- Los paréntesis `( )` agrupan \*\*expresiones aritméticas\*\*, no condiciones: `if ((a < b))` es error.

# \- Una asignación no admite relacionales: `x = a < b;` es error (`oprel` no está en E\_EXPRESION).

# \- No hay precedencia de operadores en la gramática: `+ - \* /` son un mismo nivel, asociativo por la izquierda.

# \- No hay `!`, `\&\&`, `||`, operadores unarios, arreglos, `for`, ni modificadores (`val`/`var`/`private`).

# \- La declaración lleva siempre `: Tipo` (no hay inferencia). Sin inicializador se permite: `let x: Int;`.

# \- Parámetros sin tipo (`fun f(a)`) son error.

# \- Ojo: `signo` aparece en E\_EXPRESION como operador binario; `-5` como número negativo inicial \*\*no\*\* lo contempla la gramática (EXPRESION debe empezar con `EXPR\_INI`).

# 

# \---

# 

# \## 4. Reglas de manejo de errores \[DOC + CÓDIGO]

# 

# \### 4.1 Requisitos de la práctica \[DOC]

# 1\. Cada procedimiento debe identificarse con el \*\*nombre de su autor\*\*.

# 2\. Cada rama `if / else if` del procedimiento debe tener \*\*un comentario con la producción\*\* que analiza.

# 3\. Los mensajes de error deben incluir: \*\*nombre del procedimiento\*\* + \*\*descripción correcta\*\* + \*\*número de línea\*\* del fuente.

# 4\. La sección de pruebas debe incluir al menos una prueba correcta y una incorrecta.

# 

# \### 4.2 Formato real en el código \[CÓDIGO]

# \- Error por no coincidir en una rama: `"\[NOMBRE\_PROC] Se esperaba 'x' o 'y'. No Linea N"`.

# \- Error en `emparejar(t)`: `"Se esperaba <descripción del token> se encontró <lexema | fin de archivo>. Linea N"`. Aquí \*\*no\*\* aparece el nombre del procedimiento.

# \- Descripciones especiales en `errorEmparejar`: `id` → "un identificador", `num` → "una constante entera", `num.num` → "una constante real", `literal` → "una literal", `oprel` → "un operador relacional", `opasig` → "operador de asignación", `oparit` → "un operador aritmético".

# \- Los errores se registran con `cmp.me.error(ERR\_SINTACTICO, msg)`.

# \- Tras `CLASE()`: si el token no es `$` y no hubo errores sintácticos, se emite un error de "sobra contenido" con la línea.

# 

# \---

# 

# \## 5. Checklist de verificación de la implementación (tipo B)

# 

# \- \[ ] Existen \*\*31 métodos\*\* con exactamente los nombres de la gramática, más `emparejar`, `error` y `errorEmparejar`.

# \- \[ ] El método de cada no terminal sigue la \*\*tabla 3.1\*\* (conjuntos de decisión y ε).

# \- \[ ] Los terminales usan los \*\*tokens del léxico\*\* (`opsuma`, `signo`, `opmult`, `oprel`, `opasig`, `num.num`…), no el lexema del operador.

# \- \[ ] Cada rama tiene un comentario con la producción (requisito 2).

# \- \[ ] Cada procedimiento tiene autor (requisito 1).

# \- \[ ] Todo mensaje de error lleva procedimiento + descripción + línea (requisito 3).

# \- \[ ] La lista de palabras reservadas del léxico coincide con los terminales literales de la gramática.

# \- \[ ] No quedan `System.out.println` de depuración en el código entregable.

# 

# \---

# 

# \## 6. Discrepancias detectadas en el documento (para que las revises tú)

# 

# No modifiqué nada; solo las listo.

# 

# \*\*Documentación vs. gramática/código\*\*

# \- \*\*D1. Tabla FIRST con ε de más.\*\* En la tabla, `ARGUMENTO` y `EXPRESION` incluyen `Ꜫ`, pero no son anulables (`EXPRESION → TERMINO …`, `ARGUMENTO → EXPRESION`). El código no los trata como ε, así que la tabla es la que está mal.

# \- \*\*D2. Tipos en minúscula en FIRST.\*\* La tabla FIRST de `TIPO` dice `int, double, string`, pero la gramática y el léxico usan `Int, Double, String`.

# \- \*\*D3. Nombres de tokens abstractos en las tablas.\*\* Las tablas FIRST/FOLLOW usan `OP\_ARITMETICO`, `OP\_LOGICO`, `UNION\_LOGICA`, `=`, mientras que el parser usa `opsuma/signo/opmult`, `oprel`, `|`/`\&`, `opasig`. Son equivalentes, pero no coinciden literalmente.

# \- \*\*D4. Pseudocódigo vs. Java en `E\_COMPARACION`.\*\* El pseudocódigo usa solo `oprel`. El Java añade ramas con `"<"`, `">"`, `"<="`, `">="`, `"=="`, `"!="`, que nunca se activan porque el léxico emite `oprel`. Es código muerto. Lo mismo ocurre en `E\_EXPRESION` y `VERIFICACION\_TERMINO` (esas ramas extra no hacen daño, pero son redundantes).

# \- \*\*D5. Comillas del literal.\*\* El historial de `Lexico.java` (15/FEB/2016) dice que los literales van entre \*\*comillas sencillas\*\*, pero el programa de ejemplo usa \*\*comillas dobles\*\* (`"Juan"`). Además existe el token `caracter` (`'\&'`), que \*\*la gramática no contempla\*\*: usarlo da error sintáctico. Hay que confirmar contra la clase `Automata` qué reconoce realmente `literal`.

# 

# \*\*Código vs. requisitos de la práctica\*\*

# \- \*\*D6. Faltan nombres de autor\*\* en los procedimientos (solo la cabecera del archivo trae autores).

# \- \*\*D7. Faltan comentarios de producción\*\* en la mayoría de las ramas. Solo las ramas ε los traen, y no todas (por ejemplo `PARAMETROS` no tiene comentario).

# \- \*\*D8. Mensaje de `PARAMETROS` sin nombre de procedimiento.\*\* Dice `"Expresion invalida, se esperaba 'id' o ')'…"`, sin `\[PARAMETROS]`.

# \- \*\*D9. Errores de `emparejar` sin procedimiento.\*\* `errorEmparejar` no sabe qué procedimiento lo invocó, así que no cumple el requisito 3 en ese caso.

# \- \*\*D10. Mensaje no profesional\*\* en `analizar()`: `"SE TE FUE UNA LINEA, AQUI NO DEBE HABER NADA CHIKITIN…"`, más `System.out.println("Debugg")`, `"AAA"` y otros prints de depuración en `CLASE`, `emparejar` y `VERIFICACION\_TERMINO`.

# \- \*\*D11. Comentario de operadores desactualizado.\*\* El encabezado lista `OPERADOR\_ARITMETICO -> + - \* /`, pero en el código son los tres tokens `opsuma`, `signo`, `opmult`. No queda claro qué lexema es `signo`.

# 

# \*\*Léxico\*\*

# \- \*\*D12. Sin errores léxicos.\*\* Los caracteres no reconocidos se descartan silenciosamente. Por ejemplo, `let x: Int = 5 @ 3;` pasaría el léxico sin aviso y el parser vería `5 3`.

# \- \*\*D13. `MAXTOKENS = 500`\*\* sin validación de límite.

# \- \*\*D14. Código con `TOKREC = 17`\*\* pero `caresp`/`punto` no están documentados: no hay forma de saber qué lexemas cubre cada uno sin ver `Automata`.

# 

# \*\*Gramática (decisiones de diseño, no necesariamente errores)\*\*

# \- `x;` (id solo) es una instrucción válida, porque `ASIGNACION → ε`. Probablemente no se pretendía.

# \- Una condición sin operador relacional (`if (x)`) es válida.

# \- Sin precedencia entre `+ -` y `\* /`.

# 

# \---

# 

# \## 7. Pruebas del documento \[DOC]

# 

# \- \*\*Prueba 1\*\*: programa en blanco → resultado vacío en el documento. Según el código, con el BE vacío (`$`), `CLASE()` debería dar error `Se esperaba 'class'`; conviene confirmar.

# \- \*\*Prueba 2\*\*: programa mínimo válido → sin errores (hay una captura de pantalla en el documento).

# \- El programa de ejemplo (clase `Ejemplo` con `main`, `mostrarMensaje`, `mostrarEdad`, `mostrarDatos`, `sumar`, `calcularPrecio`) cubre: declaraciones con y sin inicializar, asignaciones con aritmética y paréntesis, invocaciones como instrucción y como término, `if/else`, `while`, condiciones con `>`, `<`, `>=`, `return` con valor, parámetros múltiples y tipo de retorno. Sirve como caso positivo de referencia.

# \- Sugerencias de casos negativos: falta `;`, falta `}`, `else` sin `if`, tipo en minúscula (`int`), parámetro sin tipo, `if` sin llaves, `a < b < c`, `x = a < b;`, cadena sin cerrar, texto después del `}` final de la clase.

