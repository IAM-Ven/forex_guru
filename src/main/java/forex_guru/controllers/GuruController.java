package forex_guru.controllers;

import forex_guru.exceptions.OandaException;
import forex_guru.model.internal.RootResponse;
import forex_guru.services.GuruService;
import forex_guru.services.KibotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GuruController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    GuruService guruService;

    @Autowired
    KibotService kibotService;

    @GetMapping("/prices")
    public RootResponse getPrices() throws OandaException {
        logger.info("API Call: /prices");
        return new RootResponse(HttpStatus.OK, "OK", guruService.getPrices());
    }

    @GetMapping("/rates")
    public RootResponse getRates() {
        logger.info("API Call: /rates");
        return kibotService.getRates();
    }

}
