package vaadin.front.view;

import com.vaadin.data.HasValue;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import vaadin.back.entity.Hotel;
import vaadin.back.entity.PaymentType;
import vaadin.back.service.HotelCategoryService;
import vaadin.back.service.HotelService;
import vaadin.front.form.HotelBulkForm;
import vaadin.front.form.HotelForm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Octoplar on 16.05.2017.
 */
@org.springframework.stereotype.Component
@UIScope
public class HotelView extends VerticalLayout implements View {
    public static final String NAME="HotelView";
    //Field_ID=================
    public static final String B_ADD_HOTEL="B_ADD_HOTEL";

    //service
    public final HotelService hotelService;
    public final HotelCategoryService hotelCategoryService;

    //components===============================================
    private Grid<Hotel> hotelGrid=new Grid<>();
    private HotelForm hotelForm;

    //toolbar components
    private TextField nameFilter=new TextField();
    private Button clearNameFilterButton = new Button(FontAwesome.TIMES);
    private TextField addressFilter=new TextField();
    private Button clearAddressFilterButton = new Button(FontAwesome.TIMES);
    private Button createNewHotelButton = new Button();
    //popup
    private PopupView bulkPopup;
    private HotelBulkForm hotelBulkForm;
    private Button bulkButton;




    @Autowired
    public HotelView(HotelCategoryService hotelCategoryService, HotelService hotelService) {
        this.hotelCategoryService = hotelCategoryService;
        this.hotelService = hotelService;
        //init and add components
        this.addComponents(configureToolbarLayout(),configureHotelGridAndFormLayout());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        refresh();
    }

    //popup callback
    public void hidePopup(){
        bulkPopup.setVisible(false);
        bulkPopup.setPopupVisible(false);
    }





    //set up content configuration to default
    public void refresh(){
        //eager load items in grid
        //hotelGrid.setItems(iterableToList(hotelService.findAll()));

        //lazy load items in grid
        lazyListEntriesAll();

        //hide form
        hotelForm.setVisible(false);
        //refresh hotel categories list
        hotelForm.refreshCategories();
        //clear filters
        nameFilter.clear();
        addressFilter.clear();
    }

    //=====================================privates===================================================================


    //==============build and configure components
    private Layout configureToolbarLayout(){
        //name filter
        nameFilter.setPlaceholder("filter by name");
        nameFilter.setValueChangeMode(ValueChangeMode.LAZY);
        nameFilter.addValueChangeListener(e->nameFilterValueChange());
        nameFilter.addFocusListener(e->addressFilter.clear());

        clearNameFilterButton.setDescription("Clear name filter");
        clearNameFilterButton.addClickListener(e->clearNameFilterButtonClick());
        createNewHotelButton.setId(B_ADD_HOTEL);

        HorizontalLayout nameFilterLayout=new HorizontalLayout(nameFilter, clearNameFilterButton);
        nameFilterLayout.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);


        //address filter
        addressFilter.setPlaceholder("filter by address");
        addressFilter.setValueChangeMode(ValueChangeMode.LAZY);
        addressFilter.addValueChangeListener(e-> addressFilterValueChange());
        addressFilter.addFocusListener(e->nameFilter.clear());

        clearAddressFilterButton.setDescription("Clear address filter");
        clearAddressFilterButton.addClickListener(e->clearAddressFilterButtonClick());

        HorizontalLayout addressFilterLayout=new HorizontalLayout(addressFilter, clearAddressFilterButton);
        addressFilterLayout.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        //add new hotel button
        createNewHotelButton.setCaption("Add new hotel");
        createNewHotelButton.addClickListener(e-> createNewHotelButtonClick());


        //add popup
        hotelBulkForm=new HotelBulkForm(hotelService, hotelCategoryService, this);
        hotelBulkForm.setVisible(true);
        //invisible
        bulkButton=new Button();
        bulkButton.setCaption("Bulk");
        bulkButton.setDescription("Show bulk form");
        bulkButton.addClickListener(e->bulkButtonClick());
        bulkButton.setVisible(false);
        bulkPopup=new PopupView(null, hotelBulkForm);
        bulkPopup.setVisible(false);
        bulkPopup.setHideOnMouseOut(false);
        //bulkPopup.addPopupVisibilityListener(e-> bulkPopupClick(e));

        //return result
        return new HorizontalLayout(nameFilterLayout, addressFilterLayout, createNewHotelButton, bulkButton, bulkPopup);
    }

    private Layout configureHotelGridAndFormLayout(){
        hotelForm = new HotelForm(this, hotelService, hotelCategoryService);
        //items to display

        //eager load items in grid
        //hotelGrid.setItems(iterableToList(hotelService.findAll()));


        //lazy load items in grid
        lazyListEntriesAll();



        //columns order and content
        hotelGrid.addColumn(hotel -> hotel.getName()).setCaption("Name");
        hotelGrid.addColumn(hotel -> hotel.getRating()).setCaption("Rating");
        hotelGrid.addColumn(hotel -> hotel.getAddress()).setCaption("Address");
        hotelGrid.addColumn(hotel -> hotel.getOperatesFrom()).setCaption("Operates from");
        hotelGrid.addColumn(hotel -> (hotel.getCategory()==null?"Undefined":hotel.getCategory().getName())).setCaption("Category");

        hotelGrid.addColumn(hotel->"<a href='" + hotel.getUrl() + "' target='_blank'>"+hotel.getUrl()+"</a>",
                new HtmlRenderer()).setCaption("URL");
        HorizontalLayout gridAndFormLayout=new HorizontalLayout(hotelGrid, hotelForm);

        //selection
        hotelGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        hotelGrid.asMultiSelect().addValueChangeListener(this::hotelGridOnValueChange);

        //resize components
        hotelForm.setSizeUndefined();
        hotelGrid.setSizeFull();
        gridAndFormLayout.setSizeFull();
        gridAndFormLayout.setExpandRatio(hotelGrid, 1);

        hotelForm.setVisible(false);

        return gridAndFormLayout;
    }

    //==============listeners
    private void clearNameFilterButtonClick(){
        nameFilter.clear();
    }

    private void clearAddressFilterButtonClick(){
        addressFilter.clear();
    }

    //change grid content
    private void nameFilterValueChange(){
        //case insensitive filter

        //eager load items in grid
        //hotelGrid.setItems(iterableToList(hotelService.findAllNameFilter(nameFilter.getValue())));

        //eager load items in grid
        lazyListEntriesNameFilter(nameFilter.getValue());
    }

    //change grid content
    private void addressFilterValueChange(){
        //case insensitive filter

        //eager load items in grid
        //hotelGrid.setItems(iterableToList(hotelService.findAllAddressFilter(addressFilter.getValue())));

        //lazy load items in grid
        lazyListEntriesAddressFilter(addressFilter.getValue());
    }

    private void createNewHotelButtonClick(){
        //clear selection
        hotelGrid.asMultiSelect().clear();
        //new instance for manage
        hotelForm.setHotel(new Hotel("", "", 1, 1L, null, "", "", new PaymentType()));

        hotelForm.setVisible(true);
    }

    private void  bulkButtonClick(){
        bulkPopup.setVisible(true);
        bulkPopup.setPopupVisible(true);
        hotelBulkForm.setManagedItems(hotelGrid.asMultiSelect().getSelectedItems());
    }

    private void bulkPopupClick(PopupView.PopupVisibilityEvent e){
        if (e.isPopupVisible()) {
            hotelBulkForm.setManagedItems(hotelGrid.asMultiSelect().getSelectedItems());
            //bulkPopup.setPopupVisible(true);
        }
    }

    // hotel grid value change listener
    private void hotelGridOnValueChange(HasValue.ValueChangeEvent<Set<Hotel>> e){
        List<Hotel> values=new ArrayList<Hotel>(e.getValue());
        //0 case
        if (values.size()==0){
            bulkButton.setVisible(false);
            hotelForm.setVisible(false);
            return;
        }
        //1 case
        if (values.size()==1){
            bulkButton.setVisible(false);
            Hotel h=values.get(0);
            if(h==null)
                hotelForm.setVisible(false);
            else{
                hotelForm.setHotel(h);
                hotelForm.setVisible(true);
            }
        }
        //multi case
        else {
            hotelForm.setVisible(false);
            bulkButton.setVisible(true);
        }





    }

    //===================lazy===============================================================
    private void lazyListEntriesAll() {
        hotelGrid.setDataProvider(
                //todo
                //sortOrders not supported
                (sortOrders, offset, limit) ->
                        hotelService.findAll(offset, limit).stream(),
                () -> hotelService.AllCount()
        );
    }

    private void lazyListEntriesNameFilter(String filter) {
        hotelGrid.setDataProvider(
                //todo
                //sortOrders not supported
                (sortOrders, offset, limit) ->
                        hotelService.findAllNameFilter(filter, offset, limit).stream(),
                () -> hotelService.nameFilterCount(filter)
        );
    }

    private void lazyListEntriesAddressFilter(String filter) {
        hotelGrid.setDataProvider(
                //todo
                //sortOrders not supported
                (sortOrders, offset, limit) ->
                        hotelService.findAllAddressFilter(filter, offset, limit).stream(),
                () -> hotelService.addressFilterCount(filter)
        );
    }
}
