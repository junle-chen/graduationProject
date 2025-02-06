import ATtree.TreeNode;
import edu.princeton.cs.algs4.In;
import objects.Operations;
import objects.Transition;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static objects.Operations.op1;


public class Main {
    public static int query_num =  200;
    static double crackBound = 1000;//默认1000
    static String dataSetPath = "DataSet/202403-capitalbikeshare-tripdata.csv";//默认数据集

    static double q_theta = 1000;//默认1000
    //* update add time parameter
//    static String[] dataSetPaths = {"DataSet/202402-divvy-tripdata-time.csv","DataSet/202403-capitalbikeshare-tripdata-time.csv"
//            , "DataSet/combined_chicago_taxi_trip.csv"};
    static String[] dataSetPaths = {"DataSet/202402-divvy-tripdata-time.csv","DataSet/202403-capitalbikeshare-tripdata-time.csv"};

    static double[] thetas = {800,1000,1200,1400,1600,1800,2000};

    static double[] crackBounds = {500,1000,1500,2000,2500,3000,3500};

    static int algo_index; //算法索引 0.linear scan(LCQ_TCQ) 1. linear scan(LCQ) 2. linear scan(TCQ)
                           //        3.LCQ_TCQ 4. LCQ 5. TCQ 6. LCQ_M 7. TCQ_M 8.LCQ_TCQ_M

    static int[] lastTimes = {4,8,12,16,20,24,28};
    static int[] months = {Calendar.FEBRUARY, Calendar.MARCH};

    public static void main(String[] args) throws FileNotFoundException, ParseException {

        Path currentDir = Paths.get(System.getProperty("user.dir"));

        if (args.length != 6) {
            System.out.println("Usage: java Main <dataSetIndex> <q_theta> <crackBound> <algo_index> <last_time> <select_query>");
            System.out.println("dataSetIndex: 0. divvy-tripdata 1. capitalbikeshare-tripdata 2. chicago_taxi_trip_mouth1");
            System.out.println("q_theta: 800,1000,1200,1400,1600,1800,2000");
            System.out.println("crackBound: 500,1000,1500,2000,2500,3000,3500");
            System.out.println("algo_index: 0. linear scan(LCQ_TCQ) 1. linear scan(LCQ) 2. linear scan(TCQ) 3. LCQ_TCQ 4. LCQ 5. TCQ 6. LCQ_M 7. TCQ_M 8.LCQ_TCQ_M");
            System.out.println("time_algo_index: 0. TCQ(LS) 1. TCQ 2. TCQ(M) 3. LCQ(LS) 4. LCQ 5. LCQ(M) 6. LCQ_TCQ(LS) 7. LCQ_TCQ 8. LCQ_TCQ(M)");
            System.out.println("last_time: 4h, 8h, 12h, 16h, 20h 24h 28h");
            System.out.println("select: 0. basic query 1. time-constraint query");
            //0.TTCQ_linear_scan 1.TTCQ_ATtree 2.TTCQ_M_ATtree
            // 3. TLCQ_linear_scan 4. TLCQ_ATtree 5. TLCQ_M_ATtree
            // 6. T_LCQ_TCQ_linear_scan 7. TLCQ_TCQ_ATtree 8.TLCQ_TCQ_M_ATtree
            return;

        }
        dataSetPath = dataSetPaths[Integer.parseInt(args[0])];
        q_theta = thetas[Integer.parseInt(args[1])];
        crackBound = crackBounds[Integer.parseInt(args[2])];
        algo_index = Integer.parseInt(args[3]);
        int lastTime = lastTimes[Integer.parseInt(args[4])];
        int select = Integer.parseInt(args[5]);

        System.out.println("dataSet: "+dataSetPath+" theta: "+q_theta+" crackBound: "+crackBound+" algo_index: "+algo_index+" lastTime: "+lastTime+" select: "+select);

        String filePath = currentDir.resolve(dataSetPath).toString();
        DataProcess dp = new DataProcess(filePath);

        Transition[] transitions = dp.readData();
//        Transition[] transitions1 = dp.readData();
//        Set<Transition> transitionSet = new HashSet<>(Arrays.asList(transitions));

        TreeNode root = new TreeNode(null, 0, transitions.length - 1);
        int start = 5;
        int month = months[Integer.parseInt(args[0])];


        if (select == 0) {
            //basic query
            TestATtree(start, query_num, transitions, algo_index, q_theta, root);
        }
        else {
            TestTimeATtree(start,query_num,transitions,algo_index,q_theta,root,lastTime,month);
        }

    }

    public static void TestTimeATtree(int start, int query_num, Transition[] transitions, int choice,
                               double q_theta, TreeNode root, int lastTime, int month) {

        //0.TTCQ_linear_scan 1.TTCQ_ATtree 2.TTCQ_M_ATtree
        // 3. TLCQ_linear_scan 4. TLCQ_ATtree 5. TLCQ_M_ATtree
        // 6. T_LCQ_TCQ_linear_scan 7. TLCQ_TCQ_ATtree 8.TLCQ_TCQ_M_ATtree
        Transition[] transitions1 = Arrays.copyOf(transitions, transitions.length);
        Set<Transition> transitionSet = new HashSet<>(Arrays.asList(transitions));
        //LCQ TCQ结果集
        Set<Transition> Rt = new HashSet<>();
        Set<Transition> Rl = new HashSet<>();

        //      Test
        long buildTime = 0;
        long queryTime = 0;
        //Date settings
//        int lastTime = 24; //24h

        SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy H:mm");
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(2024, month, 1, 0, 0, 0);  // 2/1/2024 00:00
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(2024, month, 29, 23, 59, 59);  // 2/29/2024 23:59
        endCalendar.add(Calendar.HOUR, -lastTime);
        long startTimeInMillis = startCalendar.getTimeInMillis();
        long endTimeInMillis = endCalendar.getTimeInMillis();
        Random random = new Random(0);
        long randomTimeInMillis = startTimeInMillis + (long) (random.nextDouble() * (endTimeInMillis - startTimeInMillis));
        Date startT = new Date(randomTimeInMillis);
        Calendar endCalendarAdjusted = Calendar.getInstance();
        endCalendarAdjusted.setTime(startT);
        endCalendarAdjusted.add(Calendar.HOUR, lastTime);

        Date endT = endCalendarAdjusted.getTime();
        System.out.println("Random start time: " + dateFormat.format(startT)+" end time: "+dateFormat.format(endT));
        long testTime = 0;

        for (int i = start; i < start+query_num; i++) {
            Transition q1 = transitions1[i];
            Rl.clear();
            Rt.clear();
            long startTime = 0, endTime = 0;
            switch (choice) {
                case 0:
                    Set<Transition> Rl2;
                    startTime = System.currentTimeMillis();
                    Rl2 = Operations.TTCQ(transitionSet, q1, q_theta, startT, endT);
                    endTime = System.currentTimeMillis();
                    testTime += endTime-startTime;
                    System.out.println("TTCQ_linear_scan "+(i-start+1) + " Test Time: "+(endTime-startTime)+
                            "ms result's size: "+Rl2.size()+
                            " Accumulated time:"+testTime+"ms");

                    break;
                case 1:
                    startTime = System.currentTimeMillis();
                    Algorithm_Time.TTCQ_Search(transitions, q1, q_theta, root, Rt, startT, endT);
                    endTime = System.currentTimeMillis();
                    testTime += endTime-startTime;
                    System.out.println("TTCQ_ATtree "+ (i-start+1) + " Test Time: "+(endTime-startTime)+
                            "ms result's size: "+Rt.size()+
                            " Accumulated time:"+testTime+"ms");
                    break;
                case 2:
                    startTime = System.currentTimeMillis();
                    Algorithm_Time.T_Mediocre_TCQ_Search(transitions,q1,q_theta,root,Rl,startT,endT);
                    endTime = System.currentTimeMillis();
                    testTime += endTime-startTime;
                    System.out.println("TTCQ_M_ATtree "+(i-start+1) + " Test Time: "+(endTime-startTime)+
                            "ms result's size: "+Rl.size()+
                            " Accumulated time:"+testTime+"ms");
                    break;

                case 3:
                    Set<Transition> Rl1;
                    startTime = System.currentTimeMillis();
                    Rl1 = Operations.TLCQ(transitionSet, q1, q_theta, startT, endT);
                    endTime = System.currentTimeMillis();
                    testTime += endTime-startTime;
                    System.out.println("TLCQ_linear_scan "+(i-start+1) + " Test Time: "+(endTime-startTime)+
                            "ms result's size: "+Rl1.size()+
                            " Accumulated time:"+testTime+"ms");
                    break;
                case 4:
                    startTime = System.currentTimeMillis();
                    Algorithm_Time.TLCQ_Search(transitions, q1, q_theta, root, Rl, startT, endT);
                    endTime = System.currentTimeMillis();
                    testTime += endTime-startTime;
                    System.out.println("TLCQ_ATtree "+ (i-start+1) + " Test Time: "+(endTime-startTime)+
                            "ms result's size: "+Rl.size()+
                            " Accumulated time:"+testTime+"ms");
                    break;

                case 5:
                    startTime = System.currentTimeMillis();
                    Algorithm_Time.T_Mediocre_LCQ_Search(transitions,q1,q_theta,root,Rt,startT,endT);
                    endTime = System.currentTimeMillis();
                    testTime += endTime-startTime;
                    System.out.println("TLCQ_M_ATtree "+(i-start+1) + " Test Time: "+(endTime-startTime)+
                            "ms result's size: "+Rt.size()+
                            " Accumulated time:"+testTime+"ms");
                    break;

                case 6:
                    startTime = System.currentTimeMillis();
                    Operations.T_LCQ_TCQ(transitionSet, q1, q_theta, Rl, Rt, startT, endT);
                    endTime = System.currentTimeMillis();
                    testTime += endTime-startTime;
                    System.out.println("T_LCQ_TCQ_linear_scan "+(i-start+1) + " Test Time: "+(endTime-startTime)+
                            "ms LCQ result's size: "+Rl.size()+" TCQ result's size: "+Rt.size()+
                            " Accumulated time:"+testTime+"ms");
                    break;
                case 7:
                    startTime = System.currentTimeMillis();
                    Algorithm_Time.TLCQ_TCQ_Search(transitions,q1,q_theta,root,Rt,Rl,startT,endT);
                    endTime = System.currentTimeMillis();
                    testTime += endTime-startTime;
                    System.out.println("TLCQ_TCQ_ATtree "+(i-start+1) + " Test Time: "+(endTime-startTime)+
                            "ms LCQ result's size: "+Rl.size()+" TCQ result's size: "+Rt.size()+
                            " Accumulated time:"+testTime+"ms");
                    break;
                case 8:
                    startTime = System.currentTimeMillis();
                    Algorithm_Time.T_LCQ_TCQ_Search_M(transitions,q1,q_theta,root,Rt,Rl,startT,endT);
                    endTime = System.currentTimeMillis();
                    testTime += endTime-startTime;
                    System.out.println("TLCQ_TCQ_M_ATtree "+(i-start+1) + " Test Time: "+(endTime-startTime)+
                            "ms LCQ result's size: "+Rl.size()+" TCQ result's size: "+Rt.size()+
                            " Accumulated time:"+testTime+"ms");
                    break;
            }
            if (i <= start+20)
                buildTime += (endTime-startTime);
            if (i > start+20)
                queryTime += (endTime-startTime);

        }

        System.out.println("Test Time: "+testTime);
        System.out.println("Build Time: "+buildTime+" Query Time: "+queryTime);
        System.out.println("End Test");
        String dataSetName = dataSetPath.replace("DataSet/", "").replace(".csv", "");
        System.out.println("dataSet: "+dataSetName + " theta: "+ q_theta+ " crackBound: "+crackBound+" algo_index: "+algo_index +" lastTime: "+lastTime+" totalTime: "+testTime+"ms buildTime: "+buildTime+ "ms queryTime "+queryTime+"ms" );
    }

    public static void TestATtree(int start, int query_num, Transition[] transitions,int algo_index,
                           double q_theta, TreeNode root) {
        Set<Transition> Rt = new HashSet<>();
        Set<Transition> Rl = new HashSet<>();
        Set<Transition> transitionSet = new HashSet<>(Arrays.asList(transitions));
        long time = 0;
        long buildTime = 0;
        for (int i = start; i < start+query_num; i++) {
            Rt.clear();
            Rl.clear();
            Transition qq = transitions[i];
            long startTime, endTime;
            if (algo_index == 0) {
                System.out.println("LCQ_TCQ_linear_scan");
                startTime = System.currentTimeMillis();
                Operations.LCQ_TCQ(transitionSet, qq, q_theta, Rl, Rt);
                endTime = System.currentTimeMillis();
            }
            else if (algo_index == 1) {
                System.out.println("LCQ_linear_scan");
                startTime = System.currentTimeMillis();
                Rl = Operations.LCQ(transitionSet, qq, q_theta);
                endTime = System.currentTimeMillis();
            }
            else if (algo_index == 2) {
                System.out.println("TCQ_linear_scan");
                startTime = System.currentTimeMillis();
                Rt = Operations.TCQ(transitionSet, qq, q_theta);
                endTime = System.currentTimeMillis();

            }
            else if (algo_index == 3) {
                System.out.println("LCQ_TCQ");
                startTime = System.currentTimeMillis();
                Algorithm.LCQ_TCQ_Search(transitions, qq, q_theta,root, Rt,Rl);
                endTime = System.currentTimeMillis();
            }
            else if (algo_index == 4) {
                System.out.println("LCQ");
                startTime = System.currentTimeMillis();
                Algorithm.LCQ_Search(transitions, qq, q_theta,root, Rl);
                endTime = System.currentTimeMillis();
            }
            else if (algo_index == 5) {
                System.out.println("TCQ");
                startTime = System.currentTimeMillis();
                Algorithm.TCQ_Search(transitions, qq, q_theta,root, Rt);
                endTime = System.currentTimeMillis();

            }
            else if (algo_index == 6) {
                System.out.println("mediocre_LCQ");
                startTime = System.currentTimeMillis();
                Algorithm.Mediocre_LCQ_Search(transitions, qq, q_theta,root, Rl);
                endTime = System.currentTimeMillis();
            }
            else if (algo_index == 7) {
                System.out.println("mediocre_TCQ");
                startTime = System.currentTimeMillis();
                Algorithm.Mediocre_TCQ_Search(transitions, qq, q_theta,root, Rt);
                endTime = System.currentTimeMillis();
            }
            else{
                System.out.println("mediocre_LCQ_TCQ");
                startTime = System.currentTimeMillis();
                Algorithm.LCQ_TCQ_Search_M(transitions, qq, q_theta,root, Rt,Rl);
                endTime = System.currentTimeMillis();

            }
            if (i < start+20)
                buildTime += (endTime-startTime);

            if (i >= start+20)
                time += (endTime - startTime);
            System.out.println("query "+ (i-start+1) +" running time: " + (endTime - startTime) + "ms");

        }
        String dataSetName = dataSetPath.replace("DataSet/", "").replace(".csv", "");
        System.out.println("dataSet: "+dataSetName + " theta: "+ q_theta+ " crackBound: "+crackBound+" algo_index: "+algo_index +" buildTime"+buildTime+ " time "+time );

    }


}