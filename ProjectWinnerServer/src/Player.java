/*
Papadakis Fragkiskos
 */
import java.io.Serializable;

public class Player implements Serializable { //antikeimeno Player gia sundesi,register,katoxirwsi nikis

    private String username;
    private String password;

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Player() {

    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setUsername(String username) {
        username = this.username;
    }

    public void setPassword(String password) {
        password = this.password;
    }

    @Override
    public String toString() {

        return username;

    }
}
