package teamWorkMakesTheDreamWork;

import Utilities.AreWeThereYet;
import Utilities.Dying;
import Utilities.Point;
import Utilities.Tools;
import robocode.*;


import java.awt.*;
import java.io.IOException;
import java.util.*;

import static robocode.util.Utils.normalRelativeAngle;
import static robocode.util.Utils.normalRelativeAngleDegrees;

public class Bloom extends TeamRobot {
    private int myQuad;
    private Point myHome;
    private Tools t = new Tools();
    private ArrayList<String> teammates = new ArrayList<>();
    private boolean moving=true;
    private double bound;
    private boolean noTanks=false;
    private Point lastFire;
    private HashMap<String,Double> nmes= new HashMap<>();
    private int messageCounter=0;
    private HashMap<String,Double> lastHits = new HashMap<>();
    private boolean eventHappening=false;

    //All:[teamWorkMakesTheDreamWork.Bloom* (1), teamWorkMakesTheDreamWork.Stella* (1)]
    public void run() {
        Color pink = new Color(195, 132, 212, 255);
        Color blue = new Color(82, 188, 220, 255);
        setColors(pink, blue, blue);
        addCustomEvent(new Dying("isDying",this));
        for(String s : getTeammates()){
            teammates.add(s.split(" ")[0]);
        }
        if(t.getBattleFieldDimensions().getX()!= getBattleFieldWidth())
             t.setDimensions(getBattleFieldWidth(), getBattleFieldHeight());
        int xx =  getX()/getBattleFieldWidth()<0.5?0:1;
        int yy = getY()/getBattleFieldHeight()<0.5?0:1;
        Point myCorner= new Point(xx,yy);
        myHome = t.homeFromQuad(myCorner,36);
        myQuad = t.getMyQuad(myCorner);
        double r = Math.ceil(Math.sqrt(2*Math.pow(35,2)));
        Point to = t.homeFromQuad(t.getMyQuad(myQuad),150);
        Point from = t.homeFromQuad(t.getMyQuad(myQuad),36);
        bound = Math.toDegrees(Math.asin(r/t.euclidianDistance(from.getX(),from.getY(),to.getX(),to.getY())));
        updateStella(myQuad);
        turnRadarRight(360);
        orderAishas();
        flee();
        while (true) {
            turnRadarRight(normalRelativeAngleDegrees(-getRadarHeading()+getHeading()));
            turnRadarRight(50);
            turnRadarRight(-100);
            turnRadarRight(50);
            orderAishas();
            setDebugProperty("isStellaDed", String.valueOf(noTanks));
        }
    }

    private void orderAishas(){
        double min=0;
        String nameMin = "";
        for (Map.Entry<String,Double> e : nmes.entrySet()){
            if(e.getValue()>min){
                min = e.getValue();
                nameMin = e.getKey();
            }
        }
        try {
            broadcastMessage(new Object[]{"Kill", nameMin});

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Point flee() {

        Point p = t.getBattleFieldDimensions();
        boolean goLeft = new Random().nextBoolean();
        if(goLeft)
            myQuad=myQuad==0?3:myQuad-1;
        else
            myQuad=(myQuad+1)%4;

        updateStella(myQuad);
        myHome = t.homeFromQuad(t.getMyQuad(myQuad), 36);

        moving=true;
        doNothing();
        goTo(myHome);
        updateStella(myQuad);
        double center= t.getAngle(new Point(p.getX()/2,p.getY()/2),new Point(getX(),getY()), getHeadingRadians());
        turnRight(center);
        waitFor(new AreWeThereYet(this,myHome));
        updateStella(myQuad);
        turnGunRight(-getGunHeading()+getHeading());
        moving=false;

        return myHome;
    }

    private void updateStella(int quad){
        System.out.println("Move");
        try {

                broadcastMessage(new Object[]{"Move", quad ,messageCounter });
                messageCounter++;
            setDebugProperty("messageCounter", String.valueOf(messageCounter));



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        if(isTeammate(e.getName().split(" ")[0])){
        if(!moving && "teamWorkMakesTheDreamWork.Stella".equals(e.getName().split(" ")[0])){
            updateStella(myQuad);
            return;
        }}else {
            nmes.put(e.getName(), e.getEnergy());
            double enemyBearing = getHeading() + e.getBearing();
            // Calculate enemy's position
            double enemyX = getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing));
            double enemyY = getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing));
            Point ppp = new Point(enemyX, enemyY);
            if (!moving) {
                lastFire = ppp;
                Object[] msg = new Object[]{"Fire", ppp};
                try {
                    broadcastMessage(msg);
                } catch (IOException c) {
                    c.printStackTrace();
                }

                double angle = t.getAngle(new Point(enemyX, enemyY), new Point(getX(), getY()), 0.0);
                if (Math.abs(normalRelativeAngleDegrees(angle - getHeading())) > bound || noTanks) {
                    setDebugProperty("AngleOffset", String.valueOf(Math.abs(normalRelativeAngleDegrees(angle - getHeading()))));
                    setDebugProperty("OffsetAllowed", String.valueOf(bound));
                    System.out.println("Shooting :" + e.getName());
                    turnGunRight(normalRelativeAngleDegrees(-getGunHeading() + angle));
                    fire(1);
                    waitFor(new GunTurnCompleteCondition(this));
                }
            }
        }

    }

    @Override
    public boolean isTeammate(String name) {
        return teammates.contains(name);
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        if(!moving){
            flee();
            System.out.println("Moving");
        }
    }

    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        if(nmes.containsKey(event.getName())) {
            nmes.remove(event.getName());
        }
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        eventHappening = true;
            lastHits.put("Teammate",normalRelativeAngleDegrees(event.getBearing()+getHeading()));

            double direction=0;
            if(lastHits.size()>2){
                for(Double d : lastHits.values())
                    direction+=(d/(double)lastHits.size());
                turnRight(normalRelativeAngleDegrees(direction+180-getHeading()));
                ahead(50);
            }else{
                back(30);
                turnRight((new Random().nextBoolean()?1:-1) *90);
                ahead(50);
            }

        waitFor(new MoveCompleteCondition(this));
        eventHappening = false;
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        eventHappening=true;
        back(30);
        turnRight(180);
        waitFor(new TurnCompleteCondition(this));
        eventHappening=false;
    }

    @Override
    public void onMessageReceived(MessageEvent event) {
        Object[] msg =(Object[]) event.getMessage();

        switch ((String) msg[0]){
            case "Death":
                if(((String) msg[1] ).equals("Stella")){
                    noTanks=true;
                    System.out.println("Stella Ded");}
                break;
        }
    }

    void goTo(double toX, double toY){
        goTo(toX,toY,0,0);
    }
    void goTo(Point p){
        goTo(p.getX(),p.getY(),0,0);
    }
    void goTo(double toX, double toY, double shiftAngle, double shiftDistance){
        double distanceToCorner, lastDist=10000000;

        while((distanceToCorner=t.euclidianDistance(getX(),getY(),toX,toY))>1) {
            if (!eventHappening){
                if(distanceToCorner<50 && Math.abs(lastDist-distanceToCorner)<30 ){
                    System.out.println("Corner occupied");
                    flee();
                }

            double fromX = getX();
            double fromY = getY();

            double dist = t.euclidianDistance(fromX, fromY, toX, toY);
            Point vec = new Point(toX - fromX, toY - fromY);

            double atan = (180 / Math.PI) * normalRelativeAngle(Math.atan2(vec.getX(), vec.getY()) - getHeadingRadians());

            turnRight(atan + shiftAngle);
            ahead(dist + shiftDistance);
            lastDist=distanceToCorner;
        }}
    }


    @Override
    public void onCustomEvent(CustomEvent event) {
        eventHappening=true;
        Condition c = event.getCondition();
        String cname = c.getName();
        if(cname.equals("isDying")){
            Dying d = (Dying) c;
            if(c.test()){
                System.out.println("BLOOM DEAD");
                try {
                    broadcastMessage(new Object[]{"Death", "Bloom"});
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        eventHappening=false;
    }


}
