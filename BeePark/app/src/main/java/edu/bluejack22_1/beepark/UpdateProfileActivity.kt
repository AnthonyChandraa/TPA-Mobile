package edu.bluejack22_1.beepark

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import edu.bluejack22_1.beepark.UIString.UiString
import edu.bluejack22_1.beepark.controllers.UserController
import edu.bluejack22_1.beepark.databinding.ActivityUpdateProfileBinding
import java.text.SimpleDateFormat
import java.util.*

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateProfileBinding
    private lateinit var userId: String
    private lateinit var userController: UserController
    private lateinit var imageUrl: Uri
    private lateinit var username: String
    private lateinit var email: String
    private lateinit var licensePlate: String

    private var isGoogle:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUpdateProfileBinding.inflate(this.layoutInflater)

        userId = intent.extras?.getString("userId").toString()
        username = intent.extras?.getString("username").toString()
        email = intent.extras?.getString("email").toString()
        licensePlate = intent.extras?.getString("licensePlate").toString()

        isGoogle = intent.extras?.getBoolean("isGoogle").toString().toBoolean()

        binding.usernameInput.setText(username)
        binding.emailInput.setText(email)

        if(isGoogle){
            binding.emailInput.visibility = View.INVISIBLE
        } else{
            binding.emailInput.visibility = View.VISIBLE
        }

        if(licensePlate != "null"){
            binding.licensePlateInput.setText(licensePlate)
        }

        imageUrl = Uri.EMPTY

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

            if(binding.usernameInput.text.isEmpty() || binding.emailInput.text.isEmpty()){
                Toast.makeText(this@UpdateProfileActivity, UiString.StringResource(R.string.errorEmpty).asString(this), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            uploadImage()
        }
    }

    private fun setUpdate(urlUpload: String) {
//        Log.w("UserID", "$userId")
        if(isGoogle){
            userController.updateUser(binding.usernameInput.text.toString(), email,
                binding.licensePlateInput.text.toString(), urlUpload, userId)

        } else {
            userController.updateUser(binding.usernameInput.text.toString(), binding.emailInput.text.toString(),
                binding.licensePlateInput.text.toString(), urlUpload, userId)
        }
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

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val now = Date()
        val filename = formatter.format(now)
        val storageRef = FirebaseStorage.getInstance().getReference("images/$filename")
        Log.w("Storage", "$storageRef")

        if(imageUrl != Uri.EMPTY){
            storageRef.putFile(imageUrl)
                .addOnSuccessListener {
                    Log.w("Test", "")
                    Toast.makeText(this@UpdateProfileActivity, UiString.StringResource(resId = R.string.Uploaded).asString(this), Toast.LENGTH_SHORT).show()

                    storageRef.downloadUrl.addOnSuccessListener {
                        var urlUpload = it.toString()
                        setUpdate(urlUpload)
                    }

                    if(progressDialog.isShowing) progressDialog.dismiss()
                }
        } else {
            var urlUpload = intent.extras?.getString("imageUrl").toString()
            setUpdate(urlUpload)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == RESULT_OK){

            imageUrl = data?.data!!
            binding.confirmInput.text = UiString.StringResource(resId = R.string.Uploaded).asString(this)
        }
    }
}