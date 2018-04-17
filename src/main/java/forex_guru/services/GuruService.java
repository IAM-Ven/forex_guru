package forex_guru.services;

import forex_guru.model.oanda.OandaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GuruService {

    @Autowired
    OandaService oandaService;


    public OandaResponse getPrices() {
        return oandaService.getPrices("EUR_USD");
    }
}
