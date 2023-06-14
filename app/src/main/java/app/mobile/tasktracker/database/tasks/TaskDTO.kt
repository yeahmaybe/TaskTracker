package app.mobile.tasktracker.database.tasks

import java.time.LocalDate
import java.time.LocalDateTime

class TaskDTO (
    val id: String,
    val label: String,
    val description: String?,
    val completed: Boolean,
    val deadline_dt: String?
)