package remote.datatypes;

public class PIDParams {
    public double kp;
    public double ki;
    public double kd;
    public double alpha;
    public double beta;

    public double angleThreshold;
    public double speedThreshold;
    public double minValue;
    public double slope;

    public PIDParams(double kp, double ki, double kd, double alpha, double beta, double angleThreshold, double speedThreshold, double minValue, double slope) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
        this.alpha = alpha;
        this.beta = beta;
        this.angleThreshold = angleThreshold;
        this.speedThreshold = speedThreshold;
        this.minValue = minValue;
        this.slope = slope;
    }
}
