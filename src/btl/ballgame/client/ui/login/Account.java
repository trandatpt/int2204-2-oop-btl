package btl.ballgame.client.ui.login;

public class Account {
    private String username;
    private String password;

    /*
     * tạo 1 account
     */
    Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void print() {
        System.out.println("Username: " + username);
        System.out.println("PassWord: " + password);
        System.out.println();
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}
