package com.vitaltrace.app.feature.notifications.presentation

import androidx.annotation.StringRes
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.patient.domain.model.PatientNotification

data class NotificationPresentation(
    val titleResource: Int?,
    val messageResource: Int?
)

object NotificationPresentationMapper {
    fun map(notification: PatientNotification): NotificationPresentation {
        val type = notification.type?.uppercase()
        return NotificationPresentation(
            titleResource = when (type) {
                "APPOINTMENT_REMINDER" -> R.string.notification_title_appointment_reminder
                "HIGH_GLUCOSE_ALERT" -> R.string.notification_title_high_glucose_alert
                "MEASUREMENT_REVIEWED" -> R.string.notification_title_measurement_reviewed
                "TREATMENT_UPDATED" -> R.string.notification_title_treatment_updated
                else -> null
            },
            messageResource = when (notification.message?.trim()) {
                "Your appointment to review glucose measurements is scheduled in three days." ->
                    R.string.notification_message_appointment_glucose_review
                "A glucose value of 212 mg/dL generated a high-severity alert." ->
                    R.string.notification_message_high_glucose_alert
                "Upcoming medical appointment" ->
                    R.string.notification_message_upcoming_appointment
                "High glucose alert" ->
                    R.string.notification_message_high_glucose
                else -> null
            }
        )
    }

    @StringRes
    fun feedbackResource(message: String): Int? = when {
        message.startsWith("No pudimos marcar") ->
            R.string.notifications_feedback_mark_read_error
        message.startsWith("Notificaciones marcadas") ->
            R.string.notifications_feedback_mark_all_success
        message.startsWith("No pudimos actualizar") ->
            R.string.notifications_feedback_update_error
        message.startsWith("No pudimos cargar") ->
            R.string.notifications_feedback_load_more_error
        else -> null
    }
}


