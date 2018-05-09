package forex_guru.controllers;

import forex_guru.exceptions.DatabaseException;
import forex_guru.exceptions.KibotException;
import forex_guru.model.internal.RootResponse;
import forex_guru.services.GuruService;
import forex_guru.services.KibotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GuruController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    GuruService guruService;

    @Autowired
    KibotService kibotService;

    @GetMapping("/rates")
    public RootResponse getRates() throws KibotException, DatabaseException {
        logger.info("API Call: /rates");
        return kibotService.getRates("USDEUR");
    }

    @GetMapping("/predict")
    public RootResponse getPrediction() {
        logger.info("API Call: /predict");
        return null;
    }

}
