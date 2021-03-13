package teamWorkMakesTheDreamWork;

import Utilities.Point;
import Utilities.Tools;
import robocode.Droid;
import robocode.MessageEvent;
import robocode.TeamRobot;

import java.awt.*;

import static robocode.util.Utils.normalRelativeAngle;
import static robocode.util.Utils.normalRelativeAngleDegrees;


public class Stella extends TeamRobot implements Droid {
    private int myQuad;
    private Point myHome;
    Tools t = new Tools();

    @Override
    public void run() {
        if(t.getBattleFieldDimensions().getX()!= getBattleFieldWidth())
            t.setDimensions(getBattleFieldWidth(), getBattleFieldHeight());
        setBulletColor(Color.PINK);
        int xx = getX()/getBattleFieldWidth()<0.5?0:1;
        int yy = getY()/getBattleFieldHeight()<0.5?0:1;
        Point myCorner= new Point(xx,yy);
        myHome = t.homeFromQuad(myCorner, 150);
        myQuad = t.getMyQuad(myCorner);

        System.out.println("Stella waiting");

    }

    public void onMessageReceived(MessageEvent e) {
        System.out.println(e.getMessage());
        if(e.getMessage() instanceof Object[]){
            Object[] obj =(Object[]) e.getMessage();
            System.out.println("Received :"+obj[0]);
            switch ((String) obj[0]){
                case "Fire":{
                    Point p = (Point) obj[1];
                    /*
                    double angle = t.getAngle(p, new Point(getX(),getY()), getGunHeading());
                    // Turn gun to target
                    turnGunRight(angle);
                    fire(1);*/
                    // Calculate x and y to target
                    double dx = p.getX() - this.getX();
                    double dy = p.getY() - this.getY();
                    // Calculate angle to target
                    double theta = Math.toDegrees(Math.atan2(dy, dx));

                    // Turn gun to target
                    double angle = t.getAngle(p, new Point(getX(), getY()), 0.0);
                    turnGunRight(angle - getGunHeading());
                    fire(1);
                    //double angle = e.getBearing()-getGunHeading()+getHeading();

                    break;
                }
                case "Move":{
                    System.out.println("Moving...");
                    myQuad = (Integer) obj[1];
                    myHome = t.homeFromQuad(t.getMyQuad(myQuad), 150);
                    move();
                    break;
                }

            }
        }
        else{
            System.out.println("404");
        }

    }

    public void move(){
        goTo(myHome);

        Point c = t.getBattleFieldDimensions();
        double center= t.getAngle(new Point(c.getX()/2,c.getY()/2),new Point(getX(),getY()), getHeadingRadians());
        turnRight(center);
        //turnGunRight(-getGunHeading());
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
