package forex_guru.services;

import forex_guru.mappers.IndicatorMapper;
import forex_guru.model.external.ExchangeRate;
import forex_guru.model.internal.Indicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Service
public class IndicatorService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    HistoricalDataService historicalDataService;

    @Autowired
    IndicatorMapper indicatorMapper;

    /**
     * Calculates technical indicators for last 60 business days
     * @param symbol the currency pair to analyze
     * @return a list of indicator data
     */
    public ArrayList<Indicator> indicators(String symbol) {

        // creates a timeseries to calculate indicators on
        TimeSeries series = buildTimeSeries(symbol);
        ClosePriceIndicator close = new ClosePriceIndicator(series);

        // calculates 30 tick simple moving average
        SMAIndicator sma = new SMAIndicator(close, 30);

        // calculates 30 tick exponential moving average
        EMAIndicator ema = new EMAIndicator(close, 30);

        // build ArrayList of Indicator
        ArrayList<Indicator> indicators = new ArrayList<>();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        // skip first 30 (not enough trailing data)
        for (int i = 30; i < series.getBarCount(); i++) {

            Indicator indicator = new Indicator();
            indicator.setDate(series.getBar(i).getEndTime().format(df));
            indicator.setSymbol(series.getName());
            indicator.setClose(series.getBar(i).getClosePrice().doubleValue());
            indicator.setChange(series.getBar(i).getClosePrice().doubleValue()
                              - series.getBar(i - 1).getClosePrice().doubleValue());
            indicator.setSimpleMovingAverage(sma.getValue(i).doubleValue());
            indicator.setExponentialMovingAverage(ema.getValue(i).doubleValue());
            indicators.add(indicator);

        }

        logger.info("indicators calculated for " + symbol);

        // store in DB
        persistIndicators(indicators);

        return indicators;
    }

    /**
     * Builds a TimeSeries for TA4J for the past 90 days
     * @param symbol the currency pair
     * @return a TimeSeries containing Bars
     */
    private TimeSeries buildTimeSeries(String symbol) {

        TimeSeries series = new BaseTimeSeries(symbol);

        // get historical exchange rate data
        ArrayList<ExchangeRate> rates = historicalDataService.getRates(symbol);

        for (ExchangeRate rate : rates) {

            // get close date
            long timestamp = rate.getTimestamp();
            Instant temp = Instant.ofEpochSecond(timestamp);
            ZonedDateTime date = ZonedDateTime.ofInstant(temp, ZoneOffset.UTC);

            // create a bar for rate data (date, open, high, low, close, volume)
            Bar bar = new BaseBar(date, rate.getOpen(), rate.getHigh(), rate.getLow(), rate.getClose(), rate.getVolume());

            // add bar to series
            series.addBar(bar);

        }

        return series;
    }

    /**
     * Persists Indictors in `ForexGuru`.`indicators`
     * @param indicators the list of indicators to persist
     */
    private void persistIndicators(ArrayList<Indicator> indicators) {

        for (Indicator indicator : indicators) {
            try {
                // check if already in DB
                if (indicatorMapper.findIndicatorBySymbolAndDate(indicator.getSymbol(), indicator.getDate()) == null) {
                    // persist to DB
                    indicatorMapper.insertIndicator(indicator);
                    logger.info(indicator.getSymbol() + " data persisted for " + indicator.getDate());
                }
            } catch (Exception ex) {
                logger.error("could not persist to database: " + indicator.getSymbol() + " data for " + indicator.getDate());
            }
        }
    }

}
