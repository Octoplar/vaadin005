package vaadin.front.validator;

import com.vaadin.server.SerializablePredicate;
import vaadin.back.entity.PaymentType;

/**
 * Created by Octoplar on 19.05.2017.
 */
public class PaymentTypePredicate  implements SerializablePredicate<PaymentType> {
    public  static final String MESSAGE="Please configure payment type";

    @Override
    public boolean test(PaymentType paymentType) {
        //test for null
        if (paymentType==null||paymentType.getDeposit()==null||paymentType.isCash()==null||paymentType.isCard()==null)
            return false;
        //dest deposit value
        if (paymentType.getDeposit()<0||paymentType.getDeposit()>100)
            return false;
        //only one payment type must be checked
        return paymentType.isCard()^paymentType.isCash();
    }
}
