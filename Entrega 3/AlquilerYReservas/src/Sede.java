import java.util.HashMap;

public class Sede {

    private String nombre;
    private String ubicacion;
    private String[] horarios;
    private Empleado adminLocal;
    private Inventario inventario;
    private HashMap<String, Empleado> empleados;

    public Sede(String nombre, String ubicacion, String[] horarios, Empleado adminLocal,
            Inventario inventario, HashMap<String, Empleado> empleados) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.horarios = horarios;
        this.adminLocal = adminLocal;
        this.inventario = inventario;
        this.empleados = empleados;
    }

    String getNombre() {
        return this.nombre;
    }

    Inventario getInventario() {
        return this.inventario;
    }

    String getUbicacion() {
        return this.ubicacion;
    }

    String[] getHorarios() {
        return this.horarios;
    }

    Empleado getAdminLocal() {
        return this.adminLocal;
    }

    HashMap<String, Empleado> getEmpleados() {
        return this.empleados;
    }

}
