package team25core;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class MaddiesDriveTrain extends DrivetrainBaseImpl implements Drivetrain {

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;


    public MaddiesDriveTrain(DcMotor frontLeft, DcMotor frontRight, DcMotor backLeft, DcMotor backRight) { //constructor
        // this is the constructor (instanitate and sets aside memory for the program)
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;
setCanonicalMotorDirection();
        /**
         * Set a default master.  This is the wheel/motor that will be used to track distance
         * travelled when following a dead reckon path.
         */
        setMasterMotor(backRight);
        //chooses the motor enconder
    }

    public void setCanonicalMotorDirection() {
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);

    }

    //sets motors position to 0
    @Override
    public void resetEncoders() {
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    }

    //motor will try to tun at the targeted velocity
    public void encodersOn() {
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }
   // public void setCanonicalMotorDirection()
    {
        // This reverses the direction of the drivetrain.
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);
    }



    //sets the behavior when the a power level of zero is applied ie.motors not moving
    // when the motor value is 0 then apply the BRAKE
    public void brakeOnZeroPower() {
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }

    @Override
    public void straight(double speed) {

        frontRight.setPower(speed);
        backRight.setPower(speed);
        frontLeft.setPower(speed);
        backLeft.setPower(speed);
    }


    /**
     * Postitive is to the right, negative is to the left
     */
    @Override
    public void strafe(double speed){
        frontRight.setPower(-speed);
        backRight.setPower(speed);
        frontLeft.setPower(-speed);
        backLeft.setPower(speed);

}


    /**
     * Move forward or back on a diagonal at 315 (forward) / 135 (backward) degrees
     *
     * Obviously only works for mechanum drivetrains
     */
    @Override
    public void leftDiagonal(double speed) {
        frontRight.setPower(-speed);

        backLeft.setPower(-speed);
    }
    /**
     * Move forward or back on a diagonal at 45 (forward) / 225 (backward) degrees
     *
     * Obviously only works for mechanum drivetrains
     */
    @Override
    public void rightDiagonal(double speed){

        backRight.setPower(-speed);
        frontLeft.setPower(-speed);

    }
    /**
     * Allows a turn around a pivot point that is not the dead center of the drivetrain.
     */
    @Override
    public void turn(double speed) {
        frontRight.setPower(-speed);
        backRight.setPower(-speed);
        frontLeft.setPower(speed);
        backLeft.setPower(speed);
    }
   public void pivotTurn(Drivetrain.PivotSide side, double speed){

    }
   public void setPivotMultiplier(double pivotMultiplier){

    }

    public void setPowerLeft(double speed){

    }
    public void setPowerRight(double speed){

    }
    public void stop(){
        frontLeft.setPower(0.0);
        frontRight.setPower(0.0);
        backLeft.setPower(0.0);
        backRight.setPower(0.0);
    }
    public void move(double axial, double lateral, double yaw){

    }
    @Override
   public void logEncoderCounts(){

    }
}





