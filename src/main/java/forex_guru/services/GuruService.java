package forex_guru.services;

import forex_guru.model.oanda.OandaResponse;
import forex_guru.utils.EmailUtil;
import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GuruService {

    @Autowired
    private OandaService oandaService;

    @Value("${aws.email}")
    private String email;

    public OandaResponse getPrices() {
        OandaResponse response = oandaService.getPrices("EUR_USD","GBP_USD","EUR_CAD","EUR_GBP");

        String textBody = "Could not display html";

        String htmlBody = EmailUtil.formatCurrencyNotificationEmail(response.getPrices());

        EmailUtil.sendEmail(email, email, "Market Update!", textBody, htmlBody);

        return response;
    }

}
