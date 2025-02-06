package objects;

import java.util.AbstractMap.SimpleEntry;
import java.util.Date;

public class Transition {
    private Point start; // 起点
    private Point end;  // 终点
    private Date startTime;
    private Date endTime;

    private int id;
    public Transition(Point start, Point end, int id) {
        this.start = start;
        this.end = end;
        this.id = id;
    }
    public Transition(Point start, Point end, int id, Date startTime, Date endTime) {
        this.start = start;
        this.end = end;
        this.startTime = startTime;
        this.endTime = endTime;
        this.id = id;
    }
    public Point getStart() {
        return start;
    }
    public void setStart(Point start) {
        this.start = start;
    }
    public int getId() {
        return id;
    }
    public Point getEnd() {
        return end;
    }
    public void setEnd(Point end) {
        this.end = end;
    }

    public Date getStartTime() {
        return startTime;
    }
    public Date getEndTime() {
        return  endTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    /**
     * 计算当前 Transition 与另一个 Transition 之间的距离对
     * @param other 另一个 Transition 对象
     * @return SimpleEntry 包含两个 DistancePair
     */
    public SimpleEntry<DistancePair, DistancePair> distanceTo(Transition other) {
        Point start1 = this.start;
        Point end1 = this.end;
        Point start2 = other.start;
        Point end2 = other.end;
        double dis1 = start1.distanceTo(start2);
        double dis2 = end1.distanceTo(end2);
        double dis3 = start1.distanceTo(end2);
        double dis4 = end1.distanceTo(start2);
        DistancePair DP1 = new DistancePair(dis1,dis2);
        DistancePair DP2 = new DistancePair(dis3,dis4);
        return new SimpleEntry<>(DP1,DP2);
    }
    public static SimpleEntry<DistancePair, DistancePair> distanceBetween(Transition t1, Transition t2) {
        return t1.distanceTo(t2);
    }
}
