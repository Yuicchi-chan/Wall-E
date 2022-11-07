
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.table.TableRowSorter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.Scanner;
import java.util.logging.Level;

public class GoogleScrape {
    static WebDriver driver;
    WebElement SendBox;
    private static final Logger log = LoggerFactory.getLogger(GoogleScrape.class);
    static ServerSocket serverSocket;
    static int j = 0;


    public static void main(String[] args){
        System.setProperty("webdriver.chrome.driver",
                "E:\\Python Projects & files\\executables\\chromedriver.exe");
        driver = new ChromeDriver();

        GoogleScrape googleScrape = new GoogleScrape();

        googleScrape.socketInitializer(50000);

        // Listen for a new module connection all the time.
        Thread Listener = new Thread(() -> {
            while (true) {
                Socket socket = null;
                try {
                    // Accepts the connection if there is one present
                    socket = serverSocket.accept();
                    onAccept(socket);
                } catch (IOException | InterruptedException | ClassNotFoundException e) {
                    log.error("There was some error in the connection! Waiting for another Connection! " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        Listener.setName("Listener - Thread");
        Listener.start();

    }

    public static void onAccept(Socket socket) throws InterruptedException, IOException, ClassNotFoundException {

        Thread.sleep(100);

        ObjectInputStream objinput = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream objoutput = new ObjectOutputStream(socket.getOutputStream());

        log.info("Connected Bot: " + objinput.readObject().toString());
        objoutput.writeObject("Connected");

        while(true){
            String query = objinput.readObject().toString();
            objoutput.writeObject(Scrape(query));
        }
    }


    public static String Scrape(String query) {

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

    public void socketInitializer(int portNumber) {
        try{
            serverSocket = new ServerSocket(portNumber);
            log.info("Successfully created a Server Socket at port " + serverSocket.getLocalPort() + " with a timeout of " + serverSocket.getSoTimeout());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
