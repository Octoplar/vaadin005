package selenium.test;

/**
 * Created by Octoplar on 20.05.2017.
 */
public class AbstractTest {
    public static final String OPERA_DRIVER="d:\\PROG\\Java\\install\\operadriver_win64\\operadriver.exe";
    public static final String CHROME_DRIVER="d:\\PROG\\Java\\install\\chromedriver_win32\\ochromedriver.exe";
    public static final String URL="http://localhost:8080";


    public AbstractTest() {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER);
        System.setProperty("webdriver.opera.driver", OPERA_DRIVER);
    }

}
