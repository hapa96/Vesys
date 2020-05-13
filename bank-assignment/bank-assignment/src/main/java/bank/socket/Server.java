package bank.socket;

import bank.Bank;
import bank.local.Driver;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;




public class Server {

    public static void main(String[] args) throws IOException {
        Bank bank = new Driver.LocalBank(); //get a bank instance
        int port = 1234;
        try(ServerSocket serverSocket= new ServerSocket(port)) {
            while(true){
                Socket client = serverSocket.accept();  //blocks until client connects to Bank
                Thread t = new Thread(new Handler(client, bank));
                t.start();
            }
        }


    }
}



