package forex_guru.services;

import forex_guru.mappers.DataMapper;
import forex_guru.mappers.IndicatorMapper;
import forex_guru.model.external.ExchangeRate;
import forex_guru.model.internal.Indicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service

public class IndicatorService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DataMapper dataMapper;

    @Autowired
    IndicatorMapper indicatorMapper;

    /**
     * Calculates indicators and stores them in DB
     */
    @Scheduled(cron="*/10 * * * * *") // TESTING ONLY
    //@Scheduled(cron="30 0 6,19 * * *")
    public void indicators() {

        simpleMovingAverage();

        logger.info("indicators calculated");
    }

    /**
     * Calculates a 30 Day Simple Moving Average for each currency pair
     * Stores resulting calculation in `indicator` table
     */
    public void simpleMovingAverage() {

        // iterate through all symbols being tracked be prediction service
        for (String symbol : PredictionService.TRACKING) {

            // pull rates from DB
            ExchangeRate[] rates = dataMapper.findRatesBySymbol(symbol);

            // iterate through each rate datapoint that has at least 30 trailing datapoints
            for (int i = rates.length - 1; i > 30; i--) {

                // check if not already in DB
                if (indicatorMapper.findIndicatorByRateIdTimeStampType(rates[i].getId(), rates[i].getTimestamp(), "SMA") == null) {

                    // calculate simple moving average
                    Indicator indicator = calculateSMA(rates, i);

                    // persist to DB
                    indicatorMapper.insertIndicator(indicator);
                }
            }

        }
    }

    /** HELPER METHOD
     * Calculates the Simple Moving Average of a given Exchange Rate datapoint
     * Simple Moving Average = Sum of Last 30 Closing Rates / 30 Days
     *
     * @param rates Array of ExchangeRates
     * @param i position of current ExchangeRate in Array
     * @return Indicator with the Simple Moving Average
     */
    private Indicator calculateSMA(ExchangeRate[] rates, int i) {
        double sum = 0;

        // calculate trailing sum
        for (int j = i; j > i - 30; j--) {
            sum += rates[j].getClose();
        }

        // calculate current moving average
        double avg = sum / 30;

        // map to indicator
        Indicator indicator = new Indicator();
        indicator.setTimestamp(rates[i].getTimestamp());
        indicator.setRate_id(rates[i].getId());
        indicator.setType("SMA");
        indicator.setValue(avg);

        return indicator;
    }

}
