package forex_guru.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GuruService {

//    @Autowired
//    private OandaService oandaService;

    @Value("${aws.email}")
    private String email;


    public double getPricePrediction(String symbol) {

        // check if less than 250 DB records (to exclude

            // getPrices()


        return 0.0;
    }



//    public OandaResponse getPrices() throws KibotException {
//        OandaResponse response = oandaService.getPrices("EUR_USD","GBP_USD","EUR_CAD","EUR_GBP");
//
//        String textBody = "Could not display html";
//
//        String htmlBody = EmailUtil.formatCurrencyNotificationEmail(response.getPrices());
//
//        //EmailUtil.sendEmail(email, email, "Market Update!", textBody, htmlBody);
//
//        return response;
//    }

}
