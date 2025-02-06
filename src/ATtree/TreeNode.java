package ATtree;

import objects.Transition;

import java.util.Date;

public class TreeNode {
    public Transition q;
    public double theta;
    public int start;
    public int end;
    public TreeNode left, middle, right;
    public Date startT, endT;

    public TreeNode(Transition q, double theta, int start, int end) {
        this.q = q;
        this.theta = theta;
        this.start = start;
        this.end = end;
        this.left = null;
        this.middle = null;
        this.right = null;
    }

    public TreeNode(Transition q, double theta, int start, int end, TreeNode left, TreeNode middle, TreeNode right) {
        this.q = q;
        this.theta = theta;
        this.start = start;
        this.end = end;
        this.left = left;
        this.middle = middle;
        this.right = right;
    }
    //单纯进行LCQ查询，将left和middle合并
    public TreeNode(Transition q, double theta, int start, int end, TreeNode left, TreeNode right) {
        this.q = q;
        this.theta = theta;
        this.start = start;
        this.end = end;
        this.left = left;
        this.right = right;
    }

    public TreeNode(Transition q, int start, int end) {
        this.q = q;
        this.start = start;
        this.end = end;
        this.left = null;
        this.middle = null;
        this.right = null;
    }

    //*UPDATE: add Time Parameter
    public TreeNode(Transition q, int start, int end, Date startT, Date endT) {
        this.q = q;
        this.start = start;
        this.end = end;
        this.left = null;
        this.middle = null;
        this.right = null;
        this.startT = startT;
        this.endT = endT;
    }
}
