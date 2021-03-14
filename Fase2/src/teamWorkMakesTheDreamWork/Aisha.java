package teamWorkMakesTheDreamWork;

import Utilities.AreWeThereYet;
import Utilities.Point;
import Utilities.Tools;
import robocode.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static robocode.util.Utils.normalRelativeAngle;
import static robocode.util.Utils.normalRelativeAngleDegrees;


public class Aisha extends TeamRobot {
    private String target;
    private Point targetPos;
    Tools t = new Tools();
    private ArrayList<String> teammates =  new ArrayList<>();

    @Override
    public void run() {
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
            }
        }
        else{
            System.out.println("404");
        }

    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {

        if(event.getName().equals(target)){
            System.out.println("I see : "+event.getName());
            double enemyBearing = getHeading() + event.getBearing();
            // Calculate enemy's position
            double enemyX = getX() + event.getDistance() * Math.sin(Math.toRadians(enemyBearing));
            double enemyY = getY() + event.getDistance() * Math.cos(Math.toRadians(enemyBearing));
            targetPos = new Point(enemyX,enemyY);
        }
        if(isTeammate(event.getName())){
            if(event.getDistance()<10){
                back(30);
                turnRight(30);
                ahead(40);
            }
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
        back(30);
        turnRight(30);
        ahead(40);
    }

    @Override
    public boolean isTeammate(String name) {
        return teammates.contains(name);
    }
    @Override
    public void onHitRobot(HitRobotEvent event) {
        back(40);
        if(isTeammate(event.getName().split(" ")[0])){
            if(event.getBearing()>0){
                turnRight(-(90-event.getBearing()));
            }else{
                turnRight(90-event.getBearing());
            }
            ahead(40);
        }

    }

    void goTo(double toX, double toY){
        goTo(toX,toY,0,0);
    }
    void goTo(Point p){
        goTo(p.getX(),p.getY(),0,0);
    }
    void goTo(double toX, double toY, double shiftAngle, double shiftDistance){
        while(targetPos!=null && (targetPos.getX()==toX && targetPos.getY()==toY)) {
            double fromX = getX();
            double fromY = getY();

            double dist = t.euclidianDistance(fromX, fromY, toX, toY);
            Point vec = new Point(toX - fromX, toY - fromY);

            double atan = (180 / Math.PI) * normalRelativeAngle(Math.atan2(vec.getX(), vec.getY()) - getHeadingRadians());

            turnRight(atan + shiftAngle);
            turnRadarRight(-getRadarHeading()+getHeading());
            setTurnRadarRight(360);
            setAhead(dist + shiftDistance);
            setFire(2);
            execute();
        }
    }



}
