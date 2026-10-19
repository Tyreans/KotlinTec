/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:        SEMESTRE: ______________            HORA: ______________ HRS
 *:                                   
 *:               
 *:        # Casos de prueba JUnit  para el Analizador Sintactico                 
 *:                           
 *: Archivo       : SinctacticoTest.java
 *: Autor         : Fernando Gil   
 *: 
 *: Fecha         : 13/Oct/2026
 *: Compilador    : Java JDK 21,  JUnit 4.13.2 y Hamcrest 1.3
 *: Descripción   : Casos de prueba con bateria de programas correctos e incorrectos 
 *:                 escritos en lenguaje KotlinTec.   
 *:           	      
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *: 13/Oct/2026 FGil               -Se crearon los casos de prueba para KotlinTec 2026  
 *:-----------------------------------------------------------------------------
 */

package pruebas_sintactico;

import compilador.Compilador;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author FGIL.0
 */
public class SintacticoTest {
    Compilador        cmp = new Compilador ();
    File              carpeta = null;    // Carpeta que contiene los programas de prueba
    ArrayList<String> archDeProgramas = new ArrayList<> (); // Nombres de los archivos de programa
    
    public SintacticoTest() {
      // El constructor usa Java Swing para mostrar el dialogo estandar OPEN para 
      // solicitar al usuario la carpeta que contiene los archivos de programa de prueba,
      // la carpeta seleccionada se guarda en la variable de clase "carpeta".
      // Luego se leen los nombres de todos los archivos con extensión ".kt" en dicha
      // carpeta y se colocan en el arrayList "archDeProgramas".
      
      JFileChooser fileChooser = new JFileChooser(new File("test"));
      fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      fileChooser.setDialogTitle("Seleccione la carpeta con los programas de prueba (.kt)");
      
      int resultado = fileChooser.showOpenDialog(null);
      if (resultado == JFileChooser.APPROVE_OPTION) {
          carpeta = fileChooser.getSelectedFile();
          
          // Filtrar y obtener los nombres de los archivos con extensión ".kt"
          File[] archivosKt = carpeta.listFiles((dir, name) -> name.toLowerCase().endsWith(".kt"));
          if (archivosKt != null) {
              for (File f : archivosKt) {
                  archDeProgramas.add(f.getName());
              }
          }
      }
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
        System.out.println ( "\n\n:: Fin de la prueba" );
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    //--------------------------------------------------------------------------
    
    @Test
    public void programasTest () {
        
        int i = 0;
        // Por cada uno de los programas de prueba...
        for ( String archDePrograma : archDeProgramas ) { 
            i++;
            System.out.println ( "********* Prueba #" + i + ": " + archDePrograma + " *********" );
            
            String programa = codigoDePrograma ( archDePrograma );  

            // Declaración y evaluación de las banderas según el nombre del archivo
            boolean isPrograma_OKTest = archDePrograma.contains("OKTest");
            boolean isPrograma_ErrTest = archDePrograma.contains("ErrTest");

            System.out.print   ( ">>>>> RESULTADO ESPERADO: " );
            System.out.println ( ( isPrograma_OKTest  ) ? "0 mensajes de error."  : 
                                 ( isPrograma_ErrTest ) ? "1+ mensajes de error." : 
                                                          "<indeterminado>" );
            
            // Ejecutar el Lexico y Sintactico 
            cmp.analizarLexico (programa );
            cmp.analizarSintaxis();

            if ( isPrograma_OKTest ) {
              // Debe contabilizar 0 errores y el primer error registrado debe ser "" 
              assertTrue ( "Prueba #" + i, 
                            cmp.getTotErrores ( Compilador.ERR_SINTACTICO ) == 0 );              
            } else if ( isPrograma_ErrTest ) {
              // Debe contabilizar al menos 1 error
              assertTrue ( "Prueba #" + i, 
                            cmp.getTotErrores ( Compilador.ERR_SINTACTICO ) > 0 );
            }
        }
    }  
    
    //--------------------------------------------------------------------------
  
    public String codigoDePrograma ( String archivo ) {
      // Este método abre el archivo de texto dado por el argumento "archivo", ubicado en la
      // carpeta local dada por la variable de clase "carpeta", lee su contenido y
      // lo devuelve como un String.
      
      File archivoCompleto = new File(carpeta, archivo);
      try {
          return new String(Files.readAllBytes(archivoCompleto.toPath()));
      } catch (IOException e) {
          e.printStackTrace();
          return "";
      }
    }

    //--------------------------------------------------------------------------

}