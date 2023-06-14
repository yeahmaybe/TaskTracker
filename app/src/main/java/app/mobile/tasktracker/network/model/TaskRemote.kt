package app.mobile.tasktracker.network.model

import kotlinx.serialization.Serializable

@Serializable
data class TaskCreateSendRemote(
    val label :String,
    val description: String?,
    val deadline_dt: String?
)

@Serializable
data class TaskCreateResponseRemote(
    val id: String
)