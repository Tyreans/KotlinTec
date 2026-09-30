# Funciones de los no terminales

Pseudocodigo del analizador sintactico predictivo recursivo.

```text
procedure
begin

***// CASO 1 CLASE***

if preAnalisis = "class" then
   emparejar("class")
   emparejar("id")
   emparejar("{")
   FUNCIONES()
   emparejar("}")
else
   error("Se esperaba class")

***// CASO 2 FUNCIONES***

if preAnalisis = "fun" then
   FUNCION()
   FUNCIONES()
else if preAnalisis = "}" then
   // epsilon
else
   error("Se esperaba fun o }")

***// CASO 3 FUNCION***

if preAnalisis = "fun" then
   emparejar("fun")
   emparejar("id")
   emparejar("(")
   PARAMETROS()
   emparejar(")")
   TIPO_RETORNO()
   emparejar("{")
   INSTRUCCIONES()
   emparejar("}")
else
   error("Se esperaba fun")

***// CASO 4 PARAMETROS***

if preAnalisis = "id" then
   PARAMETRO()
   E_PARAMETRO()
else if preAnalisis = ")" then
   // epsilon
else
   error("Se esperaba id o )")

***// CASO 5 E_PARAMETRO***

if preAnalisis = "," then
   emparejar(",")
   PARAMETRO()
   E_PARAMETRO()
else if preAnalisis = ")" then
   // epsilon
else
   error("Se esperaba , o )")

***// CASO 6 PARAMETRO***

if preAnalisis = "id" then
   emparejar("id")
   emparejar(":")
   TIPO()
else
   error("Se esperaba id")

***// CASO 7 TIPO_RETORNO***

if preAnalisis = ":" then
   emparejar(":")
   TIPO()
else if preAnalisis = "{" then
   // epsilon
else
   error("Se esperaba : o {")

***// CASO 8 INSTRUCCIONES***

if preAnalisis = "let" or preAnalisis = "id"
   or preAnalisis = "if" or preAnalisis = "while"
   or preAnalisis = "return" then
   INSTRUCCION()
   INSTRUCCIONES()
else if preAnalisis = "}" then
   // epsilon
else
   error("Se esperaba una instruccion o }")

***// CASO 9 INSTRUCCION***

if preAnalisis = "let" then
   DECLARACION()
else if preAnalisis = "id" then
   emparejar("id")
   VERIFICACION_ID()
else if preAnalisis = "if" then
   IF()
else if preAnalisis = "while" then
   WHILE()
else if preAnalisis = "return" then
   RETURN()
else
   error("Se esperaba let, id, if, while o return")

***// CASO 10 IF***

if preAnalisis = "if" then
   emparejar("if")
   emparejar("(")
   CONDICION()
   emparejar(")")
   emparejar("{")
   INSTRUCCIONES()
   emparejar("}")
   E_IF()
else
   error("Se esperaba if")

***// CASO 11 E_IF***

if preAnalisis = "else" then
   emparejar("else")
   E_IF_PRIMA()
else if preAnalisis = "let" or preAnalisis = "id"
   or preAnalisis = "if" or preAnalisis = "while"
   or preAnalisis = "return" or preAnalisis = "}" then
   // epsilon
else
   error("Se esperaba else o el fin del bloque")

***// CASO 12 E_IF_PRIMA***

if preAnalisis = "{" then
   emparejar("{")
   INSTRUCCIONES()
   emparejar("}")
else if preAnalisis = "if" then
   IF()
else
   error("Se esperaba { o if")

***// CASO 13 WHILE***

if preAnalisis = "while" then
   emparejar("while")
   emparejar("(")
   CONDICION()
   emparejar(")")
   emparejar("{")
   INSTRUCCIONES()
   emparejar("}")
else
   error("Se esperaba while")

***// CASO 14 CONDICION***

if preAnalisis = "id" or preAnalisis = "num"
   or preAnalisis = "num.num" or preAnalisis = "literal"
   or preAnalisis = "(" then
   COMPARACION()
   E_CONDICION()
else
   error("Se esperaba el inicio de una expresion")

***// CASO 15 E_CONDICION***

if preAnalisis = "|" then
   emparejar("|")
   COMPARACION()
   E_CONDICION()
else if preAnalisis = "&" then
   emparejar("&")
   COMPARACION()
   E_CONDICION()
else if preAnalisis = ")" then
   // epsilon
else
   error("Se esperaba |, & o )")

***// CASO 16 COMPARACION***

if preAnalisis = "id" or preAnalisis = "num"
   or preAnalisis = "num.num" or preAnalisis = "literal"
   or preAnalisis = "(" then
   EXPRESION()
   E_COMPARACION()
else
   error("Se esperaba el inicio de una expresion")

***// CASO 17 E_COMPARACION***

if preAnalisis = "oprel" then
   emparejar("oprel")
   EXPRESION()
else if preAnalisis = ")" or preAnalisis = "|"
   or preAnalisis = "&" then
   // epsilon
else
   error("Se esperaba un operador relacional, |, & o )")

***// CASO 18 DECLARACION***

if preAnalisis = "let" then
   emparejar("let")
   emparejar("id")
   emparejar(":")
   TIPO()
   ASIGNACION()
   emparejar(";")
else
   error("Se esperaba let")

***// CASO 19 ASIGNACION***

if preAnalisis = "opasig" then
   emparejar("opasig")
   EXPRESION()
else if preAnalisis = ";" then
   // epsilon
else
   error("Se esperaba opasig o ;")

***// CASO 20 VERIFICACION_ID***

if preAnalisis = "opasig" or preAnalisis = ";" then
   ASIGNACION()
   emparejar(";")
else if preAnalisis = "(" then
   INVOCACION()
   emparejar(";")
else
   error("Se esperaba opasig, ; o (")

***// CASO 21 INVOCACION***

if preAnalisis = "(" then
   emparejar("(")
   ARGUMENTOS()
   emparejar(")")
else
   error("Se esperaba (")

***// CASO 22 RETURN***

if preAnalisis = "return" then
   emparejar("return")
   E_RETURN()
   emparejar(";")
else
   error("Se esperaba return")

***// CASO 23 E_RETURN***

if preAnalisis = "id" or preAnalisis = "num"
   or preAnalisis = "num.num" or preAnalisis = "literal"
   or preAnalisis = "(" then
   EXPRESION()
else if preAnalisis = ";" then
   // epsilon
else
   error("Se esperaba una expresion o ;")

***// CASO 24 ARGUMENTOS***

if preAnalisis = "id" or preAnalisis = "num"
   or preAnalisis = "num.num" or preAnalisis = "literal"
   or preAnalisis = "(" then
   ARGUMENTO()
   E_ARGUMENTOS()
else if preAnalisis = ")" then
   // epsilon
else
   error("Se esperaba una expresion o )")

***// CASO 25 E_ARGUMENTOS***

if preAnalisis = "," then
   emparejar(",")
   ARGUMENTO()
   E_ARGUMENTOS()
else if preAnalisis = ")" then
   // epsilon
else
   error("Se esperaba , o )")

***// CASO 26 ARGUMENTO***

if preAnalisis = "id" or preAnalisis = "num"
   or preAnalisis = "num.num" or preAnalisis = "literal"
   or preAnalisis = "(" then
   EXPRESION()
else
   error("Se esperaba el inicio de una expresion")

***// CASO 27 EXPRESION***

if preAnalisis = "id" or preAnalisis = "num"
   or preAnalisis = "num.num" or preAnalisis = "literal"
   or preAnalisis = "(" then
   TERMINO()
   E_EXPRESION()
else
   error("Se esperaba id, num, num.num, literal o (")

***// CASO 28 E_EXPRESION***

if preAnalisis = "opsuma" then
   emparejar("opsuma")
   TERMINO()
   E_EXPRESION()
else if preAnalisis = "signo" then
   emparejar("signo")
   TERMINO()
   E_EXPRESION()
else if preAnalisis = "opmult" then
   emparejar("opmult")
   TERMINO()
   E_EXPRESION()
else if preAnalisis = "oprel" or preAnalisis = ")"
   or preAnalisis = "|" or preAnalisis = "&"
   or preAnalisis = ";" or preAnalisis = "," then
   // epsilon
else
   error("Se esperaba un operador aritmetico o el fin de la expresion")

***// CASO 29 TERMINO***

if preAnalisis = "id" then
   emparejar("id")
   VERIFICACION_TERMINO()
else if preAnalisis = "num" then
   emparejar("num")
else if preAnalisis = "num.num" then
   emparejar("num.num")
else if preAnalisis = "literal" then
   emparejar("literal")
else if preAnalisis = "(" then
   emparejar("(")
   EXPRESION()
   emparejar(")")
else
   error("Se esperaba id, num, num.num, literal o (")

***// CASO 30 VERIFICACION_TERMINO***

if preAnalisis = "(" then
   INVOCACION()
else if preAnalisis = "opsuma" or preAnalisis = "signo"
   or preAnalisis = "opmult" or preAnalisis = "oprel"
   or preAnalisis = ")" or preAnalisis = "|"
   or preAnalisis = "&" or preAnalisis = ";"
   or preAnalisis = "," then
   // epsilon
else
   error("Se esperaba ( o el fin del termino")

***// CASO 31 TIPO***

if preAnalisis = "Int" then
   emparejar("Int")
else if preAnalisis = "Double" then
   emparejar("Double")
else if preAnalisis = "String" then
   emparejar("String")
else
   error("Se esperaba Int, Double o String")

end
```

## Correspondencia de tokens

- `id`: identificador.
- `num`: constante entera.
- `num.num`: constante real.
- `literal`: literal de texto.
- `oprel`: operador relacional como `=`, `==`, `!=`, `<`, `>`, `<=` o `>=`, segun el lexema reconocido.
- `opasig`: operador de asignacion.
- `opsuma`: operador de suma o palabra reservada relacionada con `or`.
- `signo`: signo `-`.
- `opmult`: operador de multiplicacion o division.
- `caresp`: caracteres especiales; en el buffer se almacenan con su lexema, por ejemplo `,`, `(`, `)` y `;`.
