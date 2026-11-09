package org.firstinspires.ftc.teamcode;

import static java.lang.Math.PI;
import static java.lang.Math.abs;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.drivers.GoBildaPinpoint2Driver;
import org.firstinspires.ftc.teamcode.hardware.HardwareMapper;
import org.firstinspires.ftc.teamcode.hardware.HardwareName;
import org.firstinspires.ftc.teamcode.hardware.Reversed;
import org.firstinspires.ftc.teamcode.hardware.ZeroPower;

public class GerthaHardwareMap extends HardwareMapper {

    public static final double POLLEN_UP = 0.1;
    public static final double POLLEN_ONLY = 0.49;
    public static final double POLLEN_EXTRACT = 0.54;

    @HardwareName("frontRight")
    @ZeroPower(DcMotor.ZeroPowerBehavior.BRAKE)
    public DcMotorEx frontRight;

    @HardwareName("backRight")
    @ZeroPower(DcMotor.ZeroPowerBehavior.BRAKE)
    public DcMotorEx backRight;

    @HardwareName("frontLeft")
    @Reversed
    @ZeroPower(DcMotor.ZeroPowerBehavior.BRAKE)
    public DcMotorEx frontLeft;

    @HardwareName("backLeft")
    @Reversed
    @ZeroPower(DcMotor.ZeroPowerBehavior.BRAKE)
    public DcMotorEx backLeft;

    @HardwareName("pinpoint")
    public GoBildaPinpoint2Driver pinpoint;

    @HardwareName("pFlip")
    public Servo pFlip;

    public GerthaHardwareMap(HardwareMap map) {
        super(map);

        pinpoint.setOffsets(4.78,  -5, DistanceUnit.INCH);
        pinpoint.setEncoderResolution(GoBildaPinpoint2Driver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        pinpoint.setEncoderDirections(GoBildaPinpoint2Driver.EncoderDirection.REVERSED, GoBildaPinpoint2Driver.EncoderDirection.REVERSED);
    }


    public static double normalize(double angle) {
        double tempAngle = angle % (2 * PI);
        if (tempAngle > PI) {
            tempAngle -= (2 * PI);
        } else if (tempAngle <= -PI) {
            tempAngle += (2 * PI);
        }
        return tempAngle;
    }

    public static double angleDifference(double angle1, double angle2){
        double diff = normalize(angle1) - normalize(angle2);
        diff = abs(diff);
        if (diff > PI) {
            diff = (2 * PI) - diff;
        }
        return diff;
    }
}
