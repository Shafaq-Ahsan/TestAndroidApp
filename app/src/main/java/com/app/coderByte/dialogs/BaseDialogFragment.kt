package com.app.coderByte.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.app.coderByte.interfaces.InitMethods

// base dialog fragment
internal abstract class BaseDialogFragment : DialogFragment(), InitMethods {

    //class name variable
    private lateinit var className: String

    //    data binding variable
    protected lateinit var binding: ViewDataBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        className = javaClass.name
        getViewModel()
        observe()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            getFragmentLayout(),
            container,
            false
        )

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getViewBinding() //interface method

        init() //interface method

        setLanguageData() //interface method

        setLiveDataValues() //interface method

        setListeners()//interface method

    }

}
