//package forex_guru.utils;
//
//import com.amazonaws.regions.Regions;
//import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
//import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
//import com.amazonaws.services.simpleemail.model.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class EmailUtil {
//
//    private static final Logger logger = LoggerFactory.getLogger(EmailUtil.class);
//
//    public static boolean sendEmail(String sender, String recipient, String subject, String textBody, String htmlBody) {
//
//        try {
//            // build email sender
//            AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
//                    .withRegion(Regions.US_EAST_1).build();
//
//            // build email
//            SendEmailRequest request = new SendEmailRequest()
//                    .withDestination(new Destination().withToAddresses(recipient))
//                    .withMessage(new Message()
//                            .withBody(new Body().withText(new Content().withCharset("UTF-8").withData(textBody))
//                                                .withHtml(new Content().withCharset("UTF-8").withData(htmlBody)))
//                            .withSubject(new Content().withCharset("UTF-8").withData(subject)))
//                    .withSource(sender);
//
//            // send email
//            client.sendEmail(request);
//            logger.info("email sent to " + recipient);
//            return true;
//        }
//        catch (Exception ex) {
//            logger.error("could not send email to " + recipient);
//            return false;
//        }
//    }
//
////    public static String formatCurrencyNotificationEmail(Price[] prices) {
////
////        StringBuilder table = new StringBuilder();
////        table.append("<table>");
////        table.append("<tr><th>Currency</th><th>Rate</th></tr>");
////        for (Price p : prices) {
////            table.append("<tr>");
////            table.append("<td>");
////            table.append(p.getInstrument().replace("_", "/"));
////            table.append("</td>");
////            table.append("<td>");
////            table.append(p.getAsks()[0].getPrice());
////            table.append("</td>");
////            table.append("</tr>");
////        }
////        table.append("</table>");
////
////
////
////        return
////        "<head>" +
////            "<style>" +
////                "table { font-family: arial, sans-serif; border-collapse: collapse; } " +
////                "td, th { border: 1px solid #dddddd; text-align: left; padding: 8px 48px 8px 8px; } " +
////            "</style>" +
////        "</head>" +
////
////        "<body>" +
////            "<h2>Exchange Rates</h2>" +
////            table.toString() +
////        "</body>";
////    }
////}
