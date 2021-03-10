package teamWorkMakesTheDreamWork;

import robocode.Robocode;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;
import robocode.WinEvent;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class teamLeader extends TeamRobot {
    ArrayList<Color> colors = new ArrayList<>(Arrays.asList(Color.GREEN, Color.MAGENTA));
    private boolean isColorDefined=false;

    public void run() {
        // Normal behavior
       double maxX = getBattleFieldWidth();
       double maxY = getBattleFieldHeight();

        while (true) {
            setTurnRadarRight(360);
            try {
                broadcastMessage(new Object[]{"Dance",new Point(18,18)});
                broadcastMessage(new Object[]{"Dance",new Point(maxX*0.9,18)});
                broadcastMessage(new Object[]{"Dance",new Point(18,maxX*0.9)});
                broadcastMessage(new Object[]{"Dance",new Point(maxX*0.9,maxX*0.9)});
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int i=0;i<40;i++)
                doNothing();
        }
    }

    @Override
    public void onWin(WinEvent event) {

        try {
            for(int i=0;i<10;i++)
                broadcastMessage(new Color(new Random().nextInt()));
            broadcastMessage("GG EZ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        // Don't fire on teammates
        if (isTeammate(e.getName())) {
            try {
                broadcastMessage("Hello"+e.getName());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }else {
            Object[] objs = new Object[]{"Fire", toCartesian(e.getDistance(),this.getHeading() + e.getBearing()) };
            try {
                broadcastMessage(objs);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            if(!isColorDefined){
                int colorChoice = (new Random().nextDouble())>(new Random().nextDouble())? 1: 0;
                Color col = colors.get(colorChoice);
                setBodyColor(col);
                Object[] msg = new Object[]{"SetColor",col};
                try {
                    broadcastMessage(msg);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                isColorDefined=true;
                System.out.println("SetColor :"+col);

            }
        }
    }

    Point toCartesian(double dist, double heading){

        // Calculate enemy's position
        double enemyX = getX() + dist * Math.sin(Math.toRadians(heading));
        double enemyY = getY() + dist * Math.cos(Math.toRadians(heading));
        return new Point(enemyX, enemyY);
    }

}
