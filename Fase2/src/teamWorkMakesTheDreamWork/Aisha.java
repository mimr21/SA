package teamWorkMakesTheDreamWork;

import Utilities.Point;
import Utilities.Tools;
import robocode.*;

import java.awt.*;
import java.util.ArrayList;

import static robocode.util.Utils.normalRelativeAngle;
import static robocode.util.Utils.normalRelativeAngleDegrees;


public class Aisha extends TeamRobot {
    private String target;
    private Point targetPos;
    Tools t = new Tools();
    private ArrayList<String> teammates =  new ArrayList<>();
    private boolean dontFire=false;
    private ArrayList< Double> lastHits= new ArrayList<>();
    private boolean eventHappening=false;
    private boolean strongAndIndependent=false;


    @Override
    public void run() {
        Color pink = new Color(195, 132, 212, 255);
        Color green = new Color(121, 191, 106, 255);
        setColors(pink, green, green);
        for(String s : getTeammates()){
            teammates.add(s.split(" ")[0]);
        }
        System.out.println("Aisha waiting");
        while(true) {
            while (targetPos == null)
                turnRadarRight(360);
            while (targetPos != null) {
                goTo(targetPos);
            }
        }
    }

    public void onMessageReceived(MessageEvent e) {
        if(e.getMessage() instanceof Object[]){
            Object[] obj =(Object[]) e.getMessage();
            System.out.println("Received :"+ (String) obj[0]);
            switch ((String) obj[0]){
                case "Kill":{
                    if(target!=null &&target.equals((String) obj[1]))
                        return;
                    target =(String) obj[1];
                    targetPos=null;
                    System.out.println("Kill :"+target);
                    break;
                }
                case "Death":{
                    if(((String) obj[1] ).equals("Bloom")){
                        strongAndIndependent=true;
                        System.out.println("Bloom Ded");}
                    break;
                }
            }
        }
        else{
            System.out.println("404");
        }

    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        if(isTeammate(event.getName())){
            dontFire = true;
            if(event.getDistance()<60){
                System.out.println("Avoiding Teammate");
                back(30);
                turnRight(30);
                ahead(40);
            }
        }else if((strongAndIndependent && (targetPos==null || target==null)) ||
                    event.getName().equals(target)){
                double enemyBearing = getHeading() + event.getBearing();
                // Calculate enemy's position
                double enemyX = getX() + event.getDistance() * Math.sin(Math.toRadians(enemyBearing));
                double enemyY = getY() + event.getDistance() * Math.cos(Math.toRadians(enemyBearing));

                System.out.println("I see : "+event.getName());
                targetPos = new Point(enemyX,enemyY);
                turnGunRight(normalRelativeAngleDegrees(-getGunHeading() + enemyBearing));
                fire(1);
                //turnGunRight(-getGunHeading());
                dontFire=false;
            }

    }

    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        if(event.getName().equals(target)) {
            targetPos = null;
            target = null;
        }
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        eventHappening=true;
        lastHits.add(event.getBearing()+getHeading());
        turnRight(180);
        ahead(40);
        waitFor(new MoveCompleteCondition(this));
        eventHappening=false;
    }

    @Override
    public boolean isTeammate(String name) {
        return teammates.contains(name);
    }
    @Override
    public void onHitRobot(HitRobotEvent event) {
        System.out.println(lastHits);
        eventHappening=true;
        if(isTeammate(event.getName().split(" ")[0])){
            lastHits.add(normalRelativeAngleDegrees(event.getBearing()+getHeading()));

        double direction=0;
        if(lastHits.size()>2){
            for(Double d : lastHits)
                direction+=(d/(double)lastHits.size());
        }
        turnRight(normalRelativeAngleDegrees(direction+180-getHeading()));
        ahead(30);

        }
        else{
            back(40);
        }
        waitFor(new MoveCompleteCondition(this));
        eventHappening=false;
    }

    void goTo(double toX, double toY){
        goTo(toX,toY,0,0);
    }
    void goTo(Point p){
        goTo(p.getX(),p.getY(),0,0);
    }
    void goTo(double toX, double toY, double shiftAngle, double shiftDistance){
        while(!eventHappening && targetPos!=null && (targetPos.getX()==toX && targetPos.getY()==toY) ) {

            double fromX = getX();
            double fromY = getY();

            double dist = t.euclidianDistance(fromX, fromY, toX, toY);
            Point vec = new Point(toX - fromX, toY - fromY);

            double atan = (180 / Math.PI) * normalRelativeAngle(Math.atan2(vec.getX(), vec.getY()) - getHeadingRadians());

            turnRight(atan + shiftAngle);
            //turnRadarRight(-getRadarHeading()+getHeading());
            setTurnRadarRight(3600000);
            setAhead(dist + shiftDistance);
            if(!dontFire)
                setFire(dist>300? 1 : (dist>100? 2 : 3));
            waitFor(new MoveCompleteCondition(this));
            if(lastHits.size()>3)
                lastHits= new ArrayList<>();
        }
    }


}
