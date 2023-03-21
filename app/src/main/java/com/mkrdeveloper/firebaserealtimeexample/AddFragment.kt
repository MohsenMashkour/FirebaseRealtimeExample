package com.mkrdeveloper.firebaserealtimeexample

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mkrdeveloper.firebaserealtimeexample.databinding.FragmentAddBinding
import com.mkrdeveloper.firebaserealtimeexample.databinding.FragmentHomeBinding
import com.mkrdeveloper.firebaserealtimeexample.models.Contacts


class AddFragment : Fragment() {
    private var _binding : FragmentAddBinding? = null
    private  val binding get() = _binding!!

    private lateinit var firebaseRef : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater , container, false)
        firebaseRef = FirebaseDatabase.getInstance().getReference("contacts")

        binding.btnSend.setOnClickListener {
            saveData()
        }


        return binding.root
    }

    private fun saveData() {
        val name = binding.edtName.text.toString()
        val phone = binding.edtPhone.text.toString()

        if (name.isEmpty()) binding.edtName.error = "write a name"
        if (phone.isEmpty()) binding.edtPhone.error = "write a phone number"

        val contactId = firebaseRef.push().key!!
        val contacts = Contacts(contactId, name, phone)

        firebaseRef.child(contactId).setValue(contacts)
            .addOnCompleteListener{
                Toast.makeText(context, " data stored successfully",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(context, "error ${it.message}",Toast.LENGTH_SHORT).show()
            }
    }


}