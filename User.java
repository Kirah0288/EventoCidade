import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    private String name;
    private String username;
    private String email;

    public User() {}

    public User(String name, String username, String email) {
        this.name = name.trim();
        this.username = username.trim();
        this.email = email.trim();
    }

    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s", name, username, email);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User)) return false;
        User u = (User) o;
        return Objects.equals(username, u.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
