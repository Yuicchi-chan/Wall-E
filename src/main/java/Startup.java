import com.fazecast.jSerialComm.SerialPort;
import javafx.scene.paint.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.Data;

public class Startup {

    private static final Logger log = LoggerFactory.getLogger(Startup.class);

    public static void main(String[] args) {
        SerialPort port;
        try{

            port = Startup.getArduino();

        }catch (Exception e){
            log.error(e.getMessage());
        }
    }


    static SerialPort getArduino() throws Exception {

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
                port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING,1000,0)
                if(port.openPort()){

                    return port;
                }else{
                    throw new RuntimeException("No response from Arduino, Port could not be opened!");
                }
            }
        }
        throw new NullPointerException("No Arduino Found");
    }

    static boolean ConnectArduino(){
        return true;
    }

}
