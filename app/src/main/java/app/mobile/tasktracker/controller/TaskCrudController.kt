package app.mobile.features.task_crud

import android.os.Build
import androidx.annotation.RequiresApi
import app.mobile.tasktracker.database.tasks.TaskDTO
import app.mobile.tasktracker.database.tasks.Tasks
import io.ktor.http.*
import java.time.LocalDateTime
import java.util.*

class TaskCrudController {

    @RequiresApi(Build.VERSION_CODES.O)
    fun createTask(
        label: String,
        description: String?
    ) {
        val taskId =  UUID.randomUUID().toString()
        Tasks.insert(
            TaskDTO(
                id = taskId,
                label = label,
                description = description,
                completed = false,
                deadline_dt = ""
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun putTask(task: TaskDTO) {
        Tasks.insert(
            TaskDTO(
                id = task.id,
                label = task.label,
                description = task.description,
                completed = task.completed,
                deadline_dt = task.deadline_dt
            )
        )
    }

    fun deleteTask(id: String) {
        val task = Tasks.fetchTaskById(id)
        Tasks.delete(id)
    }

    fun getTask(id: String): TaskDTO? {
        return Tasks.fetchTaskById(id)
    }

    fun getAllTasks(): List<TaskDTO>? {
        val tasks = Tasks.fetchAll()
        return tasks
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateTask(taskDTO: TaskDTO) {
        Tasks.updateTask(taskDTO)
    }

    fun getUncompletedTasks(): List<TaskDTO>? {
        return Tasks.fetchUncompletedTasks()
    }


}