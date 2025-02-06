import ATtree.TreeNode;
import objects.DistancePair;
import objects.Operations;
import objects.Transition;

import java.util.*;

public class Algorithm_Time {
    public static void TLCQ_Search(Transition[] T, Transition q, double theta, TreeNode v,
                                  Set<Transition> Rl, Date startT, Date endT) {
        //if the node is a leaf node
        if (v.left == null && v.right == null) {
            if (v.start <= v.end) {
                if (v.end - v.start + 1 > Main.crackBound) {
                    Crack_In_Two_T(T, v.start, v.end, q, theta, Rl, v, startT, endT);
                    v.q = q;
                    v.theta = theta;

                }
                //直接遍历获取答案
                else {
                    for (int i = v.start; i <= v.end; i++) {
                        if (Operations.op1_t(T[i], q, theta,startT,endT)) {
                            Rl.add(T[i]);
                        }
                    }
                }
            }

        }
        //if the node is not a leaf node
        //*UPDATE : 左边的节点是满足LCQ和时间约束，其余的是放在右节点
        else {
            //如果时间不相交
            if (endT.before(v.startT) || startT.after(v.endT)) {
                return;
            }
            else {
                //时间完全包含，后续不需要检查时间了，但是好像没区别，因为我获取startT最早和endT最晚也需要遍历

                //空间约束
                if (!Operations.op1(v.q, q, theta + v.theta)) {
                    //disjoint query ranges
                    TLCQ_Search(T,q,theta,v.left,Rl,startT,endT); //影响性能很大
                    TLCQ_Search(T, q, theta, v.right,Rl,startT,endT);
                }
                //overlapping query ranges
                else {
                    if (Operations.op2(v.q, q, theta - v.theta)) {
                        // q contains v.q
                        T_addTransition(T, v.left.start,v.left.end,Rl,startT,endT);
                        TLCQ_Search(T, q, theta, v.right, Rl,startT,endT);
                    } else {
                        //q entirely inside v.q
                        if (Operations.op2(v.q, q, v.theta - theta)) {
                            TLCQ_Search(T, q, theta, v.left, Rl,startT,endT);
                        } else {
                            //q not entirely inside v.q
                            TLCQ_Search(T, q, theta, v.left, Rl,startT,endT);
                            TLCQ_Search(T, q, theta, v.right,Rl,startT,endT);

                        }
                    }
                }
            }




        }
    }

    public static void Crack_In_Two_T(Transition[] T, int lo, int hi, Transition q, double theta,
                                    Set<Transition> Rl, TreeNode v, Date startT, Date endT) {
        int i = lo;
        int j = hi;
        Date earliestStart = new Date(Long.MAX_VALUE);
        Date latestEnd = new Date(Long.MIN_VALUE);
        while (i <= j) {
            //将满足operations.op1(T[i],q,theta)放在左边，不满足放在右边
            earliestStart = minDate(T[i].getStartTime(),earliestStart);
            latestEnd = maxDate(T[i].getEndTime(),latestEnd);
            if (!Operations.op1(T[i], q, theta)) {
                Swap(T,i,j);
                j--;
            }
            else {
                if (T[i].getStartTime().after(startT) && T[i].getEndTime().before(endT))
                    Rl.add(T[i]);
                i++;
            }
        }

        v.startT = earliestStart;
        v.endT = latestEnd;
        v.left = new TreeNode(null,lo,j);
        v.right = new TreeNode(null,j+1,hi);

    }

    public static void TTCQ_Search(Transition[] T, Transition q, double theta, TreeNode v,
                                  Set<Transition> Rt, Date startT, Date endT) {
        //if the node is a leaf node
        if (v.left == null && v.right == null) {
            if (v.start <= v.end) {
                if (v.end - v.start + 1 > Main.crackBound)
                {
                    T_Crack_In_Two_TCQ(T, v.start, v.end, q, theta, Rt, v, startT, endT);
                    v.q = q;
                    v.theta = theta;
                }
                else {
                    for (int i = v.start; i <= v.end; i++) {
                        if (Operations.op2_t(T[i], q, theta,startT,endT)) {
                            Rt.add(T[i]);
                        }
                    }
                }
            }


        }
        //if the node is not a leaf node
        else {
            //如果时间不相交
            if (startT.before(v.startT) || endT.after(v.endT)) {
                return;
            }
            else {
                if (!Operations.op1(v.q, q, theta + v.theta)) {
                    //disjoint query ranges
                    TTCQ_Search(T, q, theta, v.right, Rt,startT,endT);
                }
                //overlapping query ranges
                else {
                    if (Operations.op2(v.q, q, theta - v.theta)) {
                        // q contains v.q
                        T_addTransition(T, v.left.start, v.left.end, Rt,startT,endT);
                        TTCQ_Search(T, q, theta, v.right, Rt,startT,endT);
                    } else {
                        //q entirely inside v.q
                        if (Operations.op2(v.q, q, v.theta - theta)) {
                            TTCQ_Search(T, q, theta, v.left, Rt,startT,endT);
                        } else {
                            //q not entirely inside v.q
                            TTCQ_Search(T, q, theta, v.left, Rt,startT,endT);
                            TTCQ_Search(T, q, theta, v.right, Rt,startT,endT);

                        }
                    }
                }
            }

        }
    }
    public static void T_Crack_In_Two_TCQ(Transition[] T, int lo, int hi, Transition q, double theta,
                                        Set<Transition> Rt, TreeNode v, Date startT, Date endT) {
        int i = lo;
        int j = hi;
        Date earliestStart = new Date(Long.MAX_VALUE);
        Date latestEnd = new Date(Long.MIN_VALUE);
        while (i <= j) {
            earliestStart = minDate(T[i].getStartTime(),earliestStart);
            latestEnd = maxDate(T[i].getEndTime(),latestEnd);
            if (!Operations.op2(T[i], q, theta)) {
                Swap(T,i,j);
                j--;
            }
            else {
                if (T[i].getStartTime().after(startT) && T[i].getEndTime().before(endT))
                    Rt.add(T[i]);
                i++;
            }
        }
        v.startT = earliestStart;
        v.endT = latestEnd;
        v.left = new TreeNode(null,lo,j);
        v.right = new TreeNode(null,j+1,hi);


    }

    public static void TLCQ_TCQ_Search(Transition[] T, Transition q, double theta, TreeNode v,
                                      Set<Transition> Rt, Set<Transition> Rl, Date startT, Date endT) {
        //if the node is a leaf node
        if (v.left == null && v.right == null) {
            if (v.start <= v.end) {
                if (v.end - v.start + 1 > Main.crackBound) {
                    T_Crack_In_Three(T, v.start, v.end, q, theta, Rt, Rl, v, startT,endT);
                    v.q = q;
                    v.theta = theta;
                }

                else {
                    for (int i = v.start; i <= v.end; i++) {
                        if (Operations.op1_t(T[i], q, theta,startT,endT)) {
                            Rl.add(T[i]);
                            if (Operations.op2(T[i], q, theta))
                                Rt.add(T[i]);
                        }

                    }
                }
            }


        }


        //if the node is not a leaf node
        else {
            //如果时间不相交
            if (startT.before(v.startT) || endT.after(v.endT)) {
                return;
            }
            else {
                if (!Operations.op1(v.q, q, theta + v.theta)) {
                    //disjoint query ranges
                    TLCQ_TCQ_Search(T, q, theta, v.middle, Rt, Rl,startT,endT);
                    TLCQ_TCQ_Search(T, q, theta, v.right, Rt, Rl,startT,endT);
                }
                //overlapping query ranges
                else {
                    if (Operations.op2(v.q, q, theta - v.theta)) {
                        // q contains v.q
                        T_addTransition(T, v.left.start,v.middle.end,Rl,startT,endT);
                        T_addTransition(T, v.left.start, v.left.end, Rt,startT,endT);
                        TLCQ_TCQ_Search(T, q, theta, v.middle, Rt, Rl,startT,endT);
                        TLCQ_TCQ_Search(T, q, theta, v.right, Rt, Rl,startT,endT);
                    } else {
                        //q entirely inside v.q
                        if (Operations.op2(v.q, q, v.theta - theta)) {
                            TLCQ_TCQ_Search(T, q, theta, v.left, Rt, Rl,startT,endT);
                            TLCQ_TCQ_Search(T, q, theta, v.middle, Rt, Rl,startT,endT);
                        } else {
                            //q not entirely inside v.q
                            TLCQ_TCQ_Search(T, q, theta, v.left, Rt, Rl,startT,endT);
                            TLCQ_TCQ_Search(T, q, theta, v.middle, Rt, Rl,startT,endT);
                            TLCQ_TCQ_Search(T, q, theta, v.right, Rt, Rl,startT,endT);

                        }
                    }
                }
            }

        }
    }

    //dual-pivot quicksort to crack_in_three
    public static void T_Crack_In_Three(Transition[] T, int lo, int hi, Transition q, double theta,
                                        Set<Transition> Rt, Set<Transition> Rl, TreeNode v,
                                        Date startT, Date endT) {
        int i = lo, k = lo, j = hi;
        Date earliestStart = new Date(Long.MAX_VALUE);
        Date latestEnd = new Date(Long.MIN_VALUE);
        while (i <= j) {
            //T[i] 需要被放到最左边
            earliestStart = minDate(T[i].getStartTime(),earliestStart);
            latestEnd = maxDate(T[i].getEndTime(),latestEnd);
            if (Operations.op2(T[i], q, theta)) {
                if (T[i].getStartTime().after(startT) && T[i].getEndTime().before(endT))
                    Rt.add(T[i]);
                Swap(T,i,k);
                k++;
                i++;
            }
            //T[i]需要被放到最右边
            else if (!Operations.op1(T[i], q, theta)) {
                Swap(T,i,j);
                j--;
            }
            else {
                if (T[i].getStartTime().after(startT) && T[i].getEndTime().before(endT))
                    Rl.add(T[i]);
                i++;
            }
        }
        //update Rt and Rl
        T_addTransition(T,lo,k-1,Rt,startT,endT);
        T_addTransition(T,lo,j,Rl,startT,endT);
        v.startT = earliestStart;
        v.endT = latestEnd;
        //update the child of v
        v.left = new TreeNode(null,lo,k-1);
        v.middle = new TreeNode(null,k,j);
        v.right = new TreeNode(null,j+1,hi);

    }

    public static void T_Mediocre_LCQ_Search(Transition[] T, Transition q, double theta, TreeNode v,
                                           Set<Transition> Rl, Date startT, Date endT) {
        //if the node is a leaf node
        if (v.left == null && v.right == null) {
            if (v.start <= v.end) {
                if (v.end - v.start >= Main.crackBound) {
                    Random rand = new Random(0);
                    int a = rand.nextInt(v.end - v.start+1) + v.start;
                    int b = rand.nextInt(v.end - v.start+1) + v.start;
                    int c = rand.nextInt(v.end - v.start+1) + v.start;
                    AbstractMap.SimpleEntry<DistancePair, DistancePair> DP = q.distanceTo(T[a]);
                    AbstractMap.SimpleEntry<DistancePair, DistancePair> DP_ = q.distanceTo(T[b]);
                    AbstractMap.SimpleEntry<DistancePair, DistancePair> DP__ = q.distanceTo(T[c]);
                    DistancePair DP1 = DP.getKey(); DistancePair DP2 = DP.getValue();
                    DistancePair DP1_ = DP_.getKey(); DistancePair DP2_ = DP_.getValue();
                    DistancePair DP1__ = DP__.getKey(); DistancePair DP2__ = DP__.getValue();
                    double min_ab = Math.min(Math.min(DP1.getDistance1(),DP1.getDistance2()),Math.min(DP2.getDistance1(),DP2.getDistance2()));
                    double min_ac = Math.min(Math.min(DP1_.getDistance1(),DP1_.getDistance2()),Math.min(DP2_.getDistance1(),DP2_.getDistance2()));
                    double min_bc = Math.min(Math.min(DP1__.getDistance1(),DP1__.getDistance2()),Math.min(DP2__.getDistance1(),DP2__.getDistance2()));
                    double mediocre = Math.max(Math.min(min_ab, min_ac), Math.min(Math.max(min_ab, min_ac), min_bc));
                    T_Crack_In_Two_Me(T, v.start, v.end, q, theta,mediocre,Rl, v, startT, endT);
                    v.q = q;
                    v.theta = mediocre;
                }
                else {
                    for (int i = v.start; i <= v.end; i++) {
                        if (Operations.op1_t(T[i], q, theta,startT,endT)) {
                            Rl.add(T[i]);
                        }
                    }
                }

            }

        }
        //if the node is not a leaf node
        else {
            if (startT.before(v.startT) || endT.after(v.endT)) {
                return;
            }
            else {
                if (!Operations.op1(v.q, q, theta + v.theta)) {
                    //disjoint query ranges
                    T_Mediocre_LCQ_Search(T,q,theta,v.left,Rl,startT,endT); //影响性能很大
                    T_Mediocre_LCQ_Search(T, q, theta, v.right,Rl,startT,endT);
                }
                //overlapping query ranges
                else {
                    if (Operations.op2(v.q, q, theta - v.theta)) {
                        // q contains v.q
                        T_addTransition(T, v.left.start,v.left.end,Rl,startT,endT);
                        T_Mediocre_LCQ_Search(T, q, theta, v.right, Rl,startT,endT);
                    } else {
                        //q entirely inside v.q
                        if (Operations.op2(v.q, q, v.theta - theta)) {
                            T_Mediocre_LCQ_Search(T, q, theta, v.left, Rl,startT,endT);
                        } else {
                            //q not entirely inside v.q
                            T_Mediocre_LCQ_Search(T, q, theta, v.left, Rl,startT,endT);
                            T_Mediocre_LCQ_Search(T, q, theta, v.right,Rl,startT,endT);

                        }
                    }
                }
            }

        }
    }



    public static void T_Crack_In_Two_Me(Transition[] T, int lo, int hi, Transition q, double theta,
                                       double mediocre, Set<Transition> Rl, TreeNode v,
                                       Date startT, Date endT) {
        int i = lo;
        int j = hi;
        Date earliestStart = new Date(Long.MAX_VALUE);
        Date latestEnd = new Date(Long.MIN_VALUE);
        while (i <= j) {
            earliestStart = minDate(T[i].getStartTime(),earliestStart);
            latestEnd = maxDate(T[i].getEndTime(),latestEnd);
            //将满足operations.op1(T[i],q,mediocre)放在左边，不满足放在右边
            if (Operations.op1(T[i], q, theta)) {
                if (T[i].getStartTime().after(startT) && T[i].getEndTime().before(endT))
                    Rl.add(T[i]);
            }
            if (!Operations.op1(T[i], q, mediocre)) {
                Swap(T,i,j);
                j--;
            }
            else {
                i++;
            }
        }

        for (int k = j; k <= hi;k++) {
            if (Operations.op1_t(T[k], q, theta,startT,endT)) {
                Rl.add(T[k]);
            }
        }
        v.startT = startT;
        v.endT = endT;
        v.left = new TreeNode(null,lo,j);
        v.right = new TreeNode(null,j+1,hi);


    }


    public static void T_Mediocre_TCQ_Search(Transition[] T, Transition q, double theta, TreeNode v,
                                           Set<Transition> Rt,Date startT, Date endT) {
        //if the node is a leaf node
        if (v.left == null && v.right == null) {
            if (v.start <= v.end) {
                if (v.end - v.start >= Main.crackBound) {
                    Random rand = new Random(0);
                    int a = rand.nextInt(v.end - v.start+1) + v.start;
                    int b = rand.nextInt(v.end - v.start+1) + v.start;
                    int c = rand.nextInt(v.end - v.start+1) + v.start;
                    AbstractMap.SimpleEntry<DistancePair, DistancePair> DP = q.distanceTo(T[a]);
                    AbstractMap.SimpleEntry<DistancePair, DistancePair> DP_ = q.distanceTo(T[b]);
                    AbstractMap.SimpleEntry<DistancePair, DistancePair> DP__ = q.distanceTo(T[c]);
                    DistancePair DP1 = DP.getKey(); DistancePair DP2 = DP.getValue();
                    DistancePair DP1_ = DP_.getKey(); DistancePair DP2_ = DP_.getValue();
                    DistancePair DP1__ = DP__.getKey(); DistancePair DP2__ = DP__.getValue();
                    double min_ab = Math.min(Math.max(DP1.getDistance1(),DP1.getDistance2()),Math.max(DP2.getDistance1(),DP2.getDistance2()));
                    double min_ac = Math.min(Math.max(DP1_.getDistance1(),DP1_.getDistance2()),Math.max(DP2_.getDistance1(),DP2_.getDistance2()));
                    double min_bc = Math.min(Math.max(DP1__.getDistance1(),DP1__.getDistance2()),Math.max(DP2__.getDistance1(),DP2__.getDistance2()));
                    double mediocre = Math.max(Math.min(min_ab, min_ac), Math.min(Math.max(min_ab, min_ac), min_bc));

                    Crack_In_Two_Me_TCQ(T, v.start, v.end, q, theta,mediocre,Rt, v,startT,endT);
                    v.q = q;
                    v.theta = mediocre;
                }
                else {
                    for (int i = v.start; i <= v.end; i++) {
                        if (Operations.op2_t(T[i], q, theta,startT,endT)) {
                            Rt.add(T[i]);
                        }
                    }
                }

            }

        }
        //if the node is not a leaf node
        else {
            if (startT.before(v.startT) || endT.after(v.endT)) {
                return;
            }
            else {
                if (!Operations.op1(v.q, q, theta + v.theta)) {
                    //disjoint query ranges
                    T_Mediocre_TCQ_Search(T, q, theta, v.right,Rt,startT,endT);
                }
                //overlapping query ranges
                else {
                    if (Operations.op2(v.q, q, theta - v.theta)) {
                        // q contains v.q
                        T_addTransition(T, v.left.start,v.left.end,Rt,startT,endT);
                        T_Mediocre_TCQ_Search(T, q, theta, v.right, Rt,startT,endT);
                    } else {
                        //q entirely inside v.q
                        if (Operations.op2(v.q, q, v.theta - theta)) {
                            T_Mediocre_TCQ_Search(T, q, theta, v.left, Rt,startT,endT);
                        } else {
                            //q not entirely inside v.q
                            T_Mediocre_TCQ_Search(T, q, theta, v.left, Rt,startT,endT);
                            T_Mediocre_TCQ_Search(T, q, theta, v.right,Rt,startT,endT);

                        }
                    }
                }
            }

        }
    }



    public static void Crack_In_Two_Me_TCQ(Transition[] T, int lo, int hi, Transition q, double theta,
                                           double mediocre, Set<Transition> Rt, TreeNode v,
                                           Date startT, Date endT) {
        int i = lo;
        int j = hi;
        Date earliestStart = new Date(Long.MAX_VALUE);
        Date latestEnd = new Date(Long.MIN_VALUE);
        while (i <= j) {
            earliestStart = minDate(T[i].getStartTime(),earliestStart);
            latestEnd = maxDate(T[i].getEndTime(),latestEnd);
            //将满足operations.op1(T[i],q,mediocre)放在左边，不满足放在右边
            if (Operations.op2_t(T[i], q, theta,startT,endT)) {
                Rt.add(T[i]);
            }
            if (!Operations.op2(T[i], q, mediocre)) {
                Swap(T,i,j);
                j--;
            }
            else {
                i++;
            }
        }
        for (int k = j; k <= hi;k++) {
            if (Operations.op2_t(T[k], q, theta,startT,endT)) {
                Rt.add(T[k]);
            }
        }
        v.startT = earliestStart;
        v.endT = latestEnd;
        v.left = new TreeNode(null,lo,j);
        v.right = new TreeNode(null,j+1,hi);


    }


    public static void T_LCQ_TCQ_Search_M(Transition[] T, Transition q, double theta, TreeNode v,
                                        Set<Transition> Rt, Set<Transition> Rl,
                                          Date startT, Date endT) {
        //if the node is a leaf node
        if (v.left == null && v.right == null && v.middle == null) {
            if (v.start <= v.end) {
                if (v.end - v.start + 1 > Main.crackBound) {
                    Random rand = new Random(0);
                    int a = rand.nextInt(v.end - v.start+1) + v.start;
                    int b = rand.nextInt(v.end - v.start+1) + v.start;
                    int c = rand.nextInt(v.end - v.start+1) + v.start;
                    AbstractMap.SimpleEntry<DistancePair, DistancePair> DP = q.distanceTo(T[a]);
                    AbstractMap.SimpleEntry<DistancePair, DistancePair> DP_ = q.distanceTo(T[b]);
                    AbstractMap.SimpleEntry<DistancePair, DistancePair> DP__ = q.distanceTo(T[c]);
                    DistancePair DP1 = DP.getKey(); DistancePair DP2 = DP.getValue();
                    DistancePair DP1_ = DP_.getKey(); DistancePair DP2_ = DP_.getValue();
                    DistancePair DP1__ = DP__.getKey(); DistancePair DP2__ = DP__.getValue();
                    double min_ab = Math.max(Math.min(DP1.getDistance1(),DP1.getDistance2()),Math.min(DP2.getDistance1(),DP2.getDistance2()));
                    double min_ac = Math.max(Math.min(DP1_.getDistance1(),DP1_.getDistance2()),Math.min(DP2_.getDistance1(),DP2_.getDistance2()));
                    double min_bc = Math.max(Math.min(DP1__.getDistance1(),DP1__.getDistance2()),Math.min(DP2__.getDistance1(),DP2__.getDistance2()));
                    double mediocre = Math.max(Math.min(min_ab, min_ac), Math.min(Math.max(min_ab, min_ac), min_bc));
                    T_Crack_In_Three_M(T, v.start, v.end, q, theta,mediocre,Rt,Rl, v,startT,endT);
                    v.q = q;
                    v.theta = mediocre;


                }

                else {
                    for (int i = v.start; i <= v.end; i++) {
                        if (Operations.op1_t(T[i], q, theta, startT, endT)) {
                            Rl.add(T[i]);
                            if (Operations.op2(T[i], q, theta))
                                Rt.add(T[i]);
                        }

                    }
                }
            }

        }

        //if the node is not a leaf node
        else {
            if (startT.before(v.startT) || endT.after(v.endT)) {
                return;
            }
            else {
                if (!Operations.op1(v.q, q, theta + v.theta)) {
                    //disjoint query ranges
                    T_LCQ_TCQ_Search_M(T, q, theta, v.middle, Rt, Rl,startT,endT);
                    T_LCQ_TCQ_Search_M(T, q, theta, v.right, Rt, Rl,startT,endT);
                }
                //overlapping query ranges
                else {
                    if (Operations.op2(v.q, q, theta - v.theta)) {
                        // q contains v.q
                        T_addTransition(T, v.left.start,v.middle.end,Rl,startT,endT);
                        T_addTransition(T, v.left.start, v.left.end, Rt,startT,endT);
                        T_LCQ_TCQ_Search_M(T, q, theta, v.middle, Rt, Rl,startT,endT);
                        T_LCQ_TCQ_Search_M(T, q, theta, v.right, Rt, Rl,startT,endT);
                    } else {
                        //q entirely inside v.q
                        if (Operations.op2(v.q, q, v.theta - theta)) {
                            T_LCQ_TCQ_Search_M(T, q, theta, v.left, Rt, Rl,startT,endT);
                            T_LCQ_TCQ_Search_M(T, q, theta, v.middle, Rt, Rl,startT,endT);
                        } else {
                            //q not entirely inside v.q
                            T_LCQ_TCQ_Search_M(T, q, theta, v.left, Rt, Rl,startT,endT);
                            T_LCQ_TCQ_Search_M(T, q, theta, v.middle, Rt, Rl,startT,endT);
                            T_LCQ_TCQ_Search_M(T, q, theta, v.right, Rt, Rl,startT,endT);

                        }
                    }
                }
            }


        }
    }


    public static void T_Crack_In_Three_M(Transition[] T, int lo, int hi, Transition q, double theta,double mediocre,
                                        Set<Transition> Rt, Set<Transition> Rl, TreeNode v,
                                        Date startT, Date endT) {
        int i = lo, k = lo, j = hi;
        Date earliestStart = new Date(Long.MAX_VALUE);
        Date latestEnd = new Date(Long.MIN_VALUE);
        while (i <= j) {
            earliestStart = minDate(T[i].getStartTime(),earliestStart);
            latestEnd = maxDate(T[i].getEndTime(),latestEnd);
            if (Operations.op1_t(T[i],q,theta,startT,endT)) {
                Rl.add(T[i]);
                if (Operations.op2(T[i],q,theta))
                    Rt.add(T[i]);
            }
            //T[i] 需要被放到最左边
            if (Operations.op2(T[i], q, mediocre)) {
                Swap(T,i,k);
                k++;
                i++;
            }
            //T[i]需要被放到最右边
            else if (!Operations.op1(T[i], q, mediocre)) {
                Swap(T,i,j);
                j--;
            }
            else {
                i++;
            }
        }
        for (int kk = j; kk <= hi; kk++) {
            if (Operations.op1(T[kk],q,theta)) {
                if (T[kk].getStartTime().after(startT) && T[kk].getEndTime().before(endT)) {
                    Rl.add(T[kk]);
                    if (Operations.op2(T[kk],q,theta))
                        Rt.add(T[kk]);
                }

            }
            else {
                break;
            }
        }
        v.startT = earliestStart;
        v.endT = latestEnd;
        //update the child of v
        v.left = new TreeNode(null,lo,k-1);
        v.middle = new TreeNode(null,k,j);
        v.right = new TreeNode(null,j+1,hi);

    }



    public static Date minDate(Date date1, Date date2) {
        return date1.before(date2) ? date1 : date2;
    }

    public static Date maxDate(Date date1, Date date2) {
        return date1.after(date2) ? date1 : date2;
    }

    public static void Swap(Transition[] T, int i, int j) {
        Transition temp = T[i];
        T[i] = T[j];
        T[j] = temp;
    }

    public static void addTransition(Transition[] T, int start, int end, Set<Transition> R) {
        for (int i = start; i <= end; i++) {
            R.add(T[i]);
        }
    }

    public static void T_addTransition(Transition[] T, int start, int end, Set<Transition> R,
                                       Date startT, Date endT) {
        for (int i = start; i <= end; i++) {
            if (T[i].getStartTime().after(startT) && T[i].getEndTime().before(endT))
                R.add(T[i]);
        }
    }

}
