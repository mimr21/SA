package teamWorkMakesTheDreamWork;

import Utilities.Dying;
import Utilities.Point;
import Utilities.Tools;
import robocode.*;

import java.awt.*;
import java.io.IOException;

import static robocode.util.Utils.normalRelativeAngle;
import static robocode.util.Utils.normalRelativeAngleDegrees;


public class Stella extends TeamRobot implements Droid{
    private int myQuad;
    private Point myHome;
    private boolean inPlace=false;
    private boolean fireReady=false;
    private boolean eventHappening = false;
    Tools t = new Tools();
    private int messageCounter=0;
    private boolean goCrazy=false;

    @Override
    public void run() {
        Color pink = new Color(195, 132, 212, 255);

        Color orange = new Color(255, 148, 32, 255);
        setColors(pink, orange, orange);
        addCustomEvent(new Dying("isDying", this));
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
        while (true){
            if(goCrazy){
                while (Tools.euclidianDistance(getX(),getY(),myHome.getX(),myHome.getY())>1)
                    goTo();
                waitFor(new MoveCompleteCondition(this));
                double center= t.getAngle(new Point(getBattleFieldWidth()/2,getBattleFieldHeight()/2),new Point(getX(),getY()), getHeadingRadians());
                turnRight(center);
                waitFor(new TurnCompleteCondition(this));
                back(Math.sqrt(2*Math.pow((150-18),2)));
                waitFor(new MoveCompleteCondition(this));
                int range = 0;
                while (true){
                    turnGunRight(normalRelativeAngleDegrees((-50+(range++%100)-getGunHeading()+getHeading())));
                    fire(3);
                }
            }else {
                setDebugProperty("fireReady", String.valueOf(fireReady));
                if (!eventHappening && Tools.euclidianDistance(myHome.getX(), myHome.getY(), getX(), getY()) > 1)
                    goTo();
                if (fireReady)
                    fire(2);
                doNothing();
            }
        }
    }

    public void onMessageReceived(MessageEvent e) {
        eventHappening=true;
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
                    turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));
                    fireReady=true;
                    //double angle = e.getBearing()-getGunHeading()+getHeading();
                    break;
                }
                case "Move":{
                    fireReady=false;
                    messageCounter++;
                    setDebugProperty("messageCounter", String.valueOf(messageCounter));
                    System.out.println("Moving... "+ (int) obj[2] );
                    myQuad = (Integer) obj[1];
                    myHome = t.homeFromQuad(t.getMyQuad(myQuad), 150);
                    move();

                    break;
                }
                case "Death":{
                    if(((String) obj[1] ).equals("Bloom")){
                        goCrazy=true;
                        System.out.println("Bloom Ded");}
                    break;
                }

            }
        }
        else{
            System.out.println("404");
        }
        eventHappening=false;
    }

    public void onHitRobot(HitRobotEvent e) {
        eventHappening=true;
            if(!inPlace) {
                back(40);
                waitFor(new MoveCompleteCondition(this));
                System.out.println("Going Back");

                    if (e.getBearing() > 0) {
                        setTurnRight(-(90 - e.getBearing()));
                    } else {
                        setTurnRight(90 - e.getBearing());
                    }
                    setAhead(40);
                    waitFor(new TurnCompleteCondition(this));
            }
        eventHappening=false;
    }

    public void move(){
        goTo();
        Point c = t.getBattleFieldDimensions();
        double center= t.getAngle(new Point(c.getX()/2,c.getY()/2),new Point(getX(),getY()), getHeadingRadians());
        turnRight(center);
        waitFor(new MoveCompleteCondition(this));
        //turnGunRight(-getGunHeading());
    }



    void goTo(){
        double toX = myHome.getX();
        double toY = myHome.getY();
        inPlace=false;
        while(!eventHappening && t.euclidianDistance(getX(),getY(),toX,toY)>1) {
             toX = myHome.getX();
             toY = myHome.getY();

            double fromX = getX();
            double fromY = getY();
            System.out.println("GOTO");

            double dist = t.euclidianDistance(fromX, fromY, toX, toY);
            Point vec = new Point(toX - fromX, toY - fromY);

            double atan = (180 / Math.PI) * normalRelativeAngle(Math.atan2(vec.getX(), vec.getY()) - getHeadingRadians());

            turnRight(atan );
            ahead(dist );
        }
        inPlace=t.euclidianDistance(getX(),getY(),myHome.getX(),myHome.getY())<1;

    }

    @Override
    public void onCustomEvent(CustomEvent event) {
        Condition c = event.getCondition();
        String cname = c.getName();
        if(cname.equals("isDying")){
            Dying d = (Dying) c;
            if(c.test()){
                try {
                    broadcastMessage(new Object[]{"Death", "Stella"});
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
/*
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
*/
}
