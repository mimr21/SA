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

    private final ArrayList<Point> quadrants = new ArrayList<>(Arrays.asList(new Point(0,0), new Point(0,1),
            new Point(1,1), new Point(1,0)));
    private int myQuad;
    private Point myHome;


    public void run() {
        int xx =  getX()/getBattleFieldWidth()<0.5?0:1;
        int yy = getY()/getBattleFieldHeight()<0.5?0:1;
        Point myCorner= new Point(xx,yy); myHome = homeFromQuad(myCorner);
        myQuad = quadrants.indexOf(myCorner);
        findCorner();
        while (true) {
            stepInside();
        }
    }

    private void stepInside() {
        turnRadarRight(360);
    }
    private Point homeFromQuad(Point p){
        return new Point(p.getX()==0?36 : getBattleFieldWidth()-36, p.getY()==0?36 : getBattleFieldHeight()-36 );
    }
    private void findCorner() {
        Point p = new Point(
        getBattleFieldWidth(),getBattleFieldHeight());
       boolean goLeft = new Random().nextBoolean();
       if(goLeft)
            myQuad=myQuad==0?3:myQuad-1;
        else
            myQuad=(myQuad+1)%4;

        myHome=homeFromQuad(quadrants.get(myQuad));
        goTo(myHome);
        double center=getAngle(new Point(p.getX()/2,p.getY()/2));
        turnRight(center);
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
            double angle = e.getBearing()-getGunHeading()+getHeading();
            turnGunRight(angle);
            fire(1);
        }
    }
    double getAngle(Utilities.Point p){
        double dx = p.getX() - this.getX();
        double dy = p.getY() - this.getY();

        return Math.toDegrees(normalRelativeAngle(Math.atan2(dx,dy)-getHeadingRadians()));
    }

    void goTo(double toX, double toY){
        goTo(toX,toY,0,0);
    }
    void goTo(Utilities.Point p){
        goTo(p.getX(),p.getY(),0,0);
    }

    void goTo(double toX, double toY, double shiftAngle, double shiftDistance){
        while(euclidianDistance(getX(),getY(),toX,toY)>1) {
            double fromX = getX();
            double fromY = getY();

            double dist = euclidianDistance(fromX, fromY, toX, toY);
            Utilities.Point vec = new Point(toX - fromX, toY - fromY);

            double atan = (180 / Math.PI) * normalRelativeAngle(Math.atan2(vec.getX(), vec.getY()) - getHeadingRadians());

            turnRight(atan + shiftAngle);
            ahead(dist + shiftDistance);
        }
    }
    private static double euclidianDistance(double x1, double y1, double x2, double y2) {
        double dist = java.lang.Math.sqrt(((java.lang.Math.pow((x1 - x2), 2)) + (java.lang.Math.pow((y1 - y2), 2))));
        return dist;
    }


}
