package com.example.controle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;
import com.example.controle.ml.FashionMNISTModel;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.TransformToGrayscaleOp;

import java.io.IOException;
import java.nio.ByteBuffer;

public class PredictClass {
    private Context context;
    Interpreter.Options options=new Interpreter.Options();
    Interpreter interpreter;

    ImageProcessor imageProcessor=new
            ImageProcessor.Builder()
                    .add(new ResizeOp(28, 28, ResizeOp.ResizeMethod.BILINEAR))
                    .add(new NormalizeOp(0f, 255f))
                    .build();
    ImageProcessor grayProcessor=new
            ImageProcessor.Builder()
            .add(new TransformToGrayscaleOp())
            .build();
    public PredictClass(Context context) throws IOException {
        this.context = context;
        options.setUseNNAPI(true);
        interpreter = new Interpreter(FileUtil.loadMappedFile(context, "FashionMNISTModel.tflite"), options);
    }

    public String classify(Bitmap bitmap) {
        try {
            FashionMNISTModel model = FashionMNISTModel.newInstance(context);
            // Creates inputs for reference.
            TensorImage input=TensorImage.fromBitmap(bitmap);
            Float[] output = new Float[10];
            ByteBuffer processedInput=grayProcessor.process(imageProcessor.process(input)).getBuffer();
            Log.e("SIZE",String.valueOf(processedInput.capacity()));
            // Runs model inference and gets result.
            interpreter.run(processedInput,output);
            // Releases model resources if no longer used.
            for (int i = 0; i < 10; i++) {
                Log.e("OUTPUT",String.valueOf(output[i]));
            }
            return String.valueOf(output[0]);
        } catch (IOException e) {
            // TODO Handle the exception
        }
        return "Unknown Image";
    }

}
