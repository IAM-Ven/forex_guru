package forex_guru.services;

import forex_guru.exceptions.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ta4j.core.Decimal;
import org.ta4j.core.TimeSeries;

import java.util.HashMap;

@Service
public class SignalService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IndicatorService indicatorService;

    // Major Pairs
    final String[] TRACKING = {"EURUSD", "USDJPY", "GBPUSD", "USDCAD", "USDCHF", "AUDUSD", "NZDUSD"};

    /**
     * Determines a buy/sell rating for the major currency pairs based on indicators
     * @return a HashMap of currency pairs and their buy/sell rating
     */
    public HashMap<String, Float> scanSignals() throws CustomException {

        HashMap<String, Float> currencyRatings = new HashMap<>();

        // iterate through major currency pairs
        for (String symbol : TRACKING) {

            // get rating
            float rating = 0;
            rating += simpleTrend(symbol);
            rating += crossGoldenDeath(symbol);
            rating += crossMACD(symbol);

            // add to currencyRatings
            currencyRatings.put(symbol, rating);
        }

        return currencyRatings;
    }

    /**
     * Determines if there is a buy/sell signal based on current price compared to fifty day simple moving average
     * @param symbol the currency pair
     * @return 0.5 if buy, -0.5 if sell, 0 if no signal
     */
    private float simpleTrend(String symbol) throws CustomException {

        TimeSeries series = indicatorService.dailySeries(symbol);
        Decimal fiftyDay =  indicatorService.calculateDailyIndicator("sma", symbol, 50);

        Decimal close = series.getLastBar().getClosePrice();

        // if current price is greater than fifty day Simple Moving Average
        if (close.isGreaterThan(fiftyDay)) {
            return 0.5f;
        }

        // if current price is less than fifty day Simple Moving Average
        if (close.isLessThan(fiftyDay)) {
            return -0.5f;
        }

        return 0;

    }

    /**
     * Determines if there is a buy/sell signal using the Golden Cross and Death Cross
     * @param symbol the currency pair
     * @return 1 if buy (golden cross), -1 if sell (death cross), 0 if no signal
     */
    private float crossGoldenDeath(String symbol) throws CustomException {

        Decimal prevShortTerm = indicatorService.calculateDailyIndicator("ema", symbol, 49);
        Decimal shortTerm = indicatorService.calculateDailyIndicator("ema", symbol, 50);
        Decimal longTerm = indicatorService.calculateDailyIndicator("ema", symbol, 200);

        // short crosses above long
        if (shortTerm.isGreaterThan(longTerm) && prevShortTerm.isLessThan(longTerm)) {
            return 1;
        }

        // short crosses below long
        if (shortTerm.isLessThan(longTerm) && prevShortTerm.isGreaterThan(longTerm)) {
            return -1;
        }

        return 0;
    }

    /**
     * Determines if there is a buy/sell signal using Moving Average Convergence Divergence
     * @param symbol the currency pair
     * @return 1 if buy, -1 if sell, 0 if no signal
     */
    private float crossMACD(String symbol) throws CustomException {
        Decimal twentySixDay = indicatorService.calculateDailyIndicator("ema", symbol, 26);
        Decimal twelveDay = indicatorService.calculateDailyIndicator("ema", symbol, 12);

        Decimal macd = twentySixDay.minus(twelveDay);
        Decimal signal = indicatorService.calculateDailyIndicator("ema", symbol, 9);
        Decimal prevSignal = indicatorService.calculateDailyIndicator("ema", symbol, 8);

        // macd crosses above signal
        if (macd.isGreaterThan(signal) && macd.isLessThan(prevSignal)) {
            return 1;
        }

        // macd crosses below signal
        if (macd.isLessThan(signal) && macd.isGreaterThan(prevSignal)) {
            return -1;
        }

        return 0;
    }

}
