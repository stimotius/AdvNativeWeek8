package com.example.todoapp.view

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentCreateTodoBinding
import com.example.todoapp.model.Todo
import com.example.todoapp.util.NotificationHelper
import com.example.todoapp.util.TodoWorker
import com.example.todoapp.viewmodel.DetailTodoViewModel
import java.util.Calendar
import java.util.concurrent.TimeUnit


class CreateTodoFragment : Fragment(), RadioClick, ButtonAddTodoClickListener, DateClickListener, TimeClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: FragmentCreateTodoBinding
    private lateinit var viewModel: DetailTodoViewModel
    var year = 0
    var month = 0
    var day = 0
    var hour = 0
    var minute = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_create_todo, container, false)
        binding = FragmentCreateTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),NotificationHelper.REQUEST_NOTIF)
        }

        binding.todo = Todo("","",3,0, 0)
        binding.addListener = this
        binding.radioListener = this
        binding.listenerDate = this
        binding.listenerTime = this

        viewModel = ViewModelProvider(this).get(DetailTodoViewModel::class.java)
//        binding.todo = Todo("","",3)
//        viewModel = ViewModelProvider(this).get(DetailTodoViewModel::class.java)
//        binding.listener = this
//        binding.radioListener = this

        binding.btnAdd.setOnClickListener {
            var radio =
                view.findViewById<RadioButton>(binding.radioGroupPriority.checkedRadioButtonId)

            var todo = Todo(binding.txtTitle.text.toString(),
                binding.txtNotes.text.toString(), radio.tag.toString().toInt(), 0, 0)

            val workRequest = OneTimeWorkRequestBuilder<TodoWorker>()
                .setInitialDelay(30, TimeUnit.SECONDS)
                .setInputData(
                    workDataOf(
                        "title" to "Todo created",
                        "message" to "Stay focus"
                    ))
                .build()
            WorkManager.getInstance(requireContext()).enqueue(workRequest)

            val list = listOf(todo)
            viewModel.addTodo(list)

//            val notif = NotificationHelper(view.context, requireActivity())
//            notif.createNotification("Todo Created",
//                "A new todo has been created! Stay focus!")


            Toast.makeText(view.context, "Data Added", Toast.LENGTH_LONG).show()
            Navigation.findNavController(it).popBackStack()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,
                                            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode ==NotificationHelper.REQUEST_NOTIF) {
            if(grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {
                NotificationHelper(requireContext())
                    .createNotification("Todo Created",
                        "A new todo has been created! Stay focus!")
            }
        }
    }

    override fun onRadioClick(v: View, priority: Int, obj: Todo) {
        binding.todo!!.priority =
            v.tag.toString().toInt()
    }

    override fun onButtonAddTodo(v: View) {
        val c = Calendar.getInstance()
        c.set(year,month,day, hour, minute,0)
        val today = Calendar.getInstance()
        val diff = (c.timeInMillis/1000L)-(today.timeInMillis/1000L)
        binding.todo!!.todo_date = (c.timeInMillis/1000L).toInt()

        val workRequest = OneTimeWorkRequestBuilder<TodoWorker>()
            .setInitialDelay(diff, java.util.concurrent.TimeUnit.SECONDS)
            .setInputData(
                androidx.work.workDataOf(
                    "title" to "Todo created",
                    "message" to "Stay focus"
                )
            )
            .build()
        WorkManager.getInstance(requireContext()).enqueue(workRequest)

        val list = listOf(binding.todo!!)
        viewModel.addTodo(list)


        Toast.makeText(view?.context, "Data Added", Toast.LENGTH_LONG).show()
        Navigation.findNavController(v).popBackStack()
    }

    override fun onDateClick(v: View) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        var month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        activity?.let {it1 -> DatePickerDialog(it1, this, year, month, day).show()}
    }

    override fun onTimeClick(v: View) {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), this, hour, minute, true).show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        Calendar.getInstance().let {
            it.set(year, month, dayOfMonth)
            binding.txtDate.setText(dayOfMonth.toString().padStart(2,'0') + "-" +month.toString().padStart(2,'0')+"-"+year)
            this.year = year
            this.month = month
            this.day = day
        }
    }

    override fun onTimeSet(p0: TimePicker?, h: Int, m: Int) {
        binding.txtTime.setText(
            h.toString().padStart(2,'0') + ":" +
                    m.toString().padStart(2,'0')
        )
        this.hour = h
        this.minute = m
    }
}