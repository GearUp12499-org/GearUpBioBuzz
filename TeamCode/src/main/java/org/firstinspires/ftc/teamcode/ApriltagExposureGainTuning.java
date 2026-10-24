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

@TeleOp(name = "ApriltagExposureGainTuning", group = "Test")
public class ApriltagExposureGainTuning extends LinearOpMode {
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    private int TEST_EXPOSURE = 10;
    private int TEST_GAIN = 20;

    private boolean previousY = false;
    private boolean previousA = false;
    private boolean previousB = false;
    private boolean previousX = false;
    private boolean previousDown = false;
    private boolean previousUp = false;

    @Override
    public void runOpMode() {
        initAprilTag();

        try {
            telemetry.addLine("Waiting for camera");
            telemetry.update();

            while (!isStopRequested()
                    && visionPortal.getCameraState()
                    != VisionPortal.CameraState.STREAMING) {
                if (visionPortal.getCameraState()
                        == VisionPortal.CameraState.ERROR) {
                    telemetry.addLine("Camera error");
                    telemetry.update();
                    return;
                }

                sleep(20);
            }

            if (isStopRequested()) {
                return;
            }

            ExposureControl exposureControl =
                    visionPortal.getCameraControl(ExposureControl.class);

            boolean exposureReady = false;
            long minExposure = 1;
            long maxExposure = 1;

            if (exposureControl != null
                    && exposureControl.isExposureSupported()
                    && exposureControl.isModeSupported(ExposureControl.Mode.Manual)) {
                exposureReady =
                        exposureControl.setMode(ExposureControl.Mode.Manual);

                if (exposureReady) {
                    sleep(50);

                    minExposure = Math.max(
                            1,
                            (long) Math.ceil(
                                    exposureControl.getMinExposure(TimeUnit.MICROSECONDS)
                                            / 1000.0
                            )
                    );

                    maxExposure =
                            exposureControl.getMaxExposure(TimeUnit.MILLISECONDS);

                    exposureReady = maxExposure >= minExposure;

                    if (exposureReady) {
                        TEST_EXPOSURE = (int) Math.max(
                                minExposure,
                                Math.min(maxExposure, TEST_EXPOSURE)
                        );

                        if (!exposureControl.setExposure(
                                TEST_EXPOSURE,
                                TimeUnit.MILLISECONDS
                        )) {
                            telemetry.addLine("Camera rejected initial exposure");
                        }
                    }
                }
            }

            GainControl gainControl =
                    visionPortal.getCameraControl(GainControl.class);

            int minGain = 0;
            int maxGain = 0;
            boolean gainReady = false;

            if (gainControl != null) {
                minGain = gainControl.getMinGain();
                maxGain = gainControl.getMaxGain();
                gainReady = minGain >= 0 && maxGain >= minGain;

                if (gainReady) {
                    TEST_GAIN = Math.max(
                            minGain,
                            Math.min(maxGain, TEST_GAIN)
                    );

                    if (!gainControl.setGain(TEST_GAIN)) {
                        telemetry.addLine("Camera rejected initial gain");
                    }
                }
            }

            telemetry.addLine("Camera ready");
            telemetry.addData("Exposure control available", exposureReady);
            telemetry.addData("Gain control available", gainReady);
            telemetry.addLine("Y: exposure +1 ms | A: exposure -1 ms");
            telemetry.addLine("B: gain +1 | X: gain -1");
            telemetry.update();

            waitForStart();

            while (opModeIsActive()) {
                boolean yPressed = gamepad1.y;
                boolean aPressed = gamepad1.a;
                boolean bPressed = gamepad1.b;
                boolean xPressed = gamepad1.x;
                boolean downPressed = gamepad1.dpad_down;
                boolean upPressed = gamepad1.dpad_up;

                boolean streaming = visionPortal.getCameraState()
                        == VisionPortal.CameraState.STREAMING;

                if (exposureReady && streaming) {
                    int exposureChange = 0;

                    if (yPressed && !previousY) {
                        exposureChange++;
                    }

                    if (aPressed && !previousA) {
                        exposureChange--;
                    }

                    if (exposureChange != 0) {
                        long requestedExposure = Math.max(
                                minExposure,
                                Math.min(maxExposure, TEST_EXPOSURE + exposureChange)
                        );

                        if (exposureControl.setExposure(
                                requestedExposure,
                                TimeUnit.MILLISECONDS
                        )) {
                            TEST_EXPOSURE = (int) requestedExposure;
                        } else {
                            telemetry.addLine("Camera rejected exposure change");
                        }
                    }

                    telemetry.addData(
                            "Actual exposure",
                            "%d ms",
                            exposureControl.getExposure(TimeUnit.MILLISECONDS)
                    );
                }

                if (gainReady && streaming) {
                    int gainChange = 0;

                    if (bPressed && !previousB) {
                        gainChange++;
                    }

                    if (xPressed && !previousX) {
                        gainChange--;
                    }

                    if (gainChange != 0) {
                        int requestedGain = Math.max(
                                minGain,
                                Math.min(maxGain, TEST_GAIN + gainChange)
                        );

                        if (gainControl.setGain(requestedGain)) {
                            TEST_GAIN = requestedGain;
                        } else {
                            telemetry.addLine("Camera rejected gain change");
                        }
                    }

                    telemetry.addData("Actual gain", gainControl.getGain());
                }

                previousY = yPressed;
                previousA = aPressed;
                previousB = bPressed;
                previousX = xPressed;

                telemetry.addData("Requested exposure", "%d ms", TEST_EXPOSURE);
                telemetry.addData("Requested gain", TEST_GAIN);
                telemetry.addData("Exposure control available", exposureReady);
                telemetry.addData("Gain control available", gainReady);

                List<AprilTagDetection> detections = aprilTag.getDetections();

                telemetry.addData("Total detections", detections.size());

                int singleCount = 0;
                int clusterCount = 0;
                double CameraAngle = Math.toRadians(29);

                for (AprilTagDetection detection : detections) {
                    if (detection instanceof AprilTagSingleDetection) {
                        singleCount++;

                        AprilTagSingleDetection single =
                                (AprilTagSingleDetection) detection;

                        telemetry.addLine();
                        telemetry.addLine("only one tag...");
                        telemetry.addData("ID", single.id);

                        if (single.metadata != null && single.ftcPose != null) {
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
                            telemetry.addData(
                                    "Name",
                                    single.metadata != null
                                            ? single.metadata.name
                                            : "Idk this tag"
                            );

                            telemetry.addData(
                                    "Center",
                                    "%.0f / %.0f px",
                                    single.center.x,
                                    single.center.y
                            );
                        }
                    } else if (detection instanceof AprilTagClusterDetection) {
                        clusterCount++;

                        AprilTagClusterDetection cluster =
                                (AprilTagClusterDetection) detection;

                        telemetry.addLine();
                        telemetry.addLine("CLUSTER");

                        if (cluster.metadata != null) {
                            telemetry.addData("Cluster", cluster.metadata.name);
                        }

                        telemetry.addData(
                                "Tags Found",
                                "%d%%",
                                cluster.percentClusterFound
                        );

                        if (cluster.ftcPose != null) {
                            double A = Math.atan2(
                                    cluster.ftcPose.z,
                                    cluster.ftcPose.y
                            );

                            double B = CameraAngle + A;

                            double H = Math.hypot(
                                    cluster.ftcPose.z,
                                    cluster.ftcPose.y
                            );

                            double f = Math.cos(B) * H;
                            double up = Math.sin(B) * H;

                            telemetry.addData(
                                    "A,B,H",
                                    "%8.2f %8.2f %8.2f",
                                    A, B, H
                            );

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

                            telemetry.addData(
                                    "Range",
                                    "%.1f in",
                                    cluster.ftcPose.range
                            );

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
                        } else {
                            telemetry.addLine("Cluster pose unavailable");
                        }
                    }
                }

                telemetry.addLine();
                telemetry.addData("Single Tags", singleCount);
                telemetry.addData("Clusters", clusterCount);
                telemetry.addLine("Y: exposure +1 ms | A: exposure -1 ms");
                telemetry.addLine("B: gain +1 | X: gain -1");
                telemetry.addLine("dpad down to stop camera");
                telemetry.addLine("dpad up to resume camera");
                telemetry.update();

                if (downPressed && !previousDown) {
                    visionPortal.stopStreaming();
                } else if (upPressed && !previousUp) {
                    visionPortal.resumeStreaming();
                }

                previousDown = downPressed;
                previousUp = upPressed;

                sleep(20);
            }
        } finally {
            visionPortal.close();
        }
    }

    private void initAprilTag() {
        aprilTag = new AprilTagProcessor.Builder()
                .setLensIntrinsics(358.215, 358.215, 449.09, 352.672)
                .build();

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(960, 720))
                .enableLiveView(true)
                .addProcessor(aprilTag)
                .build();
    }
}