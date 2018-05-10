package forex_guru.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PredictionService {

    @Value("${aws.email}")
    private String email;

    public double predict(String symbol) {

        // check if less than 250 DB records (to exclude

            // getPrices()


        return 0.0;
    }


//    public void emailPrediction() {
//
//        //        String textBody = "Could not display html";
////
////        String htmlBody = EmailUtil.formatCurrencyNotificationEmail(response.getPrices());
////
////        //EmailUtil.sendEmail(email, email, "Market Update!", textBody, htmlBody);
//
//    }

}
