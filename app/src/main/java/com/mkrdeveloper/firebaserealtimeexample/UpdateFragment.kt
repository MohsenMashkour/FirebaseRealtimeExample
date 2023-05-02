package com.mkrdeveloper.firebaserealtimeexample

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mkrdeveloper.firebaserealtimeexample.databinding.FragmentHomeBinding
import com.mkrdeveloper.firebaserealtimeexample.databinding.FragmentUpdateBinding
import com.mkrdeveloper.firebaserealtimeexample.models.Contacts
import com.squareup.picasso.Picasso


class UpdateFragment : Fragment() {

    private var _binding : FragmentUpdateBinding? = null
    private  val binding get() = _binding!!

    private val args : UpdateFragmentArgs by navArgs()

    private lateinit var firebaseRef : DatabaseReference
    private lateinit var storageRef : StorageReference

    private var uri: Uri? = null
    private var imageUrl : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateBinding.inflate(inflater , container, false)

        firebaseRef = FirebaseDatabase.getInstance().getReference("contacts")
        storageRef = FirebaseStorage.getInstance().getReference("Images")

        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()){
            binding.imgUpdate.setImageURI(it)
            if (it != null){
                uri = it
            }
        }
        imageUrl = args.imageUrl

        binding.apply {
            edtUpdateName.setText(args.name)
            edtUpdatePhone.setText(args.phone)
            Picasso.get().load(imageUrl).into(imgUpdate)

            btnUpdate.setOnClickListener {
                updateData()
                findNavController().navigate(R.id.action_updateFragment_to_homeFragment)
            }

            imgUpdate.setOnClickListener {
                context?.let { context ->
                    MaterialAlertDialogBuilder(context)
                        .setTitle("changing the image")
                        .setMessage("please select the option")
                        .setPositiveButton("change image"){_,_->

                            pickImage.launch("image/*")
                        }
                        .setNegativeButton("delete image"){_,_->

                            imageUrl = null
                            binding.imgUpdate.setImageResource(R.drawable.logo)
                        }
                        .setNeutralButton("cancel"){_,_->}
                        .show()
                }
            }
        }

        return binding.root
    }

    private fun updateData() {
        val name = binding.edtUpdateName.text.toString()
        val phone = binding.edtUpdatePhone.text.toString()
        var contacts : Contacts



        if (uri != null){
            storageRef.child(args.id).putFile(uri!!)
                .addOnSuccessListener { task ->
                task.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener {url->
                    imageUrl = url.toString()
                    contacts = Contacts(args.id, name, phone , imageUrl)
                        firebaseRef.child(args.id).setValue(contacts)
                            .addOnCompleteListener{
                                Toast.makeText(context, " data updated successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener{
                                Toast.makeText(context, "error ${it.message}", Toast.LENGTH_SHORT).show()
                            }

                    }

                }

        }
        if (uri == null){
            contacts = Contacts(args.id, name, phone , imageUrl)
            firebaseRef.child(args.id).setValue(contacts)
                .addOnCompleteListener{
                    Toast.makeText(context, " data updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(context, "error ${it.message}", Toast.LENGTH_SHORT).show()
                }

        }
        if (imageUrl == null) storageRef.child(args.id).delete()
    }

    }

