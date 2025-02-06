package objects;

import java.util.AbstractMap;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

//Operations of LCQ and TCQ
public class Operations {

    public static boolean op1(Transition T1, Transition T2, double theta) {
        AbstractMap.SimpleEntry<DistancePair, DistancePair> DP = T1.distanceTo(T2);
        DistancePair DP1 = DP.getKey();
        DistancePair DP2 = DP.getValue();
        if ((DP1.getDistance1() <= theta || DP1.getDistance2() <= theta)
            || (DP2.getDistance1() <= theta || DP2.getDistance2() <= theta)) {
            return true;
        }
        return false;
    }
    public static boolean op2(Transition T1, Transition T2, double theta) {
        AbstractMap.SimpleEntry<DistancePair, DistancePair> DP = T1.distanceTo(T2);
        DistancePair DP1 = DP.getKey();
        DistancePair DP2 = DP.getValue();
        if ((DP1.getDistance1() <= theta && DP1.getDistance2() <= theta)
                || (DP2.getDistance1() <= theta && DP2.getDistance2() <= theta)) {
            return true;
        }
        return false;
    }

    public static boolean op1_t(Transition T1, Transition T2, double theta, Date startT, Date endT) {
        return op1(T1, T2, theta) && T1.getStartTime().after(startT) && T1.getEndTime().before(endT);
    }

    public static boolean op2_t(Transition T1, Transition T2, double theta, Date startT, Date endT) {
        return op2(T1, T2, theta) && T1.getStartTime().after(startT) && T1.getEndTime().before(endT);
    }

    public static Set<Transition> LCQ(Set<Transition> T, Transition Tq, double theta) {
        Set<Transition> res = new HashSet<>();
        for (Transition T1 : T) {
            if (op1(T1, Tq, theta)) {
                res.add(T1);
            }
        }
        return res;
    }

    public static Set<Transition> TCQ(Set<Transition> T, Transition Tq, double theta) {
        Set<Transition> res = new HashSet<>();
        for (Transition T1 : T) {
            if (op2(T1, Tq, theta)) {
                res.add(T1);
            }
        }
        return res;
    }

    public static void LCQ_TCQ(Set<Transition> T, Transition Tq, double theta, Set<Transition> Rl, Set<Transition> Rt) {
        for (Transition T1 : T) {
            if (op1(T1, Tq, theta)) {
                Rl.add(T1);
            }
            if (op2(T1, Tq, theta)) {
                Rt.add(T1);
            }
        }
    }
    //Loose Time-Constrained Coverage Query
    public static Set<Transition> TLCQ(Set<Transition> T, Transition Tq, double theta, Date startT, Date endT) {
        Set<Transition> res = new HashSet<>();
        for (Transition T1 : T) {
            if (op1(T1, Tq, theta) && T1.getStartTime().after(startT) && T1.getEndTime().before(endT)) {
                res.add(T1);
            }
        }
        return res;
    }

    public static Set<Transition> TTCQ(Set<Transition> T, Transition Tq, double theta, Date startT, Date endT) {
        Set<Transition> res = new HashSet<>();
        for (Transition T1 : T) {
            if (op2(T1, Tq, theta) && T1.getStartTime().after(startT) && T1.getEndTime().before(endT)) {
                res.add(T1);
            }
        }
        return res;
    }

    public static void T_LCQ_TCQ(Set<Transition> T, Transition Tq, double theta, Set<Transition> Rl, Set<Transition> Rt,
                                 Date startT, Date endT) {
        for (Transition T1 : T) {
            if (op1(T1, Tq, theta) && T1.getStartTime().after(startT) && T1.getEndTime().before(endT)) {
                Rl.add(T1);
            }
            if (op2(T1, Tq, theta) && T1.getStartTime().after(startT) && T1.getEndTime().before(endT)) {
                Rt.add(T1);
            }
        }
    }

}
