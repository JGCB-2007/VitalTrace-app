package com.vitaltrace.app.feature.treatments.presentation

import com.vitaltrace.app.feature.patient.domain.model.Page
import com.vitaltrace.app.feature.patient.domain.model.Treatment
import javax.inject.Inject

class TreatmentsMapper @Inject constructor() {
    fun map(page: Page<Treatment>) = map(page.items).copy(
        currentPage = page.meta.currentPage,
        lastPage = page.meta.lastPage
    )

    fun map(items: List<Treatment>) = TreatmentsContentUiModel(
        treatments = items.map { treatment ->
            TreatmentUiModel(
                id = treatment.id,
                diagnosisDescription = treatment.diagnosis?.description,
                diagnosisCode = treatment.diagnosis?.cieCode,
                indications = treatment.indications,
                startDate = treatment.startDate,
                endDate = treatment.endDate,
                status = TreatmentStatus.fromApiValue(treatment.status),
                prescriberName = treatment.prescriber?.fullName,
                professionalType = treatment.prescriber?.professionalType,
                specialtyName = treatment.prescriber?.specialty?.name
            )
        },
        currentPage = 1,
        lastPage = 1
    )
}
