import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class Startup {

    private static final Logger log = LoggerFactory.getLogger(Startup.class);
    private static SerialPort port = null;

    public static void main(String[] args) {
        try{

            Startup.getArduino();
            // Wait 5 seconds for initialization
            Thread.sleep(5 * 1000);

            log.info(String.valueOf(Startup.connectArduino()));

        }catch (Exception e){
            log.error(e.getMessage());
        }

    }


    static void getArduino() {

        if(port.isOpen())
           return;

        int BaudRate = 9600;
        int DataBits = 8;
        int StopBits = SerialPort.ONE_STOP_BIT;
        int Parity = SerialPort.NO_PARITY;

        SerialPort[] AvailablePorts = SerialPort.getCommPorts();

        // use the for loop to print the available serial ports
        for(SerialPort port : AvailablePorts){
            log.info("\n  " + port.toString());
            if(port.getDescriptivePortName().contains("Arudino")){

                log.info("Arduino Found");
                port.setComPortParameters(BaudRate, DataBits, StopBits, Parity);
                port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING,1000,0);
                if(port.openPort()){
                    Startup.port = port;
                }else{
                    throw new RuntimeException("No response from Arduino, Port could not be opened!");
                }
            }
        }
        throw new NullPointerException("No Arduino Found");
    }

    static boolean connectArduino() {
        // Create a buffer
        byte[] readBuffer= new byte[1024];

        // Send a SYN request
        byte[] WriteBuffer = "SYN".getBytes(StandardCharsets.UTF_8);
        port.writeBytes(WriteBuffer, WriteBuffer.length);


        port.readBytes(readBuffer, 1024);
        String Message = new String(readBuffer, StandardCharsets.UTF_8);
        return Message.equals("ACK");
    }

}
