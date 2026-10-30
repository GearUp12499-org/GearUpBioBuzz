package org.firstinspires.ftc.teamcode;

import static java.lang.String.valueOf;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class DetectPollen implements VisionProcessor {

    Mat hsv = new Mat();
    Mat mask = new Mat();

    Mat circles = new Mat();

    ArrayList<int[][]> circlesButArray = new ArrayList<>();
    Scalar yellow_upper = new Scalar(100, 255, 255);
    Scalar yellow_lower = new Scalar(10, 100, 100);
    private Mat transform(Mat img){
        Size erodeKSize = new Size(5,5);
        Size medianSize = new Size(31,31);
        Mat kernal = Imgproc.getStructuringElement(1,erodeKSize);
        Imgproc.erode(img, img, kernal);
        Imgproc.dilate(img, img, kernal);
        Imgproc.medianBlur(img, img, 5);
        Imgproc.GaussianBlur(img, img, medianSize, 1);

        return img;
    }

    @Override
    public void init(int width, int height, CameraCalibration calibration) {

    }

    @Override
    public Object processFrame(Mat frame, long captureTimeNanos) {
        double time1 = System.nanoTime();
        Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_BGR2HSV);

        Core.inRange(hsv, yellow_lower, yellow_upper, mask);

        mask = transform(mask);

        //returns a matrix for some reason and not a 2d array :(
        //[[x,y,r],
        // [x,y,r]]
        Imgproc.HoughCircles(
                mask,
                circles,
                Imgproc.HOUGH_GRADIENT,
                1,
                50,
                64,
                20,
                5,
                130
        );

        if (circles != null) {
            for (int i = 0; i < circles.rows(); i++) {
                Scalar green = new Scalar(0, 255, 0);

                //mat.get returns an array :/
                //legit should just be an array with one index
                double x = circles.get(0, i)[0];
                double y = circles.get(0, i)[1];
                double r = circles.get(0, i)[2];

                Log.i("circlex", valueOf(x));
                Log.i("circley", valueOf(y));
                Log.i("circler", valueOf(r));

                Point center = new Point(x, y);

                Imgproc.circle(frame, center, (int) r, green);
            }
        }
        double time2 = System.nanoTime();

        Log.i("delivery time", valueOf((time1 - captureTimeNanos)/1e9));
        Log.i("run time", valueOf((time2 - time1)/1e9));

        return null;
    }

    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {
//        if (circles != null){
//            for (int i = 0; i < circles.rows(); i++){
//                Paint green = new Paint();
//                green.setColor(Color.GREEN);
//
//                //mat.get returns an array :/
//                //legit should just be an array with one index
//                double x = circles.get(0, i)[0];
//                double y = circles.get(0, i)[1];
//                double r = circles.get(0, i)[2];
//
//                Log.i("scale", valueOf(scaleBmpPxToCanvasPx));
//                canvas.drawCircle((float)(x * scaleBmpPxToCanvasPx),(float)(y * scaleBmpPxToCanvasPx),(float)(r * scaleBmpPxToCanvasPx), green);
//            }
//        }
    }
}
