import ATtree.TreeNode;
import objects.Operations;
import objects.Transition;

import java.util.Arrays;
import java.util.Set;

import static objects.Operations.op1;
import static objects.Operations.op2;

//triangle inequality to enhance the query of transitions
public class Enhancement {
    public static boolean leftPrune(Transition vq, Transition q, double v_theta, double theta,
                                    Set<Transition> Rt, Set<Transition> Rl, TreeNode v, Transition[] T) {
        if (!op1(vq,q,theta+v_theta)) {
            //剪枝左子树
            return true;
        }
        else if (op2(vq,q,theta-v_theta)) {
            //左子树为结果
            if (v.left.start <= v.left.end) {
                Rt.addAll(Arrays.asList(T).subList(v.left.start, v.left.end+1));
                Rl.addAll(Arrays.asList(T).subList(v.left.start, v.left.end+1));
            }

            return true;
        }
        //需要进一步考虑左子树
        return false;

    }

    public static boolean midPrune(Transition vq, Transition q, double v_theta, double theta,
                                   Set<Transition> Rt, Set<Transition> Rl, TreeNode v, Transition[] T) {
        if (op2(vq,q,theta-v_theta)) {
            //中间子树为结果
            if (v.middle.start <= v.middle.end) {
                Rt.addAll(Arrays.asList(T).subList(v.middle.start, v.middle.end+1));
                Rl.addAll(Arrays.asList(T).subList(v.middle.start, v.middle.end+1));
            }
            return true;
        }
        //需要进一步考虑中间子树
        return false;
    }

    public static boolean rightPrune(Transition vq, Transition q, double v_theta, double theta,
                                     Set<Transition> Rt, Set<Transition> Rl, TreeNode v, Transition[] T) {
        return op2(vq, q, v_theta - theta);
    }

    public static void EN_LCQ_TCQ_Search(Transition[] T, Transition q, double theta, TreeNode v,
                                      Set<Transition> Rt, Set<Transition> Rl) {
        //if the node is a leaf node
        if (v.q == null) {
            Algorithm.Crack_In_Three(T, v.start, v.end, q, theta, Rt, Rl, v);
            v.q = q;
            v.theta = theta;
        }
        //if the node is not a leaf node
        else {
            if (!Operations.op1(v.q, q, theta + v.theta)) {
                //disjoint query ranges
                EN_LCQ_TCQ_Search(T, q, theta, v.middle, Rt, Rl);
                if (!rightPrune(v.q, q, v.theta, theta, Rt, Rl, v, T))
                    EN_LCQ_TCQ_Search(T, q, theta, v.right, Rt, Rl);
            }
            //overlapping query ranges
            else {
                if (Operations.op2(v.q, q, theta - v.theta)) {
                    // q contains v.q
                    Algorithm.addTransition(T, v.left.start,v.middle.end,Rl);
                    Algorithm.addTransition(T, v.left.start, v.left.end, Rt);
                    EN_LCQ_TCQ_Search(T, q, theta, v.middle, Rt, Rl);
                    if  (!rightPrune(v.q, q, v.theta, theta, Rt, Rl, v, T))
                        EN_LCQ_TCQ_Search(T, q, theta, v.right, Rt, Rl);
                } else {
                    //q entirely inside v.q
                    if (Operations.op2(v.q, q, v.theta - theta)) {
                        if (!leftPrune(v.q, q, v.theta, theta, Rt, Rl, v, T))
                            EN_LCQ_TCQ_Search(T, q, theta, v.left, Rt, Rl);
                        EN_LCQ_TCQ_Search(T, q, theta, v.middle, Rt, Rl);
                    } else {
                        //q not entirely inside v.q
                        if (!leftPrune(v.q, q, v.theta, theta, Rt, Rl, v, T))
                            EN_LCQ_TCQ_Search(T, q, theta, v.left, Rt, Rl);
                        EN_LCQ_TCQ_Search(T, q, theta, v.middle, Rt, Rl);
                        if (!rightPrune(v.q, q, v.theta, theta, Rt, Rl, v, T))
                            EN_LCQ_TCQ_Search(T, q, theta, v.right, Rt, Rl);

                    }
                }
            }
        }
    }
    //只进行TCQ查询
    //将middle和right合并






}
