
public class Empleado implements Usuario {

    private String login;
    private String password;
    private String autoridad;
    private String sede;

    public Empleado(String login, String password, String autoridad, String sede) {
        this.login = login;
        this.password = password;
        this.autoridad = autoridad;
        this.sede = sede;
    }

    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getAutoridad() {
        return autoridad;
    }

    public String getSede() {
        return sede;
    }

}
