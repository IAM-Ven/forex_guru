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
     * Aggregates data from 01/01/2015 to yesterday
     * @param symbol to aggregate
     */
    public ArrayList<KibotRate> aggregate(String symbol) throws KibotException, DatabaseException {

        // retrieve start timestamp in epoch time
        long startdate;
        // if no data in DB, start with 01/01/2015
        if (rateMapper.findRateBySymbol(symbol).size() == 0) {
            startdate = 1420070400;
        }
        // start from last entry
        else {
            startdate = rateMapper.findLatestTimestampBySymbol(symbol);
        }

        // end timestamp: today - 1 day (864000) in epoch time
        long enddate = (System.currentTimeMillis() / 1000) - 86400;

        // get rates
        return getRates(symbol, startdate, enddate);
    }

    /**
     * Pulls daily exchange rates for the given symbol and date range
     * @param symbol of forex (ex USDEUR)
     * @param startdate epoch time
     * @param enddate epoch time
     * @return JSON Currency Exchange Rate Data
     */
    private ArrayList<KibotRate> getRates(String symbol, long startdate, long enddate) throws KibotException, DatabaseException {

        // if the start and end are the same day, no data necessary
        if (enddate - startdate <= 86400) {
            throw new KibotException(HttpStatus.OK, "data aggregation is current");
        }

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
            logger.error("bad kibot api request");
            throw new KibotException(HttpStatus.BAD_REQUEST, "bad kibot api request");
        }

        // map response to KibotRate Objects
        ArrayList<KibotRate> kibotRates = mapRates(response, symbol);

        // persist data to DB
        persistRates(kibotRates);

        return kibotRates;
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

                long epoch;

                try {
                    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                    Date date = df.parse(data[0]);
                    epoch = date.getTime() / 1000;
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
