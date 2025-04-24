import ATtree.TreeNode;
import jdk.dynalink.Operation;
import objects.DistancePair;
import objects.Operations;
import objects.Transition;

import java.util.*;

public class Algorithm {
  public static void LCQ_TCQ_Search(Transition[] T, Transition q, double theta, TreeNode v,
                             Set<Transition> Rt, Set<Transition> Rl) {
      //if the node is a leaf node
      if (v.left == null && v.right == null) {
          if (v.start <= v.end) {
              if (v.end - v.start + 1 > Main.crackBound) {
                  Crack_In_Three(T, v.start, v.end, q, theta, Rt, Rl, v);
                  v.q = q;
                  v.theta = theta;
              }

              else {
                  for (int i = v.start; i <= v.end; i++) {
                      if (Operations.op1(T[i], q, theta)) {
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
          if (!Operations.op1(v.q, q, theta + v.theta)) {
              //disjoint query ranges
              LCQ_TCQ_Search(T, q, theta, v.middle, Rt, Rl);
              LCQ_TCQ_Search(T, q, theta, v.right, Rt, Rl);
          }
          //overlapping query ranges
          else {
              if (Operations.op2(v.q, q, theta - v.theta)) {
                  // q contains v.q
                  addTransition(T, v.left.start,v.middle.end,Rl);
                  addTransition(T, v.left.start, v.left.end, Rt);
                  LCQ_TCQ_Search(T, q, theta, v.middle, Rt, Rl);
                  LCQ_TCQ_Search(T, q, theta, v.right, Rt, Rl);
              } else {
                  //q entirely inside v.q
                  if (Operations.op2(v.q, q, v.theta - theta)) {
                      LCQ_TCQ_Search(T, q, theta, v.left, Rt, Rl);
                      LCQ_TCQ_Search(T, q, theta, v.middle, Rt, Rl);
                  } else {
                      //q not entirely inside v.q
                      LCQ_TCQ_Search(T, q, theta, v.left, Rt, Rl);
                      LCQ_TCQ_Search(T, q, theta, v.middle, Rt, Rl);
                      LCQ_TCQ_Search(T, q, theta, v.right, Rt, Rl);

                  }
              }
          }
      }
  }

  //dual-pivot quicksort to crack_in_three
  public static void Crack_In_Three(Transition[] T, int lo, int hi, Transition q, double theta,
                                    Set<Transition> Rt, Set<Transition> Rl, TreeNode v) {
      int i = lo, k = lo, j = hi;
      while (i <= j) {
          //T[i] 需要被放到最左边
          if (Operations.op2(T[i], q, theta)) {
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
              Rl.add(T[i]);
              i++;
          }
      }
      //update Rt and Rl
      addTransition(T,lo,k-1,Rt);
      addTransition(T,lo,j,Rl);
      //update the child of v
      v.left = new TreeNode(null,lo,k-1);
      v.middle = new TreeNode(null,k,j);
      v.right = new TreeNode(null,j+1,hi);

  }

//中位数Co-LCQ-TCQ
    public static void LCQ_TCQ_Search_M(Transition[] T, Transition q, double theta, TreeNode v,
                                      Set<Transition> Rt, Set<Transition> Rl) {
        if (v == null) return;
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
                    Crack_In_Three_M(T, v.start, v.end, q, theta,mediocre,Rt,Rl, v);
                    v.q = q;
                    v.theta = mediocre;


                }

                else {
                    for (int i = v.start; i <= v.end; i++) {
                        if (Operations.op1(T[i], q, theta)) {
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
            if (!Operations.op1(v.q, q, theta + v.theta)) {
                //disjoint query ranges
                LCQ_TCQ_Search_M(T, q, theta, v.middle, Rt, Rl);
                LCQ_TCQ_Search_M(T, q, theta, v.right, Rt, Rl);
            }
            //overlapping query ranges
            else {
                if (Operations.op2(v.q, q, theta - v.theta)) {
                    // q contains v.q
                    addTransition(T, v.left.start,v.middle.end,Rl);
                    addTransition(T, v.left.start, v.left.end, Rt);
                    LCQ_TCQ_Search_M(T, q, theta, v.middle, Rt, Rl);
                    LCQ_TCQ_Search_M(T, q, theta, v.right, Rt, Rl);
                } else {
                    //q entirely inside v.q
                    if (Operations.op2(v.q, q, v.theta - theta)) {
                        LCQ_TCQ_Search_M(T, q, theta, v.left, Rt, Rl);
                        LCQ_TCQ_Search_M(T, q, theta, v.middle, Rt, Rl);
                    } else {
                        //q not entirely inside v.q
                        LCQ_TCQ_Search_M(T, q, theta, v.left, Rt, Rl);
                        LCQ_TCQ_Search_M(T, q, theta, v.middle, Rt, Rl);
                        LCQ_TCQ_Search_M(T, q, theta, v.right, Rt, Rl);

                    }
                }
            }
        }
    }


    public static void Crack_In_Three_M(Transition[] T, int lo, int hi, Transition q, double theta,double mediocre,
                                      Set<Transition> Rt, Set<Transition> Rl, TreeNode v) {
        int i = lo, k = lo, j = hi;
        while (i <= j) {
            if (Operations.op1(T[i],q,theta)) {
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
                Rl.add(T[kk]);
                if (Operations.op2(T[kk],q,theta))
                    Rt.add(T[kk]);
            }
            else {
                break;
            }
        }
        //update the child of v
        v.left = new TreeNode(null,lo,k-1);
        v.middle = new TreeNode(null,k,j);
        v.right = new TreeNode(null,j+1,hi);

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


  //search the tree without building
    public static void onlySearch(Transition[] T, Transition q, double theta, TreeNode v,
                                      Set<Transition> Rt, Set<Transition> Rl) {
        if (v == null) return;

        //if the node is a leaf node
        if (v.q == null) {
            Crack_In_Three_Without_building(T, v.start, v.end, q, theta, Rt, Rl, v);
        }
        //if the node is not a leaf node
        else {
            if (!Operations.op1(v.q, q, theta + v.theta)) {
                //disjoint query ranges
                onlySearch(T, q, theta, v.middle, Rt, Rl);
                onlySearch(T, q, theta, v.right, Rt, Rl);
            }
            //overlapping query ranges
            else {
                if (Operations.op2(v.q, q, theta - v.theta)) {
                    // q contains v.q
                    addTransition(T, v.left.start,v.middle.end,Rl);
                    addTransition(T, v.left.start, v.left.end, Rt);
                    onlySearch(T, q, theta, v.middle, Rt, Rl);
                    onlySearch(T, q, theta, v.right, Rt, Rl);
                } else {
                    //q entirely inside v.q
                    if (Operations.op2(v.q, q, v.theta - theta)) {
                        onlySearch(T, q, theta, v.left, Rt, Rl);
                        onlySearch(T, q, theta, v.middle, Rt, Rl);
                    } else {
                        //q not entirely inside v.q
                        onlySearch(T, q, theta, v.left, Rt, Rl);
                        onlySearch(T, q, theta, v.middle, Rt, Rl);
                        onlySearch(T, q, theta, v.right, Rt, Rl);

                    }
                }
            }
        }
    }
    public static void Crack_In_Three_Without_building(Transition[] T, int lo, int hi, Transition q, double theta,
                                      Set<Transition> Rt, Set<Transition> Rl, TreeNode v) {
        int i = lo, k = lo, j = hi;
        while (i <= j) {
            //T[i] 需要被放到最左边
            if (Operations.op2(T[i], q, theta)) {
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
                i++;
            }
        }
        //update Rt and Rl
        addTransition(T,lo,k-1,Rt);
        addTransition(T,lo,j,Rl);

    }

    //只进行TCQ查询
    //将middle和right合并
    public static void LCQ_Search(Transition[] T, Transition q, double theta, TreeNode v,
                                  Set<Transition> Rl) {
        //if the node is a leaf node
        if (v.left == null && v.right == null) {
            if (v.start <= v.end) {
                if (v.end - v.start + 1 > Main.crackBound)
                    Crack_In_Two(T, v.start, v.end, q, theta, Rl, v);
                else {
                    for (int i = v.start; i <= v.end; i++) {
                        if (Operations.op1(T[i], q, theta)) {
                            Rl.add(T[i]);
                        }
                    }
                }
            }

            v.q = q;
            v.theta = theta;
        }
        //if the node is not a leaf node
        else {
            if (!Operations.op1(v.q, q, theta + v.theta)) {
                //disjoint query ranges
                LCQ_Search(T,q,theta,v.left,Rl); //影响性能很大
                LCQ_Search(T, q, theta, v.right,Rl);
            }
            //overlapping query ranges
            else {
                if (Operations.op2(v.q, q, theta - v.theta)) {
                    // q contains v.q
                    addTransition(T, v.left.start,v.left.end,Rl);
                    LCQ_Search(T, q, theta, v.right, Rl);
                } else {
                    //q entirely inside v.q
                    if (Operations.op2(v.q, q, v.theta - theta)) {
                        LCQ_Search(T, q, theta, v.left, Rl);
                    } else {
                        //q not entirely inside v.q
                        LCQ_Search(T, q, theta, v.left, Rl);
                        LCQ_Search(T, q, theta, v.right,Rl);

                    }
                }
            }
        }
    }

    public static void Crack_In_Two(Transition[] T, int lo, int hi, Transition q, double theta,
                                     Set<Transition> Rl, TreeNode v) {
      int i = lo;
      int j = hi;
      while (i <= j) {
          //将满足operations.op1(T[i],q,theta)放在左边，不满足放在右边
          if (!Operations.op1(T[i], q, theta)) {
              Swap(T,i,j);
              j--;
          }
          else {
              Rl.add(T[i]);
              i++;
          }
      }

      v.left = new TreeNode(null,lo,j);
      v.right = new TreeNode(null,j+1,hi);


    }

// 基于中位数的分裂LCQ
    public static void Mediocre_LCQ_Search(Transition[] T, Transition q, double theta, TreeNode v,
                                  Set<Transition> Rl) {
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
                    Crack_In_Two_Me(T, v.start, v.end, q, theta,mediocre,Rl, v);
                    v.q = q;
                    v.theta = mediocre;
                }
                else {
                    for (int i = v.start; i <= v.end; i++) {
                        if (Operations.op1(T[i], q, theta)) {
                            Rl.add(T[i]);
                        }
                    }
                }

            }

        }
        //if the node is not a leaf node
        else {
            if (!Operations.op1(v.q, q, theta + v.theta)) {
                //disjoint query ranges
                Mediocre_LCQ_Search(T,q,theta,v.left,Rl); //影响性能很大
                Mediocre_LCQ_Search(T, q, theta, v.right,Rl);
            }
            //overlapping query ranges
            else {
                if (Operations.op2(v.q, q, theta - v.theta)) {
                    // q contains v.q
                    addTransition(T, v.left.start,v.left.end,Rl);
                    Mediocre_LCQ_Search(T, q, theta, v.right, Rl);
                } else {
                    //q entirely inside v.q
                    if (Operations.op2(v.q, q, v.theta - theta)) {
                        Mediocre_LCQ_Search(T, q, theta, v.left, Rl);
                    } else {
                        //q not entirely inside v.q
                        Mediocre_LCQ_Search(T, q, theta, v.left, Rl);
                        Mediocre_LCQ_Search(T, q, theta, v.right,Rl);

                    }
                }
            }
        }
    }



    public static void Crack_In_Two_Me(Transition[] T, int lo, int hi, Transition q, double theta,
                                    double mediocre, Set<Transition> Rl, TreeNode v) {
        int i = lo;
        int j = hi;
        while (i <= j) {
            //将满足operations.op1(T[i],q,mediocre)放在左边，不满足放在右边
            if (Operations.op1(T[i], q, theta)) {
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
            if (Operations.op1(T[k], q, theta)) {
                Rl.add(T[k]);
            }
        }


        v.left = new TreeNode(null,lo,j);
        v.right = new TreeNode(null,j+1,hi);


    }

    public static void TCQ_Search(Transition[] T, Transition q, double theta, TreeNode v,
                                  Set<Transition> Rt) {
        if (v.left == null && v.right == null) {
            if (v.end - v.start + 1 >= Main.crackBound) {
                v.q = q;
                v.theta = theta;
                Crack_In_Two_TCQ(T, v.start, v.end, q, theta, Rt, v);

            }
            else {
                for (int i = v.start; i <= v.end+1; i++) {
                    if (Operations.op2(T[i],q,theta))
                        Rt.add(T[i]);
                }
            }

        }
        //if the node is not a leaf node
        else {
            if (!Operations.op1(v.q, q, theta + v.theta)) {
                //disjoint query ranges
                TCQ_Search(T, q, theta, v.right, Rt);
            }
            //overlapping query ranges
            else {
                if (Operations.op2(v.q, q, theta - v.theta)) {
                    // q contains v.q
                    addTransition(T, v.left.start, v.left.end, Rt);
                    TCQ_Search(T, q, theta, v.right, Rt);
                } else {
                    //q entirely inside v.q
                    if (Operations.op2(v.q, q, v.theta - theta)) {
                        TCQ_Search(T, q, theta, v.left, Rt);
                    } else {
                        //q not entirely inside v.q
                        TCQ_Search(T, q, theta, v.left, Rt);
                        TCQ_Search(T, q, theta, v.right, Rt);

                    }
                }
            }
        }
    }
    public static void Crack_In_Two_TCQ(Transition[] T, int lo, int hi, Transition q, double theta,
                                    Set<Transition> Rt, TreeNode v) {
        int i = lo;
        int j = hi;
        while (i <= j) {
            //将满足operations.op1(T[i],q,theta)放在左边，不满足放在右边
            if (!Operations.op2(T[i], q, theta)) {
                Swap(T,i,j);
                j--;
            }
            else {
                Rt.add(T[i]);
                i++;
            }
        }
        v.left = new TreeNode(null,lo,j);
        v.right = new TreeNode(null,j+1,hi);


    }

    // 基于中位数的分裂TCQ
    public static void Mediocre_TCQ_Search(Transition[] T, Transition q, double theta, TreeNode v,
                                           Set<Transition> Rt) {
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

                    Crack_In_Two_Me_TCQ(T, v.start, v.end, q, theta,mediocre,Rt, v);
                    v.q = q;
                    v.theta = mediocre;
                }
                else {
                    for (int i = v.start; i <= v.end; i++) {
                        if (Operations.op2(T[i], q, theta)) {
                            Rt.add(T[i]);
                        }
                    }
                }

            }

        }
        //if the node is not a leaf node
        else {
            if (!Operations.op1(v.q, q, theta + v.theta)) {
                //disjoint query ranges
                Mediocre_TCQ_Search(T, q, theta, v.right,Rt);
            }
            //overlapping query ranges
            else {
                if (Operations.op2(v.q, q, theta - v.theta)) {
                    // q contains v.q
                    addTransition(T, v.left.start,v.left.end,Rt);
                    Mediocre_TCQ_Search(T, q, theta, v.right, Rt);
                } else {
                    //q entirely inside v.q
                    if (Operations.op2(v.q, q, v.theta - theta)) {
                        Mediocre_TCQ_Search(T, q, theta, v.left, Rt);
                    } else {
                        //q not entirely inside v.q
                        Mediocre_TCQ_Search(T, q, theta, v.left, Rt);
                        Mediocre_TCQ_Search(T, q, theta, v.right,Rt);

                    }
                }
            }
        }
    }



    public static void Crack_In_Two_Me_TCQ(Transition[] T, int lo, int hi, Transition q, double theta,
                                       double mediocre, Set<Transition> Rt, TreeNode v) {
        int i = lo;
        int j = hi;

        while (i <= j) {
            //将满足operations.op1(T[i],q,mediocre)放在左边，不满足放在右边
            if (Operations.op2(T[i], q, theta)) {
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
            if (Operations.op2(T[k], q, theta)) {
                Rt.add(T[k]);
            }
        }

        v.left = new TreeNode(null,lo,j);
        v.right = new TreeNode(null,j+1,hi);


    }




    public static void getLeafNode(Transition[] T, int lo, int hi, Transition q, double theta,
                                    Set<Transition> Rl) {
        int i = lo;
        int j = hi;
        while (i <= j) {
            //将满足operations.op1(T[i],q,theta)放在左边，不满足放在右边
            if (!Operations.op1(T[i], q, theta)) {
                Swap(T,i,j);
                j--;
            }
            else {
                i++;
            }
        }
        addTransition(T,lo,j,Rl);

    }
    static int sim = 0;
    static int size = 0;
    public static void getSimilarity(Transition[] T, Transition q, Transition q1, double theta, TreeNode v) {

      if (v == null) return;
      if (v.q == q1) {
          size += v.left.end - v.left.start+1;
          for (int i = v.left.start; i <= v.left.end; i++) {
              if (Operations.op1(T[i],q,theta)) {
                  sim ++;
              }
          }
          return;
      }
      else {
          getSimilarity(T,q,q1,theta,v.left);
          getSimilarity(T,q,q1,theta,v.right);
      }

    }

    //get the diff between two query transitions based op2(TCQ)
    public static void getSimTCQ(Transition[] T, Transition q, Transition q1, double theta, TreeNode v) {
        if (v == null) return;
        if (v.q == q1) {
            size += v.left.end - v.left.start+1;
            for (int i = v.left.start; i <= v.left.end; i++) {
                if (!Operations.op2(T[i],q,theta)) {
                    sim ++;
                }
            }
            return;
        }
        else {
            getSimTCQ(T,q,q1,theta,v.left);
            getSimTCQ(T,q,q1,theta,v.right);
        }
    }






}
