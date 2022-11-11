package Main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Idle_Animations {
    private static final Logger log = LoggerFactory.getLogger(Startup.class);
    private static ScheduledExecutorService executor;
    void doIdle() {
        System.out.println("Doing Idles");
        if (Startup.isIdle)
            return;
        Executors.newScheduledThreadPool(1);

        Random random = new Random();

        int millis = 1000; //n*1000 millisecond = n second --> n minutes = n*60*1000

        //while (true){
        Runnable randomNeck = new Runnable() {
            public void run() {
                Startup.writeMessage("randomNeck");
                log.info("Executing Random Neck");
            }
        };

        Runnable randomHand = new Runnable() {
            public void run() {
                Startup.writeMessage("randomHand");
                log.info("Executing Random Hand");
            }
        };

        Runnable randomSpin = new Runnable() {
            public void run() {
                Startup.writeMessage("randomTurn");
                log.info("Executing Random Turn");
            }
        };

        executor.scheduleAtFixedRate(randomNeck, 0, random.nextInt(500, 5*1000), TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(randomHand, 0, random.nextInt(1*1000, 05*1000), TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(randomNeck, 0, random.nextInt(5*1000, 15*1000), TimeUnit.MILLISECONDS);

    }
}
