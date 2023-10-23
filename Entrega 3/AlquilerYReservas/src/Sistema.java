import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;

public class Sistema {

    private Inventario inventarioTotal;
    private HashMap<Integer, Alquiler> historial;
    private HashMap<String, Sede> sedesTotales;
    private HashMap<String, Usuario> usuarios;
    private HashMap<String, Integer> seguros;
    private HashMap<String, Object[]> tarifas;
    static private int recargoPorEntregarEnOtraSede = 500;

    static String pathUsuarios = "Entrega 3\\AlquilerYReservas\\Base de Datos\\Usuarios.txt";
    static String pathSedes = "Entrega 3\\AlquilerYReservas\\Base de Datos\\Sedes.txt";
    static String pathAutos = "Entrega 3\\AlquilerYReservas\\Base de Datos\\Autos.txt";
    static String pathSeguros = "Entrega 3\\AlquilerYReservas\\Base de Datos\\Seguros.txt";
    static String pathTarifas = "Entrega 3\\AlquilerYReservas\\Base de Datos\\Tarifas.txt";
    static String pathAlquileres = "Entrega 3\\AlquilerYReservas\\Base de Datos\\Alquileres.txt";

    public Sistema() {
        this.tarifas = new HashMap<>();
        this.seguros = new HashMap<>();
        this.usuarios = new HashMap<>();
        this.inventarioTotal = new Inventario();
        this.historial = new HashMap<>();
        this.sedesTotales = new HashMap<>();
        leerYGuardarDatos();
    }

    String[] haySuficientesAutos(String categoria, LocalDateTime fechaInicial, LocalDateTime fechaFinal) {
        String[] respuesta = new String[2];
        respuesta[0] = "";
        respuesta[1] = "";

        for (Sede sede : sedesTotales.values()) {

            String nombre = sede.getNombre();

            Inventario inventario = sedesTotales.get(nombre).getInventario();

            if (!inventario.getArrayCategorias().contains(categoria)) {
                continue;
            }

            ArrayList<Auto> autos = inventario.getAutosPorCategoria(categoria);

            for (Auto auto : autos) {
                boolean breakOutLoop = false;
                if (auto.isMantenimiento() || auto.isLimpieza()) {
                    continue;
                } else if (auto.isDisponible()) {
                    respuesta[0] = nombre;
                    respuesta[1] = auto.getPlaca();
                    return respuesta;
                } else {
                    for (Alquiler alquiler : auto.getHistorialDeAlquileres()) {
                        LocalDateTime fechaInicialReserva = alquiler.getFechaInicio();
                        LocalDateTime fechaFinalReserva = alquiler.getFechaFinal();

                        if (!(fechaFinal.isBefore(fechaInicialReserva) || fechaInicial.isAfter(fechaFinalReserva))) {
                            breakOutLoop = true;
                            break;
                        }
                    }
                    if (breakOutLoop) {
                        break;
                    }
                    respuesta[0] = nombre;
                    respuesta[1] = auto.getPlaca();
                    return respuesta;
                }
            }

        }
        return respuesta;
    }

    void leerYGuardarDatos() {
        cargarUsuarios("Entrega 3\\AlquilerYReservas\\Base de Datos\\Usuarios.txt");
        cargarSedes("Entrega 3\\AlquilerYReservas\\Base de Datos\\Sedes.txt");
        cargarAlquileres("Entrega 3\\AlquilerYReservas\\Base de Datos\\Alquileres.txt");
        cargarAutos("Entrega 3\\AlquilerYReservas\\Base de Datos\\Autos.txt");
        cargarTarifas("Entrega 3\\AlquilerYReservas\\Base de Datos\\Tarifas.txt");
        cargarSeguros("Entrega 3\\AlquilerYReservas\\Base de Datos\\Seguros.txt");
    }

    // FUNCIONES PARA CARGAR Y GUARDAR DATOS

    void cargarSeguros(String nombreArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea = br.readLine();
            while (linea != null) {
                String[] partes = linea.split(",");
                seguros.put(partes[0], Integer.parseInt(partes[1]));
                linea = br.readLine();
            }
            br.close();
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    void guardarSeguros() {
        String nombreArchivo = pathSeguros;
        try (BufferedWriter br = new BufferedWriter(new FileWriter(nombreArchivo))) {
            for (Map.Entry<String, Integer> entry : seguros.entrySet()) {
                String nombre = entry.getKey();
                Integer precio = entry.getValue();
                br.write(nombre + "," + precio);
                br.newLine();
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void cargarTarifas(String nombreArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea = br.readLine();
            while (linea != null) {
                String[] partes = linea.split(",");
                Object[] datosTarifa = new Object[3];
                datosTarifa[0] = Integer.parseInt(partes[1]);
                datosTarifa[1] = stringToDateTime(partes[2]);
                datosTarifa[2] = stringToDateTime(partes[3]);
                tarifas.put(partes[0], datosTarifa);
                linea = br.readLine();
            }
            br.close();
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    void guardarTarifas() {
        String nombreArchivo = pathTarifas;
        try (BufferedWriter br = new BufferedWriter(new FileWriter(nombreArchivo))) {
            for (Map.Entry<String, Object[]> entry : tarifas.entrySet()) {
                String nombre = entry.getKey();
                Integer precio = (Integer) entry.getValue()[0];
                LocalDateTime fecha1 = (LocalDateTime) entry.getValue()[1];
                LocalDateTime fecha2 = (LocalDateTime) entry.getValue()[2];

                br.write(nombre + "," + precio + "," +
                        dateTimeToString(fecha1) + "," + dateTimeToString(fecha2));
                br.newLine();
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void cargarUsuarios(String nombreArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea = br.readLine();
            while (linea != null) {
                String[] partes = linea.split(",");
                if (partes.length == 4) {

                    Empleado empleado = new Empleado(partes[0], partes[1], partes[2], partes[3]);
                    usuarios.put(empleado.getLogin(), empleado);
                } else {
                    Cliente cliente = new Cliente(partes[0], partes[1], partes[2], Integer.parseInt(partes[3]),
                            partes[4], new File(partes[5]), Integer.parseInt(partes[6]), partes[7],
                            stringToDateTime(partes[8]),
                            new File(partes[9]), partes[10], Integer.parseInt(partes[11]), stringToDateTime(partes[12]),
                            Integer.parseInt(partes[13]));

                    usuarios.put(cliente.getLogin(), cliente);
                }
                linea = br.readLine();
            }
            br.close();
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    void guardarUsuarios() {
        String nombreArchivo = pathUsuarios;
        try (BufferedWriter br = new BufferedWriter(new FileWriter(nombreArchivo))) {
            for (Usuario usuario : usuarios.values()) {

                if (usuario instanceof Cliente) {
                    Cliente cliente = (Cliente) usuario;
                    LicenciaConduccion licencia = cliente.getLicencia();
                    MedioDePago tarjeta = cliente.getTarjetaCredito();
                    br.write(cliente.getLogin() + "," + cliente.getPassword() + "," +
                            cliente.getNombre() + "," + cliente.getNumeroCelular() + "," +
                            cliente.getNacionalidad() + "," + cliente.getCedulaFoto().getPath()
                            + "," + licencia.getLicenciaID() + "," + licencia.getLicenciaPaisExpedicion()
                            + "," + dateTimeToString(licencia.getLicenciaFechaVen()) + "," +
                            licencia.getLicenciaFoto().getPath() + "," + tarjeta.getTipo() + "," +
                            tarjeta.getNumeroTarjeta() + "," + dateTimeToString(tarjeta.getFechaVencimiento())
                            + "," + tarjeta.getCodigoSeguridad()

                    );
                    br.newLine();
                } else {
                    Empleado empleado = (Empleado) usuario;
                    br.write(empleado.getLogin() + "," + empleado.getPassword()
                            + "," + empleado.getAutoridad() + "," + empleado.getSede());
                    br.newLine();
                }

            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void cargarSedes(String nombreArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea = br.readLine();
            while (linea != null) {
                String[] partes = linea.split(",");
                String[] horarios = new String[2];
                horarios[0] = partes[2].split("-")[0];
                horarios[1] = partes[2].split("-")[1];

                Sede sede = new Sede(partes[0], partes[1], horarios, (Empleado) getUsuario(partes[3]),
                        crearInventarioParaSede(partes[0]),
                        crearHashMapEmpleados(partes[4].split("-")));
                sedesTotales.put(sede.getNombre(), sede);
                linea = br.readLine();
            }
            br.close();
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    void guardarSedes() {
        String nombreArchivo = pathSedes;
        try (BufferedWriter br = new BufferedWriter(new FileWriter(nombreArchivo))) {
            for (Sede sede : sedesTotales.values()) {

                br.write(sede.getNombre() + "," + sede.getUbicacion() + "," +
                        sede.getHorarios()[0] + "-" + sede.getHorarios()[1] + "," +
                        sede.getAdminLocal().getLogin() + "," + crearListaEmpleados(sede.getEmpleados())

                );
                br.newLine();

            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void cargarAlquileres(String nombreArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea = br.readLine();
            while (linea != null) {
                String[] partes = linea.split(",");
                Alquiler alquiler = new Alquiler((Cliente) usuarios.get(partes[0]), partes[1],
                        sedesTotales.get(partes[2]), sedesTotales.get(partes[3]),
                        stringToDateTime(partes[4]), stringToDateTime(partes[5]), Integer.parseInt(partes[6]),
                        new File(partes[7]), crearArrayListDeConductores(partes[8]), partes[9] == "true");
                historial.put(Integer.parseInt(partes[6]), alquiler);
                linea = br.readLine();
            }
            br.close();
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    void guardarAlquileres() {
        String nombreArchivo = pathAlquileres;
        ArrayList<Alquiler> alquileres = new ArrayList<>(historial.values());
        try (BufferedWriter br = new BufferedWriter(new FileWriter(nombreArchivo))) {
            for (Alquiler alquiler : alquileres) {

                br.write(alquiler.getCliente().getLogin() + "," + alquiler.getEstado() + "," +
                        alquiler.getSedeInicio().getNombre() + "," + alquiler.getSedeFinal().getNombre()
                        + "," + dateTimeToString(alquiler.getFechaInicio()) + "," +
                        dateTimeToString(alquiler.getFechaFinal()) + "," + alquiler.getIdAlquiler()
                        + "," + alquiler.getFactura().getPath() + "," +
                        crearTextListDeConductores(alquiler.getConductoresAdicionales()) + "," +
                        alquiler.isBloqueoTarjeta());
                br.newLine();
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void cargarAutos(String nombreArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea = br.readLine();
            while (linea != null) {
                String[] partes = linea.split(",");

                Auto auto;
                if (partes[6].equals("null")) {
                    auto = new Auto(partes[0], partes[1], partes[2], partes[3], partes[4],
                            partes[5], // sin alquiler
                            sedesTotales.get(partes[7]), partes[8].equals("true"), partes[9].equals("true"),
                            partes[10].equals("true"), stringToDateTime(partes[11]),
                            crearArrayListaDeAlquileres(partes[12]));
                } else {
                    auto = new Auto(partes[0], partes[1], partes[2], partes[3], partes[4],
                            partes[5], historial.get(Integer.parseInt(partes[6])),
                            sedesTotales.get(partes[7]), partes[8].equals("true"), partes[9].equals("true"),
                            partes[10].equals("true"), stringToDateTime(partes[11]),
                            crearArrayListaDeAlquileres(partes[12]));
                }

                inventarioTotal.addAuto(auto);
                Inventario inventarioPorSede = sedesTotales.get(partes[7]).getInventario();
                inventarioPorSede.addAuto(auto);
                linea = br.readLine();
            }
            br.close();
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    void guardarAutos() {
        String nombreArchivo = pathAutos;
        ArrayList<Auto> autos = new ArrayList<>(inventarioTotal.getAutos().values());
        try (BufferedWriter br = new BufferedWriter(new FileWriter(nombreArchivo))) {
            for (Auto auto : autos) {
                System.out.println(auto.getPlaca());
                String alquilado;
                if (auto.getAlquilado() == null) {
                    alquilado = "null";
                } else {
                    alquilado = auto.getAlquilado().getIdAlquiler() + "";
                }

                br.write(auto.getCategoria() + "," + auto.getPlaca() + "," +
                        auto.getMarca() + "," + auto.getModelo() + "," + auto.getColor() + "," +
                        auto.getTransmision() + "," + alquilado
                        + "," + auto.getSedeActual().getNombre() + "," + auto.isMantenimiento()
                        + "," + auto.isLimpieza() + "," + auto.isDisponible() + "," +
                        dateTimeToString(auto.getFechaEstimadaDisponible()) + "," +
                        crearTextListDeAlquileres(auto.getHistorialDeAlquileres()));
                br.newLine();

            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // FUNCIONES AUXILIARES PARA LEER Y GUARDAR DATOS
    HashMap<String, Empleado> crearHashMapEmpleados(String[] empleadosText) {
        HashMap<String, Empleado> empleados = new HashMap<>();
        for (String login : empleadosText) {
            empleados.put(login, (Empleado) getUsuario(login));
        }
        return empleados;
    }

    String crearListaEmpleados(HashMap<String, Empleado> empleados) {
        String list = "";
        for (Empleado empleado : empleados.values()) {

            list += empleado.getLogin() + "-";
        }
        return list;
    }

    Inventario crearInventarioParaSede(String sede) {
        Inventario inventario = new Inventario();

        HashMap<String, HashMap<String, Auto>> categorias = inventario.getCategorias();

        for (String categoria : inventarioTotal.getArrayCategorias()) {
            HashMap<String, Auto> autosPorCategoria = new HashMap<>();
            categorias.put(categoria, autosPorCategoria);
        }

        HashMap<String, Auto> autosTotales = inventario.getAutos();

        for (Auto auto : inventarioTotal.getAutos().values()) {
            if (auto.getSedeActual().getNombre().equals(sede)) {
                autosTotales.put(auto.getPlaca(), auto);
                categorias.get(auto.getCategoria()).put(auto.getPlaca(), auto);
            }
        }

        return inventario;

    }

    ArrayList<LicenciaConduccion> crearArrayListDeConductores(String conductores) {
        ArrayList<LicenciaConduccion> conductoresAdicionales = new ArrayList<>();
        if (conductores.equals("")) {
            return conductoresAdicionales;
        }
        String[] licencias = conductores.split(";");
        for (String licenciaText : licencias) {
            System.out.println(licenciaText);
            String[] partes = licenciaText.split("_");
            LicenciaConduccion licencia = new LicenciaConduccion(Integer.parseInt(partes[0]), partes[1],
                    stringToDateTime(partes[2]), new File(partes[3]));
            conductoresAdicionales.add(licencia);
        }
        return conductoresAdicionales;
    }

    String crearTextListDeConductores(ArrayList<LicenciaConduccion> conductores) {
        String list = "";
        for (LicenciaConduccion licencia : conductores) {
            String miniList = licencia.getLicenciaID() + "_" + licencia.getLicenciaPaisExpedicion() + "_" +
                    dateTimeToString(licencia.getLicenciaFechaVen()) + "_" + licencia.getLicenciaFoto().getPath() + "_";
            list += miniList + ";";
        }
        return list;
    }

    ArrayList<Alquiler> crearArrayListaDeAlquileres(String alquileresTexto) {

        ArrayList<Alquiler> alquileres = new ArrayList<>();
        if (alquileresTexto.equals("nuncaAlquilado")) {
            return alquileres;
        }
        String[] partes = alquileresTexto.split("-");
        for (String idAlquiler : partes) {
            Alquiler alquiler = historial.get(Integer.parseInt(idAlquiler));
            alquileres.add(alquiler);
        }
        return alquileres;
    }

    String crearTextListDeAlquileres(ArrayList<Alquiler> alquileres) {
        String list = "";
        for (Alquiler alquiler : alquileres) {
            String id = alquiler.getIdAlquiler() + "";
            list += id + "-";
        }
        if (list.equals("")) {
            return "nuncaAlquilado";
        }
        return list;
    }

    LocalDateTime stringToDateTime(String fechaTexto) {

        if (fechaTexto.equals("sinFecha")) {
            return LocalDateTime.now();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        LocalDateTime fecha = LocalDateTime.parse(fechaTexto, formatter);

        return fecha;

    }

    static String dateTimeToString(LocalDateTime fecha) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return fecha.format(formatter);

    }

    public boolean existeUsuario(String login) {
        return usuarios.get(login) != null;
    }

    // OTRAS FUNCIONES
    public void agregarUsuario(Usuario usuario) {
        usuarios.put(usuario.getLogin(), usuario);
        guardarUsuarios();
    }

    public Usuario getUsuario(String login) {
        return usuarios.get(login);
    }

    public Inventario getInventario() {
        return this.inventarioTotal;
    }

    public HashMap<String, Usuario> getUsuarios() {
        return this.usuarios;
    }

    public HashMap<String, Sede> getSedes() {
        return this.sedesTotales;
    }

    public ArrayList<String> getNombresSedes() {
        return new ArrayList(sedesTotales.keySet());
    }

    public int getRecargo() {
        return this.recargoPorEntregarEnOtraSede;
    }

    public HashMap<String, Object[]> getTarifas() {
        return this.tarifas;
    }

    public HashMap<String, Integer> getSeguros() {
        return this.seguros;
    }

    public Alquiler getAlquiler(int idAlquiler) {
        return historial.get(idAlquiler);
    }

    public void agregarAuto(Auto auto) {
        inventarioTotal.addAuto(auto);
        Inventario inventarioSede = sedesTotales.get(auto.getSedeActual().getNombre()).getInventario();
        inventarioSede.addAuto(auto);
        guardarAutos();
    }

    public void eliminarAuto(Auto auto) {
        inventarioTotal.eliminarAuto(auto);
        String sede = auto.getSedeActual().getNombre();
        sedesTotales.get(sede).getInventario().eliminarAuto(auto);
        guardarAutos();
    }

    public void agregarSeguro(String nombre, int valor) {
        seguros.put(nombre, valor);
        guardarSeguros();
    }

    public void agregarTarifa(String nombre, int valor,
            LocalDateTime fechaInicial, LocalDateTime fechaFinal) {
        Object[] datosTarifa = new Object[3];
        datosTarifa[0] = valor;
        datosTarifa[1] = fechaInicial;
        datosTarifa[2] = fechaFinal;
        tarifas.put(nombre, datosTarifa);
        guardarTarifas();
    }

    public int getPrecioCategoria(String categoria) {
        return (Integer) tarifas.get(categoria)[0];
    }

    public int getNewAlquilerId() {
        return historial.size();
    }

    public void agregarAlquiler(Alquiler alquiler, String placa) {
        historial.put(alquiler.getIdAlquiler(), alquiler);
        Auto auto = inventarioTotal.getAuto(placa);
        auto.setDisponibilidad(false);
        auto.agregarAlquiler(alquiler);
        guardarAlquileres();
    }

}
