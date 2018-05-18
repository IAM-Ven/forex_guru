package forex_guru.services;

import forex_guru.mappers.DataMapper;
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
    DataMapper dataMapper;

    @Autowired
    IndicatorMapper indicatorMapper;

    /**
     * Calculates indicators
     */
    public ArrayList<Indicator> indicators() {

        // creates a timeseries to calculate indicators on
        TimeSeries series = buildTimeSeries("USDEUR");
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

        logger.info("indicators calculated");

        // store in DB
        persistIndicators(indicators);

        return indicators;
    }

    /**
     * Builds a TimeSeries for TA4J
     */
    private TimeSeries buildTimeSeries(String symbol) {

        TimeSeries series = new BaseTimeSeries(symbol);

        // pull all rate records for symbol from DB
        ExchangeRate[] rates = dataMapper.findRatesBySymbol(symbol);

        // iterate through rate records
        for (int i = 0; i < rates.length; i++) {

            // get close date
            long timestamp = rates[i].getTimestamp();
            Instant date = Instant.ofEpochSecond(timestamp);
            ZonedDateTime closeDate = ZonedDateTime.ofInstant(date, ZoneOffset.UTC);

            // create a bar for rate data (CloseDate, Open, High, Low, Close, Volume)
            Bar bar = new BaseBar(closeDate, rates[i].getOpen(), rates[i].getHigh(),
                                  rates[i].getLow(), rates[i].getClose(), rates[i].getVolume());

            // add bar to series
            series.addBar(bar);
        }

        return series;
    }

    /**
     * Persists Indictors in `ForexGuru`.`indicators`
     * @param indicators
     */
    private void persistIndicators(ArrayList<Indicator> indicators) {
        // iterate through rates
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
