package com.mkrdeveloper.firebaserealtimeexample.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.mkrdeveloper.firebaserealtimeexample.HomeFragmentDirections
import com.mkrdeveloper.firebaserealtimeexample.databinding.RvContactsItemBinding
import com.mkrdeveloper.firebaserealtimeexample.models.Contacts
import com.squareup.picasso.Picasso

class RvContactsAdapter(private  val contactList : java.util.ArrayList<Contacts>) : RecyclerView.Adapter<RvContactsAdapter.ViewHolder>() {

    class ViewHolder(val binding : RvContactsItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return  ViewHolder(RvContactsItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = contactList[position]
        holder.apply {
            binding.apply {
                tvNameItem.text = currentItem.name
                tvPhoneItem.text = currentItem.phoneNumber
                tvIdItem.text = currentItem.id
                Picasso.get().load(currentItem.imgUri).into(imgItem)


                rvContainer.setOnClickListener {

                    val action = HomeFragmentDirections.actionHomeFragmentToUpdateFragment(
                        currentItem.id.toString(),
                        currentItem.name.toString(),
                        currentItem.phoneNumber.toString(),
                        currentItem.imgUri.toString()
                    )
                    findNavController(holder.itemView).navigate(action)
                }

                rvContainer.setOnLongClickListener {
                    MaterialAlertDialogBuilder(holder.itemView.context)
                        .setTitle("Delete item permanently")
                        .setMessage("Are you sure you want to delete this item?")
                        .setPositiveButton("Yes"){_,_ ->
                            val firebaseRef = FirebaseDatabase.getInstance().getReference("contacts")
                            val storageRef = FirebaseStorage.getInstance().getReference("Images")
                            //storage
                            storageRef.child(currentItem.id.toString()).delete()

                            // realtime database
                            firebaseRef.child(currentItem.id.toString()).removeValue()
                                .addOnSuccessListener {
                                    Toast.makeText(holder.itemView.context,"Item removed successfully" ,Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {error ->
                                    Toast.makeText(holder.itemView.context,"error ${error.message}" ,Toast.LENGTH_SHORT).show()
                                }
                        }
                        .setNegativeButton("No"){_,_ ->
                            Toast.makeText(holder.itemView.context,"canceled" ,Toast.LENGTH_SHORT).show()
                        }
                        .show()

                    return@setOnLongClickListener true
                }
            }
        }
    }
}