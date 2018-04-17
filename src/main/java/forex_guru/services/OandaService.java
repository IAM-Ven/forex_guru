package forex_guru.services;

import forex_guru.model.oanda.OandaResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class OandaService {

    @Value("${oanda.token}")
    private String token;

    /**
     * Gets the current prices of the given instruments from the Oanda API
     * @param instruments per Oanda API docs
     * @return
     */
    public OandaResponse getPrices(String ... instruments) {

        // create client
        DefaultHttpClient client = new DefaultHttpClient();

        // create GET request with proper Authorization header
        HttpGet request = new HttpGet("https://api-fxpractice.oanda.com/v3/accounts/101-001-8216392-001/pricing?instruments=EUR_USD");
        request.addHeader("Authorization", "Bearer " + token);

        try {
            // make api call
            HttpResponse httpResponse = client.execute(request);
            HttpEntity entity = httpResponse.getEntity();

            // read response
            String json = "";
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    json += line;
                }

                System.out.println(json);
            }

        } catch (IOException ex) {
            System.out.println(ex);
        }

        return null;
    }

}
