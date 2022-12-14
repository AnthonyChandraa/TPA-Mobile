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
import androidx.viewbinding.ViewBindings
import com.google.android.material.textfield.TextInputLayout
import edu.bluejack22_1.beepark.databinding.ActivityHomeBinding
import edu.bluejack22_1.beepark.databinding.FragmentUserHomeBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserHomeFragment : Fragment() {
    // TODO: Rename and change types of parameters


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

        //        code sini

        // simpen data building
        val buildingList: MutableList<String> = ArrayList()
        // simpen data floor
        val floorList: MutableList<String> = ArrayList()


        for(i in 1..3){
            buildingList.add("Building $i")
        }

        for(i in 1..8){
            floorList.add("Floor $i")
        }

        val adapterDropdownBuilding = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, buildingList)

        val adapterDropdownFloor = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, floorList)


        binding.dropdownBuilding.adapter = adapterDropdownBuilding
        binding.dropdownFloor.adapter = adapterDropdownFloor

        return binding.root;
    }

}