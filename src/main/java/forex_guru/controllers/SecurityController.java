package forex_guru.controllers;

import forex_guru.exceptions.CustomException;
import forex_guru.mappers.SecurityMapper;
import forex_guru.model.RootResponse;
import forex_guru.model.OAuth2Client;
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
    private SecurityMapper securityMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Creates a new client with the given client details
     */
    @PostMapping("/oauth/client")
    public RootResponse createClient(@RequestBody OAuth2Client client) throws CustomException {

        // encode password
        String password = passwordEncoder.encode(client.getClient_secret());
        client.setClient_secret(password);

        // store client in DB
        try {
            securityMapper.insertClient(client);
        } catch (Exception ex) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "could not create client");

        }

        return new RootResponse(HttpStatus.OK, "client created successfully", client);
    }

    /**
     * Retrieves client details for the given client
     */
    @GetMapping("/oauth/client")
    public RootResponse retrieveClient(@RequestParam(value="client_id") String client_id) throws CustomException {

        // retrieve client from DB
        OAuth2Client client =  securityMapper.findClient(client_id);

        if (client == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "could not retrieve client");
        }

        return new RootResponse(HttpStatus.OK, "client retrieved successfully", client);
    }

    /**
     * Updates client details for the given client
     */
    @PutMapping("/oauth/client")
    public RootResponse updateClient(@RequestBody OAuth2Client client) throws CustomException {

        // encode password
        String password = passwordEncoder.encode(client.getClient_secret());
        client.setClient_secret(password);

        // store client in DB
        if (!securityMapper.updateClient(client)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "could not update client");
        }

        return new RootResponse(HttpStatus.OK, "client updated successfully", client);
    }

    /**
     * Deletes the given client
     */
    @DeleteMapping("/oauth/client")
    public RootResponse deleteClient(@RequestParam(value="client_id") String client_id) throws CustomException {

        // delete client from DB
        if (!securityMapper.deleteClient(client_id)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "could not delete client");
        }

        return new RootResponse(HttpStatus.OK, "client deleted successfully", client_id);
    }

}
