package com.example.todoapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentTodoListBinding
import com.example.todoapp.viewmodel.ListTodoViewModel

class TodoListFragment : Fragment() {
    private lateinit var binding: FragmentTodoListBinding
    private lateinit var viewModel: ListTodoViewModel
    private var todoListAdapter = TodoListAdapter(arrayListOf(), {item -> viewModel.clearTask(item)})

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_todo_list, container, false)
        binding = FragmentTodoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ListTodoViewModel::class.java)
        viewModel.refresh()
        binding.recViewTodo.layoutManager = LinearLayoutManager(context)
        binding.recViewTodo.adapter = todoListAdapter

        binding.btnFAB.setOnClickListener {
            val action = TodoListFragmentDirections.actionCreateTodo()
            Navigation.findNavController(it).navigate(action)
        }

        observeViewModel()
    }

    fun observeViewModel() {
        viewModel.todoLD.observe(viewLifecycleOwner, Observer {
            todoListAdapter.updateTodoList(it)
            if (it.isEmpty()) {
                binding.recViewTodo?.visibility = View.GONE
                binding.txtError.setText("Your todo is empty")
            } else {
                binding.recViewTodo?.visibility = View.VISIBLE
            }
        })
        viewModel.loadingLD.observe(viewLifecycleOwner, Observer {
            if (it == false) {
                binding.progressLoad?.visibility = View.GONE
            } else {
                binding.progressLoad?.visibility = View.VISIBLE
            }
        })
        viewModel.todoLoadErrorLD.observe(viewLifecycleOwner, Observer {
            if (it == false) {
                binding.txtError?.visibility = View.GONE
            } else {
                binding.txtError?.visibility = View.VISIBLE
            }
        })
    }
}