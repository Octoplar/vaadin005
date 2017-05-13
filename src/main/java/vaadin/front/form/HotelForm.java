package vaadin.front.form;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import vaadin.MyUI;
import vaadin.front.converter.LocalDateToLongDaysConverter;
import vaadin.back.entity.Hotel;
import vaadin.back.entity.HotelCategory;
import vaadin.back.service.HotelCategoryService;
import vaadin.back.service.HotelService;

import javax.persistence.OptimisticLockException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


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

    //entity to display
    private Hotel hotel;


    //owner reference
    private MyUI ui;

    //binder
    private Binder<Hotel> hotelBinder;


    public HotelForm(MyUI ui, HotelService hotelService, HotelCategoryService hotelCategoryService) {
        this.ui = ui;
        this.hotelService=hotelService;
        this.hotelCategoryService=hotelCategoryService;

        Layout buttons=new HorizontalLayout(save, delete);

        addComponents(name, address, rating, category, operatesFrom, url, description,buttons);

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
        description.setDescription("Any string up to 255");
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
        category.setItems(iterableToList(hotelCategoryService.findAll()));
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
        ui.refreshHotelGridContent();
        //hide form
        setVisible(false);
    }

    private void save(){
        //validate
        BinderValidationStatus<Hotel> validationStatus = hotelBinder.validate();

        if (validationStatus.hasErrors()) {
            Notification.show(ValidationErrorsListToString(validationStatus.getValidationErrors()));
            return;
        }
        //save
        try{
            hotelService.save(hotel);
        }
        catch (OptimisticLockException e){
            Notification.show("This hotel data is out of date, changes not saved.");
        }
        //refresh grid
        ui.refreshHotelGridContent();
        //hide form
        setVisible(false);
    }

    private void configureBinder(){
        hotelBinder=new Binder<>();

        hotelBinder.forField(name)
                .asRequired("Name is required")
                .withValidator(s->(s!=null&&s.length()<=255), "Maximum name length is 255")
                .bind(Hotel::getName, Hotel::setName);

        hotelBinder.forField(address)
                .asRequired("Address is required")
                .withValidator(s->(s!=null&&s.length()<=255), "Maximum address length is 255")
                .bind(Hotel::getAddress, Hotel::setAddress);

        hotelBinder.forField(description)
                .withValidator(s->(s!=null&&s.length()<=255), "Maximum description length is 255")
                .bind(Hotel::getDescription, Hotel::setDescription);

        hotelBinder.forField(url)
                .asRequired("URL is required")
                .withValidator(s->(s!=null&&s.length()<=255), "Maximum url length is 255")
                .bind(Hotel::getUrl,Hotel::setUrl);

        hotelBinder.forField(rating)
                .withConverter(new StringToIntegerConverter(0, "Invalid integer value"))
                .asRequired("Rating is required")
                .withValidator(i->(i>0&&i<6), "Rating must be integer between 1..5 inclusive")
                .bind(Hotel::getRating, Hotel::setRating);

        hotelBinder.forField(operatesFrom)
                .withValidator(i->(i.compareTo(LocalDate.now())<0), "Date can not be future")
                .asRequired("Date is required")
                .withConverter(new LocalDateToLongDaysConverter())
                .bind(Hotel::getOperatesFrom, Hotel::setOperatesFrom);

        hotelBinder.forField(category).asRequired("Category is required").bind(Hotel::getCategory, Hotel::setCategory);
    }

    //readable errors notification
    private String ValidationErrorsListToString(List<ValidationResult> results){
        StringBuilder sb=new StringBuilder();
        for (ValidationResult result : results) {
            sb.append(result.getErrorMessage());
            sb.append("\r\n");
        }
        return sb.toString();
    }

    private <T> List<T> iterableToList(Iterable<T> iterable){
        if (iterable instanceof List)
            return (List<T>)iterable;
        List<T> result= new ArrayList<T>();
        iterable.forEach(result::add);
        return result;
    }

}
