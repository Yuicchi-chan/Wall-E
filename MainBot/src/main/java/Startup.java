import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Startup {

    private static final Logger log = LoggerFactory.getLogger(Startup.class);
    private static SerialPort port = null;
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        try{

            Startup.getArduino();
            // Wait 5 seconds for initialization
            Thread.sleep(5 * 1000);

            log.info("Connection With arduino: " + (Startup.connectArduino()?"Successful":"Failed"));
            log.info("Starting Up Server Socket");

           new Thread(()->{
                try {
                    Thread.sleep(10000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }catch (Exception e){
            log.error(e.getMessage());
        }

    }


    static void getArduino() {

        int BaudRate = 9600;
        int DataBits = 8;
        int StopBits = SerialPort.ONE_STOP_BIT;
        int Parity = SerialPort.NO_PARITY;

        SerialPort[] AvailablePorts = SerialPort.getCommPorts();

        // use the for loop to print the available serial ports
        for(SerialPort port : AvailablePorts){
            log.info("\n  " + port.toString());
            if(port.getDescriptivePortName().contains("USB")){

                log.info("Arduino Found");
                log.info(String.valueOf(port.getDescriptivePortName()));

                port.setComPortParameters(BaudRate, DataBits, StopBits, Parity);
                port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING,1000,0);
                if(port.openPort()){
                    log.info("Opened!");
                    Startup.port = port;
                }else{
                    throw new RuntimeException("No response from Arduino, Port could not be opened!");
                }
            }
        }
    }

    static boolean connectArduino() throws InterruptedException {
        // Lets up constant listener
        new Thread(()->{
            while(true){
                byte[] readBuffer;
                int number_of_bytes = port.bytesAvailable();

                if(number_of_bytes>0){
                    readBuffer = new byte[number_of_bytes];
                    port.readBytes(readBuffer, number_of_bytes);
                    String Message = new String(readBuffer, StandardCharsets.UTF_8);
                    MessageReceived(Message);
                }
            }
        }).start();


        // Send a SYN request
        System.out.println("writing");
        writeMessage("SYN");
        Thread.sleep(100);
        return true;
    }

    static void MessageReceived(String Message){
        String[] Messages = Message.split("\n");
        for(String m:Messages){

            log.info("Message Received: " + m); //Should be ACK

            if(m.equals("Ready")){
                writeMessage("Neck");
                writeMessage("90");
            }

        }
    }

    static void writeMessage(String Msg){
        byte[] WriteBuffer = Msg.getBytes(StandardCharsets.UTF_8);
        port.writeBytes(WriteBuffer, WriteBuffer.length);
    }



}
