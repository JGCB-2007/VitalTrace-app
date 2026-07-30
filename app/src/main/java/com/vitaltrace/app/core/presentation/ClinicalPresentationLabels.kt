package com.vitaltrace.app.core.presentation

fun localizedStatusLabel(value: String): String = when (value.uppercase()) {
    "CONFIRMED" -> "Confirmada"
    "PENDING" -> "Pendiente"
    "REVIEWED" -> "Revisada"
    "ACTIVE" -> "Activo"
    else -> value
}

fun localizedMeasurementTypeLabel(value: String): String = when (value.trim().lowercase()) {
    "body weight" -> "Peso corporal"
    "blood glucose" -> "Glucosa"
    "blood pressure" -> "Presión arterial"
    "heart rate" -> "Frecuencia cardíaca"
    else -> value
}
