package objects;



import java.util.Random;

public class Point {
    private double latitude;  // 纬度
    private double longitude; // 经度

    public Point(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * 计算当前点与另一个点之间的距离 (Haversine 公式，原始经纬度坐标)
     * @param other 另一个点
     * @return 距离（米）
     */
    public double distanceTo(Point other) {
        final int R = 6371000; // 地球半径，单位是米
        double lat1 = Math.toRadians(this.latitude);
        double lon1 = Math.toRadians(this.longitude);
        double lat2 = Math.toRadians(other.latitude);
        double lon2 = Math.toRadians(other.longitude);

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // 返回计算的距离
    }

    public static double distanceBetween(Point p1, Point p2) {
        return p1.distanceTo(p2);
    }

    /**
     * 计算基于经纬度的敏感度
     *
     * @param radius        覆盖半径 (米)
     * @param baseLatitude  基准纬度
     * @param baseLongitude 基准经度
     * @return 敏感度 (经纬度)
     */
    public static double calculateSensitivity(double radius, double baseLatitude, double baseLongitude) {
        final double EARTH_RADIUS = 6371000; // 地球半径，单位是米

        // 计算经纬度差值 (单位：度)
        double deltaLatitude = radius / EARTH_RADIUS * 180 / Math.PI;
        double deltaLongitude = radius / (EARTH_RADIUS * Math.cos(Math.toRadians(baseLatitude))) * 180 / Math.PI;

        // 返回较大的差值作为敏感度
        return Math.max(deltaLatitude, deltaLongitude);
    }

    /**
     * 添加差分隐私扰动 (经纬度数值扰动)
     *
     * @param delta      敏感度，传入计算好的敏感度
     * @param epsilon    隐私预算
     * @param noiseType  噪声类型，可选 "laplace" 或 "gaussian"
     * @return 添加扰动后的新 Point 对象
     */
    public Point perturbWithSensitivity(double delta, double epsilon, double baseLat, double baseLon, String noiseType) {
        double perturbedLatitude = this.latitude;
        double perturbedLongitude = this.longitude;

        // 应用拉普拉斯机制，单独对latitude和longitude扰动

        double sensitivityLatitude = calculateSensitivity(delta,baseLat,baseLon);
        double sensitivityLongitude = calculateSensitivity(delta,baseLat,baseLon);


        if (noiseType == null || noiseType.equalsIgnoreCase("laplace")) {
            // 拉普拉斯噪声，将隐私预算分配给经纬度
            perturbedLatitude += laplaceNoise(sensitivityLatitude, epsilon/2);
            perturbedLongitude += laplaceNoise(sensitivityLongitude, epsilon/2);
        } else if (noiseType.equalsIgnoreCase("gaussian")) {
            // 高斯噪声
            perturbedLatitude += gaussianNoise(sensitivityLatitude, epsilon/2);
            perturbedLongitude += gaussianNoise(sensitivityLongitude, epsilon/2);
        } else {
            throw new IllegalArgumentException("Invalid noise type. Must be 'laplace' or 'gaussian'.");
        }

        return new Point(perturbedLatitude, perturbedLongitude);
    }

    // 拉普拉斯噪声生成器
    private double laplaceNoise(double sensitivity, double epsilon) {
        double b = sensitivity / epsilon;
        Random random = new Random();
        double u = random.nextDouble() - 0.5; // 生成 -0.5 到 0.5 之间的随机数
        return -b * Math.signum(u) * Math.log(1 - 2 * Math.abs(u)); // 拉普拉斯分布的逆变换抽样
    }

    // 高斯噪声生成器
    private double gaussianNoise(double sensitivity, double epsilon) {
        double sigma = sensitivity / epsilon;
        Random random = new Random();
        return random.nextGaussian() * sigma; // 使用 nextGaussian() 方法直接生成高斯分布的随机数
    }

    public static void main(String[] args) {
        Point p1 = new Point(41.94454, -87.654678);
        double radius = 1400; // 覆盖半径，单位米
        double baseLatitude = 41.90; // 基准纬度
        double baseLongitude = -87.65; // 基准经度
        double epsilon = 10;



// 添加扰动
        Point perturbedP1 = p1.perturbWithSensitivity(radius, epsilon, baseLatitude, baseLongitude,"laplace");

        System.out.println("原始经纬度: " + p1.getLatitude() + ", " + p1.getLongitude());
        System.out.println("扰动后的经纬度: " + perturbedP1.getLatitude() + ", " + perturbedP1.getLongitude());
    }


}