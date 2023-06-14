package app.mobile.tasktracker.network.model

import app.mobile.tasktracker.database.tasks.TaskDTO
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @GET("/task/all")
    fun getAllTasks(): Call<List<TaskDTO>>

    @GET("/task/uncompleted")
    fun getUncompletedTasks(): Call<List<TaskDTO>>

    @PUT("/task/complete/{id}")
    fun completeTask(@Path("id") id: String): Call<TaskDTO>

    @POST("/task/create")
    fun createTask(@Body taskCreateSendRemote: TaskCreateSendRemote):Call<TaskCreateResponseRemote>
}