# NetGuard Lite

Aplikasi Android sederhana untuk melihat kondisi koneksi jaringan yang **terdeteksi** oleh perangkat.

## Fitur (versi awal)
- **Network Information**: jenis koneksi, IP lokal, gateway, DNS (jika tersedia)
- **Connectivity Test**: tombol *Run Test* (HTTPS ke endpoint tetap) dengan status sukses/gagal/timeout + durasi
- **Security Checklist**: pemeriksaan otomatis (VPN, akses internet terverifikasi) dipisah jelas dari checklist manual
- **Scan History**: belum ada (rencana: Room)

## Tech stack
Kotlin, Jetpack Compose + Material 3, ConnectivityManager / NetworkCapabilities, coroutines, ViewModel + StateFlow.

## Cara menjalankan
1. Buat project **Empty Activity** (Compose) bernama NetGuardLite, package `com.example.netguardlite`.
2. Salin semua file di `app/src/main/java/...` dari template ini (timpa `MainActivity.kt`).
3. Tambahkan permission di `AndroidManifest.xml` (lihat `AndroidManifest-snippet.xml`).
4. Tambahkan dependency di `app/build.gradle.kts`:
   ```kotlin
   implementation("androidx.compose.material:material-icons-extended")
   implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
   implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
   ```
   (sesuaikan versi lifecycle dengan yang dipakai project; Compose BOM sudah mengatur versi material icons)
5. Sync Gradle, sambungkan HP, Run.

## Batasan
- Bukan audit keamanan penuh dan bukan pengganti pemeriksaan router atau penetration test.
- Beberapa informasi (mis. jenis enkripsi Wi-Fi) dibatasi versi Android dan hak akses, jadi dibuat sebagai checklist manual.
- Hasil hanya mencerminkan kondisi saat pemeriksaan dijalankan.

## Pertimbangan keamanan
- Permission minimal (`INTERNET`, `ACCESS_NETWORK_STATE`)
- Tidak mengumpulkan atau mengirim kredensial
- Pemeriksaan otomatis dibedakan dari checklist manual

## Roadmap
Hari 1-2 project + dashboard | 3-4 network info | 5-6 connectivity test | 7-8 checklist (simpan dengan DataStore) | 9-10 testing, screenshot, README, push GitHub.
Pengembangan lanjutan: Scan History dengan Room.

## Testing evidence
Tambahkan screenshot di `docs/screenshots/` untuk kondisi: Wi-Fi, data seluler, offline.
