package forex_guru.controllers;

import forex_guru.mappers.ClientMapper;
import forex_guru.model.internal.RootResponse;
import forex_guru.model.security.OAuth2Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class ClientController {

    @Autowired
    ClientMapper clientMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * Creates a new client with the given client details
     */
    @PostMapping("/client")
    public RootResponse createClient(@RequestBody OAuth2Client client) {

        // encode password
        String password = passwordEncoder.encode(client.getClient_secret());
        client.setClient_secret(password);

        // store client in DB
        clientMapper.insertClient(client);

        return new RootResponse(HttpStatus.OK, "client created successfully", client);
    }

    /**
     * Retrieves client details for the given client
     */
    @GetMapping("/client")
    public RootResponse retrieveClient(@RequestParam(value="client_id") String client_id) {

        // retrieve client from DB
        OAuth2Client client =  clientMapper.findClient(client_id);

        return new RootResponse(HttpStatus.OK, "client retrieved successfully", client);
    }

    /**
     * Updates client details for the given client
     */
    @PutMapping("/client")
    public RootResponse updateClient(@RequestBody OAuth2Client client) {

        // encode password
        String password = passwordEncoder.encode(client.getClient_secret());
        client.setClient_secret(password);

        // store client in DB
        clientMapper.updateClient(client);

        return new RootResponse(HttpStatus.OK, "client updated successfully", client);
    }

    /**
     * Deletes the given client
     */
    @DeleteMapping("/client")
    public RootResponse deleteClient(@RequestParam(value="client_id") String client_id) {

        // delete client from DB
        clientMapper.deleteClient(client_id);

        return new RootResponse(HttpStatus.OK, "client deleted successfully", client_id);
    }

}
