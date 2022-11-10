package Main;

import Main.Startup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Idle_Animations {
    private static final Logger log = LoggerFactory.getLogger(Startup.class);

    void doIdle(){
        System.out.println("Doing Idles");
        if(Startup.isIdle)
            return;

        Random random = new Random();
        Timer Neck = new Timer();

        int millis = 1000; //n*1000 millisecond = n second --> n minutes = n*60*1000

        while (true){
            Neck.schedule(new TimerTask() {
                public void run() {
                    Startup.writeMessage("randomNeck");
                    log.info("Executing Random Neck");
                }
            }, random.nextInt(500,5*1000));

            Timer Hand = new Timer();

            Neck.schedule(new TimerTask() {
                public void run() {
                    Startup.writeMessage("randomHand");
                    log.info("Executing Random Hand");
                }
            }, random.nextInt(1*1000,10*1000));

            Timer Spin = new Timer();

            Neck.schedule(new TimerTask() {
                public void run() {
                    Startup.writeMessage("randomTurn");
                    log.info("Executing Random Turn");
                }
            }, random.nextInt(5*1000,15*1000));
        }
    }
}
