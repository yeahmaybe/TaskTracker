package app.mobile.tasktracker

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import app.mobile.features.task_crud.TaskCrudController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class TaskPageActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_page)
        val titleView = findViewById<TextView>(R.id.task_title)
        val descriptionView = findViewById<TextView>(R.id.task_description)
        val deadlineView = findViewById<TextView>(R.id.task_deadline)

        val extras = intent.extras
        val taskId = extras!!.getString("taskId")


        Thread {
            val task = taskId?.let { TaskCrudController().getTask(it) }

            runOnUiThread {
                titleView.text = task!!.label
                descriptionView.text = task.description

                val localDateTime = LocalDateTime.parse(task.deadline_dt)
                val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
                val output = formatter.format(localDateTime)

                deadlineView.text = resources.getString(R.string.till) +" $output"

            }
        }.start()





    }
}