package com.yatrisetu.app.domain.model

data class OfficialContact(
    val serviceName: String,
    val phoneNumber: String,
    val description: String,
    val jurisdiction: String
)

data class SosAlertResult(
    val incidentId: String,
    val status: String,
    val dispatchUnit: String,
    val yatriMitraNotified: Boolean,
    val officialHelplines: List<String>
)
