package com.sangyan.firstai

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var buttonCamera = findViewById<Button>(R.id.myButton)

        buttonCamera.setOnClickListener {
            val intent =Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if(intent.resolveActivity(packageManager)!=null){
                startActivityForResult(intent,123)

            }else{
                Toast.makeText(this,"oops something went wronf",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==123 && resultCode== RESULT_OK){
            val extras =data?.extras
            val bitmap =extras?.get("data") as? Bitmap
            if(bitmap !=null){
                detectFace(bitmap)
            }
        }
    }

    private fun detectFace(bitmap: Bitmap) {
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
        val detector =FaceDetection.getClient(highAccuracyOpts)
        val image = InputImage.fromBitmap(bitmap,0)

        val  result =detector.process(image)
            .addOnSuccessListener { faces->
                var resultText =" "
                var i=1
                for(face in faces){
                    resultText ="face Number: $i" +
                            "\nsmile : ${face.smilingProbability?.times(100)}%" +
                            "\nLeft eye open : ${face.leftEyeOpenProbability?.times(100)}%" +
                            "\n right eye open : ${face.rightEyeOpenProbability?.times(100)}%"
                    i++
                }
                if(faces.isEmpty()){
                    Toast.makeText(this," NO DETECTED FACE",Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(this, resultText,Toast.LENGTH_LONG).show()

                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this," Some thing wrong",Toast.LENGTH_SHORT).show()

            }

    }
}
