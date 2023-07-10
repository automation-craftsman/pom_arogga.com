package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.io.FileInputStream;
import java.time.Duration;
import java.util.Properties;

public class Base {

    /**
     * Loaded the config.properties file.
     */
    public Base() {
        loadProperties();
    }

    public static WebDriver driver;
    public static Properties prop;

    private static final ThreadLocal<WebDriver> tDriver = new ThreadLocal<>();

    private static void setDriver(WebDriver driver) {
        Base.tDriver.set(driver);
    }

    public static WebDriver getDriver() {
        return tDriver.get();
    }


    /**
     * This method will initiate the thread safe driver instance of user specified browser.
     *
     * @param browser holds the user specified name of the browser to run the tests.
     */
    public static WebDriver getBrowser(String browser) {

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");

        if (browser.equalsIgnoreCase("Chrome")) {
            driver = new ChromeDriver(options);
            setDriver(driver);

        } else if (browser.equalsIgnoreCase("Firefox")) {
            driver = new FirefoxDriver();
            setDriver(driver);

        } else {
            System.out.println("[!] Invalid option provided. Initiating Chrome driver as default.");
            driver = new ChromeDriver(options);
            setDriver(driver);
        }

        return getDriver();
    }

    /**
     * Generic method to load the config.properties file
     */
    public static void loadProperties() {
        prop = new Properties();
        try {
            FileInputStream fis = new FileInputStream(".\\src\\test\\config\\config.properties");
            prop.load(fis);

        } catch (Exception e) {
            System.out.println("[!] Error loading the config file.");
        }

    }

    @BeforeSuite
    public static synchronized void setUp() {
        driver = getBrowser(prop.getProperty("browser"));
//		driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get(prop.getProperty("url"));
    }

    /**
     * Method to close the browser and clear ThreadLocal
     */
    @AfterSuite
    public static synchronized void cleanUp() {

        getDriver().quit();
    }

}
