package com.coreyhorn.mvpiframeworkexample.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.coreyhorn.mvpiframework.architecture.MVIViewModel
import com.coreyhorn.mvpiframework.views.MVIFragment
import com.coreyhorn.mvpiframeworkexample.ExampleEvent
import com.coreyhorn.mvpiframeworkexample.ExampleResult
import com.coreyhorn.mvpiframeworkexample.ExampleState
import com.coreyhorn.mvpiframeworkexample.databinding.FragmentExampleBinding

class ExampleFragment: MVIFragment<ExampleEvent, ExampleResult, ExampleState>() {

    private var _binding: FragmentExampleBinding? = null
    private val binding get() = _binding!!

    override var lifecycleOwner: LifecycleOwner = this

    override fun initialState(): ExampleState {
        return ExampleState("initial string")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if (savedInstanceState == null) {
//            events.onNext(ExampleEvent.ChangeText())
//        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExampleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun presenterProvider(): MVIViewModel<ExampleEvent, ExampleResult, ExampleState> {
        return ViewModelProvider(this@ExampleFragment).get(ExampleViewModel::class.java)
    }

    override fun renderState(view: View, state: ExampleState) {
        binding.fragmentText.text = state.whatever
    }

    override fun setupViewBindings(view: View) {
        binding.changeText.setOnClickListener {
            events.onNext(ExampleEvent.ChangeText())
        }
    }
}