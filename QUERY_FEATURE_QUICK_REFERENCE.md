# Query Management Feature - Quick Reference

## 📱 Feature Overview

Doctors can now view all patient queries assigned to them through a dedicated "Patient Queries" section on the dashboard. Each query can be opened to see full details and audio (if available), then the doctor can prescribe directly.

---

## 🗂️ File Structure

```
app/src/main/
├── java/com/example/ashajoyti_doctor_app/
│   ├── doctorapp/
│   │   ├── QueriesActivity.kt (Main list view)
│   │   ├── QueryDetailActivity.kt (Detail view)
│   │   ├── QueryAdapter.kt (List adapter)
│   │   └── CHODashboardActivity.kt (Updated - added query card)
│   └── doctorapp/Models/
│       ├── QueryModel.kt (Data model)
│       └── DoctorQueriesResponse.kt (API response)
├── res/layout/
│   ├── activity_queries.xml (List screen)
│   ├── activity_query_detail.xml (Detail screen)
│   ├── item_query_card.xml (Card item)
│   └── activity_dashboard.xml (Updated - added query card)
└── AndroidManifest.xml (Updated - registered activities)
```

---

## 🔌 API Endpoint

```
GET /doctor/:doc_id
Authorization: Bearer {token}
```

**Returns**: Doctor info + list of assigned queries

---

## 📊 Data Fields

| Field | Type | Purpose |
|-------|------|---------|
| query_id | Int | Unique identifier |
| patient_id | Int | Link to patient |
| text | String | Full query text |
| voice_url | String? | Audio recording URL |
| disease | String? | Suspected condition |
| query_status | String | pending/in_progress/resolved |
| done_or_not | Boolean | Completion status |

---

## 🎯 User Journey

```
Dashboard
    ↓
[Click "Patient Queries" Card]
    ↓
QueriesActivity (List View)
    ↓
[Click "Open Query" Button]
    ↓
QueryDetailActivity (Detail View)
    ↓
[Click "Consult/Prescribe"]
    ↓
PrescriptionActivity (with patient_id & query_id pre-filled)
```

---

## 💾 SharedPreferences Keys Used

- `doc_id` - Doctor ID from login
- `auth_token` - Authentication token for API calls

---

## 🎨 UI Elements

### Query Card Shows:
- ✅ Patient name
- ✅ Patient ID
- ✅ Phone number
- ✅ Problem text (first 100 chars)
- ✅ Play button (if voice_url exists)
- ✅ "Open Query" button

### Query Detail View Shows:
- ✅ Full query text
- ✅ Disease/condition
- ✅ Query status
- ✅ Play voice button
- ✅ "Consult/Prescribe" button

---

## 🚀 Implementation Checklist

- [x] Create QueryModel data class
- [x] Create DoctorQueriesResponse wrapper
- [x] Add API endpoint to ApiService
- [x] Create QueryAdapter for RecyclerView
- [x] Create QueriesActivity (list view)
- [x] Create QueryDetailActivity (detail view)
- [x] Create UI layouts (3 files)
- [x] Add "Queries" card to dashboard
- [x] Add click listeners
- [x] Register activities in manifest
- [ ] Test with backend API
- [ ] Implement audio player (TODO)
- [ ] Add patient details fetching enhancement (TODO)

---

## 🔍 Testing Queries

### Verify Query List Loads:
```kotlin
// Check logs for API response
Log.d("QueriesActivity", "Loaded ${queries.size} queries")
```

### Check Network Calls:
- Enable Network Inspector in Android Studio
- Monitor GET /doctor/:doc_id requests
- Verify Authorization header is present

### Test Error Scenarios:
- Disconnect WiFi before opening queries
- Use invalid doc_id
- Test with expired token

---

## 📝 Key Implementation Details

### Adapter Pattern Used:
```kotlin
QueryAdapter(
    items: List<QueryModel>,
    patientNameMap: Map<Int, String>,
    patientPhoneMap: Map<Int, String>,
    listener: OnQueryActionListener
)
```

### API Call:
```kotlin
val call = apiService.getDoctorQueries("Bearer $token", docId)
call.enqueue(object : Callback<DoctorQueriesResponse> { ... })
```

### Navigation:
```kotlin
val intent = Intent(this, QueryDetailActivity::class.java)
intent.putExtra("query_id", query.query_id)
intent.putExtra("patient_id", query.patient_id)
// ... pass other fields
startActivity(intent)
```

---

## 🐛 Common Issues & Fixes

| Issue | Cause | Fix |
|-------|-------|-----|
| Queries don't load | Token expired | Re-login |
| Empty list | No queries assigned | Check backend data |
| Patient name shows as ID | Patient map empty | Fetch patient details |
| Voice button not showing | voice_url is null | Check backend response |
| App crashes on detail open | Missing extras | Verify intent.putExtra calls |

---

## 📚 Documentation Files

- `QUERY_FEATURE_IMPLEMENTATION.md` - Detailed implementation guide
- `QUERY_FEATURE_INTEGRATION_GUIDE.md` - Backend integration steps
- `QUERY_FEATURE_QUICK_REFERENCE.md` - This file!

---

## 🔄 Future Enhancements

1. **Audio Playback** - Implement MediaPlayer for voice_url
2. **Patient Details** - Fetch and display actual patient info
3. **Filtering** - Filter by status, date, or disease
4. **Search** - Search queries by patient name or text
5. **Pagination** - Load queries in batches for large datasets
6. **Real-time Updates** - WebSocket for new query notifications
7. **Offline Support** - Cache queries locally
8. **Query Analytics** - Dashboard of query statistics

---

## 📞 Support

For issues or questions:
1. Check logs with `adb logcat`
2. Verify API response format
3. Ensure all dependencies are up to date
4. Review AndroidManifest.xml for activity registration
5. Check SharedPreferences for token/doc_id

---

**Feature Status**: ✅ Ready for Testing
**Last Updated**: December 9, 2025
**Version**: 1.0
