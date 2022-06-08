package com.example.md3.fragment

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.md3.R
import com.example.md3.fragment.popup.ChangeCurrency
import com.example.md3.fragment.popup.ChangeEmail
import com.example.md3.fragment.popup.ChangePassword
import com.example.md3.fragment.popup.ChangeUserName
import com.example.md3.utility.IActivityData
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ProfileFragment : Fragment() {

    private lateinit var activityData : IActivityData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inflaterView = inflater.inflate(R.layout.fragment_profile, container, false)

        //interface data
        if (requireActivity() !is IActivityData)
        {
            throw RuntimeException("Not implemented IActivityData")
        }
        activityData = requireActivity() as IActivityData

        //user
        val id = activityData.getId()
        val email = activityData.getEmail()
        val userName = activityData.getUserName()

        //topbackbutton
        val backButton : AppCompatImageView = inflaterView.findViewById(R.id.profileBack)
        backButton.setOnClickListener{
            val controller : NavController = requireActivity().findNavController(R.id.navigationController)
            if(controller.backQueue.size > 2)
            {
                controller.popBackStack()
            }
        }

        //picture
        val profileImage : ImageView = inflaterView.findViewById(R.id.profileImage)
        profileImage.setImageBitmap(activityData.getImageUri())

        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // Handle the returned Uri
            if(uri != null) {
                var bitmap: Bitmap =
                    MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)
                bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true)
                profileImage.setImageBitmap(bitmap)
                activityData.updateImageUri(bitmap)
            }
        }

        val pictureButton : FloatingActionButton = inflaterView.findViewById(R.id.changeImage)
        pictureButton.setOnClickListener{
            getContent.launch("image/*")
        }


        //user
        val userText : TextView = inflaterView.findViewById(R.id.profileName)
        userText.text = userName
        val userButton : AppCompatImageView = inflaterView.findViewById(R.id.profileEdit)
        userButton.setOnClickListener{
            val popup = ChangeUserName()
            popup.show(requireActivity().supportFragmentManager, "popupUser")
        }

        //email
        val cardEmail : CardView = inflaterView.findViewById(R.id.cardEmail)
        val textEmail : TextView = inflaterView.findViewById(R.id.emailProfile)
        textEmail.text = email
        cardEmail.setOnClickListener{
            val popup = ChangeEmail()
            popup.show(requireActivity().supportFragmentManager, "popupEmail")
        }

        //password
        val cardPass : CardView = inflaterView.findViewById(R.id.cardPassword)
        cardPass.setOnClickListener{
            val popup = ChangePassword()
            popup.show(requireActivity().supportFragmentManager,"popupPassword")
        }

        //currency
        val cardCurrency : CardView = inflaterView.findViewById(R.id.cardCurrency)
        cardCurrency.setOnClickListener{
            val popup = ChangeCurrency()
            popup.show(requireActivity().supportFragmentManager,"popupCurrency")
        }

        //logout
        val cardLogout : CardView = inflaterView.findViewById(R.id.cardLogout)
        cardLogout.setOnClickListener{
            activityData.removeAutoLog()
            requireActivity().finish()
        }

        return inflaterView
    }


}


