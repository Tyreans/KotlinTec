/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:                  SEMESTRE: ___________    HORA: ___________ HRS
 *:                                   
 *:               
 *:         Clase con la funcionalidad del Analizador Sintactico
 *                 
 *:                           
 *: Archivo       : SintacticoSemantico.java
 *: Autor         : Fernando Gil  ( Estructura general de la clase  )
 *:                 Grupo de Lenguajes y Automatas II ( Procedures  )
 *: Fecha         : 03/SEP/2014
 *: Compilador    : Java JDK 7
 *: Descripción   : Esta clase implementa un parser descendente del tipo 
 *:                 Predictivo Recursivo. Se forma por un metodo por cada simbolo
 *:                 No-Terminal de la gramatica mas el metodo emparejar ().
 *:                 El analisis empieza invocando al metodo del simbolo inicial.
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *: 22/Feb/2015 FGil                -Se mejoro errorEmparejar () para mostrar el
 *:                                 numero de linea en el codigo fuente donde 
 *:                                 ocurrio el error.
 *: 08/Sep/2015 FGil                -Se dejo lista para iniciar un nuevo analizador
 *:                                 sintactico.
 *:-----------------------------------------------------------------------------
 */
package compilador;

import javax.swing.JOptionPane;

public class SintacticoSemantico {

    private Compilador cmp;
    private boolean    analizarSemantica = false;
    private String     preAnalisis;
    
    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //

    public SintacticoSemantico(Compilador c) {
        cmp = c;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // Metodo que inicia la ejecucion del analisis sintactico predictivo.
    // analizarSemantica : true = realiza el analisis semantico a la par del sintactico
    //                     false= realiza solo el analisis sintactico sin comprobacion semantica

    public void analizar(boolean analizarSemantica) {
        this.analizarSemantica = analizarSemantica;
        preAnalisis = cmp.be.preAnalisis.complex;

        // * * *   INVOCAR AQUI EL PROCEDURE DEL SIMBOLO INICIAL   * * *
        
        System.out.println("Debugg");
        CLASE();
        
        if(!preAnalisis.equals("$") && cmp.me.getTotErrSintacticos() == 0){
            error("SE TE FUE UNA LINEA, AQUI NO DEBE HABER NADA CHIKITIN. No Linea "+ cmp.be.preAnalisis.getNumLinea());
        }
    }

    //--------------------------------------------------------------------------

    private void emparejar(String t) {
        System.out.println(t);
        if (cmp.be.preAnalisis.complex.equals(t)) {
            cmp.be.siguiente();
            preAnalisis = cmp.be.preAnalisis.complex;            
        } else {
            errorEmparejar( t, cmp.be.preAnalisis.lexema, cmp.be.preAnalisis.numLinea );
        }
    }
    
    //--------------------------------------------------------------------------
    // Metodo para devolver un error al emparejar
    //--------------------------------------------------------------------------
 
    private void errorEmparejar(String _token, String _lexema, int numLinea ) {
        String msjError = "";

        if (_token.equals("id")) {
            msjError += "Se esperaba un identificador";
        } else if (_token.equals("num")) {
            msjError += "Se esperaba una constante entera";
        } else if (_token.equals("num.num")) {
            msjError += "Se esperaba una constante real";
        } else if (_token.equals("literal")) {
            msjError += "Se esperaba una literal";
        } else if (_token.equals("oparit")) {
            msjError += "Se esperaba un operador aritmetico";
        } else if (_token.equals("oprel")) {
            msjError += "Se esperaba un operador relacional";
        } else if (_token.equals("opasig")) {
            msjError += "Se esperaba operador de asignacion";
        } else {
            msjError += "Se esperaba " + _token;
        }
        msjError += " se encontró " + ( _lexema.equals ( "$" )? "fin de archivo" : _lexema ) + 
                    ". Linea " + numLinea;        // FGil: Se agregó el numero de linea

        cmp.me.error(Compilador.ERR_SINTACTICO, msjError);
    }

    // Fin de ErrorEmparejar
    //--------------------------------------------------------------------------
    // Metodo para mostrar un error sintactico

    private void error(String _descripError) {
        cmp.me.error(cmp.ERR_SINTACTICO, _descripError);
    }

    // Fin de error
    //--------------------------------------------------------------------------
    //  *  *   *   *    PEGAR AQUI EL CODIGO DE LOS PROCEDURES  *  *  *  *
    //--------------------------------------------------------------------------

    // ============================================================
    //  Analizador Sintáctico Descendente Recursivo - KotlinTec
    //  31 funciones, una por cada no terminal de la gramática LL(1)
    //
    //  Convenciones de tokens:
    //   - OPERADOR_ARITMETICO -> +  -  *  /
    //   - OPERADOR_LOGICO     -> <  >  <=  >=  ==  !=
    //   - UNION_LOGICA (or/and) -> |  &
    // ============================================================

    // 1) CLASE -> class id { FUNCIONES }
    private void CLASE() {
        System.out.println(preAnalisis);
        if (preAnalisis.equals("class")) {
            System.out.println("AAA");
            emparejar("class");
            emparejar("id");
            emparejar("{");
            FUNCIONES();
            emparejar("}");
        } else {
            error("[CLASE] Se esperaba 'class'. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 2) FUNCIONES -> FUNCION FUNCIONES | ϵ
    private void FUNCIONES() {
        if (preAnalisis.equals("fun")) {
            FUNCION();
            FUNCIONES();
        } else if (preAnalisis.equals("}")) {
            // FUNCIONES -> ϵ
        } else {
            error("[FUNCIONES] Se esperaba 'fun' o '}'. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 3) FUNCION -> fun id ( PARAMETROS ) TIPO_RETORNO { INSTRUCCIONES }
    private void FUNCION() {
        if (preAnalisis.equals("fun")) {
            emparejar("fun");
            emparejar("id");
            emparejar("(");
            PARAMETROS();
            emparejar(")");
            TIPO_RETORNO();
            emparejar("{");
            INSTRUCCIONES();
            emparejar("}");
        } else {
            error("[FUNCION] Se esperaba 'fun'. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 4) PARAMETROS -> PARAMETRO E_PARAMETRO | ϵ
    private void PARAMETROS() {
        if (preAnalisis.equals("id")) {
            PARAMETRO();
            E_PARAMETRO();
        } else if (preAnalisis.equals(")")) {

        } else {
            error("Expresion invalida, se esperaba 'id' o ')'. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 5) E_PARAMETRO -> , PARAMETRO E_PARAMETRO | ϵ
    private void E_PARAMETRO() {
        if (preAnalisis.equals(",")) {
            emparejar(",");
            PARAMETRO();
            E_PARAMETRO();
        } else if (preAnalisis.equals(")")) {
            // E_PARAMETRO -> ϵ
        } else {
            error("[E_PARAMETRO] Se esperaba ',' o ')'. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 6) PARAMETRO -> id : TIPO
    private void PARAMETRO() {
        if (preAnalisis.equals("id")) {
            emparejar("id");
            emparejar(":");
            TIPO();
        } else {
            error("[PARAMETRO] Se esperaba 'id'. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 7) TIPO_RETORNO -> : TIPO | ϵ
    private void TIPO_RETORNO() {
        if (preAnalisis.equals(":")) {
            emparejar(":");
            TIPO();
        } else if (preAnalisis.equals("{")) {
            // TIPO_RETORNO -> ϵ
        } else {
            error("[TIPO_RETORNO] Se esperaba ':' o '{'. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 8) INSTRUCCIONES -> INSTRUCCION INSTRUCCIONES | ϵ
    private void INSTRUCCIONES() {
        if (preAnalisis.equals("let") || preAnalisis.equals("id") || preAnalisis.equals("if")
                || preAnalisis.equals("while") || preAnalisis.equals("return")) {
            INSTRUCCION();
            INSTRUCCIONES();
        } else if (preAnalisis.equals("}")) {
            // INSTRUCCIONES -> ϵ
        } else {
            error("[INSTRUCCIONES] Se esperaba una instrucción o '}'. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 9) INSTRUCCION -> DECLARACION | id VERIFICACION_ID | IF | WHILE | RETURN
    private void INSTRUCCION() {
        if (preAnalisis.equals("let")) {
            DECLARACION();
        } else if (preAnalisis.equals("id")) {
            emparejar("id");
            VERIFICACION_ID();
        } else if (preAnalisis.equals("if")) {
            IF();
        } else if (preAnalisis.equals("while")) {
            WHILE();
        } else if (preAnalisis.equals("return")) {
            RETURN();
        } else {
            error("[INSTRUCCION] Se esperaba 'let', 'id', 'if', 'while' o 'return'. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 10) IF -> if ( CONDICION ) { INSTRUCCIONES } E_IF
    private void IF() {
        if (preAnalisis.equals("if")) {
            emparejar("if");
            emparejar("(");
            CONDICION();
            emparejar(")");
            emparejar("{");
            INSTRUCCIONES();
            emparejar("}");
            E_IF();
        } else {
            error("[IF] Se esperaba 'if'. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 11) E_IF -> else E_IF_PRIMA | ϵ
    private void E_IF() {
        if (preAnalisis.equals("else")) {
            emparejar("else");
            E_IF_PRIMA();
        } else if (preAnalisis.equals("let") || preAnalisis.equals("id") || preAnalisis.equals("if")
                || preAnalisis.equals("while") || preAnalisis.equals("return") || preAnalisis.equals("}")) {
            // E_IF -> ϵ
        } else {
            error("[E_IF] Se esperaba 'else' o el fin del bloque. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 12) E_IF_PRIMA -> { INSTRUCCIONES } | IF
    private void E_IF_PRIMA() {
        if (preAnalisis.equals("{")) {
            emparejar("{");
            INSTRUCCIONES();
            emparejar("}");
        } else if (preAnalisis.equals("if")) {
            IF();
        } else {
            error("[E_IF_PRIMA] Se esperaba '{' o 'if'. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 13) WHILE -> while ( CONDICION ) { INSTRUCCIONES }
    private void WHILE() {
        if (preAnalisis.equals("while")) {
            emparejar("while");
            emparejar("(");
            CONDICION();
            emparejar(")");
            emparejar("{");
            INSTRUCCIONES();
            emparejar("}");
        } else {
            error("[WHILE] Se esperaba 'while'. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 14) CONDICION -> COMPARACION E_CONDICION
    private void CONDICION() {
        if (preAnalisis.equals("id") || preAnalisis.equals("num") || preAnalisis.equals("num.num")
                || preAnalisis.equals("literal") || preAnalisis.equals("(")) {
            COMPARACION();
            E_CONDICION();
        } else {
            error("[CONDICION] Se esperaba el inicio de una expresión. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 15) E_CONDICION -> UNION_LOGICA COMPARACION E_CONDICION | ϵ
    private void E_CONDICION() {
        if (preAnalisis.equals("|")) {
            emparejar("|");
            COMPARACION();
            E_CONDICION();
        } else if (preAnalisis.equals("&")) {
            emparejar("&");
            COMPARACION();
            E_CONDICION();
        } else if (preAnalisis.equals(")")) {
            // E_CONDICION -> ϵ
        } else {
            error("[E_CONDICION] Se esperaba '|', '&' o ')'. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 16) COMPARACION -> EXPRESION E_COMPARACION
    private void COMPARACION() {
        if (preAnalisis.equals("id") || preAnalisis.equals("num") || preAnalisis.equals("num.num")
                || preAnalisis.equals("literal") || preAnalisis.equals("(")) {
            EXPRESION();
            E_COMPARACION();
        } else {
            error("[COMPARACION] Se esperaba el inicio de una expresión. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 17) E_COMPARACION -> OPERADOR_LOGICO EXPRESION | ϵ
    private void E_COMPARACION() {
        if (preAnalisis.equals("oprel")) {
            emparejar("oprel");
            EXPRESION();
        } else if (preAnalisis.equals("<")) {
            emparejar("<");
            EXPRESION();
        } else if (preAnalisis.equals(">")) {
            emparejar(">");
            EXPRESION();
        } else if (preAnalisis.equals("<=")) {
            emparejar("<=");
            EXPRESION();
        } else if (preAnalisis.equals(">=")) {
            emparejar(">=");
            EXPRESION();
        } else if (preAnalisis.equals("==")) {
            emparejar("==");
            EXPRESION();
        } else if (preAnalisis.equals("!=")) {
            emparejar("!=");
            EXPRESION();
        } else if (preAnalisis.equals("|") || preAnalisis.equals("&") || preAnalisis.equals(")")) {
            // E_COMPARACION -> ϵ
        } else {
            error("[E_COMPARACION] Se esperaba un operador relacional, '|', '&' o ')'. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 18) DECLARACION -> let id : TIPO ASIGNACION ;
    private void DECLARACION() {
        if (preAnalisis.equals("let")) {
            emparejar("let");
            emparejar("id");
            emparejar(":");
            TIPO();
            ASIGNACION();
            emparejar(";");
        } else {
            error("[DECLARACION] Se esperaba 'let'. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 19) ASIGNACION -> opasig EXPRESION | ϵ
    private void ASIGNACION() {
        if (preAnalisis.equals("opasig")) {
            emparejar("opasig");
            EXPRESION();
        } else if (preAnalisis.equals(";")) {
            // ASIGNACION -> ϵ
        } else {
            error("[ASIGNACION] Se esperaba 'opasig' o ';'. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 20) VERIFICACION_ID -> ASIGNACION ; | INVOCACION ;
    private void VERIFICACION_ID() {
        if (preAnalisis.equals("opasig") || preAnalisis.equals(";")) {
            ASIGNACION();
            emparejar(";");
        } else if (preAnalisis.equals("(")) {
            INVOCACION();
            emparejar(";");
        } else {
            error("[VERIFICACION_ID] Se esperaba 'opasig', ';' o '('. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 21) INVOCACION -> ( ARGUMENTOS )
    private void INVOCACION() {
        if (preAnalisis.equals("(")) {
            emparejar("(");
            ARGUMENTOS();
            emparejar(")");
        } else {
            error("[INVOCACION] Se esperaba '('. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 22) RETURN -> return E_RETURN ;
    private void RETURN() {
        if (preAnalisis.equals("return")) {
            emparejar("return");
            E_RETURN();
            emparejar(";");
        } else {
            error("[RETURN] Se esperaba 'return'. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 23) E_RETURN -> EXPRESION | ϵ
    private void E_RETURN() {
        if (preAnalisis.equals("id") || preAnalisis.equals("num") || preAnalisis.equals("num.num")
                || preAnalisis.equals("literal") || preAnalisis.equals("(")) {
            EXPRESION();
        } else if (preAnalisis.equals(";")) {
            // E_RETURN -> ϵ
        } else {
            error("[E_RETURN] Se esperaba una expresión o ';'. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 24) ARGUMENTOS -> ARGUMENTO E_ARGUMENTOS | ϵ
    private void ARGUMENTOS() {
        if (preAnalisis.equals("id") || preAnalisis.equals("num") || preAnalisis.equals("num.num")
                || preAnalisis.equals("literal") || preAnalisis.equals("(")) {
            ARGUMENTO();
            E_ARGUMENTOS();
        } else if (preAnalisis.equals(")")) {
            // ARGUMENTOS -> ϵ
        } else {
            error("[ARGUMENTOS] Se esperaba una expresión o ')'. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 25) E_ARGUMENTOS -> , ARGUMENTO E_ARGUMENTOS | ϵ
    private void E_ARGUMENTOS() {
        if (preAnalisis.equals(",")) {
            emparejar(",");
            ARGUMENTO();
            E_ARGUMENTOS();
        } else if (preAnalisis.equals(")")) {
            // E_ARGUMENTOS -> ϵ
        } else {
            error("[E_ARGUMENTOS] Se esperaba ',' o ')'. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 26) ARGUMENTO -> EXPRESION
    private void ARGUMENTO() {
        if (preAnalisis.equals("id") || preAnalisis.equals("num") || preAnalisis.equals("num.num")
                || preAnalisis.equals("literal") || preAnalisis.equals("(")) {
            EXPRESION();
        } else {
            error("[ARGUMENTO] Se esperaba el inicio de una expresión. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 27) EXPRESION -> TERMINO E_EXPRESION
    private void EXPRESION() {
        if (preAnalisis.equals("id") || preAnalisis.equals("num") || preAnalisis.equals("num.num")
                || preAnalisis.equals("literal") || preAnalisis.equals("(")) {
            TERMINO();
            E_EXPRESION();
        } else {
            error("[EXPRESION] Se esperaba 'id', 'num', 'num.num', 'literal' o '('. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 28) E_EXPRESION -> OPERADOR_ARITMETICO TERMINO E_EXPRESION | ϵ
    private void E_EXPRESION() {
        if (preAnalisis.equals("opsuma")) {
            emparejar("opsuma");
            TERMINO();
            E_EXPRESION();
        } else if (preAnalisis.equals("signo")) {
            emparejar("signo");
            TERMINO();
            E_EXPRESION();
        } else if (preAnalisis.equals("opmult")) {
            emparejar("opmult");
            TERMINO();
            E_EXPRESION();
        } else if (preAnalisis.equals("<") || preAnalisis.equals(">") || preAnalisis.equals("<=")
                || preAnalisis.equals(">=") || preAnalisis.equals("==") || preAnalisis.equals("!=")
                || preAnalisis.equals("oprel") || preAnalisis.equals("|") || preAnalisis.equals("&") || preAnalisis.equals(")")
                || preAnalisis.equals(";") || preAnalisis.equals(",")) {
            // E_EXPRESION -> ϵ
        } else {
            error("[E_EXPRESION] Se esperaba un operador aritmético o el fin de la expresión. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 29) TERMINO -> id VERIFICACION_TERMINO | num | num.num | literal | ( EXPRESION )
    private void TERMINO() {
        if (preAnalisis.equals("id")) {
            emparejar("id");
            VERIFICACION_TERMINO();
        } else if (preAnalisis.equals("num")) {
            emparejar("num");
        } else if (preAnalisis.equals("num.num")) {
            emparejar("num.num");
        } else if (preAnalisis.equals("literal")) {
            emparejar("literal");
        } else if (preAnalisis.equals("(")) {
            emparejar("(");
            EXPRESION();
            emparejar(")");
        } else {
            error("[TERMINO] Se esperaba 'id', 'num', 'num.num', 'literal' o '('. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 30) VERIFICACION_TERMINO -> ϵ | INVOCACION
    private void VERIFICACION_TERMINO() {
        System.out.println(" ######### Token actual: "
        + cmp.be.preAnalisis.complex
        + " Lexema: "
        + cmp.be.preAnalisis.lexema);
        
        if (preAnalisis.equals("(")) {
            INVOCACION();
        } else if (preAnalisis.equals("opsuma") || preAnalisis.equals("signo") || preAnalisis.equals("opmult")
                || preAnalisis.equals("<") || preAnalisis.equals(">") || preAnalisis.equals("<=") || preAnalisis.equals(">=")
                || preAnalisis.equals("==") || preAnalisis.equals("!=")
                || preAnalisis.equals("oprel")
                || preAnalisis.equals("|") || preAnalisis.equals("&")
                || preAnalisis.equals(")") || preAnalisis.equals(";") || preAnalisis.equals(",")) {
            // VERIFICACION_TERMINO -> ϵ
        } else {
            error("[VERIFICACION_TERMINO] Se esperaba '(' o el fin del término. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // 31) TIPO -> Int | Double | String
    private void TIPO() {
        if (preAnalisis.equals("Int")) {
            emparejar("Int");
        } else if (preAnalisis.equals("Double")) {
            emparejar("Double");
        } else if (preAnalisis.equals("String")) {
            emparejar("String");
        } else {
            error("[TIPO] Se esperaba 'Int', 'Double' o 'String'. No Linea " + cmp.be.preAnalisis.getNumLinea());
        }
    }
}
//------------------------------------------------------------------------------
//::