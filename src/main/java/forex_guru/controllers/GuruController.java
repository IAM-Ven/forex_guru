package forex_guru.controllers;

import forex_guru.model.internal.RootResponse;
import forex_guru.services.HistoricalDataService;
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
     * Gives technical indicators for given currency pair
     * @return
     */
    @GetMapping("/indicators")
    public RootResponse indicate(@RequestParam(value="symbol") String symbol) {
        logger.info("API Call: /indicators?symbol=" + symbol);
        return new RootResponse(HttpStatus.OK, "OK", indicatorService.indicators(symbol));
    }

}
