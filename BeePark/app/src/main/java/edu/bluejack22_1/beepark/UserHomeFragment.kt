package edu.bluejack22_1.beepark

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ListPopupWindow
import android.widget.Spinner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBindings
import com.google.android.material.textfield.TextInputLayout
import edu.bluejack22_1.beepark.adapters.ParkingSpotsAdapter
import edu.bluejack22_1.beepark.databinding.ActivityHomeBinding
import edu.bluejack22_1.beepark.databinding.FragmentUserHomeBinding
import edu.bluejack22_1.beepark.model.ParkingSpot
import java.util.*
import kotlin.collections.ArrayList


class UserHomeFragment : Fragment() {

    private var _binding: FragmentUserHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserHomeBinding.inflate(inflater, container, false)
        return binding.root;
    }

}