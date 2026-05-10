# Sleding Es Puzzle Solver

Program ini adalah aplikasi berbasis Java yang dirancang untuk mencari solusi jalur terpendek dalam permainan teka-teki seluncur es (*Ice Sliding Puzzle*). Character 'Z' adalah player yang harus bergerak meluncur dari titik awal, mengumpulkan seluruh angka target secara berurutan, hingga mencapai titik akhir (Target) dengan total biaya (*cost*) yang paling minimal.

---

## 1. Deskripsi Program
Program ini mensimulasikan mekanisme *sliding* di atas es dengan kondisi: player tidak dapat berhenti kecuali menabrak tembok (`X`) atau objek tertentu. Aplikasi ini menyediakan visualisasi dari algoritma pencarian jalur serta statistik eksekusi secara mendetail.

---

## 2. Requirement & Instalasi
* **Java Development Kit (JDK)**: Versi 11 atau yang lebih baru.
* **Penyimpanan**: Pastikan direktori proyek memiliki folder bernama `test` untuk penyimpanan log laporan.
* **Resolusi Layar**: Minimal 1024x768 (Disarankan).

---

## 3. Cara Kompilasi
Buka terminal atau command prompt pada direktori utama proyek, lalu jalankan perintah berikut:

```bash
# Membuat folder bin untuk hasil kompilasi
mkdir bin

# Kompilasi seluruh kode sumber Java
javac -d bin src/**/*.java
