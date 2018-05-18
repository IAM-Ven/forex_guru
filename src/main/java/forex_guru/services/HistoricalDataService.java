package forex_guru.services;

import forex_guru.model.external.ExchangeRate;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

@Service
public class HistoricalDataService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RestTemplate restTemplate;

    /**
     * Pulls daily exchange rates for the last 90 business days
     * @param symbol the currency pair
     * @return a list of exchange rate data
     */
    public ArrayList<ExchangeRate> getRates(String symbol) {

        // range for last 90 days
        long enddate = System.currentTimeMillis() / 1000;
        long startdate = enddate - 7776000;

        // convert dates to Kibot formatting (MM/DD/YYYY)
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String start = df.format(new Date(startdate * 1000)).toString();
        String end = df.format(new Date(enddate * 1000)).toString();

        // build query
        StringBuilder queryBuilder = new StringBuilder("http://api.kibot.com/?action=history");
        queryBuilder.append("&user=guest");
        queryBuilder.append("&password=guest");
        queryBuilder.append("&type=forex");
        queryBuilder.append("&symbol=" + symbol);
        queryBuilder.append("&interval=daily");
        queryBuilder.append("&startdate=" + start);
        queryBuilder.append("&enddate=" + end);
        String query =  queryBuilder.toString();

        // make API call
        String response = null;

        try {
            ResponseEntity<String> fullResponse = restTemplate.exchange(query, HttpMethod.GET, new HttpEntity(new HttpHeaders()), String.class);
            response = fullResponse.getBody();
        }
        // catch bad API call
        catch (HttpClientErrorException ex) {
            logger.error("bad external api request");
        }

        // map response to ExchangeRate Objects
        return mapRates(response, symbol);
    }

    /**
     * Maps String response data to ExchangeRate Objects
     * @param rates to map
     * @param symbol of rates
     * @return an ArrayList of ExchangeRate Objects with the given data
     */
    private ArrayList<ExchangeRate> mapRates(String rates, String symbol) {

        ArrayList<ExchangeRate> output = new ArrayList<>();

        // read response
        try (BufferedReader reader = new BufferedReader(new StringReader(rates))) {
            String line;

            DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            // read lines
            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                // parse timestamp
                LocalDate local = LocalDate.parse(data[0], df);
                long epoch =local.atStartOfDay(ZoneId.of("GMT")).toInstant().getEpochSecond();

                // populate ExchangeRate Object
                ExchangeRate rate = new ExchangeRate();
                rate.setDate(data[0]);
                rate.setTimestamp(epoch);
                rate.setSymbol(symbol);
                rate.setOpen(Double.parseDouble(data[1]));
                rate.setHigh(Double.parseDouble(data[2]));
                rate.setLow(Double.parseDouble(data[3]));
                rate.setClose(Double.parseDouble(data[4]));
                rate.setVolume(Long.parseLong(data[5]));

                // add to results
                output.add(rate);
            }

        } catch (IOException ex) {
            logger.error("could not map response");
            return null;
        }

        return output;
    }

}
