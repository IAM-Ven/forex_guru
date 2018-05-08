package forex_guru.services;

import forex_guru.exceptions.OandaException;
import forex_guru.model.internal.RootResponse;
import forex_guru.model.kibot.KibotRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

@Service
public class KibotService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RestTemplate restTemplate;

    public RootResponse getRates() {

        // default parameters
        String symbol = "USDEUR";
        int period = 5;

        // build query
        StringBuilder queryBuilder = new StringBuilder("http://api.kibot.com/?action=history");
        queryBuilder.append("&symbol=" + symbol);
        queryBuilder.append("&interval=daily");
        queryBuilder.append("&period=" + period);
        queryBuilder.append("&type=forex");
        String query =  queryBuilder.toString();

        // make API call
        String response = null;

        try {
            ResponseEntity<String> fullResponse = restTemplate.exchange(query, HttpMethod.GET, new HttpEntity(new HttpHeaders()), String.class);
            response = fullResponse.getBody();
        }
        // catch bad API call
        catch (HttpClientErrorException ex) {
            logger.error("bad kibot api request");
            // throw exception
        }

        // map response to KibotRate Objects
        ArrayList<KibotRate> kibotRates = mapRates(response, symbol);

        // persist data to DB


        return new RootResponse(HttpStatus.OK, "OK", mapRates(response, symbol));
    }

    private ArrayList<KibotRate> mapRates(String rates, String symbol) {

        ArrayList<KibotRate> output = new ArrayList<>();

        // read response
        try (BufferedReader reader = new BufferedReader(new StringReader(rates))) {
            String line;

            // read lines
            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                // create new KibotRate Object
                KibotRate rate = new KibotRate();
                rate.setDate(data[0]);
                rate.setTimestamp("");
                rate.setSymbol(symbol);
                rate.setClose(Float.parseFloat(data[4]));

                // add to results
                output.add(rate);
            }

        } catch (IOException ex) {
            logger.error("could not map response");
            // throw exception);
        }

        return output;
    }

}
