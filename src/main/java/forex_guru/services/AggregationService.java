package forex_guru.services;

import forex_guru.exceptions.DatabaseException;
import forex_guru.exceptions.KibotException;
import forex_guru.mappers.DataMapper;
import forex_guru.model.external.ExchangeRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
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
public class AggregationService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    DataMapper dataMapper;

    /**
     * Aggregates data from 01/01/2015 to most recent market close, twice daily
     * The data is stored in the 'rates' DB table
     */
    @Scheduled(cron="*/5 * * * * *") // TESTING ONLY
    //@Scheduled(cron="0 0 6,19 * * *")
    public void aggregate() throws KibotException, DatabaseException {

        // iterate through all symbols being tracked be prediction service
        for (String symbol : PredictionService.TRACKING) {

            // retrieve start timestamp in epoch time
            long startdate;
            // if no data in DB, start with 01/01/2015
            if (dataMapper.findRatesBySymbol(symbol).length == 0) {
                startdate = 1420070400;
            }
            // start from last entry
            else {
                startdate = dataMapper.findLatestTimestampBySymbol(symbol);
            }

            // end timestamp: today - 1 day (864000) in epoch time
            long enddate = (System.currentTimeMillis() / 1000) - 86400;

            // pull/map rates
            ArrayList<ExchangeRate> exchangeRates = getRates(symbol, startdate, enddate);

            // persist rates to DB
            if (exchangeRates != null) {
                persistRates(exchangeRates);
            }
        }

        logger.info("aggregation complete");
    }

    /**
     * Pulls daily exchange rates for the given symbol and date range
     * @param symbol of forex (ex USDEUR)
     * @param startdate epoch time
     * @param enddate epoch time
     * @return JSON Currency Exchange Rate Data
     */
    private ArrayList<ExchangeRate> getRates(String symbol, long startdate, long enddate) {

        // if the start and end are the same day, no data necessary
        if (enddate - startdate <= 86400) {
            logger.info(symbol + " data is current");
            return null;
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
                    return null;
                }

                // create new ExchangeRate Object
                ExchangeRate rate = new ExchangeRate();
                rate.setDate(data[0]);
                rate.setTimestamp(epoch);
                rate.setSymbol(symbol);
                rate.setClose(Double.parseDouble(data[4]));

                // add to results
                output.add(rate);
            }

        } catch (IOException ex) {
            logger.error("could not map response");
            return null;
        }

        return output;
    }

    /**
     * Persists KibotRates to DB
     * @param rates to persist
     */
    private void persistRates(ArrayList<ExchangeRate> rates) throws DatabaseException {
        // iterate through rates
        for (ExchangeRate rate : rates) {
            try {
                // check if already in DB
                if (dataMapper.findRateBySymbolAndTimestamp(rate.getSymbol(), rate.getTimestamp()) == null) {
                    // persist to DB
                    dataMapper.insertRate(rate);
                    logger.info(rate.getSymbol() + " data persisted for " + rate.getDate());
                }
            } catch (Exception ex) {
                logger.error("could not persist to database: " + rate.getSymbol() + " data for " + rate.getDate());
            }
        }
    }

}
