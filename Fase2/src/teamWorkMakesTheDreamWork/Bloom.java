package teamWorkMakesTheDreamWork;

import Utilities.Point;
import robocode.*;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static robocode.util.Utils.normalRelativeAngle;

public class Bloom extends TeamRobot {
    Point myCorner;

    public void run() {
        findCorner();
        while (true) {
            stepInside();
        }
    }

    private void stepInside() {
        for(int i=1;i<10;i++){
           if(i%2==0)
               turnRight(45/i);
           else
               turnLeft(45/i);
            ahead(30);
            if(i%2==1)
                turnRight(45/i);
            else
                turnLeft(45/i);
            turnRadarRight(45);
            turnRadarLeft(90);
            turnRadarRight(45);
        }
    }

    private void findCorner() {
        double dontHit = 36;
        Point p = new Point(getBattleFieldHeight(),
        getBattleFieldWidth());
       boolean leftSide = (2*getX()/p.getX())<1;
       boolean bottomSide =(2*getY()/p.getY())<1;

        boolean goLeft = new Random().nextBoolean();
       if(leftSide){
           if(bottomSide)
               myCorner= goLeft? new Point(0+dontHit,p.getY()-dontHit) : new Point(p.getY()-dontHit,0+dontHit);
           else
               myCorner= goLeft? new Point(p.getY()-dontHit,0+dontHit) : new Point(0+dontHit,p.getY()-dontHit);
            }else{
           if(bottomSide)
               myCorner= goLeft? new Point(0+dontHit,0+dontHit) : new Point(p.getY()-dontHit,p.getY()-dontHit);
           else
               myCorner= goLeft? new Point(p.getY()-dontHit,0+dontHit) : new Point(0+dontHit,p.getY()-dontHit);
       }
       goTo(myCorner);
       turnRight(-getHeading()+getAngle(new Point(p.getX()/2,p.getY()/2)));
    }

    @Override
    public void onMessageReceived(MessageEvent event) {
        super.onMessageReceived(event);
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {

    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        findCorner();
    }

    @Override
    public void onHitWall(HitWallEvent event) {

    }

    public void onScannedRobot(ScannedRobotEvent e) {
        if(!isTeammate(e.getName())){
            turnGunRight(getHeading()+getAngle(Point.toCartesian(new Point(getX(),getY()),e.getDistance(),e.getBearing())));
            fire(1);
        }
    }
    double getAngle(Utilities.Point p){
        double dx = p.getX() - this.getX();
        double dy = p.getY() - this.getY();
        // Calculate angle to target
        return Math.toDegrees(Math.atan2(dx, dy));

    }

    void goTo(double toX, double toY){
        goTo(toX,toY,0,0);
    }
    void goTo(Utilities.Point p){
        goTo(p.getX(),p.getY(),0,0);
    }

    void goTo(double toX, double toY, double shiftAngle, double shiftDistance){
        double fromX =getX();
        double fromY = getY();

        double dist =  euclidianDistance(fromX, fromY, toX, toY);
        Utilities.Point vec = new Point(toX-fromX, toY-fromY);

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
