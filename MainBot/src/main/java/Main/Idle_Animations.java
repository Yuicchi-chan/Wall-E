package Main;

import Main.Startup;

import java.util.Random;

public class Idle_Animations {

    void doIdle(){

        if(Startup.isIdle)
            return;

        Random random = new Random();
        int time = random.nextInt(500,10*1000);
        try{
            Startup.writeMessage("randomNeck");
            if(time%2==0 && time%3==0) Startup.writeMessage("randomHand");
            Startup.writeMessage("randomTurn");
            Thread.sleep(time);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
