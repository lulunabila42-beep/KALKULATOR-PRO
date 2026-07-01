# AGENT.md

Dokumen ini menjelaskan struktur arsitektur, prinsip desain, dan persyaratan rekayasa dari **KalkulatorPro**, sebuah aplikasi kalkulator ilmiah dan teknis dengan ketepatan tinggi (high-fidelity) bergaya NeoBrutalisme yang dibangun menggunakan Jetpack Compose.

---

## 🏛️ Arsitektur Aplikasi

Aplikasi ini dibangun menggunakan pola arsitektur **MVVM (Model-View-ViewModel)**. Aplikasi ini sepenuhnya mandiri (self-contained), luring-pertama (offline-first), dan tidak memiliki dependensi database eksternal apa pun.

### 1. Komponen Utama

*   **`MainActivity.kt` (Gerbang & Kontroler Utama)**
    *   Berperan sebagai entrypoint utama aplikasi yang menyatukan tata letak responsif dual-orientasi menggunakan Jetpack Compose dan Material Design 3.
    *   Mendeteksi orientasi perangkat secara dinamis (`Configuration.ORIENTATION_LANDSCAPE` vs `PORTRAIT`) dan merender `PortraitLayout` atau `LandscapeLayout`.
    *   Mengatur modal-modal global penting seperti dialog konfigurasi API Key Gemini kustom dan petunjuk sambutan pertama kali (*First Run Prompt*).
    *   Mengelola animasi idle taktil global berbasis state `LocalIdleState`, `LocalIdleBobbingOffset`, `LocalIdleWiggleRotation`, dan `LocalIdleShadowMultiplier` yang menyuntikkan efek gerak retro ke seluruh elemen UI saat pengguna sedang mendiamkan aplikasi.
    *   Menerapkan render layar penuh (*edge-to-edge*) menggunakan `enableEdgeToEdge()` dan penanganan padding aman `safeDrawing`.

*   **Arsitektur Layar UI Terbagi (Modularized Screens - `com.example.ui.screens.*`)**
    *   **`calc/CalcTab.kt`**: Mengimplementasikan antarmuka kalkulator standar dan ilmiah dengan keypad 5 kolom di mode potret dan ekspansi 8 kolom di mode lanskap, dilengkapi drawer riwayat interaktif.
    *   **`base/BaseTab.kt`**: Menyediakan panel peninjau nilai biner, oktal, desimal, dan heksadesimal dengan tombol input dinamis yang menonaktifkan karakter ilegal berdasarkan jenis basis terpilih.
    *   **`solver/SolverTab.kt`**: Memisahkan penyelesaian matematika kuadrat dan sistem linear 2x2 ke dalam panel visual dengan rincian langkah determinan Cramer atau diskriminan yang terorganisir.
    *   **`formulas/FormulasTab.kt`**: Mengelompokkan 4 formulir rumus interaktif (Hukum Ohm, Geometri Lingkaran, Teorema Pythagoras, dan BMI) secara modular dengan validasi input yang aman.
    *   **`ai/AiTab.kt`**: Berperan sebagai wadah interaksi Asisten MTK AI Gaul, menyajikan visual status kesiapan API Key, kolom input soal cerita, dan layar panel rincian jawaban AI.

*   **`com.example.ui.components.NeoBrutalWidgets.kt` (Perpustakaan Widget Neo-Brutalisme)**
    *   Membangun tombol kustom `NeoBrutalButton`, kartu `NeoBrutalCard`, kolom teks `NeoTextField`, tombol tab `NeoTabButton`, dan elemen masukan teks masif `NeoWordProblemField` bergaya brutalist.
    *   Meniru pergerakan bayangan CSS dengan pergeseran translasi taktil `:active` (`offset(3.dp, 3.dp)`) untuk menghasilkan respons fisik kartu ketika ditekan.
    *   Mendukung animasi *idle pulse* dinamis terpusat dari `MainActivity` agar widget bergoyang atau melayang gemas saat tidak ada sentuhan.

*   **`CalculatorViewModel.kt` (Pemegang Status / State Holder Terpusat)**
    *   Mengelola status aktif untuk kelima tab utama: Kalkulator Standar/Ilmiah, Konverter Basis Pemrogram, Penyelesai Persamaan (Equation Solver), Lembar Rumus (Formulas Sheet), dan Asisten AI.
    *   Mengintegrasikan penyimpanan memori kalkulasi (MC, MR, M+, M-), riwayat pencatatan ekspresi secara langsung, mode sudut derajat/radian, buffer input koefisien, dan penyimpanan lokal key via `SharedPreferences`.

*   **`CalculatorParser.kt` (Logika Bisnis & Matematika)**
    *   Sebuah parser matematika kustom (recursive descent parser) yang memproses ekspresi matematika kompleks tanpa pustaka evaluasi matematika pihak ketiga.
    *   Mendukung:
        *   **Aritmatika Standar:** Penjumlahan, pengurangan, perkalian, pembagian, modulo (`mod`), dan urutan prioritas tanda kurung.
        *   **Fungsi Ilmiah Tingkat Lanjut:** Trigonometri (`sin`, `cos`, `tan`), invers trigonometri (`asin`, `acos`, `atan`), hiperbolik (`sinh`, `cosh`, `tanh`), logaritma (`ln`, `log`, `log2`), eksponen, akar kuadrat, dan akar pangkat tiga.
        *   **Fungsi Kombinatorika & Teknis:** Kombinasi (`nCr`), Permutasi (`nPr`), Pembagi Persekutuan Terbesar (`gcd`), Kelipatan Persekutuan Terkecil (`lcm`), dan Faktorial (`!`).

*   **`GeminiService.kt` & Integrasi AI**
    *   Menangani panggilan HTTP REST langsung ke API Google AI Studio (Gemini 3.5 Flash) dengan aman.
    *   Menyediakan prompt sistem terstruktur agar respons Gemini selalu berlokalisasi Bahasa Indonesia Gaul dengan langkah matematika yang asyik, gampang dimengerti, dan bebas kebingungan.
    *   Memvalidasi orisinalitas API Key yang diinput pengguna sebelum disimpan permanen ke memori lokal perangkat.

---

## 🎨 Bahasa Desain: NeoBrutalisme

Tema visual dan tata letak aplikasi ini terinspirasi langsung oleh **NeoBrutalisme**, membawa definisi CSS dari `KALKULATORPRO.html` secara tepat ke dalam Jetpack Compose.

### 🖌️ Aturan Gaya Desain

1.  **Batas Kontras Tinggi (High-Contrast Borders):**
    *   Setiap komponen interaktif (tombol, kartu, kolom teks) dibingkai dengan garis batas hitam pekat (`Color.Black`) setebal `2.5.dp` hingga `3.dp`.
    *   Sudut-sudut komponen dirancang dengan radius kelengkungan sebesar `12.dp` atau `16.dp` untuk memberikan tampilan kartu yang kokoh namun modern.

2.  **Bayangan Solid (Solid Drop Shadows):**
    *   Alih-alih menggunakan efek bayangan gradasi yang halus atau buram, bayangan digambar menggunakan pergeseran hitam solid yang tajam (`offset(x = 5.dp, y = 5.dp)` hingga `6.dp`).
    *   Bayangan ini dirender secara programatis di dalam pengubah (modifier) `drawBehind` kustom dari masing-masing komponen.

3.  **Umpan Balik Visual Interaktif (Tactile Translation):**
    *   Ketika tombol interaktif diklik atau ditekan, ia menerapkan animasi taktil yang meniru perilaku translasi CSS `:active`:
        *   Tombol akan bergeser ke bawah dan ke kanan sejauh `3.dp` (`offset(x = 3.dp, y = 3.dp)`).
        *   Secara bersamaan, jarak offset bayangan akan merata dari `5.dp` menjadi `1.5.dp`.
    *   Pergeseran ini menghasilkan efek fisik "kartu yang ditekan" yang sangat meyakinkan dan taktil.

4.  **Palet Warna yang Khas:**
    *   **Latar Belakang Kanvas:** Kuning gading hangat (`#FFFBE6`) untuk kenyamanan visual yang maksimal.
    *   **Tombol Tindakan:** Oranye cerah (`#FFFF5C00`) dan merah muda Neo (`#FFFF5C8A`).
    *   **Operator:** Sian pastel (`#FF3DD5F3`).
    *   **Pilihan Ilmiah:** Ungu lembut (`#FF9E77F1`).
    *   **Angka:** Putih bersih (`#FFFFFF`) dengan teks hitam tebal.
    *   **Sama Dengan / Tindakan Utama:** Hitam pekat (`#000000`) dengan teks putih berlawanan warna.

5.  **Penggunaan Ikon SVG NeoBrutalisme Mandatori (Wajib):**
    *   **MANDATORI:** Semua ikon fungsional, operator matematika, dan kontrol pengaturan **WAJIB** dirender menggunakan ikon kustom berbasis berkas vektor XML (SVG) berkontras tinggi dengan garis tebal (stroke pekat) yang didesain khusus agar menyatu dengan estetika NeoBrutalisme.
    *   **TIDAK BOLEH** menggunakan emoticon atau emoji kaku biasa sebagai representasi ikon fungsional atau ikon kalkulasi utama.
    *   Ikon-ikon yang wajib didefinisikan secara vektor meliputi:
        *   Tambah (`ic_neo_plus.xml`)
        *   Kurang (`ic_neo_minus.xml`)
        *   Kali (`ic_neo_multiply.xml`)
        *   Bagi (`ic_neo_divide.xml`)
        *   Sama Dengan (`ic_neo_equals.xml`)
        *   Hapus/Delete (`ic_neo_delete.xml`)
        *   Bersihkan/AC (`ic_neo_clear.xml`)
        *   Riwayat/History (`ic_neo_history.xml`)
        *   Pangkat/Power (`ic_neo_power.xml`)
        *   Persen/Percent (`ic_neo_percent.xml`)
        *   Pi (`ic_neo_pi.xml`)
        *   Akar Kuadrat/Sqrt (`ic_neo_sqrt.xml`)
        *   Pengaturan/Settings (`ic_neo_settings.xml`)

---

## 🗣️ Panduan Bahasa Aplikasi (MANDATORI: Bahasa Indonesia Gaul/Informal)

Untuk mempertahankan estetika NeoBrutalisme yang berani, santai, dan unik, aplikasi ini memiliki aturan mutlak mengenai lokalisasi bahasa:
*   **MANDATORI:** Seluruh lini instrumen aplikasi, teks antarmuka (UI strings), petunjuk (placeholders), pesan kesalahan, label tombol, panduan rumus, kategori BMI, hingga respons asisten AI **WAJIB** menggunakan **Bahasa Indonesia yang tidak baku (Gaul/Santai/Slang Jakarta/Sehari-hari)**.
*   **Gaya Penulisan:** Gunakan kata-kata seperti *lu*, *gua*, *bisa/gila/mager*, *cuy*, *bro*, *deh*, *nih*, *dong*, *aja*, *beneran*, *sih*, dan sejenisnya. Hindari penggunaan kalimat yang terlalu formal atau kaku (seperti penggunaan kata *Anda*, *saya*, *merupakan*, *silakan*, dsb).
*   **Contoh Penerapan:**
    *   *Sebelum (Formal):* "Silakan masukkan angka pertama" ➡️ *Sesudah (Gaul):* "Masukin angka pertamanya dulu dong cuy!"
    *   *Sebelum (Formal):* "Sistem Persamaan Linear tidak memiliki solusi" ➡️ *Sesudah (Gaul):* "Gak ada hasilnya bro, persamaannya aneh nih!"
    *   *Sebelum (Formal):* "BMI Anda termasuk kategori Obesitas" ➡️ *Sesudah (Gaul):* "Waduh, BMI lu masuk kategori Obesitas nih. Jaga makan ya bro!"
    *   *Sebelum (Formal):* "Error: API Key belum diatur." ➡️ *Sesudah (Gaul):* "Waduh, lu belom pasang API Key Gemini nih. Atur dulu gih!"

---

## 📱 Spesifikasi Fitur Tab demi Tab

### Tab 1: CALC (Mode Standar & Ilmiah)
*   Menyediakan input standar dengan kisi tombol 5 kolom yang responsif dalam mode potret.
*   Menyertakan dock horizontal yang dapat digulir untuk akses cepat ke 18 fungsi matematika tingkat lanjut.
*   Dalam mode lanskap, secara otomatis meluas menjadi tata letak 8 kolom yang menampilkan tombol standar dan tombol ilmiah tingkat lanjut secara berdampingan.
*   Memelihara daftar riwayat kalkulasi formula sebelumnya yang memungkinkan pengguna untuk mengetuk riwayat tersebut guna memuatnya kembali ke panel aktif.

### Tab 2: BASE (Konverter Basis Pemrogram)
*   Memungkinkan konversi langsung antara format **Heksadesimal, Desimal, Oktal, dan Biner**.
*   Mengetuk salah satu basis akan secara otomatis menyorotnya sebagai format input aktif.
*   Papan tombol (keypad) beradaptasi secara dinamis dengan format input yang dipilih, menonaktifkan huruf/angka yang tidak valid dalam basis saat ini (misalnya, hanya tombol `0` dan `1` yang interaktif dalam mode Biner, sementara tombol `A-F` diaktifkan dalam mode Heksadesimal).

### Tab 3: SOLVER (Penyelesai Persamaan)
*   Mencakup penyelesai untuk **Persamaan Kuadrat** ($ax^2 + bx + c = 0$) dan **Sistem Persamaan Linear $2\times2$** yang diselesaikan menggunakan Aturan Determinan Cramer.
*   Menampilkan langkah-langkah perhitungan matematika yang lengkap beserta rumus visual, perhitungan diskriminan, dan determinan ($D$, $D_x$, $D_y$).
*   Menangani akar kompleks ($x = \text{riil} \pm \text{imajiner } i$) dengan benar untuk penyelesai kuadrat ketika $D < 0$.

### Tab 4: FORMULAS (Lembar Interaktif Teknik)
*   Menyediakan 4 utilitas perhitungan teknis dan fisik:
    1.  **Penyelesai Hukum Ohm:** Menyelesaikan variabel ketiga yang hilang dari rumus $V = I \times R$ setelah dua bidang diisi.
    2.  **Penyelesai Geometri Lingkaran:** Menyelesaikan dan menghasilkan luas ($A = \pi r^2$) dan keliling ($C = 2\pi r$) langkah-demi-langkah berdasarkan input jari-jari secara dinamis.
    3.  **Teorema Pythagoras:** Menyelesaikan sisi yang hilang dari segitiga siku-siku ($a^2 + b^2 = c^2$).
    4.  **Kalkulator BMI:** Menghitung dan mengevaluasi Indeks Massa Tubuh (BMI), menampilkan skor BMI dan kategori penilaian yang dipetakan pada indikator warna brutalist kustom.

### Tab 5: AI (Asisten Matematika AI Hybrid)
*   **Mode Offline & Hybrid Sejati:** Aplikasi berjalan 100% luring (offline) secara default untuk fitur kalkulasi standar, basis pemrogram, penyelesai kuadrat, hukum Ohm, geometri, teorema Pythagoras, dan BMI. Fitur AI berjalan secara hibrida (hybrid) dan merupakan satu-satunya fitur yang memerlukan akses internet saat digunakan.
*   **Penyelesai Soal Cerita AI:** Mengintegrasikan model **Gemini 3.5 Flash** untuk memecahkan soal cerita matematika atau soal logika kompleks lengkap dengan penjabaran langkah-demi-langkah yang terstruktur, rapi, dan mudah dipahami.
*   **Manajemen API Key Mandiri & Aman:** Pengguna dapat mengonfigurasi dan menyimpan API Key Gemini milik mereka secara langsung dari UI aplikasi (disimpan secara lokal menggunakan `SharedPreferences` terenkripsi aman perangkat) atau menyiapkannya melalui berkas rahasia lingkungan (`BuildConfig.GEMINI_API_KEY`).
*   **Prosedur Penggunaan Mode Hybrid Bagi Pengguna:**
    1.  **Dapatkan API Key:** Buat API Key secara gratis melalui platform [Google AI Studio](https://aistudio.google.com/).
    2.  **Buka Fitur AI:** Jalankan aplikasi dan navigasikan ke tab **AI** (baik di mode potret maupun lanskap).
    3.  **Buka Panel Konfigurasi:** Ketuk teks **Atur Key** pada kartu konfigurasi API Key Gemini di bagian atas layar.
    4.  **Simpan API Key:** Tempelkan atau ketik API Key Anda pada kolom teks yang tersedia, lalu klik tombol **Simpan**. Lampu indikator status akan berubah menjadi warna hijau segar (**Status: API Key Terpasang (Siap)**).
    5.  **Tulis Pertanyaan Anda:** Masukkan soal cerita matematika atau pertanyaan ilmiah apa pun pada kolom teks input yang kokoh (misalnya: *"Jika x + y = 10 dan 2x - y = 8, berapakah nilai x dan y?"*).
    6.  **Selesaikan dengan AI:** Ketuk tombol **SOLVE WITH AI**. Aplikasi akan menghubungkan API Key Anda secara luring-ke-daring (hybrid) ke server Google Generative Language untuk menganalisis dan merumuskan jawaban lengkap dengan penjelasan langkah-demi-langkah.
    7.  **Bersihkan Layar:** Ketuk tombol **CLEAR** kapan saja untuk membersihkan input teks dan solusi sebelumnya untuk memulai perhitungan baru.

---

## 🛠️ Petunjuk Verifikasi & Pembuatan (Build)

1.  **Pembuatan APK (APK Generation):**
    *   Bangun proyek menggunakan alat pembuat Gradle standar.
    *   Jalankan `gradle assembleDebug` untuk mengompilasi dan menandatangani aplikasi dengan `debug.keystore` standar.
2.  **Lokasi Output & Sinkronisasi (MANDATORI):**
    *   APK debug yang dikompilasi secara otomatis dihasilkan di `.build-outputs/app-debug.apk`.
    *   **PENTING & WAJIB:** Berkas `APK_DOWNLOAD/app-debug.apk` harus selalu disinkronkan agar pengguna yang mengunduh proyek via ZIP mendapatkan APK versi terbaru yang menyertakan semua fitur teranyar (seperti UI NeoBrutalisme, logika kalkulator, dan AI Hybrid Solve).
    *   Untuk memastikan hal ini tidak pernah terlewat, berkas `app/build.gradle.kts` dikonfigurasi dengan tugas salin kustom `copyApkToDownload` yang dijalankan otomatis sebagai finalisasi dari proses pembuatan berkas APK (`assembleDebug` difinalisasi oleh `copyApkToDownload`).
    *   Selalu pastikan setiap kali ada pembaruan kode, jalankan kompilasi penuh untuk memperbarui berkas APK di kedua lokasi tersebut.
