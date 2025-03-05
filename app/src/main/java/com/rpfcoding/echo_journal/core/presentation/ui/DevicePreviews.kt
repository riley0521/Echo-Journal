package com.rpfcoding.echo_journal.core.presentation.ui

import androidx.compose.ui.tooling.preview.Preview

private const val DEVICE_PREVIEWS_GROUP = "DEVICE_PREVIEWS_GROUP"

@Preview(
    group = DEVICE_PREVIEWS_GROUP,
    device = "spec:width=411dp,height=891dp"
)
@Preview(
    group = DEVICE_PREVIEWS_GROUP,
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
annotation class DevicePreviews