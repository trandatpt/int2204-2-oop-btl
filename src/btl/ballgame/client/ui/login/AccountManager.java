package btl.ballgame.client.ui.login;

import java.util.ArrayList;
import java.util.List;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;

public class AccountManager {
    private static List<Account> accounts = new ArrayList<>();
    private static final String file = "int2204-2-oop-btl/src/btl/ballgame/client/ui/resources/Accounts.txt";
    public static void addAccount(String account, String pass) {
        accounts.add(new Account(account, pass));
    }

    public static List<Account> getAllAccounts() {
        return AccountManager.accounts;
    }

    /*
     * lưu vào file Accounts.txt để khi off game thì vẫn lưu
     */
    public static void saveAccounts() {
        Out out = new Out(file);
        out.println("Username*****Password");
        for (Account a : accounts) {
            out.println(a.getUsername() + "*****" + a.getPassword());
        }
        out.close();
    }

    /*
     * khi mở game ra thì sẽ add vào lại
     */
    public static void loadAccounts() {
        In in = new In(file);
        String first = in.readLine();
        while (!in.isEmpty()) {
            String line = in.readLine();
            String[] parts = line.split("\\*\\*\\*\\*\\*");
            if (parts.length == 2) {
                addAccount(parts[0], parts[1]);
            }
        }
    }

    public static void clearAccounts() {
        accounts.clear();
        Out out = new Out("src/resources/Accounts.txt");
        out.close();
    }
}
