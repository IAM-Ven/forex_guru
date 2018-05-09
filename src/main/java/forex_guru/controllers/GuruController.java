package forex_guru.controllers;

import forex_guru.exceptions.DatabaseException;
import forex_guru.exceptions.KibotException;
import forex_guru.model.internal.RootResponse;
import forex_guru.services.KibotService;
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
    KibotService kibotService;

    /** CRON JOB ENDPOINT
     * Aggregates all available data up to yesterday for the given symbol
     * @return JSON Currency Exchange Data
     */
    @GetMapping("/aggregate")
    public RootResponse aggregate(@RequestParam(value="symbol") String symbol) throws KibotException, DatabaseException {
        logger.info("API Call: /aggregate");
        return new RootResponse(HttpStatus.OK, "OK", kibotService.aggregate(symbol));
    }

}
