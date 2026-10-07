package com.vitaltrace.app.feature.timeline.presentation

enum class TimelineEventType { ALL, APPOINTMENT, MEASUREMENT, TREATMENT }

data class TimelineEventUiModel(
    val id: String,
    val type: TimelineEventType,
    val title: String,
    val subtitle: String,
    val date: String,
    val sortValue: String,
    val status: String
)

data class HealthTimelineUiState(
    val loading: Boolean = true,
    val refreshing: Boolean = false,
    val events: List<TimelineEventUiModel> = emptyList(),
    val selectedType: TimelineEventType = TimelineEventType.ALL,
    val query: String = "",
    val error: String? = null
) {
    val visibleEvents: List<TimelineEventUiModel>
        get() = events.filter { event ->
            (selectedType == TimelineEventType.ALL || event.type == selectedType) &&
                (query.isBlank() || listOf(event.title, event.subtitle, event.status, event.date)
                    .any { it.contains(query.trim(), ignoreCase = true) })
        }
}
