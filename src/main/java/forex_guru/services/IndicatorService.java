package forex_guru.services;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ta4j.core.*;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

@Service
public class IndicatorService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    HistoricalDataService historicalDataService;

    // TODO - MAKE ENUM
    final static String[] TYPES = {"SMA", "EMA"};

    /**
     * Calculates Technical Indicators
     * @param type the indicator ("SMA" for Simple Moving Average, "EMA" for Exponential Moving Average)
     * @param symbol the currency pair ("USDEUR", "USDGBP")
     * @param trailing the number of trailing days
     * @return the calculated Decimal value
     */
    public Pair<String, Decimal> calculateDailyIndicators(String type, String symbol, int trailing) {

        TimeSeries series = historicalDataService.getDailySeries(symbol);
        ClosePriceIndicator last = new ClosePriceIndicator(series);

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
                // TODO THROW EXCEPTION
                return null;
        }

        return new Pair<>(type, indicator.getValue(series.getEndIndex()));
    }

}
