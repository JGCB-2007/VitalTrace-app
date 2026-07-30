package com.vitaltrace.app.feature.relatives.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.relatives.presentation.RelativeAction

@Composable
fun RelativeConfirmationDialog(
    action: RelativeAction,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val isAuthorize = action == RelativeAction.AUTHORIZE
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(
                    if (isAuthorize) R.string.relatives_authorize_title
                    else R.string.relatives_revoke_title
                )
            )
        },
        text = {
            Text(
                stringResource(
                    if (isAuthorize) R.string.relatives_authorize_confirmation
                    else R.string.relatives_revoke_confirmation
                )
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    stringResource(
                        if (isAuthorize) R.string.relatives_authorize
                        else R.string.relatives_revoke
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.relatives_cancel)) }
        }
    )
}
