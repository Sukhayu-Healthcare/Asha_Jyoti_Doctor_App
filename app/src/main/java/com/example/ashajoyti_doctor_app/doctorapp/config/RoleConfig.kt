package com.example.ashajoyti_doctor_app.config

import com.example.ashajoyti_doctor_app.model.Role

data class RoleConfig(
    val role: Role,
    val dashboardTitle: String,
    val designationLabel: String,
    val patientQueueLabel: String,
    val quickConsultLabel: String,
    val showRedirectCard: Boolean,
    val canPrescribe: Boolean,
    val canRedirect: Boolean,
    val canAdmit: Boolean
)

object RoleConfigFactory {
    fun get(role: Role): RoleConfig {
        return when (role) {
            Role.CHO -> RoleConfig(
                role = role,
                dashboardTitle = "CHO Dashboard",
                designationLabel = "Chief Health Officer",
                patientQueueLabel = "Patient Queue",
                quickConsultLabel = "Quick Consultation",
                showRedirectCard = true,
                canPrescribe = false,
                canRedirect = true,
                canAdmit = false
            )
            Role.MO -> RoleConfig(
                role = role,
                dashboardTitle = "MO Dashboard",
                designationLabel = "Medical Officer",
                patientQueueLabel = "OPD Queue",
                quickConsultLabel = "Quick Consultation",
                showRedirectCard = false,
                canPrescribe = true,
                canRedirect = false,
                canAdmit = false
            )
            Role.CIVIL -> RoleConfig(
                role = role,
                dashboardTitle = "Civil Hospital",
                designationLabel = "Civil Hospital Doctor",
                patientQueueLabel = "Hospital Queue",
                quickConsultLabel = "Quick Consultation",
                showRedirectCard = false,
                canPrescribe = true,
                canRedirect = false,
                canAdmit = true
            )
            Role.EMERGENCY -> RoleConfig(
                role = role,
                dashboardTitle = "Emergency Doctor",
                designationLabel = "Emergency Doctor",
                patientQueueLabel = "Emergency Queue",
                quickConsultLabel = "Emergency Triage",
                showRedirectCard = false,
                canPrescribe = true,
                canRedirect = false,
                canAdmit = true
            )
        }
    }
}
