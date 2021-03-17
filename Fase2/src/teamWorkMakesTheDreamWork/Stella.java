package teamWorkMakesTheDreamWork;

import Utilities.AreWeThereYet;
import Utilities.Dying;
import Utilities.Point;
import Utilities.Tools;
import robocode.*;

import java.awt.*;
import java.io.IOException;
import java.util.List;

import static robocode.util.Utils.normalRelativeAngle;
import static robocode.util.Utils.normalRelativeAngleDegrees;


public class Stella extends TeamRobot implements Droid{
    private int myQuad;
    private Point myHome;
    private boolean inPlace=false;
    private boolean fireReady=false;
    Tools t = new Tools();

    @Override
    public void run() {
        addCustomEvent(new JustFire("justFire", this));
        addCustomEvent(new MessageReader("msgReader", this));
        addCustomEvent(new Dying("isDying", this, getEnergy()));
        if(t.getBattleFieldDimensions().getX()!= getBattleFieldWidth())
            t.setDimensions(getBattleFieldWidth(), getBattleFieldHeight());
        setBulletColor(Color.PINK);
        int xx = getX()/getBattleFieldWidth()<0.5?0:1;
        int yy = getY()/getBattleFieldHeight()<0.5?0:1;
        Point myCorner= new Point(xx,yy);
        myHome = t.homeFromQuad(myCorner, 150);
        myQuad = t.getMyQuad(myCorner);
        for(int i=0;i<10;i++)
        System.out.println("Stella waiting");


    }
/*
    public void onMessageReceived(MessageEvent e) {
        if(e.getMessage() instanceof Object[]){
            Object[] obj =(Object[]) e.getMessage();
            System.out.println("Received :"+ (String) obj[0]);
            switch ((String) obj[0]){
                case "Fire":{
                    Point p = (Point) obj[1];
                    double dx = p.getX() - this.getX();
                    double dy = p.getY() - this.getY();
                    // Calculate angle to target
                    double theta = Math.toDegrees(Math.atan2(dx, dy));

                    // Turn gun to target
                    double angle = t.getAngle(p, new Point(getX(), getY()), 0.0);
                    turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));
                    fire(3);
                    //double angle = e.getBearing()-getGunHeading()+getHeading();
                    break;
                }
                case "Move":{
                    inPlace=false;
                    System.out.println("Moving...");
                    myQuad = (Integer) obj[1];
                    myHome = t.homeFromQuad(t.getMyQuad(myQuad), 150);
                    move();
                    inPlace=true;
                    break;
                }

            }
        }
        else{
            System.out.println("404");
        }

    }
*/
    public void onHitRobot(HitRobotEvent event) {


            if (!inPlace) {
                back(40);
                System.out.println("Going Back");
                if ("teamWorkMakesTheDreamWork.Bloom".equals(event.getName().split(" ")[0])) {
                    if (event.getBearing() > 0) {
                        turnRight(-(90 - event.getBearing()));
                    } else {
                        turnRight(90 - event.getBearing());
                    }
                    ahead(40);
                }
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

    @Override
    public void onCustomEvent(CustomEvent event) {
       Condition c = event.getCondition();
       switch (c.getName()){
           case "isDying":{
               if(c.test()) {
                   System.out.println("ImDED");
                   try {
                       broadcastMessage(new Object[]{"Death", getName()});
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
               break;
           }
           case "msgReader":{
               ((MessageReader) c).funct();
               break;
           }


       }
    }

    public class JustFire extends Condition {
        private final Stella r;

        public JustFire(String name, Stella r) {
            super(name);
            this.r=r;
        }


        public void fire() {
            r.fire(2); }

        public boolean test(){
            return true;
        }
    }

    public class MessageReader extends Condition {
        private final Stella r;

        public MessageReader(String name, Stella r) {
            super(name);
            this.r=r;
        }


        public void funct() {
            for(MessageEvent e : getMessageEvents()){

                Object[] msg = (Object[]) e.getMessage() ;
                System.out.println((String) msg[0]);
                    switch ((String) msg[0]){
                        case "Fire":{
                            Point p = (Point) msg[1];
                            double dx = p.getX() - getX();
                            double dy = p.getY() - getY();
                            // Calculate angle to target
                            double theta = Math.toDegrees(Math.atan2(dx, dy));
                            // Turn gun to target
                            double angle = Tools.getAngle(p, new Point(getX(), getY()), 0.0);
                            r.turnGunBy(normalRelativeAngleDegrees(theta - getGunHeading()));
                            fireReady=true;
                            break;
                        }
                        case "Move":{
                            r.setDebugProperty("InPlace", String.valueOf(inPlace));
                            inPlace=false;
                            r.setDebugProperty("InPlace", String.valueOf(inPlace));
                            fireReady= false;
                            System.out.println("Moving...");
                            myQuad = (Integer) msg[1];
                            myHome = t.homeFromQuad(t.getMyQuad(myQuad), 150);
                            r.move();
                            waitFor(new AreWeThereYet(r, myHome));
                            r.setDebugProperty("InPlace", String.valueOf(inPlace));
                            inPlace=true;
                            r.setDebugProperty("InPlace", String.valueOf(inPlace));
                            break;
                        }
                        default:{
                            System.out.println("404");
                            }
                    }
                }
        }

        public boolean test(){
            return true;
        }
    }

    private void turnGunBy(double normalRelativeAngleDegrees) {
        turnGunRight(normalRelativeAngleDegrees);
    }

}
