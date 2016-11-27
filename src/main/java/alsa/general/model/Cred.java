package alsa.general.model;

/**
 * Created by alsa on 27.11.2016.
 */
public class Cred {
    public static final Cred AJJTJT = new Cred("Ajj"+"tjt", "AbcdefQWpqdG");
    private String username;
    private String password;

    public Cred(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
