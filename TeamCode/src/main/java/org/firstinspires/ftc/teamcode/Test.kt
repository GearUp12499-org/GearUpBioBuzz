package org.firstinspires.ftc.teamcode.org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode

class Test: LinearOpMode(){
    override fun runOpMode() {
        waitForStart()
        while (opModeIsActive()){
            telemetry.addData(":)", "yay")
            telemetry.update()
        }
    }
}