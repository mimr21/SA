import robocode.*;

import java.awt.geom.Point2D;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

import static robocode.util.Utils.normalRelativeAngle;

public class CircumNav extends AdvancedRobot {
    private final Odom ourOdometer = new Odom("isRunning", this, new Point2D.Double(18,18));
    boolean starting=true;
    boolean scanning=false;
    boolean circumnavigating = false;

    ArrayList<Map.Entry<String,Point2D>> orderedScannedRobots= new ArrayList<>();

    public void run() {
        addCustomEvent(ourOdometer);
        while(starting){
            goTo(18,18);
            if (euclidianDistance(18,18,getX(),getY())<1)
                starting=false;
        }

        normalizeHeading();

        int i=0;
        while(i<100){
            doNothing();
            i++;
        }

        scanning=true;
        while (orderedScannedRobots.size()<3){
            turnRadarRight(5);
        }
        scanning=false;
        circumnavigating=true;
        System.out.println("Ready to start!");

        while (circumnavigating && !orderedScannedRobots.isEmpty()){
            Map.Entry<String,Point2D> rbot=  orderedScannedRobots.remove(0);
            Point2D to =rbot.getValue();
            System.out.println("Goto: "+ to);
            double r = Math.ceil(Math.sqrt(2*Math.pow(36,2)));
            double deviation = Math.toDegrees(Math.asin(r/euclidianDistance(getX(),getY(),to.getX(),to.getY())));
            System.out.println("Dev :"+ deviation);
            goTo(to.getX(),to.getY(), -Math.ceil(deviation)-2,50);
        }
        goTo(18,18);

        System.out.println("DONE");
    }



    public void onScannedRobot(ScannedRobotEvent e) {
        if(scanning){
            int robotIndex = orderedScannedRobots.size();

            Point2D nme = getCartesianFromPolar(e.getBearing(), e.getDistance());

            if(robotIndex==1) nme = new Point2D.Double(nme.getX()+36,nme.getY()+36);
            System.out.println(nme+" :" + e.getName() +"@Angle: " +e.getBearing() );

            for(Map.Entry<String,Point2D> elem : orderedScannedRobots){
                if (elem.getKey().equals(e.getName()))
                    return;
            }
            orderedScannedRobots.add(new AbstractMap.SimpleEntry<>(e.getName(), nme));
        }
    }

    private Point2D getCartesianFromPolar(double angle, double distance) {
        double rads = (java.lang.Math.PI/180)*angle;
        double cos = Math.cos(rads)*distance;
        double sin = Math.sin(rads)*distance;
        Point2D pt =new Point2D.Double(sin,cos);
        return pt;
    }

    void normalizeHeading(){
        turnLeft(getHeading());
    }

    void goTo(double toX, double toY){
        goTo(toX,toY,0,0);
    }

    void goTo(double toX, double toY, double shiftAngle, double shiftDistance){
        double fromX = getX();
        double fromY = getY();

        double dist =  euclidianDistance(fromX, fromY, toX, toY);
        Point2D vec = new Point2D.Double(toX-fromX, toY-fromY);

        double atan = (180/Math.PI)*  normalRelativeAngle(Math.atan2(vec.getX(),vec.getY())-getHeadingRadians());
        System.out.println("Turning by: "+atan);

        turnRight(atan+ shiftAngle);
        ahead(dist+shiftDistance);
    }

    private static double euclidianDistance(double x1, double y1, double x2, double y2) {
        double dist = java.lang.Math.sqrt(((java.lang.Math.pow((x1 - x2), 2)) + (java.lang.Math.pow((y1 - y2), 2))));
        return dist;
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        back(10);

    }

    @Override
    public void onHitWall(HitWallEvent event) {
        stop();
    }

    public void onCustomEvent(CustomEvent ev) {
        Condition cd = ev.getCondition();
        if(cd.getName().equals("isRunning"))
            this.ourOdometer.getDistance();
    }
}
