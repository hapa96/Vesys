package bank.socket;

import bank.*;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

/**
 * Client Side
 **/
public class SocketClient implements BankDriver {

    public final int CREATE_ACCOUNT = 1;
    public final int CLOSE_ACCOUNT = 2;
    public final int GET_ACCOUNT_NUMBERS = 3;
    public final int GET_ACCOUNT = 4;
    public final int TRANSFER = 5;
    public final int DEPOSIT = 6;
    public final int WITHDRAW = 7;
    public final int GET_BALANCE = 8;
    public final int IOException = 10;
    public final int IllegalArgumentException = 11;
    public final int InactiveException = 12;
    public final int OverdrawException = 13;


    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private Bank bank;


    @Override
    public void connect(String[] args) throws IOException {
        int port = Integer.parseInt(args[1]);
        socket = new Socket(args[0], port);
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        out.flush(); // nie vergessen
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

        //Setup Bank
        bank = new Bank();
        System.out.println("connected to " + args[0] + ":" + port);
    }

    @Override
    public void disconnect() throws IOException {
        out.close();
        in.close();
        socket.close();
        bank = null;
        System.out.println("disconnected...");
    }

    @Override
    public bank.Bank getBank() {
        return bank;
    }


    class Bank implements bank.Bank {

        @Override
        public String createAccount(String owner) throws IOException {
            out.writeInt(CREATE_ACCOUNT);
            out.writeUTF(owner);
            out.flush();
            String id = in.readUTF();
            return id.equals("") ? null : id;
        }

        @Override
        public boolean closeAccount(String number) throws IOException {
            out.writeInt(CLOSE_ACCOUNT);
            out.writeUTF(number);
            out.flush();
            if (in.readUTF() == "IOException") throw new IOException();
            else {
                Boolean response = in.readBoolean();
                return response;
            }

        }

        @Override
        public Set<String> getAccountNumbers() throws IOException {
            out.writeInt(GET_ACCOUNT_NUMBERS);
            out.flush();
            int n = in.readInt();
            Set<String> accounts = new HashSet<>(n);
            while (n > 0) {
                accounts.add(in.readUTF());
                n--;
            }
            return accounts;
        }

        @Override
        public Account getAccount(String number) throws IOException {
            out.writeInt(GET_ACCOUNT);
            out.writeUTF(number);
            out.flush();
            String acc_number = in.readUTF();
            if (acc_number == null) return null;
            String acc_owner = in.readUTF();
            Boolean acc_active = in.readBoolean();
            Account acc = new Account(acc_owner, acc_number, acc_active);
            return acc;
        }

        @Override
        public void transfer(bank.Account a, bank.Account b, double amount) throws IOException, IllegalArgumentException, OverdrawException, InactiveException {
            out.writeInt(TRANSFER);
            out.writeUTF(a.getNumber());
            out.writeUTF(b.getNumber());
            out.writeDouble(amount);
            out.flush();
            //Error Handling
            String exception = in.readUTF();
            switch (exception) {
                case "IOException":
                    throw new IOException();
                case "IllegalArgumentException":
                    throw new IllegalArgumentException();
                case "OverdrawException":
                    throw new OverdrawException();
                case "InactiveException":
                    throw new InactiveException();
            }
            a.getBalance();
            b.getBalance();
        }


    }

    class Account implements bank.Account {
        private String number;
        private String owner;
        private double balance;
        private boolean active;

        Account(String owner, String number, boolean active) {
            this.owner = owner;
            this.number = number;
            this.active = active;

        }

        @Override
        public String getNumber() throws IOException {
            return this.number;
        }

        @Override
        public String getOwner() throws IOException {
            return this.owner;
        }

        @Override
        public boolean isActive() throws IOException {
            return this.active;
        }

        @Override
        public void deposit(double amount) throws IOException, IllegalArgumentException, InactiveException {
            out.writeInt(DEPOSIT);
            out.writeUTF(this.number);
            out.writeDouble(amount);
            out.flush();
            String response = in.readUTF();
            switch (response) {
                case "IOException":
                    throw new IOException();
                case "IllegalArgumentException":
                    throw new IllegalArgumentException();
                case "InactiveException":
                    throw new InactiveException();
            }
            if (response == "success") {
                this.getBalance();
            }


        }

        @Override
        public void withdraw(double amount) throws IOException, IllegalArgumentException, OverdrawException, InactiveException {
            out.writeInt(WITHDRAW);
            out.writeUTF(this.number);
            out.writeDouble(amount);
            out.flush();
            String response = in.readUTF();
            switch (response) {
                case "IOException":
                    throw new IOException();
                case "IllegalArgumentException":
                    throw new IllegalArgumentException();
                case "InactiveException":
                    throw new InactiveException();
            }
            if (response == "success") {
                this.getBalance();
            }
        }

        @Override
        public double getBalance() throws IOException {
            out.writeInt(GET_BALANCE);
            out.writeUTF(this.number);
            out.flush();
            String response = in.readUTF();
            if (response == "IOException") throw new IOException();
            this.balance = in.readDouble();
            return balance;
        }
    }


}
