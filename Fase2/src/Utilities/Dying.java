package Utilities;

import robocode.Condition;
import robocode.Robot;
import robocode.Rules;

public class Dying extends Condition {
    private Robot r;
    private double maxNRG;

    public Dying(String name, Robot r) {
        super(name);
        this.r = r;

    }

    @Override
    public boolean test() {
        return (r.getEnergy())<16;
    }
}
