package teamWorkMakesTheDreamWork;

import robocode.Droid;
import robocode.MessageEvent;
import robocode.TeamRobot;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.Queue;

import static robocode.util.Utils.normalRelativeAngle;
import static robocode.util.Utils.normalRelativeAngleDegrees;


public class teamPlayer extends TeamRobot implements Droid {
    Queue<Point> danceRoutine = new LinkedList<>();
    @Override
    public void run() {
        setBulletColor(Color.PINK);

        danceRoutine.add(new Point(getX(),getY()));
        while (true) {
            if (danceRoutine.isEmpty()){
                System.out.println(danceRoutine + " :IsEmpty=" + danceRoutine.size());
            return;
        } else{
                Point dest = danceRoutine.remove();
                goTo(dest);
            }

        }

    }

    public void onMessageReceived(MessageEvent e) {
        // Fire at a point

        //System.out.println(e.getMessage());
         if(e.getMessage() instanceof Object[]){
            Object[] obj =(Object[]) e.getMessage();
            System.out.println("Received :"+obj[0]);
            switch ((String) obj[0]){
                case "Dance":{
                    Point p = (Point) obj[1];
                    danceRoutine.add(p);
                    break;
                }
                case "Fire":{
                    Point p = (Point) obj[1];
                    turnGunRight(normalRelativeAngleDegrees(getAngle(p) - getGunHeading()));
                    fire(1);
                    break;
                }
                case "SetColor":{
                    System.out.println("CHANGING COLOR");
                    Color c = (Color) obj[1];
                    setBodyColor(c);
                    break;
                }

            }
        }
        else{

            System.out.println("404");
        }

    }

    double getAngle(Point p){
        double dx = p.getX() - this.getX();
        double dy = p.getY() - this.getY();
        // Calculate angle to target
        double theta = Math.toDegrees(Math.atan2(dx, dy));
        return  theta;
        // Turn gun to target

    }

    void goTo(double toX, double toY){
        goTo(toX,toY,0,0);
    }
    void goTo(Point p){
        goTo(p.getX(),p.getY(),0,0);
    }

    void goTo(double toX, double toY, double shiftAngle, double shiftDistance){
        double fromX = getX();
        double fromY = getY();

        double dist =  euclidianDistance(fromX, fromY, toX, toY);
        Point vec = new Point(toX-fromX, toY-fromY);

        double atan = (180/Math.PI)*  normalRelativeAngle(Math.atan2(vec.getX(),vec.getY())-getHeadingRadians());
        System.out.println("Turning by: "+atan);

        turnRight(atan+ shiftAngle);
        ahead(dist+shiftDistance);
    }
    private static double euclidianDistance(double x1, double y1, double x2, double y2) {
        double dist = java.lang.Math.sqrt(((java.lang.Math.pow((x1 - x2), 2)) + (java.lang.Math.pow((y1 - y2), 2))));
        return dist;
    }


}
