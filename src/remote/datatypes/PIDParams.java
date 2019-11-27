package remote.datatypes;

import helpers.DataConversionHelper;

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
        // TODO Add parameter checking 
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

    public byte[] toBytes() {
        byte[] kpBytes = DataConversionHelper.doubleToByteArray(kp);
        byte[] kiBytes = DataConversionHelper.doubleToByteArray(ki);
        byte[] kdBytes = DataConversionHelper.doubleToByteArray(kd);
        byte[] alphaBytes = DataConversionHelper.doubleToByteArray(alpha);
        byte[] betaBytes = DataConversionHelper.doubleToByteArray(beta);

        byte[] angleBytes = DataConversionHelper.doubleToByteArray(angleThreshold);
        byte[] speedBytes = DataConversionHelper.doubleToByteArray(speedThreshold);
        byte[] minBytes = DataConversionHelper.doubleToByteArray(minValue);
        byte[] slopeBytes = DataConversionHelper.doubleToByteArray(slope);

        byte[] bytes = new byte[8 * 9];

        System.arraycopy(kpBytes, 0, bytes, 0, 8);
        System.arraycopy(kiBytes, 0, bytes, 8, 8);
        System.arraycopy(kdBytes, 0, bytes, 16, 8);
        System.arraycopy(alphaBytes, 0, bytes, 24, 8);
        System.arraycopy(betaBytes, 0, bytes, 32, 8);

        System.arraycopy(angleBytes, 0, bytes, 40, 8);
        System.arraycopy(speedBytes, 0, bytes, 48, 8);
        System.arraycopy(minBytes, 0, bytes, 56, 8);
        System.arraycopy(slopeBytes, 0, bytes, 64, 8);

        return bytes;
    }
}
