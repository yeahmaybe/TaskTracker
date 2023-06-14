package app.mobile.tasktracker


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.mobile.features.task_crud.TaskCrudController
import app.mobile.tasktracker.database.tasks.TaskDTO
import app.mobile.tasktracker.network.model.ApiInterface
import app.mobile.tasktracker.plugins.configureDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


const val BASE_URL = "http://10.0.2.2:8082"
class MainActivity : ComponentActivity(), SwipeRefreshLayout.OnRefreshListener {
    lateinit var taskAdapter: TaskAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var recyclerview_tasks : RecyclerView
    lateinit var refreshLayout : SwipeRefreshLayout
    lateinit var createTaskButton: FloatingActionButton

    var newTaskListener: View.OnClickListener = object: View.OnClickListener {
        override fun onClick(v: View?) {
            val context: Context = baseContext
            val intent = Intent(context, NewTaskActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;

            // start your next activity
            ContextCompat.startActivity(context, intent, Bundle())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        val gfgPolicy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(gfgPolicy)

        super.onCreate(savedInstanceState)
        configureDatabase()

        setContentView(R.layout.activity_main)
        recyclerview_tasks = findViewById(R.id.recyclerview_tasks)
        refreshLayout = findViewById(R.id.refreshLayout)
        createTaskButton = findViewById(R.id.newTaskButton)

        recyclerview_tasks.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerview_tasks.layoutManager = linearLayoutManager

        refreshLayout.setOnRefreshListener(this)
        createTaskButton.setOnClickListener(newTaskListener)

        getAllTasks()
        getUncompletedTasks()
    }


    fun getAllTasks() {

        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiInterface::class.java)


        val retrofitAllTasks = retrofitBuilder.getAllTasks()
        retrofitAllTasks.enqueue(object: Callback<List<TaskDTO>?> {

            @RequiresApi(Build.VERSION_CODES.O)
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<List<TaskDTO>?>, response: Response<List<TaskDTO>?>) {
                val responseBody = response.body()!!

                insertToLocalDB(responseBody)

            }

            override fun onFailure(call: Call<List<TaskDTO>?>, t: Throwable) {
                Log.d("MainActivity", "OnFailure "+t.message)
            }
        })

    }

    @SuppressLint("NotifyDataSetChanged")
    fun getUncompletedTasks() {

        var tasks: List<TaskDTO>? = null
        tasks = TaskCrudController().getUncompletedTasks()!!


        taskAdapter = tasks.let { TaskAdapter(baseContext, it) }
        recyclerview_tasks.adapter = taskAdapter
        taskAdapter.notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertToLocalDB(taskList: List<TaskDTO>) {
        for(task in taskList) {
            if(TaskCrudController().getTask(task.id) == null)
                TaskCrudController().putTask(task)
            else
                TaskCrudController().updateTask(task)
        }
    }

    override fun onRefresh() {
        getAllTasks()
        refreshLayout.isRefreshing = true
        refreshLayout.postDelayed ({
            getAllTasks()
            getUncompletedTasks()
            refreshLayout.isRefreshing = false
        }, 100)
    }

}