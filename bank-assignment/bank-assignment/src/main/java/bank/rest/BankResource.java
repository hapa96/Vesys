package bank.rest;

import bank.Account;
import bank.Bank;
import bank.local.Driver;


import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

@Singleton  //guarantees that resource is only instantiated once
@Path("accounts")   //resource is accessible under URI http://localhost:9998/bank/accounts
public class BankResource {
    private final Bank bank;

    public BankResource() {
        this.bank = new Driver.LocalBank();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Set<String> getAccountNumbers() throws IOException {
        return bank.getAccountNumbers();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getAccountNumbers(@Context UriInfo uriInfo) throws IOException {
        StringBuffer buf = new StringBuffer();
        for (String acc : bank.getAccountNumbers()) {
            buf.append(uriInfo.getAbsolutePathBuilder().path(acc).build());
            buf.append("\n");
        }
        return buf.toString();
    }

    @POST
    @Consumes("application/x-www-form-urlencoded")
    public Response createAccount(@Context UriInfo uriInfo, @FormParam("owner") String owner) throws IOException {
        String id = bank.createAccount(owner);
        URI location = uriInfo.getRequestUriBuilder().path(id).build();
        System.out.println(location);
        return Response.created(location).build();
    }




}
