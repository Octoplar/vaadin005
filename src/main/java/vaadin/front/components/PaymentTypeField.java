package vaadin.front.components;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.RadioButtonGroup;
import vaadin.back.entity.PaymentType;

/**
 * Created by Octoplar on 19.05.2017.
 */
public class PaymentTypeField extends CustomField<PaymentType>{
    private static final String CASH="Cash";
    private static final String CARD="Card";



    private RadioButtonGroup<String> radioGroup;

    public PaymentTypeField() {
        radioGroup=new RadioButtonGroup<>("Payment type");
        radioGroup.setItems("Cash", "Card");
    }

    @Override
    protected Component initContent() {
        return null;
    }

    @Override
    protected void doSetValue(PaymentType paymentType) {

    }

    @Override
    public PaymentType getValue() {
        return null;
    }
}


//@SuppressWarnings("serial")
//public class FreeServiceField extends CustomField<FreeServices> {
//
//    CheckBox breakfast = new CheckBox();
//    CheckBox towels = new CheckBox();
//    CheckBox spirits = new CheckBox();
//
//    private FreeServices value;
//    private String caption = "Free";
//
//    public FreeServiceField(String caption) {
//        super();
//        this.caption = caption;
//    }
//
//    @Override
//    public FreeServices getValue() {
//        return value;
//    }
//
//    @Override
//    protected Component initContent() {
//        HorizontalLayout hor = new HorizontalLayout();
//        super.setCaption(caption);
//        breakfast.setIcon(VaadinIcons.SPOON);
//        towels.setIcon(VaadinIcons.SQUARE_SHADOW);
//        spirits.setIcon(VaadinIcons.TROPHY);
//
//        breakfast.setDescription("Breakfast");
//        towels.setDescription("Towels");
//        spirits.setDescription("Spirits");
//
//        breakfast.addValueChangeListener(l -> value.setBrekfast(l.getValue()));
//        towels.addValueChangeListener(l -> value.setTowels(l.getValue()));
//        spirits.addValueChangeListener(l -> value.setColdSpirits(l.getValue()));
//
//        hor.addComponent(breakfast);
//        hor.addComponent(towels);
//        hor.addComponent(spirits);
//
//        updateValues();
//
//        // value = new FreeServices();
//        return hor;
//    }
//
//    private void updateValues() {
//        if (getValue() != null) {
//            breakfast.setValue(value.isBrekfast());
//            towels.setValue(value.isTowels());
//            spirits.setValue(value.isColdSpirits());
//        }
//
//    }
//
//    @Override
//    protected void doSetValue(FreeServices value) {
//        this.value = new FreeServices(value);
//        updateValues();
//    }
//
//}
