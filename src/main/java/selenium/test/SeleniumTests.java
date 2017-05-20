package selenium.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import vaadin.back.entity.Hotel;
import vaadin.back.entity.HotelCategory;
import vaadin.back.entity.PaymentType;
import vaadin.front.components.PaymentTypeField;
import vaadin.front.converter.LocalDateToLongDaysConverter;
import vaadin.front.form.HotelForm;
import vaadin.front.view.HotelView;

import java.time.format.DateTimeFormatter;
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
        //	public Hotel(String name, String address, Integer rating, Long operatesFrom,
        // HotelCategory category, String url, String description, PaymentType paymentType)

        Hotel testHotel=new Hotel("TEST_HOTEL_NAME",
                "address1321",
                3,
                333L,
                new HotelCategory("Hostel"),
                "some_url",
                "description here",
                new PaymentType(false, true, (byte) 90));
        addHotel(testHotel);

    }





    private void addHotel(Hotel hotel) throws InterruptedException {
        //open form by create_button click
        Thread.sleep(5000);
        myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(HotelView.B_ADD_HOTEL)));
        WebElement addBtn=driver.findElement(By.id(HotelView.B_ADD_HOTEL));
        addBtn.click();

        //fill data
        fillHotelFields(hotel);

        //save
        WebElement saveBtn=driver.findElement(By.id(HotelForm.B_SAVE));
        saveBtn.click();
    }

    private void fillHotelFields(Hotel hotel) throws InterruptedException {
        //name
        myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(HotelForm.F_NAME)));
        WebElement nameField=driver.findElement(By.id(HotelForm.F_NAME));
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>"+hotel.getName());
        nameField.sendKeys(hotel.getName());
        Thread.sleep(500);

        //address
        myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(HotelForm.F_ADDRESS)));
        WebElement addressField=driver.findElement(By.id(HotelForm.F_ADDRESS));
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>"+hotel.getAddress());
        addressField.sendKeys(hotel.getAddress());
        Thread.sleep(500);

        //rating
        myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(HotelForm.F_RATING)));
        WebElement ratingField=driver.findElement(By.id(HotelForm.F_RATING));
        ratingField.clear();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>"+hotel.getRating().toString());
        ratingField.sendKeys(hotel.getRating().toString());
        Thread.sleep(500);

        //description
        myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(HotelForm.F_DESCRIPTION)));
        WebElement descriptionField=driver.findElement(By.id(HotelForm.F_DESCRIPTION));
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>"+hotel.getDescription());
        descriptionField.sendKeys(hotel.getDescription());
        Thread.sleep(500);

        //url
        myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(HotelForm.F_URL)));
        WebElement urlField=driver.findElement(By.id(HotelForm.F_URL));
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>"+hotel.getUrl());
        urlField.sendKeys(hotel.getUrl());
        Thread.sleep(500);

        //category
        myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(HotelForm.CB_CATEGORY)));
        WebElement selectField=driver.findElement(By.id(HotelForm.CB_CATEGORY));
        Select select=new Select(selectField);
        String categoryStr=hotel.getCategory().getName();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>"+categoryStr);
        select.selectByVisibleText(categoryStr);
        Thread.sleep(500);

        //payment type
        PaymentType pt=hotel.getPaymentType();
        if (pt.isCard()){
            myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(PaymentTypeField.RBG_TYPE)));
            WebElement rbgField=driver.findElement(By.id(PaymentTypeField.RBG_TYPE));
            WebElement cardLabel=rbgField.findElement(By.id("gwt-uid-77"));
            WebElement label=rbgField.findElement(By.linkText("CARD"));
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>"+"CARD");
            cardLabel.click();
            Thread.sleep(500);


            myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(PaymentTypeField.F_DEPOSIT)));
            WebElement depositField=driver.findElement(By.id(PaymentTypeField.F_DEPOSIT));
            depositField.sendKeys(pt.getDeposit().toString());
        }
        else{
            myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(PaymentTypeField.RBG_TYPE)));
            WebElement rbgField=driver.findElement(By.id(PaymentTypeField.RBG_TYPE));
            WebElement cashLabel=rbgField.findElement(By.id("gwt-uid-78"));
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>"+"CASH");
            cashLabel.click();
        }
        Thread.sleep(500);


        //operates
        myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(HotelForm.CAL_OPERATES_FROM)));
        WebElement operatesField=driver.findElement(By.id(HotelForm.CAL_OPERATES_FROM));
        WebElement dateField=operatesField.findElement(By.className("v-textfield v-datefield-textfield"));

        dateField.clear();
        LocalDateToLongDaysConverter converter=new LocalDateToLongDaysConverter();
        String date=converter.convertToPresentation(hotel.getOperatesFrom(), null).format(dtf);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>"+date);
        dateField.sendKeys(date);
        Thread.sleep(500);

    }
}
