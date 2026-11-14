
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

import java.util.List;

@TeleOp(name = "ApriltagIsLookingUp")
public class ApriltagIsLookingUp extends LinearOpMode {

    private static final boolean USE_WEBCAM = true;

    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    @Override
    public void runOpMode() {
        initAprilTag();
        telemetry.addLine("Camera initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            telemetryAprilTag();
            telemetry.update();
            sleep(20);}

        if (visionPortal != null) {visionPortal.close();
        }
    }

    private void initAprilTag() {
        aprilTag = AprilTagProcessor.easyCreateWithDefaults();

        if (USE_WEBCAM) {
            visionPortal = VisionPortal.easyCreateWithDefaults(
                    hardwareMap.get(WebcamName.class, "Webcam 1"),
                    aprilTag
            );
        } else {
            visionPortal = VisionPortal.easyCreateWithDefaults(
                    BuiltinCameraDirection.BACK, aprilTag);
        }
    }


    private void telemetryAprilTag() {
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        boolean clusterFound = false;

        for (AprilTagDetection detection : currentDetections) {
            if (detection instanceof AprilTagClusterDetection && detection.ftcPose != null) {
                clusterFound = true;

                AprilTagClusterDetection clusterDet = (AprilTagClusterDetection) detection;

                telemetry.addLine("\n==== Tag Cluster");
                telemetry.addData("Percent tags found",
                        clusterDet.percentClusterFound);

                telemetry.addLine(String.format("XYZ %.1f %.1f %.1f", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));

                double roll = detection.ftcPose.roll;
                double pitch = detection.ftcPose.pitch;
                double yaw = detection.ftcPose.yaw;

                String clusterName = clusterDet.metadata.name;

                telemetry.addData("Cluster Name", clusterName);
                telemetry.addData("Roll", roll);
                telemetry.addData("Pitch", pitch);
                telemetry.addData("Yaw", yaw);

                boolean isAudienceCluster = clusterName.toLowerCase().contains("audience");

                boolean isScoringCluster = clusterName.toLowerCase().contains("scoring");

                boolean inverted = Math.abs(roll) > 90;
                double correctedPitch = inverted ? -pitch : pitch;

                telemetry.addData("Inverted", inverted);
                telemetry.addData("Corrected Pitch", correctedPitch);

                if (isAudienceCluster) {
                    if (correctedPitch < 0) {
                        telemetry.addLine("audience side up");
                    } else {
                        telemetry.addLine("scoring side up");}
                } else if (isScoringCluster) {
                    if (pitch > 0) {
                        telemetry.addLine("arudience side up");
                    } else {
                        telemetry.addLine("scoring side up");}
                } else {
                    telemetry.addLine("Unknown cluster");}
            }
        }

        if (!clusterFound) {
            for (AprilTagDetection detection : currentDetections) { if (detection instanceof AprilTagSingleDetection) {
                    AprilTagSingleDetection singleDet = (AprilTagSingleDetection) detection;

                    if (singleDet.ftcPose != null) {
                        telemetry.addLine(String.format("\n==== Tag ID %d", singleDet.id));

                        double roll = singleDet.ftcPose.roll;
                        telemetry.addData("Roll", roll);

                        if (Math.abs(roll) < 90) {
                            telemetry.addLine("audience side up");
                        } else {
                            telemetry.addLine("scoring side up");}
                    }
                }
            }
        }

        if (currentDetections.isEmpty()) {
            telemetry.addLine("No AprilTags detected");}
    }
}
