import robocode.*;
import standardOdometer.Odometer;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

import static robocode.util.Utils.normalRelativeAngleDegrees;

public class CircumNav extends AdvancedRobot {
    private final Odometer odometer = new Odometer("IsRacing", this);
    private final Odom ourOdometer = new Odom("isRunning", this, new Point2D.Double(18,18));
    boolean starting=true;
    boolean scanning=false;
    boolean circumnavigating = false;

    ArrayList<Point2D> orderedScannedRobots= new ArrayList<>();


    public void run() {
        addCustomEvent(odometer);

        addCustomEvent(ourOdometer);
        while(starting){
            goTo(18,18);
            turnRight(360-getHeading());
            if (euclidianDistance(18,18,getX(),getY())<1)
                starting=false;
        }
        scanning=true;
        turnRadarRight(90);
        scanning=false; circumnavigating=true;
        System.out.println("Ready to start!");

        while (circumnavigating && !orderedScannedRobots.isEmpty()){
            Point2D to = orderedScannedRobots.remove(0);
            System.out.println("Goto: "+ to);
            goTo(to.getX(),to.getY());
        }


        System.out.println("DONE");




    }


    public void onScannedRobot(ScannedRobotEvent e) {
        if(scanning){
            fire(1);
            Point2D nme = getCartesianFromPolar(e.getBearing()+getHeading(), e.getDistance());
            //System.out.println(nme+" :" + e.getName() +"@Angle: " +e.getBearing() );
           orderedScannedRobots.add(nme);
        }
    }

    private Point2D getCartesianFromPolar(double angle, double distance) {
        double rads = (java.lang.Math.PI/360)*angle;
        double cos = Math.cos(rads)*distance;
        double sin = Math.sin(rads)*distance;

        Point2D pt =new Point2D.Double(sin,cos);

        return pt;


    }
    void normalizeHeading(){
        turnLeft(getHeading());
    }


    void goTo(double toX, double toY){
        double fromX = getX();
        double fromY = getY();
        double dist =  euclidianDistance(fromX, fromY, toX, toY);

        // calculate the angle
        double complementaryAngle = getAngle(fromX, fromY, toX, toY);
        double turningAngle = 180-complementaryAngle;


        // Turn to initial position
        double nrad = normalRelativeAngleDegrees(turningAngle + getHeading());
        System.out.println("Turning: "+nrad);
        turnLeft(nrad);

        ahead(dist);

    }

    // calculates the complementary angle aka the arc cosine of an angle
    private static double getAngle(double fromX, double fromY, double toX, double toY){
        // pythagoras theorem
        double h = euclidianDistance(fromX, fromY, toX, toY);
        double adj = euclidianDistance(fromX, fromY, fromX, toY);

        double cosAlpha = adj/h;

        // arc cosine aka inverse of cosine
        double acosAlpha = java.lang.Math.acos(cosAlpha);

        // transform to degrees
        double acosAlphaDegrees = (180/java.lang.Math.PI)*acosAlpha;

        return acosAlphaDegrees;
    }

    private static double euclidianDistance(double x1, double y1, double x2, double y2) {
        double dist = java.lang.Math.sqrt(((java.lang.Math.pow((x1 - x2), 2)) + (java.lang.Math.pow((y1 - y2), 2))));
        return dist;
    }



    @Override
    public void onHitRobot(HitRobotEvent event) {

    }

    @Override
    public void onHitWall(HitWallEvent event) {
        stop();
    }

    public void onCustomEvent(CustomEvent ev) {
        Condition cd = ev.getCondition();
        if (cd.getName().equals("IsRacing"))
            this.odometer.getRaceDistance();
        if(cd.getName().equals("isRunning"))
            this.ourOdometer.getDistance();

    }
}
