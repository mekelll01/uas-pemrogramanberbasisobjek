package absensi;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

public class DeteksiWajah {

    private VideoCapture camera;
    private CascadeClassifier faceCascade;
    private Mat frameAsli = new Mat();

    public DeteksiWajah() {
        faceCascade = new CascadeClassifier("src/resources/haarcascade_frontalface_default.xml");
        camera = new VideoCapture(0);
    }

    public Mat ambilFrame() {
        Mat frameTampil = new Mat();
        Mat gray = new Mat();

        if (camera.isOpened() && camera.read(frameAsli)) {
            frameAsli.copyTo(frameTampil);

            Imgproc.cvtColor(frameTampil, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.equalizeHist(gray, gray);

            MatOfRect faces = new MatOfRect();
            faceCascade.detectMultiScale(
                    gray,
                    faces,
                    1.2,
                    6,
                    0,
                    new Size(100, 100),
                    new Size()
            );

            for (Rect rect : faces.toArray()) {
                Imgproc.rectangle(
                        frameTampil,
                        rect.tl(),
                        rect.br(),
                        new Scalar(0, 255, 0),
                        2
                );
            }
        }

        return frameTampil;
    }

    public Mat ambilFrameAsli() {
        return frameAsli.clone();
    }

    public void stop() {
        if (camera != null && camera.isOpened()) {
            camera.release();
        }
    }
}
