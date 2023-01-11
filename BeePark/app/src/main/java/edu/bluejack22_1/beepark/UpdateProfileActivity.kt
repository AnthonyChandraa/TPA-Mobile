package edu.bluejack22_1.beepark

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import edu.bluejack22_1.beepark.controllers.UserController
import edu.bluejack22_1.beepark.databinding.ActivityUpdateProfileBinding
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateProfileBinding
    private lateinit var userId: String
    private lateinit var userController: UserController
    private lateinit var imageUrl: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUpdateProfileBinding.inflate(this.layoutInflater)

        userId = intent.extras?.getString("userId").toString()

        userController = UserController(this)

        setUpActionButton()
        setContentView(binding.root)
    }

    private fun setUpActionButton() {
        binding.btnBack.setOnClickListener {
            finishAndRemoveTask()
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        binding.confirmInput.setOnClickListener {
            getImage()
        }

        binding.btnUpdateProfile.setOnClickListener {
            uploadImage()
        }
    }

    private fun setUpdate(){
        userController.updateUser(binding.usernameInput.text.toString(), binding.emailInput.text.toString(), binding.licensePlateInput.text.toString(), imageUrl.toString(), userId)
    }

    private fun getImage(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100);
    }

    private fun uploadImage(){
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading File")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val now = Date()
        val filename = formatter.format(now)
        val storageRef = FirebaseStorage.getInstance().getReference("images/$filename")
        storageRef.putFile(imageUrl)
            .addOnSuccessListener {
                Log.w("Test", "")
                Toast.makeText(this@UpdateProfileActivity, "File Uploaded", Toast.LENGTH_SHORT).show()
                setUpdate()
                if(progressDialog.isShowing) progressDialog.dismiss()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == RESULT_OK){
            imageUrl = data?.data!!
        }
    }
}