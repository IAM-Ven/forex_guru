package forex_guru.services;

import forex_guru.exceptions.DatabaseException;
import forex_guru.exceptions.KibotException;
import forex_guru.mappers.RateMapper;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class KibotService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    RateMapper rateMapper;

    /**
     * Pulls exchange rates for the given symbol
     * If no dates are given, the daily rates for the last year are retrieved
     * @param symbol String
     * @param dates String startdate, String enddate (format: MM/DD/YYYY)
     * @return JSON Currency Exchange Rate Data
     */
    public RootResponse getRates(String symbol, String ... dates) throws KibotException, DatabaseException {

        // default parameters
        int period = 365;

        // build query
        StringBuilder queryBuilder = new StringBuilder("http://api.kibot.com/?action=history");
        queryBuilder.append("&symbol=" + symbol);
        queryBuilder.append("&interval=daily");

        if (dates.length == 2) {
            queryBuilder.append("&startdate=" + dates[0]);
            queryBuilder.append("&enddate=" + dates[1]);
        } else {
            queryBuilder.append("&period=" + period);
        }

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
            throw new KibotException(HttpStatus.BAD_REQUEST, "bad kibot api request");
        }

        // map response to KibotRate Objects
        ArrayList<KibotRate> kibotRates = mapRates(response, symbol);

        // persist data to DB
        persistRates(kibotRates);

        return new RootResponse(HttpStatus.OK, "OK", mapRates(response, symbol));
    }

    /**
     * Aggregates data to fill in gap between last DB record and current available data
     * @param symbol to aggregate
     */
    public void aggregateGap(String symbol) {

        // retrieve last DB timestamp

        // retrieve current timestamp

        // convert timestamps to dates

        // get rates

    }

    /**
     * Maps String response data to KibotRate Objects
     * @param rates to map
     * @param symbol of rates
     * @return an ArrayList of KibotRate Objects with the given data
     */
    private ArrayList<KibotRate> mapRates(String rates, String symbol) throws KibotException {

        ArrayList<KibotRate> output = new ArrayList<>();

        // read response
        try (BufferedReader reader = new BufferedReader(new StringReader(rates))) {
            String line;

            // read lines
            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                // parse timestamp

                long epoch = 0;

                try {
                    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                    Date date = df.parse(data[0]);
                    epoch = date.getTime();
                } catch (ParseException ex) {
                    logger.error("could not parse date");
                    throw new KibotException(HttpStatus.BAD_REQUEST, "could not parse kibot date");
                }

                // create new KibotRate Object
                KibotRate rate = new KibotRate();
                rate.setDate(data[0]);
                rate.setTimestamp(epoch);
                rate.setSymbol(symbol);
                rate.setClose(Double.parseDouble(data[4]));

                // add to results
                output.add(rate);
            }

        } catch (IOException ex) {
            logger.error("could not map response");
            throw new KibotException(HttpStatus.BAD_REQUEST, "could not map kibot response");
        }

        return output;
    }

    // TODO: background task
    /**
     * Persists KibotRates to DB
     * @param rates to persist
     */
    private void persistRates(ArrayList<KibotRate> rates) throws DatabaseException {
        // iterate through rates
        for (KibotRate rate : rates) {
            try {
                // check if already in DB
                if (rateMapper.findRateBySymbolAndTimestamp(rate.getSymbol(), rate.getTimestamp()) == null) {
                    // persist to DB
                    logger.info("persisted to database: " + rate.getSymbol() + " " + rate.getTimestamp());
                    rateMapper.insertRate(rate);
                }
            } catch (Exception ex) {
                logger.error("could not persist to database: " + rate.getSymbol() + " " + rate.getTimestamp());
                throw new DatabaseException(HttpStatus.BAD_REQUEST, "could not persist to database");
            }
        }
    }

}
