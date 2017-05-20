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
 * Created by Octoplar on 20.05.2017.
 */
public class PaymentTypeField extends CustomField<PaymentType> {
    //Field_ID=================
    public static final String F_DEPOSIT="F_DEPOSIT";
    public static final String RBG_TYPE="RBG_TYPE";






    private static final String CASH="Cash";
    private static final String CARD="Card";




    private String caption;
    //components
    private RadioButtonGroup<String> radioGroup;
    private TextField guaranty;
    private Label label;
    private Binder<PaymentType> binder;


    //values
    private PaymentType oldValue;
    private PaymentType value;

    public PaymentTypeField(String caption) {
        this.caption = caption;

        //component config
        radioGroup=new RadioButtonGroup<>("Payment type");
        radioGroup.setItems(CARD, CASH);
        radioGroup.setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        radioGroup.setId(RBG_TYPE);



        guaranty=new TextField();
        guaranty.setPlaceholder("Guaranty Deposit");
        guaranty.setValueChangeMode(ValueChangeMode.LAZY);
        guaranty.setId(F_DEPOSIT);


        label=new Label("Payment directly in hotel");

        //binder
        binder=new Binder<>();
        binder.forField(guaranty)
                .withConverter(new StringToByteConverter())
                .withValidator(i->(i>=0&&i<=100), "Value must be between 0 and 100")
                .bind(PaymentType::getDeposit, PaymentType::setDeposit);

        binder.forField(radioGroup).bind(paymentType -> radioButtonProviderGetter(paymentType)
                , (paymentType, s) ->radioButtonProviderSetter(paymentType, s)  );
    }

    @Override
    protected Component initContent() {
        super.setCaption(caption);
        //listeners
        radioGroup.addValueChangeListener(e->radioOnValueChange());
        guaranty.addValueChangeListener(e->depositOnValueChange());

        radioGroup.setSelectedItem(null);
        guaranty.clear();
        //initial values
        oldValue=null;
        value=new PaymentType();
        VerticalLayout layout=new VerticalLayout(radioGroup, guaranty, label);
        layout.setSizeUndefined();
        layout.setMargin(false);

        return layout;
    }

    @Override
    protected void doSetValue(PaymentType paymentType) {
        PaymentType tmp;
        //null bean/properties protection============
        if (paymentType==null)
            tmp=new PaymentType(false, false, (byte) 0);
        else {
            try {
                tmp=paymentType.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
        if (tmp.isCard()==null)
            tmp.setCard(false);
        if (tmp.isCash()==null)
            tmp.setCash(false);
        if (tmp.getDeposit()==null)
            tmp.setDeposit((byte) 0);
        //============================================

        this.value = tmp;
        binder.setBean(this.value);
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

    public BinderValidationStatus<PaymentType> getValidationStatus(){
        return binder.validate();
    }

    //private========================================================================================================
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

    private void radioOnValueChange() {
        //save old value, prepare new value
        PaymentType newValue;
        try {
            oldValue=value.clone();
            newValue=value.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }


        //resolve selection
        Optional<String> optional = radioGroup.getSelectedItem();
        String item=optional.isPresent()?optional.get():null;
        if (CARD.equals(item)){
            newValue.setCard(true);
            newValue.setCash(false);
        }

        if (CASH.equals(item)){
            newValue.setCash(true);
            newValue.setCard(false);
        }
        //replace current value
        setValue(newValue);

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
            //save old value, prepare new value
            PaymentType newValue;
            try {
                oldValue=value.clone();
                newValue=value.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            newValue.setDeposit(Byte.parseByte(guaranty.getValue()));
            setValue(newValue);
        }
    }

    private String radioButtonProviderGetter(PaymentType p){
        //multi or none selection
        if (p.isCash().equals(p.isCard()))
            return null;
        if (p.isCard())
            return CARD;
        else
            return CASH;
    }
    private void radioButtonProviderSetter(PaymentType p, String s){
        if (s==null){
            p.setCash(false);
            p.setCard(false);
            return;
        }

        if (CARD.equals(s)){
            p.setCard(true);
            return;
        }
        if (CASH.equals(s)){
            p.setCash(true);
            return;
        }

    }
}
