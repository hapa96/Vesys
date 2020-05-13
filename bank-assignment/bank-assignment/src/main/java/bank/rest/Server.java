package bank.rest;


import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URISyntaxException;

import java.net.URI;

public class Server {
    public static void main(String[] args) throws URISyntaxException {
        URI baseUri = new URI("http://localhost:9998/bank/");
        ResourceConfig rc = new ResourceConfig().packages("bank.rest");
        // Create and start the JDK HttpServer with the Jersey application
        System.out.println("Starting HttpServer...");
        JdkHttpServerFactory.createHttpServer(baseUri, rc);

    }
}

