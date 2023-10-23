import java.io.File;
import java.time.LocalDateTime;

public class Cliente implements Usuario {

    private String login;
    private String password;
    private String nombre;
    private int numeroCelular;
    private String nacionalidad;
    private File cedulaFoto;

    // LICENCIA
    private LicenciaConduccion licencia;
    // Datos para la licencia
    // private int licenciaID;
    // private String licenciaPaisExpedicion;
    // private LocalDateTime licenciaFechaVen;
    // private File licenciaFoto;

    // METODO DE PAGO
    private MedioDePago tarjetaCredito;
    // Datos para el medio de pago
    // private String tipo;
    // private int numeroTarjeta;
    // private LocalDateTime fechaVencimiento;
    // private int codigoSeguridad;

    public Cliente(String login, String password, String nombre, int numeroCelular, String nacionalidad,
            File cedulaFoto,
            int licenciaID, String licenciaPaisExpedicion, LocalDateTime licenciaFechaVen,
            File licenciaFoto, String tipo, int numeroTarjeta, LocalDateTime fechaVencimiento,
            int codigoSeguridad) {
        this.login = login;
        this.password = password;
        this.nombre = nombre;
        this.numeroCelular = numeroCelular;
        this.nacionalidad = nacionalidad;
        this.cedulaFoto = cedulaFoto;

        this.licencia = new LicenciaConduccion(licenciaID, licenciaPaisExpedicion, licenciaFechaVen, licenciaFoto);

        this.tarjetaCredito = new MedioDePago(tipo, numeroTarjeta, fechaVencimiento, codigoSeguridad);

    }

    public Cliente(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public String getLogin() {
        return this.login;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public String getNombre() {
        return nombre;
    }

    public int getNumeroCelular() {
        return numeroCelular;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public File getCedulaFoto() {
        return cedulaFoto;
    }

    public LicenciaConduccion getLicencia() {
        return licencia;
    }

    public MedioDePago getTarjetaCredito() {
        return tarjetaCredito;
    }

}
