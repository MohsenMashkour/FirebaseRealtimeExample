package com.mkrdeveloper.firebaserealtimeexample

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mkrdeveloper.firebaserealtimeexample.databinding.FragmentHomeBinding
import com.mkrdeveloper.firebaserealtimeexample.databinding.FragmentUpdateBinding
import com.mkrdeveloper.firebaserealtimeexample.models.Contacts


class UpdateFragment : Fragment() {

    private var _binding : FragmentUpdateBinding? = null
    private  val binding get() = _binding!!

    private val args : UpdateFragmentArgs by navArgs()

    private lateinit var firebaseRef : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateBinding.inflate(inflater , container, false)

        firebaseRef = FirebaseDatabase.getInstance().getReference("contacts")

        binding.apply {
            edtUpdateName.setText(args.name)
            edtUpdatePhone.setText(args.phone)
            btnUpdate.setOnClickListener {
                updateData()
                findNavController().navigate(R.id.action_updateFragment_to_homeFragment)
            }
        }

        return binding.root
    }

    private fun updateData() {
        val name = binding.edtUpdateName.text.toString()
        val phone = binding.edtUpdatePhone.text.toString()
        val contacts = Contacts(args.id, name , phone)

        firebaseRef.child(args.id).setValue(contacts)
            .addOnCompleteListener{
                Toast.makeText(context, " data updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(context, "error ${it.message}", Toast.LENGTH_SHORT).show()
            }

    }

}