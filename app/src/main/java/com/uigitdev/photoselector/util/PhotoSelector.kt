package com.uigitdev.photoselector.util

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import me.echodev.resizer.Resizer
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.min

class PhotoSelector(private val context: AppCompatActivity) {
    private lateinit var _uri: Uri
    private var minCropSize = 400 //Minimal crop size
    private var maxCropSize = 5000 //Maximal crop size
    private val cropRatio = 1 //Photo ratio
    private var finalPhotoSize = 400 //Final photo size

    companion object{
        const val PHOTO_REQUEST = 1001
    }

    fun photoDetail(minCropSize: Int, maxCropSize: Int, finalPhotoSize: Int){
        this.minCropSize = minCropSize
        this.maxCropSize = maxCropSize
        this.finalPhotoSize = finalPhotoSize
    }

    fun photoChooser(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        context.startActivityForResult(intent, PHOTO_REQUEST)
    }

    fun photoCropper(data: Intent){
        _uri = data.data!!
        CropImage.activity(_uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setMultiTouchEnabled(false)
            .setAspectRatio(cropRatio, cropRatio)
            .setMinCropResultSize(minCropSize, minCropSize)
            .setMaxCropResultSize(maxCropSize, maxCropSize)
            .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
            .start(context)
    }

    fun converter(data: Intent, listener: PictureListener){
        val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
        listener.showPhoto(photoResize(result), convertToBytes(photoResize(result)), _uri)
    }

    private fun photoResize(result: CropImage.ActivityResult): Bitmap{
        return Resizer(context)
            .setTargetLength(finalPhotoSize)
            .setSourceImage(File(result.uri.path.toString()))
            .resizedBitmap
    }

    private fun convertToBytes(bitmap: Bitmap): ByteArray{
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        return bytes.toByteArray()
    }

    interface PictureListener{
        fun showPhoto(bitmap: Bitmap, byteArray: ByteArray, uri: Uri)
    }
}