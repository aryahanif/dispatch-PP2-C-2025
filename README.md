# Dispatch PP2 C 2025 – Aplikasi Manajemen Jadwal Praktikum

Aplikasi berbasis **Java** untuk mengelola master data (Mata Praktikum, Asisten, Ruangan) dan **Jadwal Praktikum** yang tersimpan di **MySQL**.

## Fitur

### Umum

- UI berbasis tab: **Mata Praktikum**, **Asisten**, **Ruangan**, **Jadwal**
- CRUD (Tambah/Ubah/Hapus) untuk seluruh data
- Validasi input dasar (field wajib, format angka, dll)
- Inisialisasi tabel database otomatis saat aplikasi dijalankan

### Mata Praktikum

- Kelola data mata praktikum (kode & nama)

### Asisten

- Kelola data asisten (NIM & nama)

### Ruangan

- Kelola data ruangan (kode, nama, kapasitas)

### Jadwal Praktikum

- Kelola jadwal: mata praktikum, asisten, ruangan, hari, jam mulai & jam selesai
- **Time picker** untuk jam mulai/selesai (format **jam:menit**):
  - `Jam Mulai (jam:menit) - Jam Selesai (jam:menit)`
- Deteksi bentrok jadwal:
  - Bentrok **ruangan** (ruangan sama, hari sama, waktu overlap)
  - Bentrok **asisten** (asisten sama, hari sama, waktu overlap)
- Pencarian/filter jadwal:
  - Filter berdasarkan **Asisten** (Semua / pilih asisten)
  - Filter berdasarkan **Ruangan** (Semua / pilih ruangan)
- Export laporan jadwal ke **PDF** sesuai tampilan tabel (hasil filter ikut terbawa)

## Teknologi

- Java 
- MySQL
- JDBC MySQL Connector
- Apache PDFBox (export PDF)

## Persiapan (Database)

1. Pastikan MySQL Server berjalan.

2. Buat database (default: `dispatch`).

Secara default aplikasi mencoba konek ke:

- `DB_URL` (default: `jdbc:mysql://localhost:3306/dispatch?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`)
- `DB_USER` (default: `root`)
- `DB_PASS` (default: kosong)

Jika kredensial kamu berbeda, set environment variables sebelum menjalankan aplikasi.

Contoh PowerShell:

```powershell
$env:DB_URL  = "jdbc:mysql://localhost:3306/dispatch?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USER = "root"
$env:DB_PASS = ""
```

Catatan:

- Aplikasi akan membuat tabel otomatis saat start (lihat `DbInit.init()`), tetapi **tidak** membuat database jika belum ada.

## Cara Menjalankan

### Opsi Jalankan dari IDE

1. Buka project.
2. Jalankan main class: `App.java`.

## Panduan Pengguna

### Navigasi

- Aplikasi terdiri dari 4 tab: **Mata Praktikum**, **Asisten**, **Ruangan**, **Jadwal**.
- Pilih tab untuk mengelola data terkait.

### Pola umum (CRUD)

1. **Tambah**
   - Isi form
   - Klik **Tambah**
2. **Ubah**
   - Klik salah satu baris pada tabel (form akan terisi)
   - Ubah nilai
   - Klik **Ubah**
3. **Hapus**
   - Klik salah satu baris pada tabel
   - Klik **Hapus** (akan ada konfirmasi)
4. **Clear**
   - Menghapus form input

### Tab Mata Praktikum

- Isi `Kode` dan `Nama`, lalu klik **Tambah**.
- Klik baris tabel untuk mengubah/menghapus.

### Tab Asisten

- Isi `NIM` dan `Nama`, lalu klik **Tambah**.
- Klik baris tabel untuk mengubah/menghapus.

### Tab Ruangan

- Isi `Kode`, `Nama`, dan `Kapasitas`, lalu klik **Tambah**.
- Klik baris tabel untuk mengubah/menghapus.

### Tab Jadwal

**Menambah jadwal**

1. Pilih `Mata Praktikum`, `Asisten`, `Ruangan`, dan `Hari`.
2. Atur waktu pada input:
   - `Jam Mulai (jam:menit) - Jam Selesai (jam:menit)`
3. Klik **Tambah**.

**Validasi & bentrok**

- Jam mulai harus lebih kecil dari jam selesai.
- Jika ruangan/asisten sudah terpakai pada hari & jam yang overlap, aplikasi akan menampilkan pesan bentrok.

**Filter jadwal**

- Gunakan dropdown **Asisten** dan/atau **Ruangan** pada bagian “Pencarian & Laporan”.
- Pilih `Semua ...` untuk menampilkan semua data.
- Tombol **Reset Filter** akan mengembalikan filter ke default.

**Export PDF**

- Atur filter (jika diperlukan), pastikan tabel menampilkan data yang diinginkan.
- Klik **Export PDF**.
- Pilih lokasi penyimpanan file.
