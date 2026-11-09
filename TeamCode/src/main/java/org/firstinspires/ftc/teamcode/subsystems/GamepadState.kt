package org.firstinspires.ftc.teamcode.org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.Gamepad

class GamepadState(private val gp1: Gamepad, private val gp2: Gamepad) {

    var jx1 = 0.0
    var jy1 = 0.0
    var rx1 = 0.0

    fun update() {
        jx1 = gp1.left_stick_x.toDouble()
        jy1 = -gp1.left_stick_y.toDouble()
        rx1 = gp1.right_stick_x.toDouble()

    }
}