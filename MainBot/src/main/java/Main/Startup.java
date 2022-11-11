package Main;

import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import javax.speech.Central;


public class Startup {

    private static final Logger log = LoggerFactory.getLogger(Startup.class);
    private static SerialPort port = null;
    private static ServerSocket serverSocket;
    public static boolean isIdle = false;
    static VoiceParser parser;

    public static void main(String[] args) {
        try{

            System.setProperty(
                    "freetts.voices",
                    "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
            // Register Engine
            Central.registerEngineCentral(
                    "com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");

            parser = new VoiceParser();
            log.info("Initializing");
            GoogleScrape.connectServer();
            //Main.Startup.getArduino();
            // Wait 5 seconds for initialization
             Thread.sleep(5 * 1000);

            //log.info("Connection With arduino: " + (Main.Startup.connectArduino()?"Successful":"Failed"));
            log.info("Starting Up Server Socket");
            serverInit();

            isIdle = true;
            Idle_Animations idle_animations = new Idle_Animations();
            idle_animations.doIdle();
            parser.setupAudio();
            parser.playAudio(new File("/home/pi/Wall-E/startup.wav"));
            Scanner sc = new Scanner(System.in);
            while(true){
                writeMessage(sc.nextLine());
            }

            //Process process = new ProcessBuilder("/usr/bin/python", "-m", "/home/pi/Wall-E/FaceRecog.py", "/home/pi/Wall-E/cascade.xml").start();
            //log.info("Python Process " + process.isAlive());
            //log.info(new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8));

        }catch (Exception e){
            log.error(e.getMessage());
        }
    }


    static void getArduino() {

        int BaudRate = 19200;
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
                log.info(port.getBaudRate() + " " + port.getDescriptivePortName() + " " + port.getPortDescription() + " " + port.getPortLocation());
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
        Thread Arduino = new Thread(()->{
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
        });
        Arduino.setName("Arduino");
        Arduino.start();

        //Writes to the thing
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

    static void serverInit() {
        try{
            serverSocket = new ServerSocket(25555);

            new Thread(()->{
                try {

                    onAccept(serverSocket.accept());

                } catch (IOException | InterruptedException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }).start();
            log.info("Successfully created a Server Socket at port " + serverSocket.getLocalPort() + " with a timeout of " + serverSocket.getSoTimeout());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void onAccept(Socket socket) throws InterruptedException, IOException, ClassNotFoundException {

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String Message = in.readLine();

        log.info("Connected: " + Message);

        if(Message.equals("Face")){
            Thread faceRecog = new Thread(()->{
                try {
                    FaceHandler(socket);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
            faceRecog.setName("Face recognition");
            faceRecog.start();

        }
        if(Message.equals("Voice")){
            Thread voiceRecog = new Thread(()-> {
                try {
                    VoiceHandler(socket);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
            voiceRecog.setName("Voice recognition");
            voiceRecog.start();
        }

    }

    public static void FaceHandler(Socket socket) throws IOException, InterruptedException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        while (true){
            String Values = in.readLine();
            if(Values==null)
                continue;

            // center values seem to be around 200, so box it at around 180< and >220

            int x = Integer.parseInt(Values.split(":")[0]);
            int y = Integer.parseInt(Values.split(":")[1]);
            long time = System.currentTimeMillis();
            // face is too low, move down
            Thread.sleep(500);
            // TODO MESS WITH THESE VALUES, same goes for the arduino side
            log.info(Values);

            if(y > 280){
                //if(System.currentTimeMillis()-time>100)
                //    continue;

                log.info("Down");

                writeMessage("Neck");
                Thread.sleep(50);
                writeMessage("-10");
            }
            if(y < 120){
                //if(System.currentTimeMillis()-time>100)
                //    continue;

                log.info("Up");

                writeMessage("Neck");
                Thread.sleep(50);
                writeMessage("10");
            }

            // Max right deflection will be 400
            // Max deflection of X should be 130
            // Mapping from 210 - 340 to 0-255
            // 275 -> 50
            // 320 -> 255
            if(x > 210){


                log.info("Right " + (x-210));
                writeMessage("Right");
                Thread.sleep(50);
                writeMessage(String.valueOf(x-220));
            }

            // Max right deflection will be 50
            // Max deflection of X should be 130
            // Mapping from -180 -> -50 to -255->-0

            // 115 -> 50
            // 70 -> 255
            if(x < 180){

                log.info("Left " + (x-180));
                writeMessage("Left");
                Thread.sleep(50);
                writeMessage(String.valueOf(x-180));//writeMessage("Left");
            }

            //log.info(Values);
            ////Thread.sleep(1000);
        }


    }
    public static void VoiceHandler(Socket socket) throws IOException, InterruptedException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String Message = in.readLine();

        log.info("Connected: " + Message);
        Message = Message.toLowerCase();
        while (true){

            if(Message.contains("turn left")){
                writeMessage("TurnL");
                Thread.sleep(50);
            }
            if(Message.contains("turn right")){
                writeMessage("TurnR");
                Thread.sleep(50);
            }
            if(Message.contains("move forward")){
                writeMessage("moveF");
                Thread.sleep(50);
            }
            if(Message.contains("move backward")){
                writeMessage("moveB");
                Thread.sleep(50);
            }

            if(Message.contains("search")){

                String query = Message.split("search")[1];
                if(query.split(" ")[0].equals("for")){
                    query = query.substring(4);
                }

                try{
                    query = GoogleScrape.startSearch(query);
                    parser.saySomething(query);
                }catch (ClassNotFoundException e){
                    log.error(e.getMessage());
                }

            }
        }
    }
}
