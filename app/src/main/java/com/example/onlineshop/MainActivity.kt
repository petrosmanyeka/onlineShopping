package com.example.onlineshop

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1

    private lateinit var mButtonChooseImage: Button
    private lateinit var mButtonUpload: Button
    private lateinit var mTextViewShowUploads: TextView
    private lateinit var mEditTextFileName: EditText
    private lateinit var mImageView: ImageView
    private lateinit var mProgressBar: ProgressBar

    private var imageUrl: Uri? = null

    private lateinit var mStorageRef: StorageReference
    private lateinit var mDatabaseRef: DatabaseReference

    private var mUploadTask: StorageTask<UploadTask.TaskSnapshot>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mButtonChooseImage = findViewById(R.id.button_choose_image)
        mButtonUpload = findViewById(R.id.button_upload)
        mTextViewShowUploads = findViewById(R.id.text_view_show_uploads)
        mEditTextFileName = findViewById(R.id.edit_text_file_name)
        mImageView = findViewById(R.id.image_view)
        mProgressBar = findViewById(R.id.progress_bar)

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads")
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads")

        mButtonChooseImage.setOnClickListener {
            openFileChooser()
        }

        mButtonUpload.setOnClickListener {
            if (mUploadTask != null && mUploadTask!!.isInProgress) {
                Toast.makeText(this@MainActivity, "Upload in progress", Toast.LENGTH_SHORT).show()
            } else {
                uploadFile()
            }
        }

        mTextViewShowUploads.setOnClickListener {
            openImagesActivity()
        }
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
            && data != null && data.data != null
        ) {
            imageUrl = data.data

            Picasso.get().load(imageUrl).into(mImageView)
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val cR: ContentResolver = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    private fun uploadFile() {
        imageUrl?.let { imageUri ->
            val fileReference: StorageReference = mStorageRef.child(
                System.currentTimeMillis().toString()
                        + "." + getFileExtension(imageUri)!!
            )

            mUploadTask = fileReference.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    val handler = Handler()
                    handler.postDelayed({
                        mProgressBar.progress = 0
                    }, 500)

                    Toast.makeText(
                        this@MainActivity,
                        "Upload successful",
                        Toast.LENGTH_LONG
                    ).show()

                    val priceText = findViewById<EditText>(R.id.price)
                    val price = priceText.text.toString().toDoubleOrNull() ?: 0.0

                    fileReference.downloadUrl.addOnSuccessListener { uri ->
                        val upload = Upload(
                            mEditTextFileName.text.toString().trim(),
                            uri.toString(),
                            price
                        )

                        val uploadId = mDatabaseRef.push().key
                        uploadId?.let { mDatabaseRef.child(it).setValue(upload) }
                    }.addOnFailureListener { e ->
                        Toast.makeText(
                            this@MainActivity,
                            e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this@MainActivity,
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress =
                        (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                    mProgressBar.progress = progress.toInt()
                }
        } ?: Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
    }
    private fun openImagesActivity() {
        val intent = Intent(this, ImagesActivity::class.java)
        startActivity(intent)
    }
}