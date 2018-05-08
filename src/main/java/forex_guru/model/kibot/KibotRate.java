package forex_guru.model.kibot;

public class KibotRate {

    private String date;
    private long timestamp;
    private String symbol;
    private double close;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }
}
