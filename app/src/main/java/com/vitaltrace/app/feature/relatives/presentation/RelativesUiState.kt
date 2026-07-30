package com.vitaltrace.app.feature.relatives.presentation

data class RelativesUiState(
    val content: RelativesContentState = RelativesContentState.Loading,
    val confirmation: RelativeConfirmation? = null,
    val actionInProgressId: Long? = null,
    val message: String? = null
)

sealed interface RelativesContentState {
    data object Loading : RelativesContentState
    data class Success(val relatives: List<RelativeUiModel>) : RelativesContentState
    data class Error(val message: String) : RelativesContentState
}

data class RelativeUiModel(
    val id: Long,
    val fullName: String?,
    val relationship: String,
    val phone: String?,
    val status: String,
    val isAuthorized: Boolean
)

data class RelativeConfirmation(
    val relativeId: Long,
    val action: RelativeAction
)

enum class RelativeAction {
    AUTHORIZE,
    REVOKE
}
