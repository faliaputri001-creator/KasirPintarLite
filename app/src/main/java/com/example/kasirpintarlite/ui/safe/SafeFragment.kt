package com.example.kasirpintarlite.ui.safe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kasirpintarlite.R

class SafeFragment : Fragment() {

    private var _root: View? = null
    protected val root get() = _root!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _root = inflater.inflate(R.layout.fragment_safe, container, false)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _root = null
    }
}

