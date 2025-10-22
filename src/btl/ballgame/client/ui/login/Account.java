package btl.ballgame.client.ui.login;

public class Account {
    private String name;
    private String username;
    private String password;

    /*
     * tạo 1 account
     */
    Account(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }

    public void print() {
        System.out.println("Username: " + username);
        System.out.println("PassWord: " + password);
        System.out.println();
    }

    public String getName() {
        return this.name;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}
