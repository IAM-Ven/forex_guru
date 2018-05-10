package forex_guru.controllers;

import forex_guru.exceptions.ClientException;
import forex_guru.mappers.SecurityMapper;
import forex_guru.model.internal.RootResponse;
import forex_guru.model.security.OAuth2Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * OAuth2 Client CRUD Endpoints
 */
@RestController
public class SecurityController {

    @Autowired
    SecurityMapper securityMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * Creates a new client with the given client details
     */
    @PostMapping("/oauth/client")
    public RootResponse createClient(@RequestBody OAuth2Client client) throws ClientException {

        // encode password
        String password = passwordEncoder.encode(client.getClient_secret());
        client.setClient_secret(password);

        // store client in DB
        try {
            securityMapper.insertClient(client);
        } catch (Exception ex) {
            throw new ClientException("could not create client", HttpStatus.BAD_REQUEST);

        }

        return new RootResponse(HttpStatus.OK, "client created successfully", client);
    }

    /**
     * Retrieves client details for the given client
     */
    @GetMapping("/oauth/client")
    public RootResponse retrieveClient(@RequestParam(value="client_id") String client_id) throws ClientException {

        // retrieve client from DB
        OAuth2Client client =  securityMapper.findClient(client_id);

        if (client == null) {
            throw new ClientException("could not retrieve client", HttpStatus.BAD_REQUEST);
        }

        return new RootResponse(HttpStatus.OK, "client retrieved successfully", client);
    }

    /**
     * Updates client details for the given client
     */
    @PutMapping("/oauth/client")
    public RootResponse updateClient(@RequestBody OAuth2Client client) throws ClientException {

        // encode password
        String password = passwordEncoder.encode(client.getClient_secret());
        client.setClient_secret(password);

        // store client in DB
        if (!securityMapper.updateClient(client)) {
            throw new ClientException("could not update client", HttpStatus.BAD_REQUEST);
        }

        return new RootResponse(HttpStatus.OK, "client updated successfully", client);
    }

    /**
     * Deletes the given client
     */
    @DeleteMapping("/oauth/client")
    public RootResponse deleteClient(@RequestParam(value="client_id") String client_id) throws ClientException {

        // delete client from DB
        if (!securityMapper.deleteClient(client_id)) {
            throw new ClientException("could not delete client", HttpStatus.BAD_REQUEST);
        }

        return new RootResponse(HttpStatus.OK, "client deleted successfully", client_id);
    }

}
