package com.uigitdev.photoselector

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.theartofdev.edmodo.cropper.CropImage
import com.uigitdev.photoselector.util.PhotoSelector
import com.uigitdev.photoselector.util.PhotoSelector.Companion.PHOTO_REQUEST
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var photoSelector: PhotoSelector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        selectPhoto()
    }

    private fun init(){
        //INFO [Init Photo selector class]
        photoSelector = PhotoSelector(this)
        //INFO [min select photo size, max select photo size, final photo size]
        photoSelector.photoDetail(500, 5000, 700)
    }

    private fun selectPhoto(){
        item_picture_select.setOnClickListener {
            //INFO [Open media store]
            photoSelector.photoChooser()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //INFO [PHOTO_REQUEST is my request code in PhotoSelector class]
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            photoSelector.photoCropper(data)
        }
        //INFO [Photo crop result]
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                item_picture_progress.visibility = View.VISIBLE
                photoSelector.converter(data!!, object : PhotoSelector.PictureListener{
                    override fun showPhoto(bitmap: Bitmap, byteArray: ByteArray, uri: Uri) {
                        item_picture_progress.visibility = View.GONE

                        //INFO [Use the photo as bitmap]
                        item_picture.setImageBitmap(bitmap)

                        //INFO [Use the photo as uri]
                        //item_picture.setImageURI(uri)

                        //INFO [If you want to upload the photo into FireStore | Use it as byteArray]
                        //...[YOUR_REFERENCE].putBytes(byteArray)
                    }
                })
            }
        }
    }
}