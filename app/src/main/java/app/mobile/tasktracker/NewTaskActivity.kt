package app.mobile.tasktracker

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import app.mobile.tasktracker.network.model.ApiInterface
import app.mobile.tasktracker.network.model.TaskCreateResponseRemote
import app.mobile.tasktracker.network.model.TaskCreateSendRemote
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class NewTaskActivity : AppCompatActivity() {

    lateinit var sendButton: FloatingActionButton
    lateinit var labelInput : EditText
    lateinit var descriptionInput : EditText
    lateinit var deadlineInput : DatePicker

    var sendInputsListener: View.OnClickListener = object: View.OnClickListener {
        override fun onClick(v: View?) {
            val label = labelInput.text.toString()
            val description = descriptionInput.text.toString()
            val deadline = getDeadlineFormatted()

            val taskRemoteModel = TaskCreateSendRemote(
                label = label,
                description = description,
                deadline_dt = deadline
            )

            val retrofitBuilder = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
                .create(ApiInterface::class.java)


            val call: Call<TaskCreateResponseRemote> = retrofitBuilder.createTask(taskRemoteModel)
            call.enqueue(object : Callback<TaskCreateResponseRemote?> {
                @SuppressLint("NotifyDataSetChanged")

                override fun onResponse(
                    call: Call<TaskCreateResponseRemote?>,
                    response: Response<TaskCreateResponseRemote?>
                ) {
                    this@NewTaskActivity.finish()
                }

                override fun onFailure(call: Call<TaskCreateResponseRemote?>, t: Throwable) {
                    Log.d("MainActivity", "OnFailure "+t.message)
                }
            })

        }
    }

    fun getDeadlineFormatted(): String? {

        // Получаем текущую дату из DatePicker
        val year = deadlineInput.year
        val month = deadlineInput.month
        val dayOfMonth = deadlineInput.dayOfMonth

        // Создаем объект Calendar и устанавливаем в него выбранную дату
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        calendar.set(Calendar.HOUR, 0)
        calendar.set(Calendar.MINUTE, 0)

        // Преобразуем дату в паттерн datetime
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task_activtiy)

        sendButton = findViewById(R.id.sendButton)
        labelInput = findViewById(R.id.task_input)
        descriptionInput = findViewById(R.id.description_input)
        deadlineInput = findViewById(R.id.datePicker)

        sendButton.setOnClickListener(sendInputsListener)
    }
}