package vaadin;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.HasValue;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ContextLoaderListener;
import vaadin.back.entity.Hotel;
import vaadin.back.entity.HotelCategory;
import vaadin.back.service.HotelCategoryService;
import vaadin.back.service.HotelService;
import vaadin.front.form.HotelCategoryForm;
import vaadin.front.form.HotelForm;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import java.util.*;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@SpringUI
@Theme("mytheme")
public class MyUI extends UI {

    @WebListener
    public static class MyContextLoaderListener extends ContextLoaderListener {
    }

    @Configuration
    @EnableVaadin
    public static class MyConfiguration {
    }

    @Autowired
    public HotelService hotelService;
    @Autowired
    public HotelCategoryService hotelCategoryService;



    //===================components====================================================================================

    //===========================menu=========================================
    private MenuBar menuBar=new MenuBar();

    //content mapping
    private Map<MenuBar.MenuItem, Layout> contentMap;
    private Layout currentContent;

    //content references to handle menu
    private Layout hotelEditorContent;
    private Layout hotelCategoryEditorContent;
    private Layout menuLayout;

    //handler
    private MenuBar.Command menuHandler=new MenuBar.Command() {
        @Override
        public void menuSelected(MenuBar.MenuItem menuItem) {
            //refresh content
            refreshHotelCategoryGridContent();
            refreshHotelGridContent();
            //set content by selected menuItem
            currentContent=contentMap.get(menuItem);

            repaint();



        }
    };
    //tabs
    private MenuBar.MenuItem menuItemHotel=menuBar.addItem("Hotels", null, menuHandler);
    private MenuBar.MenuItem menuItemHotelCategories=menuBar.addItem("Hotel_categories", null, menuHandler);


    //menuItem 1 components===============================================
    private Grid<Hotel> hotelGrid=new Grid<>();
    private HotelForm hotelForm;

    //toolbar components
    private TextField nameFilter=new TextField();
    private Button clearNameFilterButton = new Button(FontAwesome.TIMES);
    private TextField addressFilter=new TextField();
    private Button clearAddressFilterButton = new Button(FontAwesome.TIMES);
    private Button createNewHotelButton = new Button();

    //menuItem 2 components===============================================
    private Grid<HotelCategory> hotelCategoryGrid;
    private HotelCategoryForm hotelCategoryForm;
    private Button createNewHotelCategoryButton;
    private Button deleteSelectedHotelCategoryButton;






    @Override
    protected void init(VaadinRequest vaadinRequest) {
        //content mapping configuration
        hotelEditorContent=new VerticalLayout(configureToolbarLayout(),configureHotelGridAndFormLayout());
        hotelCategoryEditorContent=new VerticalLayout(configureHotelCategoryPageLayout());
        menuLayout=configureMenuBarLayout();

        contentMap=new IdentityHashMap<>();
        contentMap.put(menuItemHotel, hotelEditorContent);
        contentMap.put(menuItemHotelCategories, hotelCategoryEditorContent);

        //set hotel editor as default content
        currentContent=hotelEditorContent;

        //show content
        repaint();
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends SpringVaadinServlet {
    }



    //refresh grid content
    public void refreshHotelGridContent(){
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
    public void refreshHotelCategoryGridContent(){
        hotelCategoryGrid.setItems(iterableToList(hotelCategoryService.findAll()));
        //hide form
        hotelCategoryForm.setVisible(false);
    }



    //======================privates====================================================================================



    private Layout configureToolbarLayout(){
        //name filter
        nameFilter.setPlaceholder("filter by name");
        nameFilter.setValueChangeMode(ValueChangeMode.LAZY);
        nameFilter.addValueChangeListener(e->nameFilterValueChange());
        nameFilter.addFocusListener(e->addressFilter.clear());

        clearNameFilterButton.setDescription("Clear name filter");
        clearNameFilterButton.addClickListener(e->clearNameFilterButtonClick());

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

        return new HorizontalLayout(nameFilterLayout, addressFilterLayout, createNewHotelButton);
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
        hotelGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        hotelGrid.asSingleSelect().addValueChangeListener(this::hotelGridOnValueChange);

        //resize components
        hotelForm.setSizeUndefined();
        hotelGrid.setSizeFull();
        gridAndFormLayout.setSizeFull();
        gridAndFormLayout.setExpandRatio(hotelGrid, 1);

        hotelForm.setVisible(false);

        return gridAndFormLayout;
    }

    private Layout configureHotelCategoryPageLayout(){
        //construct
        hotelCategoryGrid=new Grid<>();
        hotelCategoryForm=new HotelCategoryForm(this, hotelCategoryService);
        createNewHotelCategoryButton=new Button();
        deleteSelectedHotelCategoryButton =new Button();

        //grid config
        //content
        hotelCategoryGrid.setItems(iterableToList(hotelCategoryService.findAll()));
        //columns
        hotelCategoryGrid.addColumn(HotelCategory::getName).setCaption("Category name");
        //selection
        hotelCategoryGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        hotelCategoryGrid.asMultiSelect().addValueChangeListener(this::hotelCategoryGridOnValueChange);


        //form config
        hotelCategoryForm.setVisible(false);

        //toolbar config
        createNewHotelCategoryButton.setCaption("Create new hotel category");
        createNewHotelCategoryButton.addClickListener(e->createNewHotelCategoryButtonClick());
        deleteSelectedHotelCategoryButton.setCaption("Delete selected");
        deleteSelectedHotelCategoryButton.addClickListener(e-> deleteSelectedHotelCategoryButtonClick());
        deleteSelectedHotelCategoryButton.setVisible(false);


        //layouts===========
        HorizontalLayout gridAndFormLayout=new HorizontalLayout(hotelCategoryGrid, hotelCategoryForm);
        HorizontalLayout toolbar=new HorizontalLayout(createNewHotelCategoryButton, deleteSelectedHotelCategoryButton);
        Layout result=new VerticalLayout(toolbar, gridAndFormLayout);

        //resize=============
        hotelCategoryForm.setSizeUndefined();
        hotelCategoryGrid.setSizeFull();
        gridAndFormLayout.setSizeFull();
        gridAndFormLayout.setExpandRatio(hotelCategoryGrid, 1);

        return  result;
    }


    private Layout configureMenuBarLayout(){
        return new VerticalLayout(menuBar);
    }


    // hotel grid value change listener
    private void hotelGridOnValueChange(HasValue.ValueChangeEvent<Hotel> e){
        Hotel h=e.getValue();
        if(h==null)
            hotelForm.setVisible(false);
        else{
            hotelForm.setHotel(h);
            hotelForm.setVisible(true);
        }
    }

    //hotel category grid value change listener
    private void hotelCategoryGridOnValueChange(HasValue.ValueChangeEvent<Set<HotelCategory>> e){
        List<HotelCategory> values=new ArrayList<>(e.getValue());
        //selected one
        if (values.size()==1){
            //transfer value to form
            hotelCategoryForm.setHotelCategory(values.get(0));
            hotelCategoryForm.setVisible(true);
            deleteSelectedHotelCategoryButton.setVisible(true);
            return;
        }
        //selected none
        if (values.size()==0){
            hotelCategoryForm.setVisible(false);
            deleteSelectedHotelCategoryButton.setVisible(false);
            return;
        }
        //selected multi
        else {
            hotelCategoryForm.setVisible(false);
            deleteSelectedHotelCategoryButton.setVisible(true);
        }

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
        hotelGrid.asSingleSelect().clear();
        //new instance for manage
        hotelForm.setHotel(new Hotel("", "", 1, 1L, null, "", ""));

        hotelForm.setVisible(true);
    }

    private void createNewHotelCategoryButtonClick(){
        //clear selection
        hotelCategoryGrid.asMultiSelect().clear();
        //new instance for manage
        hotelCategoryForm.setHotelCategory(new HotelCategory(""));

        hotelCategoryForm.setVisible(true);
    }

    private void deleteSelectedHotelCategoryButtonClick() {
        List<HotelCategory> markToDelete=new ArrayList<>(hotelCategoryGrid.getSelectedItems());
        //clear selection
        hotelCategoryGrid.asMultiSelect().clear();
        //hide button
        deleteSelectedHotelCategoryButton.setVisible(false);
        //delete
        hotelCategoryService.deleteAll(markToDelete);
        //refresh
        refreshHotelCategoryGridContent();



    }

    //clear name filter content
    private void clearNameFilterButtonClick(){
        nameFilter.clear();
    }
    //clear address filter content
    private void clearAddressFilterButtonClick(){
        addressFilter.clear();
    }

    //refresh layout items to show
    private void repaint(){
        VerticalLayout layout = new VerticalLayout();

        layout.addComponents(menuLayout, currentContent);

        setContent(layout);
    }

    //need for service compatibility
    private <T> List<T> iterableToList(Iterable<T> iterable){
        if (iterable instanceof List)
            return (List<T>)iterable;
        List<T> result= new ArrayList<T>();
        iterable.forEach(result::add);
        return result;
    }
    //===================lazy===============================================================
    private void lazyListEntriesAll() {
        hotelGrid.setDataProvider(
                //todo
                //sortOrders not supported
                (sortOrders, offset, limit) ->
                        iterableToList(hotelService.findAll(offset, limit)).stream(),
                () -> hotelService.AllCount()
        );
    }

    private void lazyListEntriesNameFilter(String filter) {
        hotelGrid.setDataProvider(
                //todo
                //sortOrders not supported
                (sortOrders, offset, limit) ->
                        iterableToList(hotelService.findAllNameFilter(filter, offset, limit)).stream(),
                () -> hotelService.nameFilterCount(filter)
        );
    }

    private void lazyListEntriesAddressFilter(String filter) {
        hotelGrid.setDataProvider(
                //todo
                //sortOrders not supported
                (sortOrders, offset, limit) ->
                        iterableToList(hotelService.findAllAddressFilter(filter, offset, limit)).stream(),
                () -> hotelService.addressFilterCount(filter)
        );
    }
}