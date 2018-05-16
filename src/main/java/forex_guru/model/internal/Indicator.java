package forex_guru.model.internal;

public class Indicator {

    private String date;
    private String symbol;
    private double close;
    private double change;
    private double simpleMovingAverage;
    private double exponentialMovingAverage;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getSimpleMovingAverage() {
        return simpleMovingAverage;
    }

    public void setSimpleMovingAverage(double simpleMovingAverage) {
        this.simpleMovingAverage = simpleMovingAverage;
    }

    public double getExponentialMovingAverage() {
        return exponentialMovingAverage;
    }

    public void setExponentialMovingAverage(double exponentialMovingAverage) {
        this.exponentialMovingAverage = exponentialMovingAverage;
    }
}
