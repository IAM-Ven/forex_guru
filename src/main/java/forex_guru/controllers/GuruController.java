package forex_guru.controllers;

import forex_guru.exceptions.DatabaseException;
import forex_guru.exceptions.KibotException;
import forex_guru.model.internal.RootResponse;
import forex_guru.services.AggregationService;
import forex_guru.services.IndicatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Prediction Endpoint
 */
@RestController
public class GuruController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    IndicatorService indicatorService;

    /**
     * Gives technical indicators for USDEUR
     * @return
     */
    @GetMapping("/indicators")
    public RootResponse indicate() {
        logger.info("API Call: /indicators");
        return new RootResponse(HttpStatus.OK, "OK", indicatorService.indicators());
    }

    /**
     * Gives future currency exchange predictions for USDEUR
     * @return prediction data
     */
    @GetMapping("/predict")
    public RootResponse predict() {
        logger.info("API Call: /predict");
        // TODO
        return new RootResponse(HttpStatus.OK, "OK", null);
    }

}
