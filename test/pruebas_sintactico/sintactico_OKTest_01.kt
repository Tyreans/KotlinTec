class Ejemplo {

    fun main() {

        let edad: Int = 25;
        let precio: Double = 10.5;
        let nombre: String = 'Juan';

        let limite: Int = 100;

        edad = edad + 5 * 2;
        precio = (precio + 5.0) * 2.0;

        mostrarMensaje();

        mostrarEdad(edad);

        mostrarDatos(nombre, edad);

        let resultado: Int = sumar(edad, limite);

        if (resultado > 50) {
            resultado = resultado + 10;
        } else {
            resultado = resultado * 2;
        }

        while (resultado < 200) {
            resultado = resultado + 10;
        }

        let total: Double = calcularPrecio(precio, 2.5, edad);

        if (total >= 100.0) {
            mostrarMensaje();
        }
    }


    fun mostrarMensaje() {
        let mensaje: String = 'Hola';
    }


    fun mostrarEdad(edad: Int) {
        let copia: Int = edad;
    }


    fun mostrarDatos(nombre: String, edad: Int) {
        let numero: Int = edad + 1;
        let texto: String = nombre;
    }


    fun sumar(a: Int, b: Int): Int {
        let resultado: Int = a + b;
        return resultado;
    }


    fun calcularPrecio(precio: Double, incremento: Double, cantidad: Int): Double {
        let resultado: Double = precio + incremento * cantidad;
        return resultado;
    }
}
