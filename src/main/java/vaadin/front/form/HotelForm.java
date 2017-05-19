package vaadin.front.form;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import vaadin.back.entity.Hotel;
import vaadin.back.entity.HotelCategory;
import vaadin.back.service.HotelCategoryService;
import vaadin.back.service.HotelService;
import vaadin.front.components.PaymentTypeField;
import vaadin.front.converter.LocalDateToLongDaysConverter;
import vaadin.front.validator.*;
import vaadin.front.view.HotelView;

import javax.persistence.OptimisticLockException;

import static vaadin.util.HotelUtils.validationErrorsListToString;


/**
 * Created by Octoplar on 03.05.2017.
 */
public class HotelForm extends FormLayout {

    //services
    private HotelService hotelService;
    private HotelCategoryService hotelCategoryService;

    //components===========================
    private TextField name=new TextField("Name");
    private TextField address=new TextField("Address");
    private TextField rating=new TextField("Rating");
    private ComboBox<HotelCategory> category=new ComboBox<>("Category");
    private DateField operatesFrom=new DateField("Operates from");
    private TextField url=new TextField("URL");
    private Button save = new Button("Save");
    private Button delete = new Button("Delete");

    private TextArea description = new TextArea("Description");
    private PaymentTypeField paymentTypeField=new PaymentTypeField("Payment type");

    //entity to display
    private Hotel hotel;


    //owner reference
    private HotelView ui;

    //binder
    private Binder<Hotel> hotelBinder;


    public HotelForm(HotelView ui, HotelService hotelService, HotelCategoryService hotelCategoryService) {
        this.ui = ui;
        this.hotelService=hotelService;
        this.hotelCategoryService=hotelCategoryService;

        //init and add components
        Layout buttons=new HorizontalLayout(save, delete);
        addComponents(name, address, rating, category, paymentTypeField, operatesFrom, url, description,buttons);

        //=========================comboBox=====================================

        //caption for items
        category.setItemCaptionGenerator(
                (ItemCaptionGenerator<HotelCategory>) (c) -> c==null||c.getName()==null?"":c.getName());
        refreshCategories();
        //=====================buttons=============================================
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        //=====================binding=============================================
        configureBinder();

        //=====================listeners=============================================
        save.addClickListener(e->save());
        delete.addClickListener(e->delete());

        //tooltips
        name.setDescription("Any string up to 255");
        address.setDescription("Any string up to 255");
        rating.setDescription("Integer value from 1 to 5 inclusive");
        category.setDescription("Category from drop list");
        operatesFrom.setDescription("Date from Dec 02 BDT 292269055 to now exclusive");
        url.setDescription("Any string up to 255");
        description.setDescription("Any string up to 65535");
        paymentTypeField.setDescription("Select payment type");
    }

    public Hotel getHotel() {
        return hotel;
    }
    public void setHotel(Hotel entity){
        try {
            this.hotel=entity.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }


        if (hotel.getCategory()==null){
            //clear caption here in null value case.
            //ItemCaptionGenerator not works (???)
            category.setSelectedItem(new HotelCategory(""));
        }
        //refresh categories list to display
        refreshCategories();

        //bind bean to form
        hotelBinder.setBean(hotel);



        delete.setVisible(hotel.isPersisted());
        name.selectAll();
    }

    //refresh categories values
    public void refreshCategories(){
        category.setItems(hotelCategoryService.findAll());
    }


    //=====================================privates====================================================================
    private void delete(){
        //delete
        try{

            hotelService.delete(this.hotel);
        }
        catch (OptimisticLockException e){
            Notification.show("This hotel data is out of date, changes not saved.");
        }

        //refresh grid
        ui.refresh();
        //hide form
        setVisible(false);
    }

    private void save(){
        //validate
        BinderValidationStatus<Hotel> validationStatus = hotelBinder.validate();

        if (validationStatus.hasErrors()) {
            Notification.show(validationErrorsListToString(validationStatus.getValidationErrors()));
            return;
        }
        //save
        try{
            //TODO
            //binder not work???
            //hotel.setPaymentType(paymentTypeField.getValue());
            hotelService.save(hotel);
        }
        catch (OptimisticLockException e){
            Notification.show("This hotel data is out of date, changes not saved.");
        }
        //refresh grid
        ui.refresh();
        //hide form
        setVisible(false);
    }

    private void configureBinder(){
        hotelBinder=new Binder<>();

        hotelBinder.forField(name)
                .asRequired("Name is required")
                .withValidator(new HotelNamePredicate(), HotelNamePredicate.MESSAGE)
                .bind(Hotel::getName, Hotel::setName);

        hotelBinder.forField(address)
                .asRequired("Address is required")
                .withValidator(new HotelAddressPredicate(), HotelAddressPredicate.MESSAGE)
                .bind(Hotel::getAddress, Hotel::setAddress);

        hotelBinder.forField(description)
                .withValidator(new HotelDescriptionPredicate(), HotelDescriptionPredicate.MESSAGE)
                .bind(Hotel::getDescription, Hotel::setDescription);

        hotelBinder.forField(url)
                .asRequired("URL is required")
                .withValidator(new HotelUrlPredicate(), HotelUrlPredicate.MESSAGE)
                .bind(Hotel::getUrl,Hotel::setUrl);

        hotelBinder.forField(rating)
                .asRequired("Rating is required")
                .withConverter(new StringToIntegerConverter(0, "Invalid integer value"))
                .withValidator(new HotelRatingPredicate(), HotelRatingPredicate.MESSAGE)
                .bind(Hotel::getRating, Hotel::setRating);

        hotelBinder.forField(operatesFrom)
                .asRequired("Date is required")
                .withValidator(new HotelOperatesFromPredicate(), HotelOperatesFromPredicate.MESSAGE)
                .withConverter(new LocalDateToLongDaysConverter())
                .bind(Hotel::getOperatesFrom, Hotel::setOperatesFrom);

        hotelBinder.forField(category).asRequired("Category is required").bind(Hotel::getCategory, Hotel::setCategory);


        hotelBinder.forField(paymentTypeField)
                .withValidator(new PaymentTypePredicate(), PaymentTypePredicate.MESSAGE)
                .bind(Hotel::getPaymentType, Hotel::setPaymentType);
    }



}
