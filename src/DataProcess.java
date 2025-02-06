import objects.Point;
import objects.Transition;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Arrays;

public class DataProcess {
    public String filePath;

    public DataProcess(String filePath) {
        this.filePath = filePath;
    }
    public Transition[] readData() throws FileNotFoundException {
        List<Transition> transitions = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy H:mm");
        int invalidLine = 0;
        int validLine = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // 读取第一行标题行
            if (line == null) {
                return null; // 如果文件为空，则直接返回
            }

            // 解析标题行获取列索引
            String[] headers = line.split(",");
            int pickupLatIndex = findColumnIndex(headers, "Pickup Centroid Latitude");
            int pickupLonIndex = findColumnIndex(headers, "Pickup Centroid Longitude");
            int dropoffLatIndex = findColumnIndex(headers, "Dropoff Centroid Latitude");
            int dropoffLonIndex = findColumnIndex(headers, "Dropoff Centroid Longitude");
            int startedAtIndex = findColumnIndex(headers, "started_at");
            int endedAtIndex = findColumnIndex(headers, "ended_at");
//            int tripIndex = findColumnIndex(headers,"Trip ID");
            int tripId = 1;
            while ((line = br.readLine()) != null) {
                try {
                    String[] data = line.split(",");
                    if (data.length > Math.max(Math.max(pickupLatIndex, pickupLonIndex), Math.max(dropoffLatIndex, dropoffLonIndex))) {
                        double pickupLat = Double.parseDouble(data[pickupLatIndex].trim());
                        double pickupLon = Double.parseDouble(data[pickupLonIndex].trim());
                        double dropoffLat = Double.parseDouble(data[dropoffLatIndex].trim());
                        double dropoffLon = Double.parseDouble(data[dropoffLonIndex].trim());
                        Date startedAt = dateFormat.parse(data[startedAtIndex].trim());
                        Date endedAt = dateFormat.parse(data[endedAtIndex].trim());
                        Point start = new Point(pickupLat, pickupLon);
                        Point end = new Point(dropoffLat, dropoffLon);
//                        Transition transition = new Transition(start, end,tripId);
                        //*update
                        Transition transition = new Transition(start,end,tripId,startedAt,endedAt);
                        tripId++;
                        transitions.add(transition);
                        validLine++;
                    }
                } catch (NumberFormatException e) {
                    // Log the error along with the problematic line
//                    System.err.println("Skipping invalid data on line: " + line);
                    invalidLine ++;
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("The number of invalid lines is " + invalidLine);
        System.out.println("The number of valid lines is " + validLine);
        return transitions.toArray(new Transition[0]);

    }
    private static int findColumnIndex(String[] headers, String columnName) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1; // Return -1 if column name is not found
    }


}
