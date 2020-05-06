/*
 * Copyright (c) 2020 Fachhochschule Nordwestschweiz (FHNW)
 * All Rights Reserved.
 */

package bank.local;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import bank.InactiveException;
import bank.OverdrawException;

public class Driver implements bank.BankDriver {
    private LocalBank localBank = null;

    @Override
    public void connect(String[] args) {
        localBank = new LocalBank();
        System.out.println("connected...");
    }

    @Override
    public void disconnect() {
        localBank = null;
        System.out.println("disconnected...");
    }

    @Override
    public bank.Bank getBank() {
        return localBank;
    }

    public static class LocalBank implements bank.Bank {

        private final Map<String, Account> accounts = new HashMap<>();

        @Override
        public Set<String> getAccountNumbers() {
            HashSet<String> set = new HashSet<>();
            accounts.forEach((key, value) -> {
                if (value.isActive()) set.add(key);
            });
            return set;
        }

        @Override
        public String createAccount(String owner) {
            Account acc = new Account(owner);
            accounts.put(acc.number, acc);
            if (accounts.containsKey(acc.number)) return acc.number;        //returns the number of the account
            return null;    //account was not added to HashMap
        }

        @Override
        public boolean closeAccount(String number) {
            if(! accounts.containsKey(number))return false; //Account is not collected in HashMap
            if (accounts.get(number).getBalance() == 0 && accounts.get(number).isActive() == true) {
                accounts.get(number).active = false;    //set boolean active to false
                return true;    //Account was successfully closed
            }
            return false;
        }

        @Override
        public bank.Account getAccount(String number) {
            return accounts.get(number);
        }

        @Override
        public void transfer(bank.Account from, bank.Account to, double amount)
                throws IOException, InactiveException, OverdrawException {
            if (!from.isActive() || !to.isActive()) throw new InactiveException();
            if (amount > from.getBalance()) throw new OverdrawException();
            if (amount < 0) throw new IllegalArgumentException();
            from.withdraw(amount);
            to.deposit(amount);
        }

    }

    private static class Account implements bank.Account {
        private String number;
        private String owner;
        private double balance;
        private boolean active = true;

        private static int IncrementNumber = 0;

        private Account(String owner) {
            this.owner = owner;
            this.number = String.format("%07d", IncrementNumber); //Integer filled with Zeros in the beginning
            this.active = true;
            IncrementNumber++;
        }

        @Override
        public double getBalance() {
            return balance;
        }

        @Override
        public String getOwner() {
            return owner;
        }

        @Override
        public String getNumber() {
            return number;
        }

        @Override
        public boolean isActive() {
            return active;
        }

        @Override
        public void deposit(double amount) throws InactiveException {
            if (!isActive()) throw new InactiveException();
            if (amount < 0) throw new IllegalArgumentException();
            this.balance += amount;        //added amount to balance
        }

        @Override
        public void withdraw(double amount) throws InactiveException, OverdrawException {
            if (!isActive()) throw new InactiveException();
            if (amount > this.balance) throw new OverdrawException();
            if (amount < 0) throw new IllegalArgumentException();
            this.balance -= amount;        //subtract the given amount from balance
        }

    }

}