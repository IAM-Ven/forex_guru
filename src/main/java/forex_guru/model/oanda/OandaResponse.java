package forex_guru.model.oanda;

public class OandaResponse {

    String time;
    Price[] prices;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Price[] getPrices() {
        return prices;
    }

    public void setPrices(Price[] prices) {
        this.prices = prices;
    }

}
