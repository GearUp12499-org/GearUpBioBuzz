package org.firstinspires.ftc.teamcode;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

import java.util.List;
import java.util.concurrent.TimeUnit;

@TeleOp(name = "AprilTag Cluster Test", group = "Test")
public class SimpleApriltagClusterTest extends LinearOpMode {
    //this is 90% the ConceptAprilTag sample code
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;
    private int TEST_EXPOSURE = 10;   // Change this to test exposure speeds
    private int TEST_GAIN = 20;       // Change this to test image brightness

    @Override
    public void runOpMode() {

        initAprilTag();

        telemetry.addLine("Camera initialized");
        telemetry.update();
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
//list all the apriltags it detects
            List<AprilTagDetection> detections = aprilTag.getDetections();

            telemetry.addData("Total detections", detections.size());

            int singleCount = 0;
            int clusterCount = 0;
            double f = 0;
            double CameraAngle = 1.203;
            double up = 0;
            double B = 0;
            double H = 0;
            double A = 0;

            for (AprilTagDetection detection : detections) {
                //is it only one tag or what
                if (detection instanceof AprilTagSingleDetection) {

                    singleCount++;

                    AprilTagSingleDetection single =
                            (AprilTagSingleDetection) detection;

                    telemetry.addLine();
                    telemetry.addLine("only one tag...");
                    telemetry.addData("ID", single.id);

                    if (single.metadata != null) {
                        telemetry.addData("Name", single.metadata.name);

                        telemetry.addData(
                                "Position (X,Y,Z)",
                                "%.1f , %.1f , %.1f in",
                                single.ftcPose.x,
                                single.ftcPose.y,
                                single.ftcPose.z
                        );

                        telemetry.addData(
                                "Range",
                                "%.1f in",
                                single.ftcPose.range
                        );

                        telemetry.addData(
                                "Bearing",
                                "%.1f deg",
                                single.ftcPose.bearing
                        );

                        telemetry.addData(
                                "Yaw",
                                "%.1f deg",
                                single.ftcPose.yaw
                        );

                    } else {

                        telemetry.addData("Name", "Idk this tag");

                        telemetry.addData(
                                "Center",
                                "%.0f / %.0f px",
                                single.center.x,
                                single.center.y
                        );
                    }
                    // is it a cluster
                    //the opmode sample reports percentClusterFound
                } else if (detection instanceof AprilTagClusterDetection) {

                    clusterCount++;

                    AprilTagClusterDetection cluster =
                            (AprilTagClusterDetection) detection;

                    A = Math.atan2(cluster.ftcPose.z, cluster.ftcPose.y);
                    B = CameraAngle - A;
                    H = Math.hypot(cluster.ftcPose.z, cluster.ftcPose.y);

                    f = Math.cos(B)  *  H;
                    up = Math.tan(B)  *  f;

                    telemetry.addData(
                            "A,B,H",
                            "%8.2f %8.2f %8.2f",
                            A, B, H
                    );





                    telemetry.addLine();
                    telemetry.addLine("CLUSTER");
                    //metadata is the SDKs stored info about the cluster
                    // so it checks if the SDK knowd anything about this cluster first
                    //if metadata is null the opmode will crash :,( so we put a name
                    if (cluster.metadata != null) {
                        telemetry.addData(
                                "Cluster",
                                cluster.metadata.name
                        );
                    }
                    //how much of the cluster did we find
                    //part of the sdk
                    telemetry.addData(
                            "Tags Found",
                            "%d%%",
                            cluster.percentClusterFound
                    );
                    //3d cluster position
                    telemetry.addData(
                            "Position (X,Y,Z)",
                            "%.1f , %.1f , %.1f in",
                            cluster.ftcPose.x,
                            cluster.ftcPose.y,
                            cluster.ftcPose.z
                    );

                    telemetry.addData(
                         "Forward Distance",
                         "%.1f in",
                            f
                    );

                    telemetry.addData(
                            "Upward Distance",
                            "%.1f in",
                            up
                    );

                    //distance from camera to cluster
                    telemetry.addData(
                            "Range",
                            "%.1f in",
                            cluster.ftcPose.range
                    );
                    //bearing angle
                    telemetry.addData(
                            "Bearing",
                            "%.1f deg",
                            cluster.ftcPose.bearing
                    );

                    telemetry.addData(
                            "Pitch",
                            "%.1f deg",
                            cluster.ftcPose.pitch
                    );

                    telemetry.addData(
                            "Yaw",
                            "%.1f deg",
                            cluster.ftcPose.yaw
                    );

                    telemetry.addData(
                            "Roll",
                            "%.1f deg",
                            cluster.ftcPose.roll
                    );
                }
            }

            telemetry.addLine();
            telemetry.addData("Single Tags", singleCount);
            telemetry.addData("Clusters", clusterCount);


            telemetry.addLine();
            telemetry.addLine("dpad down tp stop camera");
            telemetry.addLine("dpad up to resume camera");

            telemetry.update();
            //ig this is here so we can save rescources if necessary
            if (gamepad1.dpad_down) {
                visionPortal.stopStreaming();
            }

            if (gamepad1.dpad_up) {
                visionPortal.resumeStreaming();
            }

            sleep(20);
        }

        visionPortal.close();
    }
    //set up the cameras
    private void initAprilTag() {
        //create the april tag processor
        aprilTag = new AprilTagProcessor.Builder()
                .setLensIntrinsics(358.215, 358.215, 449.09, 352.672)
                .build();
        //start camera
        //idk if there is a hardware thing already so it is just here
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(960, 720))
                .enableLiveView(true)
                .addProcessor(aprilTag)
                .build();
    }
}