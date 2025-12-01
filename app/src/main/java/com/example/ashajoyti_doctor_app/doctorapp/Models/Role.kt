package com.example.ashajoyti_doctor_app.model

/**
 * Normalized roles used across the app.
 * Use Role.fromName(...) to safely convert backend / UI strings to an enum.
 */
enum class Role {
    CHO,
    MO,
    CIVIL,
    EMERGENCY;

    companion object {
        fun fromName(name: String?): Role {
            if (name.isNullOrBlank()) return CHO
            return when (name.trim().uppercase()) {
                "MO", "MEDICAL_OFFICER", "MEDICAL OFFICER", "MEDICALOFFICER" -> MO
                "CIVIL", "CIVIL_HOSPITAL", "CIVIL HOSPITAL", "CD", "CIVILHOSPITAL" -> CIVIL
                "EMERGENCY", "EMERGENCY_DOCTOR", "EMERGENCY DOCTOR", "EMERGENCYDOCTOR" -> EMERGENCY
                else -> CHO
            }
        }
    }
}
