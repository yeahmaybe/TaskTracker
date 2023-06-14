package app.mobile.tasktracker.database.tasks

import android.os.Build
import androidx.annotation.RequiresApi
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object Tasks: Table("tasks") {
    private val id = Tasks.varchar("id", 50)
    private val label = Tasks.varchar("label", 100)
    private val description = Tasks.text("description")
    private val completed = Tasks.bool("completed")
    private val deadline_dt = Tasks.datetime("deadline_dt")

    @RequiresApi(Build.VERSION_CODES.O)
    fun insert(taskDTO: TaskDTO) {
        transaction {
            Tasks.insert {
                it[id] = taskDTO.id
                it[label] = taskDTO.label
                it[description] = taskDTO.description?: ""
                it[completed] = taskDTO.completed
                it[deadline_dt] = LocalDateTime.parse(taskDTO.deadline_dt)?: LocalDateTime.MAX

            }
        }
    }

    fun delete(id: String) {
        transaction {
            Tasks.deleteWhere{
                Tasks.id.eq(id)
            }
        }
    }

    fun fetchTaskById(id: String): TaskDTO? {
        return try {
            transaction {
                val taskModel = Tasks.select{ Tasks.id.eq(id) }.first()
                TaskDTO(
                    id = taskModel[Tasks.id],
                    label = taskModel[label],
                    description = taskModel[description],
                    completed = taskModel[completed],
                    deadline_dt = taskModel[deadline_dt].toString()
                )
            }
        } catch (e: NoSuchElementException) {
            null
        }
    }

    fun fetchUncompletedTasks(): List<TaskDTO>? {
        return try {
            transaction {
                val tasks = Tasks.select{ completed.eq(false) }.toList()
                    .map{
                        TaskDTO(
                            id = it[Tasks.id],
                            label = it[label],
                            description = it[description],
                            completed = it[completed],
                            deadline_dt = it[deadline_dt].toString()
                        )
                    }
                tasks
            }
        } catch (e: NoSuchElementException) {
            null
        }
    }

    fun fetchAll(): List<TaskDTO>? {
        return try {
            transaction {
                val tasks = Tasks.selectAll().map{
                        TaskDTO(
                            id = it[Tasks.id],
                            label = it[label],
                            description = it[description],
                            completed = it[completed],
                            deadline_dt = it[deadline_dt].toString()
                        )
                    }
                tasks
            }
        } catch (e: NoSuchElementException) {
            null
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun updateTask(task: TaskDTO) {
        transaction {
            Tasks.update({ Tasks.id.eq(task.id) }) {
                it[id] = task.id
                it[label] = task.label
                it[description] = task.description ?: ""
                it[completed] = task.completed
                it[deadline_dt] = LocalDateTime.parse(task.deadline_dt) ?: LocalDateTime.MAX
            }
        }
    }
}