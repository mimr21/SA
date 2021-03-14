package Utilities;

import robocode.Condition;
import robocode.Robot;

public class Dying extends Condition {
    private Robot r;
    private double maxNRG;

    public Dying(String name, Robot r, double mxnrg) {
        super(name);
        this.r = r;
        maxNRG= mxnrg;
    }

    @Override
    public boolean test() {
        return (r.getEnergy())<5;
    }
}
