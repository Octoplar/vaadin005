package vaadin.back.entity;

import javax.persistence.Embeddable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by Octoplar on 19.05.2017.
 */
@Embeddable
public class PaymentType implements Cloneable, Serializable{
    @NotNull
    private Boolean cash=Boolean.FALSE;
    @NotNull
    private Boolean card=Boolean.FALSE;

    @Min(value = 0, message = "Deposit must be between 0 and 100")
    @Max(value = 100, message = "Deposit must be between 0 and 100")
    private Byte deposit=0;

    public PaymentType() {
    }

    public PaymentType(Boolean cash, Boolean card, Byte deposit) {
        this.cash = cash;
        this.card = card;
        this.deposit = deposit;
    }

    public Boolean getCash() {
        return cash;
    }

    public void setCash(Boolean cash) {
        this.cash = cash;
    }

    public Boolean getCard() {
        return card;
    }

    public void setCard(Boolean card) {
        this.card = card;
    }

    public Byte getDeposit() {
        return deposit;
    }

    public void setDeposit(Byte deposit) {
        this.deposit = deposit;
    }

    @Override
    public PaymentType clone() throws CloneNotSupportedException {
        return (PaymentType) super.clone();
    }
}
