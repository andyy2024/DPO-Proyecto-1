import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Consola_Empleado {

    static Empleado empleado;

    public static void mostrarOpciones(Sistema sistema, Empleado empleadoActual) {

        System.out.println(empleado.getLogin());

        System.out.println("\n");
        System.out.println("Hola! Bienvenido a la consola empleado ^-^");
        while (true) {
            System.out.println("\n");
            System.out.println("Ingresa una de las siguientes opciones:");
            System.out.println("    0 --> Volver al menu principal");
            System.out.println("    1 --> Registrar conductores adicionales");
            System.out.println("    2 --> Mandar Auto a limpieza");
            System.out.println("    3 --> Mandar Auto a mantenimiento");
            System.out.println("    4 --> Registrar pago Final");
            System.out.println("    5 --> Crear empleado (disponible solo si eres Administrador Local de tu sede)");
            System.out.println("\n");
            String respuesta = input("--> ");

            if (respuesta.equals("0")) {
                break;
            } else if (respuesta.equals("1")) {
                registrarConductoresAdicionales(sistema);
            } else if (respuesta.equals("2")) {
                mandarALimpieza(sistema);
            } else if (respuesta.equals("3")) {
                mandarAMantenimiento(sistema);
            } else if (respuesta.equals("4")) {
                registrarPagoFinal(sistema);
            } else if (respuesta.equals("5")) {
                crearEmpleado(sistema, empleado);
            }
        }
    }

    public static void crearEmpleado(Sistema sistema, Empleado empleado) {
        String respuesta;
        String login;
        String password;
        String autoridad;
        String sede;

        if ((empleado.getAutoridad().equals("adminLocal")) || (empleado.getAutoridad().equals("adminGlobal"))) {

            System.out.println("\n");
            System.out.println("Ingresa el login");

            while (true) {
                System.out.println("\n");
                respuesta = input("--> ");

                if (!sistema.existeUsuario(respuesta)) {
                    login = respuesta;
                    break;
                } else {
                    System.out.println("Ese usuario ya existe, intentalo de nuevo :(");
                }
            }

            System.out.println("\n");
            System.out.println("Ingresa un password");
            System.out.println("\n");
            password = input("--> ");

            System.out.println("\n");
            System.out.println("Ingresa su autoridad");
            System.out.println("    1 --> Administrador local de la Sede");
            System.out.println("    2 --> Empleado de la Sede");
            System.out.println("Escoge una");
            while (true) {
                System.out.println("\n");
                respuesta = input("--> ");

                if (respuesta.equals("1")) {
                    autoridad = "adminLocal";
                    break;
                } else if (respuesta.equals("2")) {
                    autoridad = "empleado";
                    break;
                } else {
                    System.out.println("Escoge una opcion valida >:v");
                }
            }

            System.out.println("\n");
            System.out.println("Escoge la sede donde lo quieres ubicar");
            ArrayList<String> sedes = new ArrayList<>(sistema.getSedes().keySet());
            for (int i = 0; i < sedes.size(); i++) {
                System.out.println("    " + i + " --> " + sedes.get(i));
            }
            int numeroSede;
            while (true) {
                System.out.println("\n");
                respuesta = input("--> ");
                try {
                    numeroSede = Integer.parseInt(respuesta);
                    if (0 <= numeroSede && sedes.size() - 1 >= numeroSede) {
                        break;
                    } else {
                        System.out.println("\n");
                        System.out.println("Escoge un opcion valida >:v");
                    }
                } catch (Exception e) {
                    System.out.println("\n");
                    System.out.println("Escoge un opcion valida >:v");
                    continue;
                }
            }

            sede = sedes.get(numeroSede);

            Empleado nuevoEmpleado = new Empleado(login, password, autoridad, sede);
            sistema.agregarUsuario(nuevoEmpleado);
            System.out.println("\n");
            System.out.println("Muy bien se agrego el nuevo empleado!");
            System.out.println("Ingresa cualquier caracter para volver al menu");
            System.out.println("\n");
            String nada = input("--> ");

        } else {
            System.out.println("\n");
            System.out.println(
                    "Lo sentimos, parece que no eres Admnistrador Local ni Global, eres un simple empleado :(");
        }
    }

    public static void registrarConductoresAdicionales(Sistema sistema) {
        String respuesta;

        int licenciaID;
        String licenciaPaisExpedicion;
        LocalDateTime licenciaFechaVen;
        File licenciaFoto;

        System.out.println("\n");
        System.out.println("Para registrar los conductores, necesitamos los datos de su licencia de conduccion!");

        System.out.println("Ingresa el ID de la licencia");

        while (true) {
            System.out.println("\n");
            respuesta = input("--> ");
            try {
                licenciaID = Integer.parseInt(respuesta);
                break;
            } catch (Exception e) {
                System.out.println("El valor debe ser un numero >:v");
            }
        }

        System.out.println("\n");
        System.out.println("Ingresa el pais de expedicion");
        System.out.println("\n");
        respuesta = input("--> ");
        licenciaPaisExpedicion = respuesta;

        System.out.println("\n");
        System.out.println("Ingresa la fecha de vencimiento");
        System.out.println("    -> formato 'yyyy-MM-dd' ej: '2003-09-05'");
        while (true) {
            System.out.println("\n");
            String fechaTexto = input("--> ");

            try {
                licenciaFechaVen = sistema.stringToDateTime(fechaTexto + " 00:00");
                break;
            } catch (Exception e) {
                System.out.println("\n");
                System.out.println("ERROR! Tienes que respetar el formato >:v");
                System.out.println("Intentalo de nuevo ^-^");
            }
        }

        System.out.println("\n");
        System.out.println("Escoge la foto de tu licencia");
        Login.escogerFoto();
        licenciaFoto = new File(Login.getPath());

        LicenciaConduccion licencia = new LicenciaConduccion(licenciaID, licenciaPaisExpedicion, licenciaFechaVen,
                licenciaFoto);

        System.out.println("\n");
        System.out.println("Muy bien, ahora escoge el ID de alquiler para agregar los conductores adicionales");
        System.out.println("Se encuentra en la facutra del cliente");
        Alquiler alquiler;
        while (true) {
            System.out.println("\n");
            respuesta = input("--> ");
            try {
                int idAlquiler = Integer.parseInt(respuesta);
                alquiler = sistema.getAlquiler(idAlquiler);
                break;
            } catch (Exception e) {
                System.out.println("ERROR! no encontramos ese alquiler");
                System.out.println("Intentalo de nuevo ^-^");
            }
        }
        alquiler.agregarConductor(licencia);
        System.out.println("\n");
        System.out.println("Muy bien se ha agregado la licencia!");
        System.out.println("Ingresa cualquier caracter para volver al menu");
        System.out.println("\n");
        String nada = input("--> ");
        sistema.guardarAlquileres();
    }

    public static void mandarALimpieza(Sistema sistema) {
        System.out.println("\n");
        System.out.println("Digita la placa del auto");

        Auto auto;
        while (true) {
            System.out.println("\n");
            String placa = input("--> ");
            if (sistema.getInventario().getAuto(placa) != null) {
                auto = sistema.getInventario().getAuto(placa);
                break;
            } else {
                System.out.println("No encontramos ese numero de placa");
                System.out.println("Intentalo de nuevo ^-^");
            }
        }

        auto.setDisponibilidad(false);
        auto.setLimpieza(true);

        System.out.println("\n");
        System.out.println("Ahora ingresa una fecha estimada cuando estara disponible el Auto");
        System.out.println("    -> formato 'yyyy-MM-dd' ej: '2003-09-05'");
        LocalDateTime fecha;
        while (true) {
            System.out.println("\n");
            String fechaTexto = input("--> ");

            try {
                fecha = sistema.stringToDateTime(fechaTexto + " 00:00");
                break;
            } catch (Exception e) {
                System.out.println("\n");
                System.out.println("ERROR! Tienes que respetar el formato >:v");
                System.out.println("Intentalo de nuevo ^-^");
            }
        }
        auto.setFechaEstimadaDisponible(fecha);
        sistema.guardarAutos();
        System.out.println("\n");
        System.out.println("Muy bien, Auto enviado a limpieza con exito!");
        System.out.println(
                "RECUERDA: Cuando pase la fecha, el sistema volvera el auto disponible otra vez (requiere reiniciar la aplicacion)");
        System.out.println("Ingresa cualquier caracter para volver al menu");
        System.out.println("\n");
        String nada = input("--> ");
    }

    public static void mandarAMantenimiento(Sistema sistema) {
        System.out.println("\n");
        System.out.println("Digita la placa del auto");

        Auto auto;
        while (true) {
            System.out.println("\n");
            String placa = input("--> ");
            if (sistema.getInventario().getAuto(placa) != null) {
                auto = sistema.getInventario().getAuto(placa);
                break;
            } else {
                System.out.println("No encontramos ese numero de placa");
                System.out.println("Intentalo de nuevo ^-^");
            }
        }

        auto.setDisponibilidad(false);
        auto.setMantenimieto(true);

        System.out.println("\n");
        System.out.println("Ahora ingresa una fecha estimada cuando estara disponible el Auto");
        System.out.println("    -> formato 'yyyy-MM-dd' ej: '2003-09-05'");
        LocalDateTime fecha;
        while (true) {
            System.out.println("\n");
            String fechaTexto = input("--> ");

            try {
                fecha = sistema.stringToDateTime(fechaTexto + " 00:00");
                break;
            } catch (Exception e) {
                System.out.println("\n");
                System.out.println("ERROR! Tienes que respetar el formato >:v");
                System.out.println("Intentalo de nuevo ^-^");
            }
        }
        auto.setFechaEstimadaDisponible(fecha);
        sistema.guardarAutos();
        System.out.println("\n");
        System.out.println("Muy bien, Auto enviado a mantenimiento con exito!");
        System.out.println(
                "RECUERDA: Cuando pase la fecha, el sistema volvera el auto disponible otra vez (requiere reiniciar la aplicacion)");
        System.out.println("Ingresa cualquier caracter para volver al menu");
        System.out.println("\n");
        String nada = input("--> ");
    }

    public static void registrarPagoFinal(Sistema sistema) {
        String respuesta;
        System.out.println("\n");
        System.out.println("Si un cliente vino a reclamar su auto, puedes registrar el pago restaste (el 70%)");
        System.out.println("Se realizara un cobro automatico a la terjate del cliente");
        System.out.println("Por favor digita el ID de alquiler");

        Alquiler alquiler;
        while (true) {
            System.out.println("\n");
            respuesta = input("--> ");
            try {
                int idAlquiler = Integer.parseInt(respuesta);
                alquiler = sistema.getAlquiler(idAlquiler);
                break;
            } catch (Exception e) {
                System.out.println("ERROR! no encontramos ese alquiler");
                System.out.println("Intentalo de nuevo ^-^");
            }
        }

        System.out.println("Muy bien, registramos el pago!");
        System.out.println("Ingresa cualquier caracter para volver al menu");
        System.out.println("\n");
        String nada = input("--> ");
    }

    public static void levantarBloqueo(Sistema sistema) {
        String respuesta;
        System.out.println("\n");
        System.out.println("Si estas reciviendo un auto, puedes levantar el bloque de la tarjeta al cliente");
        System.out.println("Por favor digita el ID de alquiler");

        Alquiler alquiler;
        while (true) {
            System.out.println("\n");
            respuesta = input("--> ");
            try {
                int idAlquiler = Integer.parseInt(respuesta);
                alquiler = sistema.getAlquiler(idAlquiler);
                break;
            } catch (Exception e) {
                System.out.println("ERROR! no encontramos ese alquiler");
                System.out.println("Intentalo de nuevo ^-^");
            }
        }
        alquiler.setBloqueoTarjeta(false);
        System.out.println("Muy bien, levantamos el bloqueo!");
        System.out.println("Ingresa cualquier caracter para volver al menu");
        System.out.println("\n");
        String nada = input("--> ");
        sistema.guardarAlquileres();
    }

    public static String input(String mensaje) {
        try {
            System.out.print(mensaje);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            return reader.readLine();
        } catch (IOException e) {
            System.out.println("Error leyendo de la consola");
            e.printStackTrace();
        }
        return null;
    }

}
