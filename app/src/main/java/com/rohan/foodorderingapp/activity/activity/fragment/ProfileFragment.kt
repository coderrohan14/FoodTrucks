package com.rohan.foodorderingapp.activity.activity.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rohan.foodorderingapp.R

class ProfileFragment:Fragment() {
    lateinit var txtName:TextView
    lateinit var txtMobileNo:TextView
    lateinit var txtEmail: TextView
    lateinit var txtAddress:TextView
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        setHasOptionsMenu(true)
        sharedPreferences = this.activity!!.getSharedPreferences(getString(R.string.data_file_name), Context.MODE_PRIVATE)
        txtName = view.findViewById(R.id.txtName)
        txtMobileNo = view.findViewById(R.id.txtMobileNo)
        txtEmail = view.findViewById(R.id.txtEmail)
        txtAddress = view.findViewById(R.id.txtAddress)
        txtName.text = sharedPreferences.getString("Name",null).toString()
        txtMobileNo.text = "+91-"+sharedPreferences.getString("Mobile",null).toString()
        txtEmail.text = sharedPreferences.getString("Email",null).toString()
        txtAddress.text = sharedPreferences.getString("Address",null).toString()
        return view
    }
}