package com.vitaltrace.app.feature.relatives.presentation

import com.vitaltrace.app.feature.patient.domain.model.PatientRelative
import javax.inject.Inject

class RelativesMapper @Inject constructor() {
    fun map(relative: PatientRelative) = RelativeUiModel(
        id = relative.id,
        fullName = relative.relative?.person?.fullName,
        relationship = relative.relationship,
        phone = relative.relative?.person?.phone,
        status = relative.status,
        isAuthorized = relative.status == "ACTIVE"
    )
}
