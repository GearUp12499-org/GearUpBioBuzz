package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.vision.VisionPortal;
import java.util.concurrent.TimeUnit;

@TeleOp
public class SimpleExposureandGainTest extends LinearOpMode {


    private int TEST_EXPOSURE = 10;   // Change this to test exposure speeds
    private int TEST_GAIN = 15;       // Change this to test image brightness

    private VisionPortal visionPortal;

    @Override
    public void runOpMode() {

        //  Set up the camera
        visionPortal = VisionPortal.easyCreateWithDefaults(
                hardwareMap.get(WebcamName.class, "Webcam 1")
        );

        while (!isStopRequested() && visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            sleep(20);
        }

//makes the camera use our set values
        ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
        if (exposureControl != null) {
            exposureControl.setMode(ExposureControl.Mode.Manual);
            exposureControl.setExposure(TEST_EXPOSURE, TimeUnit.MILLISECONDS);
        }

        GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
        if (gainControl != null) {
            gainControl.setGain(TEST_GAIN);
        }

        //let us know when it is ready
        telemetry.addData("", "Values applied");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData(" Exposure", TEST_EXPOSURE);
            telemetry.addData(" Gain", TEST_GAIN);
            telemetry.update();
            sleep(100);
        }
    }
}