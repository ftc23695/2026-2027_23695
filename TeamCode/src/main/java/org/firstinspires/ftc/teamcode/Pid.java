package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
@TeleOp
public class Pid extends OpMode {


    private DcMotorEx shooter = null;
    private DcMotor intakeFront = null;
    public static double NEW_P = 2.5;
    public static double NEW_I = 0.1;
    public static double NEW_D = 0.2;
    public static double NEW_F = 0;
    double targetVelocity = 1500;
    double error = 0;
    FtcDashboard dashboard = FtcDashboard.getInstance();
    Telemetry dashboardTelemetry = dashboard.getTelemetry();

    @Override
    public void init(){
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        intakeFront = hardwareMap.get(DcMotor.class, "intakeForward");
        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //shooter.setVelocityPIDFCoefficients(NEW_P,NEW_I,NEW_D,NEW_F);

    }
    @Override
    public void loop(){
        error = targetVelocity - shooter.getVelocity();
        double power = NEW_P * error + NEW_F * targetVelocity;
        dashboardTelemetry.addData("ShooterVelocity",shooter.getVelocity());
        dashboardTelemetry.addData("Error",error);
        dashboardTelemetry.addData("Power",power);


        dashboardTelemetry.update();
        if(power<0){
            shooter.setPower(0);
        }else{
            shooter.setPower(power);
        }

        if(gamepad1.a){
            intakeFront.setPower(1);
        }else {
            intakeFront.setPower(0);
        }

    }
}
