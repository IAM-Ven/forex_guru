package forex_guru.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import forex_guru.model.oanda.OandaResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class OandaService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${oanda.token}")
    private String token;

    @Value("${oanda.account}")
    private String account;

    /**
     * Gets the current prices of the given instruments from the Oanda API
     * @param instruments per Oanda API docs
     * @return
     */
    public OandaResponse getPrices(String ... instruments) {

        // create client
        DefaultHttpClient client = new DefaultHttpClient();

        // build url
        StringBuilder urlbuilder = new StringBuilder();
        urlbuilder.append("https://api-fxpractice.oanda.com/v3/accounts/");
        urlbuilder.append(account);
        urlbuilder.append("/pricing?instruments=");

        for (int i = 0; i < instruments.length; i++) {
            if (i > 0) {
                urlbuilder.append(",");
            }
            urlbuilder.append(instruments[i]);
        }

        // create GET request with proper Authorization header
        HttpGet request = new HttpGet(urlbuilder.toString());
        request.addHeader("Authorization", "Bearer " + token);

        String json = "";

        try {
            // make api call
            HttpResponse httpResponse = client.execute(request);
            HttpEntity entity = httpResponse.getEntity();

            logger.info("Response Status: " + Integer.toString(httpResponse.getStatusLine().getStatusCode()));

            // read response
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    json += line;
                }

            }

        } catch (IOException ex) {
            logger.error("could not connect to Oanda API");
        }

        return mapOanda(json);
    }

    /**
     * Maps JSON to OandaResponse Object
     * @param json to map
     * @return OandaResponse Object
     */
    public OandaResponse mapOanda(String json) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            return mapper.readValue(json, OandaResponse.class);
        } catch (Exception ex) {
            logger.error("could not map response");
            return null;
        }
    }

}
