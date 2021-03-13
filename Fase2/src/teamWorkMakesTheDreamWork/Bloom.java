package teamWorkMakesTheDreamWork;

import Utilities.Point;
import Utilities.Tools;
import robocode.*;


import java.io.IOException;
import java.util.Random;

import static robocode.util.Utils.normalRelativeAngle;

public class Bloom extends TeamRobot {
    private int myQuad;
    private Point myHome;
    private Tools t = new Tools();


    public void run() {
        if(t.getBattleFieldDimensions().getX()!= getBattleFieldWidth())
             t.setDimensions(getBattleFieldWidth(), getBattleFieldHeight());
        int xx =  getX()/getBattleFieldWidth()<0.5?0:1;
        int yy = getY()/getBattleFieldHeight()<0.5?0:1;
        Point myCorner= new Point(xx,yy);
        myHome = t.homeFromQuad(myCorner);
        myQuad = t.getMyQuad(myCorner);
        flee();

        while (true) {
            stepInside();
        }
    }

    private void stepInside() {
        turnRadarRight(360);
    }

    private Point flee() {
        Point p = t.getBattleFieldDimensions();
        boolean goLeft = new Random().nextBoolean();
        if(goLeft)
            myQuad=myQuad==0?3:myQuad-1;
        else
            myQuad=(myQuad+1)%4;

        myHome = t.homeFromQuad(t.getMyQuad(myQuad));

        updateStella(myHome,myQuad);

        goTo(myHome);
        double center= t.getAngle(new Point(p.getX()/2,p.getY()/2),new Point(getX(),getY()), getHeading());
        turnRight(center);
        return myHome;
    }

    private void updateStella(Point p, int quad){
        Object[] msg = new Object[]{"Move", p, quad};
        try {
            broadcastMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(MessageEvent event) {
        super.onMessageReceived(event);
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
            back(20);
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        flee();
    }

    @Override
    public void onHitWall(HitWallEvent event) {

    }

    public void onScannedRobot(ScannedRobotEvent e) {
        if(!isTeammate(e.getName())){
            Point p = t.getRobotCoordinates(e);

            Object[] msg = new Object[]{"Fire", p};
            try {
                broadcastMessage(msg);
            } catch (IOException c) {
                c.printStackTrace();
            }

            /*
            double angle = e.getBearing()-getGunHeading()+getHeading();
            turnGunRight(angle);
            fire(1);
            */

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
