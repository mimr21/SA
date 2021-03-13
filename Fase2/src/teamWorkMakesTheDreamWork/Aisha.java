package teamWorkMakesTheDreamWork;

import Utilities.AreWeThereYet;
import Utilities.Point;
import Utilities.Tools;
import robocode.Droid;
import robocode.MessageEvent;
import robocode.TeamRobot;

import java.awt.*;

import static robocode.util.Utils.normalRelativeAngle;
import static robocode.util.Utils.normalRelativeAngleDegrees;


public class Aisha extends TeamRobot implements Droid {

    Tools t = new Tools();

    @Override
    public void run() {
        System.out.println("Aisha waiting");
    }

    public void onMessageReceived(MessageEvent e) {
        if(e.getMessage() instanceof Object[]){
            Object[] obj =(Object[]) e.getMessage();
            System.out.println("Received :"+ (String) obj[0]);
            switch ((String) obj[0]){
                case "Fire":{

                    Point p = (Point) obj[1];
                    setAhead(1000000);
                    double dx = p.getX() - this.getX();
                    double dy = p.getY() - this.getY();
                    // Calculate angle to target
                    double theta = Math.toDegrees(Math.atan2(dx, dy));
                    setTurnRight(normalRelativeAngleDegrees(theta - getHeading()));
                    setTurnLeft(90);
                    setTurnRight(90);
                    for(int i=0;i<100;i++){
                        setFire(1);
                    }
                    execute();


                    break;
                }
            }
        }
        else{
            System.out.println("404");
        }

    }



    void goTo(double toX, double toY){
        goTo(toX,toY,0,0);
    }
    void goTo(Point p){
        goTo(p.getX(),p.getY(),0,0);
    }
    void goTo(double toX, double toY, double shiftAngle, double shiftDistance){
        while(t.euclidianDistance(getX(),getY(),toX,toY)>1) {
            double fromX = getX();
            double fromY = getY();

            double dist = t.euclidianDistance(fromX, fromY, toX, toY);
            Point vec = new Point(toX - fromX, toY - fromY);

            double atan = (180 / Math.PI) * normalRelativeAngle(Math.atan2(vec.getX(), vec.getY()) - getHeadingRadians());

            turnRight(atan + shiftAngle);
            ahead(dist + shiftDistance);
        }
    }



}
