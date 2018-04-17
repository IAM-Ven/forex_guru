package forex_guru.model.oanda;

public class Bid {

    Double price;
    Long liquidity;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getLiquidity() {
        return liquidity;
    }

    public void setLiquidity(Long liquidity) {
        this.liquidity = liquidity;
    }

}
