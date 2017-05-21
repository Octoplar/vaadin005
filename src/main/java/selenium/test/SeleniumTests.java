package selenium.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import vaadin.MyUI;
import vaadin.back.entity.Hotel;
import vaadin.back.entity.HotelCategory;
import vaadin.back.entity.PaymentType;
import vaadin.front.components.PaymentTypeField;
import vaadin.front.converter.LocalDateToLongDaysConverter;
import vaadin.front.form.HotelCategoryForm;
import vaadin.front.form.HotelForm;
import vaadin.front.view.CategoryView;
import vaadin.front.view.HotelView;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Octoplar on 20.05.2017.
 */
public class SeleniumTests extends AbstractTest {
    private WebDriver driver;
    WebDriverWait myWait;
    DateTimeFormatter dtf=DateTimeFormatter.ofPattern("dd.MM.yy");

    @Before
    public void init(){
        driver=new OperaDriver();
    }

    @After
    public void destroy() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    @Test
    public void simpleTest() throws InterruptedException {
        driver.get(URL);
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);

        //Create explicit wait.
        myWait = new WebDriverWait(driver, 10);

        //===========================================DATA_GENERATION====================================================
        Random rand=new Random();
        //new category
        HotelCategory newCategory=new HotelCategory("SELENIUM_NEW_CATEGORY_"+rand.nextInt());

        //name address rating operatesFrom category url description paymentType
        Hotel testHotel0=new Hotel("SELENIUM_TEST_HOTEL_NAME", "address1321", 3, 333L, new HotelCategory("Hostel"),
                "some_url", "description here", new PaymentType(false, true, (byte) 90));
        Hotel testHotel1=new Hotel("SELENIUM_TEST_HOTEL_NAME", "address1321", 3, 333L, newCategory, "some_url",
                "description here", new PaymentType(false, true, (byte) 90));
        Hotel testHotel2=new Hotel("SELENIUM_TEST_HOTEL_NAME", "address1321", 3, 333L, newCategory, "some_url",
                "description here", new PaymentType(true, false, null));
        //==============================================================================================================


        Thread.sleep(5000);
        //addHotel(testHotel);
        goToCategories();
        addCategory(newCategory);
        goToHotels();
        addHotel(testHotel0);
        addHotel(testHotel1);
        addHotel(testHotel2);

        //no assertion


    }




    //hotel view must be opened
    private void addHotel(Hotel hotel) throws InterruptedException {
        Thread.sleep(1000);
        //open form by create_button click
        myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(HotelView.B_ADD_HOTEL)));
        WebElement addBtn=driver.findElement(By.id(HotelView.B_ADD_HOTEL));
        addBtn.click();

        //fill data
        fillHotelFields(hotel);

        //save by button
        WebElement saveBtn=driver.findElement(By.id(HotelForm.B_SAVE));
        saveBtn.click();
    }

    //hotel form must be opened
    private void fillHotelFields(Hotel hotel) throws InterruptedException {
        //name
        myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(HotelForm.F_NAME)));
        WebElement nameField=driver.findElement(By.id(HotelForm.F_NAME));
        nameField.sendKeys(hotel.getName());
        Thread.sleep(500);

        //address
        myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(HotelForm.F_ADDRESS)));
        WebElement addressField=driver.findElement(By.id(HotelForm.F_ADDRESS));
        addressField.sendKeys(hotel.getAddress());
        Thread.sleep(500);

        //rating
        myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(HotelForm.F_RATING)));
        WebElement ratingField=driver.findElement(By.id(HotelForm.F_RATING));
        ratingField.clear();
        ratingField.sendKeys(hotel.getRating().toString());
        Thread.sleep(500);

        //description
        myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(HotelForm.F_DESCRIPTION)));
        WebElement descriptionField=driver.findElement(By.id(HotelForm.F_DESCRIPTION));
        descriptionField.sendKeys(hotel.getDescription());
        Thread.sleep(500);

        //url
        myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(HotelForm.F_URL)));
        WebElement urlField=driver.findElement(By.id(HotelForm.F_URL));
        urlField.sendKeys(hotel.getUrl());
        Thread.sleep(500);

        //payment type
        PaymentType pt=hotel.getPaymentType();
        if (pt.isCard()){
            myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(PaymentTypeField.RBG_TYPE)));
            WebElement rbgField=driver.findElement(By.id(PaymentTypeField.RBG_TYPE));
            //find nested label
            WebElement label=rbgField.findElement(By.xpath("//*[contains(text(), 'Card')]"));
            label.click();
            Thread.sleep(500);


            myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(PaymentTypeField.F_DEPOSIT)));
            WebElement depositField=driver.findElement(By.id(PaymentTypeField.F_DEPOSIT));
            depositField.clear();
            depositField.sendKeys(pt.getDeposit().toString());
        }
        else{
            myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(PaymentTypeField.RBG_TYPE)));
            WebElement rbgField=driver.findElement(By.id(PaymentTypeField.RBG_TYPE));
            //find nested label
            WebElement label=rbgField.findElement(By.xpath("//*[contains(text(), 'Cash')]"));
            label.click();
        }
        Thread.sleep(500);

        //category
        myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(HotelForm.CB_CATEGORY)));
        WebElement selectField=driver.findElement(By.id(HotelForm.CB_CATEGORY));
        WebElement selectTextField=driver.findElement(By.xpath("//*[@class='v-filterselect-input']"));
        selectTextField.sendKeys(hotel.getCategory().getName());
        selectTextField.sendKeys(Keys.ENTER);

//        WebElement showButton=driver.findElement(By.xpath("//*[@class='v-filterselect-button']"));
//        showButton.click();
//        Thread.sleep(500);
//        WebElement category=selectField.findElement(By.xpath("//*[contains(text(), '"+hotel.getCategory().getName()+"')]"));
//        category.click();

        Thread.sleep(500);

        //operates
        myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(HotelForm.CAL_OPERATES_FROM)));
        WebElement operatesField=driver.findElement(By.id(HotelForm.CAL_OPERATES_FROM));
        WebElement dateField=operatesField.findElement(By.xpath("//*[@class='v-textfield v-datefield-textfield']"));

        dateField.clear();
        LocalDateToLongDaysConverter converter=new LocalDateToLongDaysConverter();
        String date=converter.convertToPresentation(hotel.getOperatesFrom(), null).format(dtf);
        dateField.sendKeys(date);
        Thread.sleep(500);

    }

    //category view must be opened
    private void addCategory(HotelCategory category) throws InterruptedException {
        Thread.sleep(1000);
        //open form by create_button click
        myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(CategoryView.B_ADD_CATEGORY)));
        WebElement addBtn=driver.findElement(By.id(CategoryView.B_ADD_CATEGORY));
        addBtn.click();

        //fill data
        fillCategoryFields(category);

        //save by button
        WebElement saveBtn=driver.findElement(By.id(HotelCategoryForm.B_CATEGORY_SAVE));
        saveBtn.click();
    }

    //category form must be opened
    private void fillCategoryFields(HotelCategory category) throws InterruptedException {
        //name
        myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(HotelCategoryForm.F_CATEGORY_NAME)));
        WebElement nameField=driver.findElement(By.id(HotelCategoryForm.F_CATEGORY_NAME));
        nameField.sendKeys(category.getName());
        Thread.sleep(500);
    }

    private void goToHotels(){
        myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(MyUI.M_MENU)));
        WebElement menu=driver.findElement(By.id(MyUI.M_MENU));
        List<WebElement> hotelItem=driver.findElements(By.xpath("//*[@class='v-menubar-menuitem']"));
        hotelItem.get(0).click();


    }
    private void goToCategories(){
        myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(MyUI.M_MENU)));
        WebElement menu=driver.findElement(By.id(MyUI.M_MENU));
        List<WebElement> hotelItem=driver.findElements(By.xpath("//*[@class='v-menubar-menuitem']"));
        hotelItem.get(1).click();
    }
}
