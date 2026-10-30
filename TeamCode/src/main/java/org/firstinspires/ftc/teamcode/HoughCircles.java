package org.firstinspires.ftc.teamcode;

import org.opencv.*;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.*;

import java.util.ArrayList;

public class HoughCircles extends OpenCvPipeline {

    Mat hsv = new Mat();
    Mat mask = new Mat();

    Mat circles = new Mat();

    ArrayList<int[][]> circlesButArray = new ArrayList<>();
    Scalar yellow_upper = new Scalar(30, 255, 255);
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
    public Mat processFrame(Mat mat) {
        Imgproc.cvtColor(mat, hsv, Imgproc.COLOR_BGR2HSV );

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

        if (circles != null){
            for (int i = 0; i < circles.rows(); i++){

                //mat.get returns an array :/
                //legit should just be an array with one index
                double[] x = circles.get(i, 0);
                double[] y = circles.get(i, 1);
                double[] r = circles.get(i, 2);

                Point center = new Point(x[0],y[0]);
                Scalar green = new Scalar(0,255,0);

                Imgproc.circle(mat, center, (int)r[0], green);
            }
        }

        return mat;
    }

    public Mat getCircles(){
        return circles;
    }
}
