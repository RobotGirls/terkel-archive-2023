/*
 * Copyright (c) September 2017 FTC Teams 25/5218
 *
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted (subject to the limitations in the disclaimer below) provided that
 *  the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this list
 *  of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice, this
 *  list of conditions and the following disclaimer in the documentation and/or
 *  other materials provided with the distribution.
 *
 *  Neither the name of FTC Teams 25/5218 nor the names of their contributors may be used to
 *  endorse or promote products derived from this software without specific prior
 *  written permission.
 *
 *  NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 *  LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 *  TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package team25core;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public class FourWheelDirectIMUDrivetrain extends DrivetrainBaseImpl implements DrivetrainWithIMU {

    DcMotor rearLeft;
    DcMotor rearRight;
    DcMotor frontLeft;
    DcMotor frontRight;

    double multiplier;
    boolean doStrafeReverse = false;

    Telemetry.Item targetHeadingTlm;
    Telemetry.Item currentYawTlm;
    Telemetry.Item yawErrorTlm;

    // FIXME proportional_constant is 0.8 on Mayfield's carpeting
    //  try 0.9 or 0.7 to see what is better
    // took out final and made it public so that we can see the constant in the FTC dashboard
    // started at 0.88 (1/20)
    public static double PROPORTIONAL_CONSTANT = 0.5; // try a little larger but lower than 0.90
    // FIXME Temporarily set the derivative constant to 0
    //  (I put 0.0001 initially) but first make sure
    //  proportional constant so that it makes sure it is strafing
    //  straight
    public static double DERIVATIVE_CONSTANT = 0;

    // this the value for the desired yaw or heading
    // if you want to go straight this value is zero
    private double targetYaw;

    private double currentYaw;

    private double currentYawRate;





    public FourWheelDirectIMUDrivetrain(DcMotor frontRight, DcMotor rearRight, DcMotor frontLeft, DcMotor rearLeft)
    {
        super();

        this.rearLeft = rearLeft;
        this.rearRight = rearRight;
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;

        this.encoderTarget = 0;
        this.multiplier = 1.0;

        setCanonicalMotorDirection();

        /**
         * Set a default master.  This is the wheel/motor that will be used to track distance
         * travelled when following a dead reckon path.
         */
        setMasterMotor(rearRight);
    }
    public FourWheelDirectIMUDrivetrain(DcMotor frontRight, DcMotor rearRight,
                                        DcMotor frontLeft, DcMotor rearLeft,
                                        Telemetry.Item targetYawTlm, Telemetry.Item yawErrorTlm,
                                        Telemetry.Item currentYawTlm)
    {
        super();

        this.rearLeft = rearLeft;
        this.rearRight = rearRight;
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;

        this.encoderTarget = 0;
        this.multiplier = 1.0;

        this.targetHeadingTlm = targetYawTlm;
        this.yawErrorTlm = yawErrorTlm;
        this.currentYawTlm = currentYawTlm;


        setCanonicalMotorDirection();

        /**
         * Set a default master.  This is the wheel/motor that will be used to track distance
         * travelled when following a dead reckon path.
         */
        setMasterMotor(rearRight);
    }

    public void setCanonicalMotorDirection()
    {
        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        rearLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        rearRight.setDirection(DcMotor.Direction.REVERSE);
    }

    public void setNoncanonicalMotorDirection()
    {
        // This reverses the direction of the drivetrain.
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        rearLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        rearRight.setDirection(DcMotor.Direction.FORWARD);
    }

    public void setSplitPersonalityMotorDirection(boolean isTeleop)
    {
        if (isTeleop == true) {
            frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
            frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
            rearLeft.setDirection(DcMotorSimple.Direction.FORWARD);
            rearRight.setDirection(DcMotorSimple.Direction.REVERSE);
        } else {
            frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
            frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
            rearLeft.setDirection(DcMotorSimple.Direction.REVERSE);
            rearRight.setDirection(DcMotorSimple.Direction.FORWARD);
        }

    }

    @Override
    public void resetEncoders()
    {
        rearLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rearRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    @Override
    public void encodersOn()
    {
        rearLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rearRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void logEncoderCounts()
    {
        RobotLog.i("POS Counts FL %d, FR %d, RL %d, RR %d", frontLeft.getCurrentPosition(), frontRight.getCurrentPosition(), rearLeft.getCurrentPosition(), rearRight.getCurrentPosition());
    }

    @Override
    public void straight(double speed)
    {
        frontRight.setPower(speed);
        frontLeft.setPower(speed);
        rearRight.setPower(speed);
        rearLeft.setPower(speed);
    }
    @Override
    public void strafe(double speed)
    {
        double yawError;
        double yawCorrection;

        yawError = targetYaw - currentYaw;
        // FIXME We need to constrain this value so it does not flip over
        // if it gets too big or too small.
        yawCorrection = yawError * PROPORTIONAL_CONSTANT
                + currentYawRate * DERIVATIVE_CONSTANT;

        frontRight.setPower(-speed - yawCorrection);
        rearRight.setPower(speed - yawCorrection);
        frontLeft.setPower(speed + yawCorrection);
        rearLeft.setPower(-speed + yawCorrection);
    }
    @Override
    public void turnWithIMU(double speed)
    {
        double yawError;
        double yawCorrection;

        yawError = targetYaw - currentYaw;
        // FIXME We need to constrain this value so it does not flip over
        // if it gets too big or too small.
        yawCorrection = yawError * PROPORTIONAL_CONSTANT
                + currentYawRate * DERIVATIVE_CONSTANT;

        currentYawTlm.setValue(currentYaw);
        yawErrorTlm.setValue(yawError);


        // Turn around center
        frontRight.setPower(-speed - yawCorrection);
        rearRight.setPower(-speed - yawCorrection);
        frontLeft.setPower(speed + yawCorrection);
        rearLeft.setPower(speed + yawCorrection);
    }


    // this method is used to set a value for the target yaw used for the
    // yaw correction

    // don't need the @Override because there is no setTarget in the parent
    // class which is DrivetrainWithIMU
    public void setTargetYaw(double desiredTargetYaw)
    {
       targetYaw = desiredTargetYaw;
       targetHeadingTlm.setValue(targetYaw);
    }

    @Override
    public void setCurrentYaw(double currentYawFromIMU)
    {
       currentYaw = currentYawFromIMU;
    }

    public void setStrafeReverse(boolean strafeReverse)
    {
        doStrafeReverse = strafeReverse;
    }

    @Override
    public void setCurrentYawRate(double currentYawRateFromIMU)
    {
        currentYawRate = currentYawRateFromIMU;
    }

    @Override
    public void leftDiagonal(double speed)
    {
        // Not tested
        frontRight.setPower(speed);
        rearRight.setPower(0);
        frontLeft.setPower(0);
        rearLeft.setPower(speed);
    }

    @Override
    public void rightDiagonal(double speed)
    {
        // Not tested
        frontRight.setPower(0);
        rearRight.setPower(speed);
        frontLeft.setPower(speed);
        rearLeft.setPower(0);
    }

    @Override
    public void turn(double speed)
    {
        // Turn around center 
        frontRight.setPower(-speed);
        rearRight.setPower(-speed);
        frontLeft.setPower(speed);
        rearLeft.setPower(speed);
    }

    @Override
    public void pivotTurn(PivotSide side, double speed)
    {
        switch (side) {
            case RIGHT_OVER_RIGHT:
                frontRight.setPower((1 / multiplier) * -speed);
                rearRight.setPower((1 / multiplier) * -speed);
                frontLeft.setPower(speed);
                rearLeft.setPower(speed);
                break;
            case RIGHT_OVER_LEFT:
                frontLeft.setPower( (1 / multiplier) * speed);
                rearLeft.setPower((1 / multiplier) * speed);
                frontRight.setPower(-speed);
                rearRight.setPower(-speed);
                break;
            case LEFT_OVER_RIGHT:
                frontRight.setPower((1 / multiplier) * speed);
                rearRight.setPower((1 / multiplier) * speed);
                frontLeft.setPower(-speed);
                rearLeft.setPower(-speed);
                break;
            case LEFT_OVER_LEFT:
                frontLeft.setPower((1 / multiplier) * -speed);
                rearLeft.setPower((1 / multiplier) * -speed);
                frontRight.setPower(speed);
                rearRight.setPower(speed);
                break;
        }
    }

    @Override
    public void setPivotMultiplier(double pivotMultiplier)
    {

    }

    @Override
    public void setPowerLeft(double speed)
    {
        frontLeft.setPower(speed);
        rearLeft.setPower(speed);
    }

    @Override
    public void setPowerRight(double speed)
    {
        frontRight.setPower(speed);
        rearRight.setPower(speed);
    }

    @Override
    public void stop()
    {
        frontLeft.setPower(0.0);
        frontRight.setPower(0.0);
        rearLeft.setPower(0.0);
        rearRight.setPower(0.0);
    }

    @Override
    public void move(double axial, double lateral, double yaw)
    {
        // calculate required motor speeds to achieve axis motions
        double backLeft;
        double backRight;
        double left;
        double right;

        backLeft = axial - lateral + yaw;
        backRight = axial + lateral - yaw;
        left = axial + lateral + yaw;
        right = axial - lateral - yaw;

        // normalize all motor speeds so no values exceeds 100%.
        double max = Math.max(Math.abs(backLeft), Math.abs(right));
        max = Math.max(max, Math.abs(backRight));
        max = Math.max(max, Math.abs(left));
        if (max > 1.0)
        {
            backLeft /= max;
            backRight /= max;
            right /= max;
            left /= max;
        }

        rearLeft.setPower(backLeft);
        rearRight.setPower(backRight);
        frontLeft.setPower(left);
        frontRight.setPower(right);

        RobotLog.i("141 Axes A[%+5.2f], L[%+5.2f], Y[%+5.2f]", axial, lateral, yaw);
        RobotLog.i("141 Wheels L[%+5.2f], R[%+5.2f], BL[%+5.2f], BR[%+5.2f]", left, right, backLeft, backRight);
    }
}
