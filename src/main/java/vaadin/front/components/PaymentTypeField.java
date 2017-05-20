package vaadin.front.components;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import vaadin.back.entity.PaymentType;
import vaadin.front.converter.StringToByteConverter;

import java.util.Optional;

/**
 * Created by Octoplar on 19.05.2017.
 */
public class PaymentTypeField extends CustomField<PaymentType>{
    private static final String CASH="Cash";
    private static final String CARD="Card";


    private String caption;
    //components
    private RadioButtonGroup<String> radioGroup;
    private TextField guaranty;
    private Label label;
    private Binder<PaymentType> binder;
    private Layout layout;

    //values
    private PaymentType oldValue;
    private PaymentType value;



    public PaymentTypeField(String caption) {
        this.caption = caption;

        //component config
        radioGroup=new RadioButtonGroup<>("Payment type");
        radioGroup.setItems(CARD, CASH);
        radioGroup.setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        radioGroup.addValueChangeListener(e->radioOnValueChange());
        radioGroup.addContextClickListener(e->radioOnValueChange());

        guaranty=new TextField();
        guaranty.setPlaceholder("Guaranty Deposit");
        guaranty.setValueChangeMode(ValueChangeMode.LAZY);
        guaranty.addValueChangeListener(e->depositOnValueChange());

        label=new Label("Payment will be made directly in hotel");
        layout=new VerticalLayout(radioGroup, guaranty, label);
        layout.setSizeUndefined();

        //binder
        binder=new Binder<>();
        binder.forField(guaranty)
                .asRequired("Value is required")
                .withConverter(new StringToByteConverter())
                .withValidator(i->(i>=0&&i<=100), "Value must be between 0 and 100")
                .bind(PaymentType::getDeposit, PaymentType::setDeposit);

        value=new PaymentType(false, false, (byte) 0);
        oldValue=new PaymentType(false, false, (byte) 0);
    }

    @Override
    protected Component initContent() {
        super.setCaption(caption);

        updateView();

        return layout;

    }


    @Override
    protected void doSetValue(PaymentType paymentType) {
        //handle null case and clone object
        if (paymentType==null)
            value=new PaymentType(false, false, (byte) 0);
        else{
            try {
                this.value=paymentType.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        //null replacement
        if (value.isCard()==null)
            value.setCard(false);
        if (value.isCash()==null)
            value.setCash(false);
        if (value.getDeposit()==null)
            value.setDeposit((byte) 0);

        //prevent double selection
        if (value.isCard()&&value.isCash()){
            value.setCard(false);
            value.setCash(false);
        }
        //clear deposit in cash selection case
        if (value.isCash())
            value.setDeposit((byte) 0);

        //copy current value to old value
        try {
            this.oldValue=value.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        //binder
        binder.setBean(value);
        //radioButton selection
        radioGroup.clear();
        if (value.isCash())
            radioGroup.setSelectedItem(CASH);
        if (value.isCard())
            radioGroup.setSelectedItem(CARD);

        updateView();
    }

    @Override
    public PaymentType getValue() {
        try {
            return value.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public PaymentType getOldValue() {
        try {
            return oldValue.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }


    //inner fields validation result
    public BinderValidationStatus<PaymentType> getValidationStatus(){
        return binder.validate();
    }


    //privates==========================================================================================================

    private void radioOnValueChange() {
        //save old value
        try {
            oldValue=value.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        //resolve selection
        Optional<String> optional = radioGroup.getSelectedItem();
        String item=optional.isPresent()?optional.get():null;
        if (CARD.equals(item)){
            value.setCard(true);
            value.setCash(false);
        }

        if (CASH.equals(item)){
            value.setCash(true);
            value.setCard(false);
        }
        updateView();
    }

    private void depositOnValueChange(){
        //validate
        BinderValidationStatus<PaymentType> validationStatus = binder.validate();

        if (validationStatus.hasErrors()) {
            //do nothing
            //changes in value field not saved
            return;
        }
        else{
            //save old value
            try {
                oldValue=value.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            //update new value
            value.setDeposit(Byte.parseByte(guaranty.getValue()));
        }
        updateView();
    }

    private void updateView(){
        //reset visibility
        guaranty.setVisible(false);
        label.setVisible(false);


        if(value.isCard()){
            guaranty.setVisible(true);
        }

        if(value.isCash()){
            label.setVisible(true);
        }
    }
}

