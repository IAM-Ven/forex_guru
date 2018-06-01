package forex_guru.services;

import forex_guru.exceptions.CustomException;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.ta4j.core.*;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;

@Service
public class IndicatorService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestTemplate restTemplate;

    HashMap<String, TimeSeries> seriesMap = new HashMap<>();

    /**
     * Calculates Technical Indicators
     * @param type the indicator ("SMA" for Simple Moving Average, "EMA" for Exponential Moving Average)
     * @param symbol the currency pair ("USDEUR", "USDGBP")
     * @param trailing the number of trailing days
     * @return a pair with the indicator type and the calculated indicator as Decimal
     */
    public Decimal calculateDailyIndicator(String type, String symbol, int trailing) throws CustomException {

        TimeSeries series = dailySeries(symbol);
        ClosePriceIndicator last = new ClosePriceIndicator(series);

        // not enough data trailing data
        if (series.getBarCount() < trailing) {
            logger.error("insufficient daily historical data for " + symbol);
            throw new CustomException(HttpStatus.BAD_REQUEST, "insufficient daily historical data for " + symbol);
        }

        Indicator<Decimal> indicator;
        type = type.toUpperCase();

        switch (type) {
            case "SMA":
                indicator = new SMAIndicator(last, trailing);
                break;

            case "EMA":
                indicator = new EMAIndicator(last, trailing);
                break;

            default:
            logger.error(type + "is an invalid indicator type");
            throw new CustomException(HttpStatus.BAD_REQUEST, type + "is an invalid indicator type");
        }

        return indicator.getValue(series.getEndIndex());
    }

    /**
     * Gets a daily TimeSeries for the last 365 business days
     * @param symbol the currency pair
     * @return a TimeSeries containing all available daily data from Kibot API
     */
    public TimeSeries dailySeries(String symbol) throws CustomException {

        TimeSeries series = seriesMap.get(symbol);

        // series already exists
        if (series != null) {
            logger.info("retrieving stored result from HashMap at" + symbol);
            return series;
        }

        // range for last 365 days
        long enddate = System.currentTimeMillis() / 1000;
        long startdate = enddate - 31536000;

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
            throw new CustomException(HttpStatus.BAD_REQUEST, "bad external api request");
        }

        // map response to ExchangeRate Objects
        series = buildTimeSeries(response, symbol);

        // store series in hashmap
        logger.info("storing series in HashMap at " + symbol);
        seriesMap.put(symbol, series);

        return series;
    }

    /**
     * Maps String response data to a TimeSeries containing Bars
     * @param rates the Kibot Response
     * @param symbol the currency pair
     * @return a TimeSeries of the given data
     */
    private TimeSeries buildTimeSeries(String rates, String symbol) throws CustomException {

        TimeSeries series = new BaseTimeSeries(symbol);

        // read response
        try (BufferedReader reader = new BufferedReader(new StringReader(rates))) {
            String line;

            DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            // read lines
            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                // parse timestamp
                LocalDate local = LocalDate.parse(data[0], df);
                long epoch = local.atStartOfDay(ZoneId.of("GMT")).toInstant().getEpochSecond();
                Instant temp = Instant.ofEpochSecond(epoch);
                ZonedDateTime date = ZonedDateTime.ofInstant(temp, ZoneOffset.UTC);

                // populate Bar Object
                Decimal open = Decimal.valueOf(data[1]);
                Decimal high = Decimal.valueOf(data[2]);
                Decimal low = Decimal.valueOf(data[3]);
                Decimal close = Decimal.valueOf(data[4]);
                Decimal volume = Decimal.valueOf(data[5]);

                // create a bar for rate data (date, open, high, low, close, volume)
                Bar bar = new BaseBar(date, open, high, low, close, volume);

                // add bar to series
                series.addBar(bar);
            }

        } catch (IOException ex) {
            logger.error("could not map response");
            throw new CustomException(HttpStatus.BAD_REQUEST, "could not parse response");
        }

        logger.info("series built for " + symbol);
        return series;
    }

    /**
     * Clears Series HashMap Daily
     */
    @Scheduled(cron = "0 0 8 * * *")
    private void clearSeriesMap() {
        logger.info("clearing HashMap");
        seriesMap = null;
    }
}
