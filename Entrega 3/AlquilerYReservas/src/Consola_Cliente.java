import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.awt.Desktop;

public class Consola_Cliente {

    public static Cliente cliente;

    public static void mostrarOpciones(Sistema sistema, Cliente clienteActual) {
        cliente = clienteActual;
        System.out.println("\n");
        System.out.println("Hola! Bienvenido al portal de reservas ^-^");
        while (true) {
            System.out.println("\n");
            System.out.println("Ingresa una de las siguientes opciones:");
            System.out.println("    0 --> Volver al menu principal");
            System.out.println("    1 --> Escoje una sede para reservar un auto");
            System.out.println("\n");
            String respuesta = input("--> ");

            if (respuesta.equals("0")) {
                break;
            } else if (respuesta.equals("1")) {
                hacerReserva(sistema);
            } else {
                System.out.println("\n");
                System.out.println("Escoge una opcion valida >:v");
            }

        }
    }

    public static void hacerReserva(Sistema sistema) {
        String respuesta;

        // ATRIBUTOS ALQUILER
        // Cliente cliente;
        String estado = "Activo";
        Sede sedeInicio;
        Sede sedeFinal;
        LocalDateTime fechaInicio;
        LocalDateTime fechaFinal;
        int idAlquiler = sistema.getNewAlquilerId();
        File factura;
        ArrayList<LicenciaConduccion> conductoresAdicionales = new ArrayList<>();
        boolean bloqueoTarjeta = false;

        // --------------------------------------------------------
        // ESCOGER SEDE
        System.out.println("\n");
        System.out.println("Escoge en cual sede quieres reservar:");
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
        String sede = sedes.get(numeroSede);
        sedeInicio = sistema.getSedes().get(sede);
        sedeFinal = sedeInicio;

        // FIN-----------------------------------------------------

        // --------------------------------------------------------
        // ESCOGER AUTO
        System.out.println("\n");
        System.out.println("Muy bien! Ahora escoge un tipo de auto uwu");
        ArrayList<String> categorias = sistema.getInventario().getArrayCategorias();
        for (int i = 0; i < categorias.size(); i++) {
            String categoria = categorias.get(i);

            System.out.println("    " + i + " --> " + categoria + "  $" + sistema.getPrecioCategoria(categoria));
        }
        System.out.println("A la derecha encuentras tarifa diaria");
        System.out.println("Escoge cual quieres:");

        int numeroCategoria;
        while (true) {
            System.out.println("\n");
            respuesta = input("--> ");
            try {
                numeroCategoria = Integer.parseInt(respuesta);
                if (0 <= numeroCategoria && categorias.size() - 1 >= numeroCategoria) {
                    break;
                } else {
                    System.out.println("Escoge un opcion valida >:v");
                }
            } catch (Exception e) {
                System.out.println("Escoge un opcion valida >:v");
                continue;
            }
        }
        String categoria = categorias.get(numeroCategoria);
        int tarifaCategoria = (Integer) sistema.getTarifas().get(categoria)[0];
        // FIN-----------------------------------------------------

        // --------------------------------------------------------
        // ESCOGER FECHA
        System.out.println("\n");
        System.out.println("Muy bien! Ahora vamos a escoger un rango de fechas ^-^");
        System.out.println("Empezemos por la FECHA INICIAL:");
        System.out.println("Ingresa que dia quieres reservar en el formato 'yyyy-MM-dd HH:mm' ej: '2003-09-05'");

        while (true) {
            System.out.println("\n");
            String fechaTexto = input("--> ");

            try {
                fechaInicio = sistema.stringToDateTime(fechaTexto + " 00:00");
                break;
            } catch (Exception e) {
                System.out.println("\n");
                System.out.println("ERROR! Tienes que respetar el formato >:v");
                System.out.println("Intentalo de nuevo ^-^");
            }
        }

        System.out.println("Muy bien! Ahora escoge la FECHA FINAL ^-^");

        while (true) {
            System.out.println("\n");
            String fechaTexto = input("--> ");

            try {
                fechaFinal = sistema.stringToDateTime(fechaTexto + " 00:00");
                break;
            } catch (Exception e) {
                System.out.println("\n");
                System.out.println("ERROR! Tienes que respetar el formato >:v");
                System.out.println("Intentalo de nuevo ^-^");
            }
        }

        // FIN-----------------------------------------------------

        // --------------------------------------------------------
        // ESCOGER SEGURO
        System.out.println("\n");
        System.out.println("Muy bien! Ahora escoge un seguro");
        ArrayList<String> seguros = new ArrayList<>(sistema.getSeguros().keySet());
        for (int i = 0; i < seguros.size(); i++) {
            String seguro = seguros.get(i);
            System.out.println("    " + i + " --> " + seguro + " $" + sistema.getSeguros().get(seguro));
        }

        System.out.println("A la derecha encuentras tarifa diaria");
        System.out.println("Escoge cual quieres:");

        int numeroSeguro;
        while (true) {
            System.out.println("\n");
            respuesta = input("--> ");
            try {
                numeroSeguro = Integer.parseInt(respuesta);
                if (0 <= numeroSeguro && seguros.size() - 1 >= numeroSeguro) {
                    break;
                } else {
                    System.out.println("Escoge un opcion valida >:v");
                }
            } catch (Exception e) {
                System.out.println("Escoge un opcion valida >:v");
                continue;
            }
        }
        String seguro = seguros.get(numeroSeguro);
        int tarifaSeguro = sistema.getSeguros().get(seguro);
        // FIN-----------------------------------------------------

        System.out.println("\n");
        System.out.println("Revisando disponibilidad en Sede...");

        String[] reserva = sistema.haySuficientesAutos(categoria, fechaInicio, fechaFinal);
        String sedeAutoDisponible = reserva[0];
        String placaAutoDisponible = reserva[1];

        if (sedeAutoDisponible.equals("")) {
            System.out.println("\n");
            System.out.println("Lo sentimos :(");
            System.out.println("Parece que no hay autos disponibles en ninguna sede en tu rango de fechas");
            System.out.println("Intentalo de nuevo cambiando el rango o seleccionando otra categoria!");
            System.out.println("\n");
            System.out.println("Ingresa cualquier tecla para voler al menu principal");
            System.out.println("\n");
            respuesta = input("--> ");

        } else {
            int recargo = 0;
            System.out.println("\n");
            System.out.println("Muy bien! Encontramos un auto disponible en tu rango de fecha y categoria");
            System.out.println("Quieres entregar tu auto en otra sede por un costo adicional? Escoge 'Si' o 'No'");

            while (true) {
                System.out.println("\n");
                respuesta = input("--> ").toLowerCase();

                if (respuesta.equals("si")) {
                    System.out.println("\n");
                    System.out.println("Escoge una entre todas nuestras sedes:");
                    for (int i = 0; i < sedes.size(); i++) {

                        System.out.println("    " + i + " --> " + sedes.get(i));
                    }
                    System.out.println("");
                    System.out.println("Escoge cual quieres:");

                    while (true) {
                        System.out.println("\n");
                        respuesta = input("--> ");
                        try {
                            numeroSede = Integer.parseInt(respuesta);
                            if (0 <= numeroSede && sistema.getNombresSedes().size() - 1 >= numeroSede) {
                                sedeFinal = sistema.getSedes().get(sedes.get(numeroSede));
                                recargo = sistema.getRecargo();
                                break;
                            }
                        } catch (Exception e) {
                            System.out.println("Escoge un opcion valida >:v");
                            continue;
                        }
                    }
                    break;
                } else if (respuesta.equals("no")) {
                    break;
                } else {
                    System.out.println("Escoge una opcion valida >:v");
                }

            }

            int tarifaDiaraTotal = tarifaCategoria + tarifaSeguro;
            factura = Alquiler.generarYGuardarFactura(fechaInicio, fechaFinal, categoria, tarifaCategoria, seguro,
                    tarifaSeguro, tarifaDiaraTotal, recargo, idAlquiler, cliente, sedeInicio.getNombre(),
                    sedeFinal.getNombre());
            Alquiler alquiler = new Alquiler(cliente, estado, sedeInicio, sedeFinal, fechaInicio, fechaFinal,
                    idAlquiler, factura, conductoresAdicionales, bloqueoTarjeta);

            sistema.agregarAlquiler(alquiler, placaAutoDisponible);

            System.out.println("Muy bien! se realizara un cargo a tu tarjeta por el 30%");
            System.out.println("El dia que recojas el auto se te cobrara el costo restante");
            System.out.println("Puedes reclamar tu carro desde las 8 am hasta las 11 pm");
            System.out.println("Presiona cualquier tecla para ver la factura ^-^");
            System.out.println("\n");
            respuesta = input("--> ");
            abrirFacutra(factura);
            System.out.println("\n");
            System.out.println("Ingresa cualquier tecla para voler al menu principal");
            System.out.println("\n");
            respuesta = input("--> ");

        }

    }

    private static void abrirFacutra(File factura) {
        String rutaArchivo = factura.getPath();

        try {
            Desktop desktop = Desktop.getDesktop();

            if (desktop.isSupported(Desktop.Action.OPEN)) {
                File archivo = new File(rutaArchivo);

                desktop.open(archivo);
            } else {
                System.out.println("Lo sentimos, parace que en tu computador no se pueden abrir archivos :(");
                System.out.println(
                        "Puedes consultar la factura en la carpeta 'Facturas' que se encuentra dentro del directorio 'Base de Datos'");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
