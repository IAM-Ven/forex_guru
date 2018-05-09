package forex_guru.controllers;

import forex_guru.exceptions.ClientException;
import forex_guru.mappers.ClientMapper;
import forex_guru.model.internal.RootResponse;
import forex_guru.model.security.OAuth2Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
/**
 * OAuth2 Client CRUD
 */
public class ClientController {

    @Autowired
    ClientMapper clientMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * Creates a new client with the given client details
     */
    @PostMapping("/client")
    public RootResponse createClient(@RequestBody OAuth2Client client) throws ClientException {

        // encode password
        String password = passwordEncoder.encode(client.getClient_secret());
        client.setClient_secret(password);

        // store client in DB
        try {
            clientMapper.insertClient(client);
        } catch (Exception ex) {
            throw new ClientException("could not create client", HttpStatus.BAD_REQUEST);

        }

        return new RootResponse(HttpStatus.OK, "client created successfully", client);
    }

    /**
     * Retrieves client details for the given client
     */
    @GetMapping("/client")
    public RootResponse retrieveClient(@RequestParam(value="client_id") String client_id) throws ClientException {

        // retrieve client from DB
        OAuth2Client client =  clientMapper.findClient(client_id);

        if (client == null) {
            throw new ClientException("could not retrieve client", HttpStatus.BAD_REQUEST);
        }

        return new RootResponse(HttpStatus.OK, "client retrieved successfully", client);
    }

    /**
     * Updates client details for the given client
     */
    @PutMapping("/client")
    public RootResponse updateClient(@RequestBody OAuth2Client client) throws ClientException {

        // encode password
        String password = passwordEncoder.encode(client.getClient_secret());
        client.setClient_secret(password);

        // store client in DB
        if (!clientMapper.updateClient(client)) {
            throw new ClientException("could not update client", HttpStatus.BAD_REQUEST);
        }

        return new RootResponse(HttpStatus.OK, "client updated successfully", client);
    }

    /**
     * Deletes the given client
     */
    @DeleteMapping("/client")
    public RootResponse deleteClient(@RequestParam(value="client_id") String client_id) throws ClientException {

        // delete client from DB
        if (!clientMapper.deleteClient(client_id)) {
            throw new ClientException("could not delete client", HttpStatus.BAD_REQUEST);
        }

        return new RootResponse(HttpStatus.OK, "client deleted successfully", client_id);
    }

}
