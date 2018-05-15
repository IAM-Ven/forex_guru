package forex_guru.controllers;

import forex_guru.exceptions.DatabaseException;
import forex_guru.exceptions.KibotException;
import forex_guru.model.internal.RootResponse;
import forex_guru.services.AggregationService;
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

    /**
     * Gives future currency exchange predictions
     * @return prediction data
     */
    @GetMapping("/aggregate")
    public RootResponse predict() {
        logger.info("API Call: /predict");
        return new RootResponse(HttpStatus.OK, "OK", null);
    }

}
