package org.firstinspires.ftc.teamcode.org.firstinspires.ftc.teamcode

import android.util.Log
import android.util.Size
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.DetectPollen
import org.firstinspires.ftc.teamcode.HoughCircles
import org.firstinspires.ftc.teamcode.SummerBotHardware
import org.firstinspires.ftc.vision.VisionPortal
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvPipeline

@TeleOp
class TestPipeline: LinearOpMode() {

    lateinit var hardware: SummerBotHardware
    var prevTime = System.nanoTime()
    lateinit var pollen: DetectPollen

    override fun runOpMode() {

        hardware = SummerBotHardware(hardwareMap)

        pollen = DetectPollen()

        val visionPortalBuilder = VisionPortal.Builder()

        visionPortalBuilder.setCamera(hardware.gsc)
            .setCameraResolution(Size(640, 480))
            .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
            .enableLiveView(true)
            .setAutoStopLiveView(true)
            .addProcessor(pollen)

        val visionPortal = visionPortalBuilder.build()

        waitForStart()

        visionPortal.stopLiveView()
        visionPortal.resumeStreaming()

        while (opModeIsActive()){

            val time = System.nanoTime()
            Log.i("loopTime", ((time - prevTime)/1e9).toString())

            prevTime = time



        }
    }
}