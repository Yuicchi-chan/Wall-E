package Processes;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Scanner;

public class GoogleScrape {
    static WebDriver driver;
    WebElement SendBox;
    private static final Logger log = LoggerFactory.getLogger(GoogleScrape.class);

    static int j = 0;
    public static void main(String[] args){
        System.setProperty("webdriver.chrome.driver",
                "E:\\Bots\\Yuudachi\\Python Scripts_and_Executables\\chromedriver.exe");
        driver = new ChromeDriver();

        GoogleScrape googleScrape = new GoogleScrape();
        Scanner Input = new Scanner(System.in);

        while(true)
            googleScrape.Scrape(Input.nextLine());
    }
    public String Scrape(String query) {

        String url ="https://google.com/search?q=" + query;
        driver.get(url);
        log.info(driver.getTitle());

        // Wait until the footer loads in
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(d -> d.findElement(new By.ByCssSelector("div[role=navigation]")));

        String haha = "";
        try {
            haha = driver.findElement(new By.ByCssSelector("div.xpdopen div[aria-level='3'][role^=head]")).getText();
            log.info("Featured Snippet: " + haha);
        }catch (Exception ignored){}

        // Dictionary
        try {
            haha = driver.findElement(new By.ByCssSelector("div.lr_container"))
                    .findElement(new By.ByCssSelector("div[jsname=x3Eknd]")).findElement(new By.ByCssSelector("div.vmod[jsname=r5Nvmf]"))
                    .findElement(new By.ByCssSelector("ol")).getText().replaceAll("\n", " ").split("2.")[0].replace("1.", "");
            log.info("Dictionary: " + haha);
        }catch (Exception ignored){}


        //THe side bar thing
        try {
            haha = driver.findElement(new By.ByCssSelector("div[role='complementary']"))
                    .findElement(new By.ByCssSelector("div[data-attrid='description']"))
                    .getText().replaceAll("Wikipedia", "");
            log.info("Complementary: "+haha);
        }catch (Exception ignored){}
        //driver.close();
        return haha;
    }
}
