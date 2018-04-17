package forex_guru.model.oanda;

import forex_guru.model.oanda.Ask;
import forex_guru.model.oanda.Bid;

public class Price {

    String time;

    Bid[] bids;
    Ask[] asks;

    String status;

    String instrument;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Bid[] getBids() {
        return bids;
    }

    public void setBids(Bid[] bids) {
        this.bids = bids;
    }

    public Ask[] getAsks() {
        return asks;
    }

    public void setAsks(Ask[] asks) {
        this.asks = asks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

}
