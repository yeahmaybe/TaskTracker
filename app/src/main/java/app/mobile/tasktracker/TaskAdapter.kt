package app.mobile.tasktracker

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import app.mobile.tasktracker.database.tasks.TaskDTO
import app.mobile.tasktracker.network.model.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class TaskAdapter(val context: Context, val taskList: List<TaskDTO>): RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    inner class ViewHolder(taskView: View): RecyclerView.ViewHolder(taskView) {
        var label: TextView
        var description: TextView
        var deadline: TextView
        lateinit var id: String

        var executeListener: RadioGroup.OnCheckedChangeListener = object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                completeTask(id)
            }
        }

        var openDetailsListener: View.OnClickListener = object: View.OnClickListener {
            override fun onClick(v: View?) {
                val context: Context = this@TaskAdapter.context
                val intent = Intent(context, TaskPageActivity::class.java)
                intent.putExtra("taskId", id)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;

                // start your next activity
                startActivity(context, intent, Bundle())
            }
        }

        init {
            label = taskView.findViewById(R.id.label_text_view)
            description = taskView.findViewById(R.id.description_text_view)
            deadline = taskView.findViewById(R.id.deadline_text_view)
            lateinit var id : String

            val radioGroup = taskView.findViewById<RadioGroup>(R.id.complete_group)
            radioGroup.setOnCheckedChangeListener(executeListener)

            taskView.setOnClickListener(openDetailsListener)

        }
    }


    fun completeTask(id: String) {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiInterface::class.java)


        val retrofitCompleteTask = retrofitBuilder.completeTask(id)
        retrofitCompleteTask.enqueue(object: Callback<TaskDTO> {

            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<TaskDTO>, response: Response<TaskDTO>) {

            }

            override fun onFailure(call: Call<TaskDTO>, t: Throwable) {
                Log.d("MainActivity", "OnFailure "+t.message)
            }
        })
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.label.text = taskList[position].label
        holder.description.text = taskList[position].description
        holder.id = taskList[position].id

        val localDateTime = LocalDateTime.parse(taskList[position].deadline_dt)
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
        val output = formatter.format(localDateTime)

        holder.deadline.text = output
    }

}