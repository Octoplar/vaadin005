package vaadin;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ContextLoaderListener;
import vaadin.front.view.CategoryView;
import vaadin.front.view.HotelView;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

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
    public static final String M_MENU="M_MENU";

    @WebListener
    public static class MyContextLoaderListener extends ContextLoaderListener {
    }

    @Configuration
    @EnableVaadin
    public static class MyConfiguration {
    }

    //views
    @Autowired
    private HotelView hotelView;
    @Autowired
    private CategoryView categoryView;


    //===================components====================================================================================
    private MenuBar menuBar;
    private Navigator navigator;


    @Override
    protected void init(VaadinRequest vaadinRequest) {

        // create layout
        VerticalLayout mainLayout = new VerticalLayout();
        // add menu
        mainLayout.addComponents(configureMenuBar());
        // create panel for main layout
        Panel mainPanel = new Panel();
        mainLayout.addComponent(mainPanel);
        mainPanel.setSizeFull();
        setContent(mainLayout);
        setSizeFull();
        super.setId("mainView");
        mainPanel.setId("mainPanel");
        mainLayout.setId("mainLayout");
        mainLayout.setSizeFull();
        mainLayout.setExpandRatio(mainPanel, 100);
        mainLayout.setMargin(false);

        // initialize navigator
        navigator = new Navigator(this, mainPanel);
        // register views
        navigator.addView(HotelView.NAME, hotelView);
        navigator.addView(CategoryView.NAME, categoryView);

        // navigate to hotels
        navigator.navigateTo(HotelView.NAME);

        setContent(mainLayout);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends SpringVaadinServlet {
    }










    //======================privates====================================================================================
    private Component configureMenuBar(){
        menuBar = new MenuBar();

        // create menu items
        menuBar.addItem("Hotel", command -> navigator.navigateTo(HotelView.NAME));
        menuBar.addItem("", null).setEnabled(false);
        menuBar.addItem("Categories", command -> navigator.navigateTo(CategoryView.NAME));
        menuBar.setId(M_MENU);


        return menuBar;

    }





}
