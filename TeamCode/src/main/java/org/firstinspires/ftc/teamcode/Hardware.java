package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.teamcode.hardware.HardwareMapper;
import org.firstinspires.ftc.teamcode.hardware.HardwareName;
import org.firstinspires.ftc.teamcode.hardware.Reversed;

public class Hardware extends HardwareMapper {

    @HardwareName("shooter")
    @Reversed
    public DcMotorEx shooter;

    public void setShooterVel(double vel) {
        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooter.setVelocity(vel);
    }
    public Hardware(HardwareMap map) {
        super(map);

        shooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(360, 40, 20, 0));
    }
}
