package bank.rest;

import bank.InactiveException;
import bank.OverdrawException;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Set;

public class RestClient implements bank.BankDriver {

    private Bank bank;
    private final static String DEFAULT_PATH = "http://localhost:9998/bank/accounts";
    private String path = DEFAULT_PATH;
    private Client client;
    private WebTarget target;



    @Override
    public void connect(String[] args) throws IOException {
       bank = new Bank();
       client= ClientBuilder.newClient();
       target = client.target(path);


    }

    @Override
    public void disconnect() throws IOException {

    }

    @Override
    public Bank getBank() {
        return bank;
    }


    class Bank implements bank.Bank {

        @Override
        public String createAccount(String owner) throws IOException {
            Form f = new Form();
            f.param("owner", owner);
            Response response = target.request().post(Entity.form(f));
            if(response.getStatusInfo() != Response.Status.CREATED) return null;
            String loc= response.getHeaderString("Location");
            return loc.substring(loc.lastIndexOf("/") + 1);
        }

        @Override
        public boolean closeAccount(String number) throws IOException {
            return false;
        }

        @Override
        public Set<String> getAccountNumbers() throws IOException {
            return null;
        }

        @Override
        public Account getAccount(String number) throws IOException {
            return null;
        }

        @Override
        public void transfer(bank.Account a, bank.Account b, double amount) throws IOException, IllegalArgumentException, OverdrawException, InactiveException {

        }

        @Override
        public void transfer(Account a, Account b, double amount) throws IOException, IllegalArgumentException, OverdrawException, InactiveException {

        }
        class Account implements bank.Account{
            private final String number;
            private final String owner;
            private double balance;
            private boolean active;
            private String etag;

            Account(String number, String owner) {
                this.number = number;
                this.owner = owner;
            }

            @Override
            public String getNumber() throws IOException {
                return this.number;
            }

            @Override
            public String getOwner() throws IOException {
                return null;
            }

            @Override
            public boolean isActive() throws IOException {
                return false;
            }

            @Override
            public void deposit(double amount) throws IOException, IllegalArgumentException, InactiveException {

            }

            @Override
            public void withdraw(double amount) throws IOException, IllegalArgumentException, OverdrawException, InactiveException {

            }

            @Override
            public double getBalance() throws IOException {
                return 0;
            }
        }
    }
}