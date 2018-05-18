package forex_guru.controllers;

import forex_guru.model.RootResponse;
import forex_guru.services.IndicatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GuruController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    IndicatorService indicatorService;

    /**
     * Calculates Technical Indicator
     * @param type the indicator ("SMA" for Simple Moving Average, "EMA" for Exponential Moving Average)
     * @param symbol the currency pair ("USDEUR", "USDGBP")
     * @param count the number of trailing days
     * @return the calculated Decimal value
     */
    @GetMapping("/dailyindicator")
    public RootResponse indicator(@RequestParam(value="type") String type,
                                  @RequestParam(value="symbol") String symbol,
                                  @RequestParam(value="count") int count) {
        logger.info("API Call: /indicators?type=" + type + "&symbol=" + symbol + "&count=" + count);
        return new RootResponse(HttpStatus.OK, "OK", indicatorService.calculateDailyIndicators(type, symbol, count));
    }

}
