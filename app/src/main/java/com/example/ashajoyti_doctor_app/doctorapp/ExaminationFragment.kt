package com.example.ashajoyti_doctor_app.doctorapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ashajoyti_doctor_app.R

class ExaminationFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // uses res/layout/fragment_examination.xml
        return inflater.inflate(R.layout.fragment_examination, container, false)
    }
}
