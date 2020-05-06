package bank.Socket;

import bank.Account;
import bank.Bank;
import bank.InactiveException;
import bank.OverdrawException;

import java.io.*;
import java.net.Socket;
import java.util.Set;

public class Handler implements Runnable {
    public final int CREATE_ACCOUNT = 1;
    public final int CLOSE_ACCOUNT = 2;
    public final int GET_ACCOUNT_NUMBERS = 3;
    public final int GET_ACCOUNT = 4;
    public final int TRANSFER = 5;
    public final int DEPOSIT = 6;
    public final int WITHDRAW = 7;
    public final int GET_BALANCE = 8;

    private final Socket socket;
    private final Bank localBank;
    private final DataInputStream in;
    private final DataOutputStream out;

    Handler(Socket socket, Bank bank) throws IOException {
        this.socket = socket;
        this.localBank = bank;
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    }


    @Override
    public void run() {
        while (true) {
            try {
                int command = in.readInt();
                switch (command) {
                    case CREATE_ACCOUNT:
                        String res = localBank.createAccount(in.readUTF());
                        out.writeUTF(res == null ? "" : res); //give back result from local bank;
                        out.flush();
                        break;

                    case CLOSE_ACCOUNT:
                        String number = in.readUTF();
                        boolean response = false;
                        try {
                            response = localBank.closeAccount(number);
                        } catch (IOException e) {
                            out.writeUTF("IOException");
                        }
                        out.writeUTF("success");
                        out.writeBoolean(response);
                        out.flush();
                        break;

                    case GET_ACCOUNT_NUMBERS:
                        Set<String> accounts = localBank.getAccountNumbers();
                        out.writeInt(accounts.size());
                        for (String s : accounts) {
                            out.writeUTF(s);
                        }
                        out.flush();
                        break;

                    case GET_ACCOUNT:
                        Account account = localBank.getAccount(in.readUTF());
                        out.writeUTF(account.getNumber());
                        out.writeUTF(account.getOwner());
                        out.writeBoolean(account.isActive());
                        out.flush();
                        break;

                    case TRANSFER:
                        String a = in.readUTF();
                        String b = in.readUTF();
                        Double amount = in.readDouble();
                        try {
                            localBank.transfer(localBank.getAccount(a), localBank.getAccount(b), amount);
                        } catch (IOException e) {
                            out.writeUTF("IOException");
                        } catch (IllegalArgumentException e) {
                            out.writeUTF("IllegalArgumentException");
                        } catch (OverdrawException e) {
                            out.writeUTF("OverdrawException");
                        } catch (InactiveException e) {
                            out.writeUTF("InactiveException");
                        }
                        out.writeUTF("success");
                        out.flush();

                        break;
                    case DEPOSIT:
                        a = in.readUTF();
                        System.out.println("DEPOSIT" + a);
                        amount = in.readDouble();
                        try {
                            System.out.println("Before" + localBank.getAccount(a).getBalance());
                            localBank.getAccount(a).deposit(amount);
                            System.out.println("After" + localBank.getAccount(a).getBalance());

                        } catch (IOException e) {
                            out.writeUTF("IOException");
                        } catch (IllegalArgumentException e) {
                            out.writeUTF("IllegalArgumentException");
                        } catch (InactiveException e) {
                            out.writeUTF("InactiveException");
                        }
                        out.writeUTF("success");

                        out.flush();
                        break;
                    case WITHDRAW:
                        a = in.readUTF();
                        System.out.println("DEPOSIT" + a);
                        amount = in.readDouble();
                        try {
                            System.out.println("Before" + localBank.getAccount(a).getBalance());
                            localBank.getAccount(a).withdraw(amount);
                            System.out.println("After" + localBank.getAccount(a).getBalance());
                        } catch (IOException e) {
                            out.writeUTF("IOException");
                        } catch (IllegalArgumentException e) {
                            out.writeUTF("IllegalArgumentException");
                        } catch (OverdrawException e) {
                            out.writeUTF("OverdrawException");
                        } catch (InactiveException e) {
                            out.writeUTF("InactiveException");
                        }
                        out.writeUTF("success");

                        out.flush();
                        break;

                    case GET_BALANCE:
                        a = in.readUTF();
                        System.out.println("GET_BALANCE-number" + a);
                        double balance = 0;
                        try {
                            balance = localBank.getAccount(a).getBalance();
                        } catch (IOException e) {
                            out.writeUTF("IOException");
                        } finally {
                            out.writeUTF("success");
                        }
                        out.writeDouble(balance);
                        out.flush();
                        break;


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
