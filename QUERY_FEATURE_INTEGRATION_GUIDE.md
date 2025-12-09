# Query Management Feature - Integration Guide

## Backend Implementation Required

The Android app is ready to consume the following API endpoint. Ensure your backend implements this:

### Endpoint: GET /doctor/:doc_id

**Base URL**: `https://sukhayu-backend.onrender.com/api/v1/`

**Full URL**: `https://sukhayu-backend.onrender.com/api/v1/doctor/1`

**Request Headers**:
```
Authorization: Bearer {auth_token}
Content-Type: application/json
```

**Response Example (200 OK)**:
```json
{
  "message": "Query assigned successfully",
  "doctor": {
    "doc_id": 1,
    "doc_name": "Dr. John Doe",
    "doc_role": "CHO",
    "doc_phone": 9876543210,
    "doc_speciality": "General Physician",
    "doc_status": "active",
    "hospital_address": "123 Main Street",
    "hospital_village": "Springfield"
  },
  "query": [
    {
      "query_id": 101,
      "patient_id": 5,
      "asha_id": 2,
      "text": "Patient complains of persistent headache and high fever for 3 days",
      "voice_url": "https://example.com/audio/query_101.mp3",
      "disease": "Viral Fever",
      "doc": "Dr. John Doe",
      "doc_id": 1,
      "query_status": "pending",
      "done_or_not": false
    },
    {
      "query_id": 102,
      "patient_id": 8,
      "asha_id": 3,
      "text": "Patient experiencing severe abdominal pain",
      "voice_url": null,
      "disease": "Gastroenteritis",
      "doc": "Dr. John Doe",
      "doc_id": 1,
      "query_status": "in_progress",
      "done_or_not": false
    },
    {
      "query_id": 103,
      "patient_id": 12,
      "asha_id": 1,
      "text": "Patient reports improvement with prescribed medication",
      "voice_url": "https://example.com/audio/query_103.mp3",
      "disease": "Common Cold",
      "doc": "Dr. John Doe",
      "doc_id": 1,
      "query_status": "resolved",
      "done_or_not": true
    }
  ]
}
```

---

## Data Model Documentation

### QueryModel Fields

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| query_id | Integer | Unique query identifier | 101 |
| patient_id | Integer | ID of patient submitting query | 5 |
| asha_id | Integer | ASHA worker ID | 2 |
| text | String | Full query text/description | "Patient complains of..." |
| voice_url | String (nullable) | URL to voice recording | "https://example.com/audio/query_101.mp3" |
| disease | String (nullable) | Suspected disease/condition | "Viral Fever" |
| doc | String (nullable) | Doctor name | "Dr. John Doe" |
| doc_id | Integer | Doctor ID | 1 |
| query_status | String | Current status | "pending", "in_progress", "resolved" |
| done_or_not | Boolean | Completion flag | false |

---

## Android App Flow

### 1. User Login
- Doctor logs in through CHOLoginActivity
- `doc_id` and `auth_token` are saved to SharedPreferences

### 2. Dashboard
- QueriesActivity is accessible via "Patient Queries" card on dashboard
- Located after "Past Consultations" card

### 3. Query List
- Fetches: `GET /doctor/{doc_id}`
- Displays vertical list of query cards
- Each card shows:
  - Patient name (fetch from patient details or map)
  - Patient ID
  - Phone number
  - Truncated problem text (100 characters)
  - Play button (if voice_url exists)
  - "Open Query" button

### 4. Query Detail
- Click "Open Query" opens QueryDetailActivity
- Shows full query text, disease, and status
- Play button for voice if available
- "Consult / Prescribe" button navigates to PrescriptionActivity

### 5. Prescription
- Passes query_id and patient_id to PrescriptionActivity
- Form auto-fills with patient data

---

## Testing the Feature

### Manual Testing Steps

1. **Build and Run**
   ```bash
   ./gradlew clean assembleDebug
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

2. **Login as Doctor**
   - Navigate to role selection
   - Select "Community Health Officer"
   - Enter credentials
   - Verify successful login

3. **Test Queries Feature**
   - On dashboard, click "Patient Queries" card
   - Verify queries load from API
   - Verify patient information displays correctly
   - Click "Open Query" to view details
   - Click "Consult / Prescribe" to navigate to prescription

4. **Test Error Handling**
   - Disconnect network and try loading queries
   - Verify error message displays
   - Reconnect and retry

---

## Required Android Dependencies

The app uses:
- Retrofit2 (HTTP client)
- GSON (JSON serialization)
- RecyclerView (list display)
- Material Components (UI elements)
- ConstraintLayout (layouts)

All dependencies should be already defined in `build.gradle.kts`

---

## Troubleshooting

### Issue: Queries not loading
**Solutions**:
- Check auth_token is valid and not expired
- Verify doctor_id is correct in SharedPreferences
- Check network connectivity
- Verify backend endpoint is accessible
- Check API response format matches expected structure

### Issue: Patient details not showing
**Current Behavior**: Patient names show as "Patient {patient_id}"

**Enhancement**: To show actual patient names:
1. Modify QueriesActivity to fetch patient details for each query
2. Build patient name/phone maps from patient API responses
3. Or: Have backend return full patient details in query response

### Issue: Voice playback not working
**Status**: Audio player UI is prepared but playback implementation is TODO

**To Implement**:
```kotlin
// In QueryDetailActivity.kt or new QueryPlayerActivity.kt
private fun playAudio(voiceUrl: String) {
    val mediaPlayer = MediaPlayer()
    mediaPlayer.setDataSource(voiceUrl)
    mediaPlayer.prepare()
    mediaPlayer.start()
}
```

---

## Security Considerations

1. **Token Management**
   - Token stored in SharedPreferences (current approach)
   - Consider Android Keystore for sensitive data in production
   - Implement token refresh mechanism if needed

2. **API Security**
   - Always use HTTPS (verified in ApiClient base URL)
   - Validate user authorization on backend
   - Implement rate limiting on backend

3. **Data Privacy**
   - Consider encrypting patient data in transit
   - Implement GDPR compliance features
   - Add audit logging for data access

---

## Performance Notes

- Query list uses RecyclerView for efficient scrolling
- Consider pagination for large datasets
- Cache patient details locally to reduce API calls
- Implement search/filter for better UX

---

## Next Steps

1. Implement the backend endpoint if not already done
2. Test API response with sample data
3. Run Android app build: `./gradlew clean assembleDebug`
4. Test on emulator or device
5. Implement audio player for voice_url support
6. Add pagination if needed
7. Consider adding offline caching capability
