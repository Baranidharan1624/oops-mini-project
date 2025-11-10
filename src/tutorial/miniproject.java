package tutorial;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

// ===============================
// Module 1: Core Finance Entities
// ===============================
abstract class Transaction {
    protected double amount;
    protected String description;

    public Transaction(double amount, String description) {
        this.amount = amount;
        this.description = description;
    }

    public abstract void process();
}

class IncomeTransaction extends Transaction {
    public IncomeTransaction(double amount, String description) {
        super(amount, description);
    }

    @Override
    public void process() {
        System.out.println("Income Added: " + amount + " (" + description + ")");
    }
}

class ExpenseTransaction extends Transaction {
    public ExpenseTransaction(double amount, String description) {
        super(amount, description);
    }

    @Override
    public void process() {
        System.out.println("Expense Recorded: " + amount + " (" + description + ")");
    }
}

class Account {
    private String userName;
    private double balance;
    private static int totalAccounts = 0;

    public Account(String userName, double balance) {
        this.userName = userName;
        this.balance = balance;
        totalAccounts++;
    }

    public void addBalance(double amount) {
        balance += amount;
    }

    public void deductBalance(double amount) throws InsufficientFundsException {
        FinanceUtility.validateTransaction(amount, balance);
        balance -= amount;
    }

    public double getBalance() {
        return balance;
    }

    public static int getTotalAccounts() {
        return totalAccounts;
    }
}

// ===============================
// Module 2: Exception Handling & Utility
// ===============================
class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

class FinanceUtility {
    public static void validateTransaction(double amount, double balance)
            throws InsufficientFundsException {
        if (amount > balance) {
            throw new InsufficientFundsException("Not enough funds available.");
        }
    }
}

// ===============================
// Module 3: Generics & Multi-threading
// ===============================
class Repository<T> {
    private java.util.List<T> items = new java.util.ArrayList<>();

    public void add(T item) {
        items.add(item);
    }

    public T get(int index) {
        return items.get(index);
    }

    public java.util.List<T> getAll() {
        return items;
    }
}

class AutoSaveThread extends Thread {
    private String threadName;

    public AutoSaveThread(String name) {
        this.threadName = name;
    }

    public void run() {
        System.out.println(threadName + " is auto-saving finance data...");
        try {
            Thread.sleep(1000);
            System.out.println(threadName + " finished auto-saving.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// ===============================
// Module 4: Networking & Database
// ===============================
class FinanceServer {
    public static void startServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(5678);
        System.out.println("Finance Server started on port 5678");
        System.out.println("Waiting for client connection (press Ctrl+C to stop server)...");
        // To prevent blocking forever, timeout added
        serverSocket.setSoTimeout(5000);
        try {
            Socket client = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            out.println("Connected to Finance Server");
            client.close();
        } catch (SocketTimeoutException e) {
            System.out.println("No client connected. Closing server.");
        }
        serverSocket.close();
    }
}

class DBConnector {
    public static Connection connect() throws SQLException {
        // Update these details if you have a database configured
        String url = "jdbc:mysql://localhost:3306/finance";
        String user = "root";
        String pass = "password";
        return DriverManager.getConnection(url, user, pass);
    }

    public static void addTransaction(String type, double amount, String desc) throws SQLException {
        // Commented out actual DB part to make runnable without DB
        /*
        Connection conn = connect();
        PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO transactions (type, amount, description) VALUES (?, ?, ?)");
        stmt.setString(1, type);
        stmt.setDouble(2, amount);
        stmt.setString(3, desc);
        stmt.executeUpdate();
        conn.close();
        */
        System.out.println("Transaction [" + type + "] added: " + amount + " (" + desc + ")");
    }
}

// ===============================
// Module 5: GUI Interfaces
// ===============================
class FinanceForm extends JFrame {
    JTextField amountField, descField;
    JButton incomeButton, expenseButton;

    public FinanceForm() {
        setTitle("Finance Manager");
        setLayout(new FlowLayout());
        amountField = new JTextField(10);
        descField = new JTextField(10);
        incomeButton = new JButton("Add Income");
        expenseButton = new JButton("Add Expense");

        add(new JLabel("Amount:"));
        add(amountField);
        add(new JLabel("Description:"));
        add(descField);
        add(incomeButton);
        add(expenseButton);

        incomeButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                String desc = descField.getText();
                DBConnector.addTransaction("Income", amount, desc);
                JOptionPane.showMessageDialog(this, "Income Added: " + amount);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        expenseButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                String desc = descField.getText();
                DBConnector.addTransaction("Expense", amount, desc);
                JOptionPane.showMessageDialog(this, "Expense Added: " + amount);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        setSize(350, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}

// ===============================
// Main Class
// ===============================
public class miniproject {
    public static void main(String[] args) {
        try {
            Account acc = new Account("User1", 1000);
            Transaction income = new IncomeTransaction(500, "Salary");
            Transaction expense = new ExpenseTransaction(200, "Groceries");

            income.process();
            acc.addBalance(500);

            expense.process();
            acc.deductBalance(200);

            System.out.println("Final Balance: " + acc.getBalance());
            System.out.println("Total Accounts: " + Account.getTotalAccounts());

            Repository<String> repo = new Repository<>();
            repo.add("Finance Report 1");
            repo.add("Finance Report 2");
            System.out.println("Repository items: " + repo.getAll());

            AutoSaveThread save1 = new AutoSaveThread("AutoSave-1");
            AutoSaveThread save2 = new AutoSaveThread("AutoSave-2");
            save1.start();
            save2.start();

            System.out.println("Trying to overspend...");
            acc.deductBalance(2000);

        } catch (InsufficientFundsException e) {
            System.out.println("Transaction failed: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> new FinanceForm());

        new Thread(() -> {
            try {
                FinanceServer.startServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
