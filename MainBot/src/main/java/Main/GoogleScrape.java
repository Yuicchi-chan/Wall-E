package Main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class GoogleScrape {
    static Socket socket;
    static ObjectOutputStream outputStream;
    static ObjectInputStream InputStream;


    private static final Logger log = LoggerFactory.getLogger(GoogleScrape.class);

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        connectServer();

        Scanner scanner = new Scanner(System.in);
        System.out.println("ready");
        while (true){
            System.out.println(startSearch(scanner.nextLine()));
        }

    }



    public static void connectServer() throws IOException, ClassNotFoundException{

        socket = new Socket("yuicchi.ddns.net", 61234);

        outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.writeObject("Wall-E");

        InputStream = new ObjectInputStream(socket.getInputStream());
        String Confirmation_Token = InputStream.readObject().toString();

        if(Confirmation_Token.equals("Connected")){
            log.info("Successfully Connected with the server!");

        }else{
            log.error("The authentication was not possible! There is some error with the Authtoken");
        }
    }

    static String startSearch(String query) throws IOException, ClassNotFoundException {
        outputStream.writeObject(query);

        return InputStream.readObject().toString();
    }
}
