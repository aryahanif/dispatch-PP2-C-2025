package id.ac.unpas.Model;

import java.time.LocalTime;

public class Jadwal {
    private Long id;
    private Long mataPraktikumId;
    private Long asistenId;
    private Long ruanganId;
    private Hari hari;
    private LocalTime jamMulai;
    private LocalTime jamSelesai;

    public Jadwal() {
    }

    public Jadwal(Long id,
            Long mataPraktikumId,
            Long asistenId,
            Long ruanganId,
            Hari hari,
            LocalTime jamMulai,
            LocalTime jamSelesai) {
        this.id = id;
        this.mataPraktikumId = mataPraktikumId;
        this.asistenId = asistenId;
        this.ruanganId = ruanganId;
        this.hari = hari;
        this.jamMulai = jamMulai;
        this.jamSelesai = jamSelesai;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMataPraktikumId() {
        return mataPraktikumId;
    }

    public void setMataPraktikumId(Long mataPraktikumId) {
        this.mataPraktikumId = mataPraktikumId;
    }

    public Long getAsistenId() {
        return asistenId;
    }

    public void setAsistenId(Long asistenId) {
        this.asistenId = asistenId;
    }

    public Long getRuanganId() {
        return ruanganId;
    }

    public void setRuanganId(Long ruanganId) {
        this.ruanganId = ruanganId;
    }

    public Hari getHari() {
        return hari;
    }

    public void setHari(Hari hari) {
        this.hari = hari;
    }

    public LocalTime getJamMulai() {
        return jamMulai;
    }

    public void setJamMulai(LocalTime jamMulai) {
        this.jamMulai = jamMulai;
    }

    public LocalTime getJamSelesai() {
        return jamSelesai;
    }

    public void setJamSelesai(LocalTime jamSelesai) {
        this.jamSelesai = jamSelesai;
    }

    @Override
    public String toString() {
        return "Jadwal{" +
                "id=" + id +
                ", mataPraktikumId=" + mataPraktikumId +
                ", asistenId=" + asistenId +
                ", ruanganId=" + ruanganId +
                ", hari=" + hari +
                ", jamMulai=" + jamMulai +
                ", jamSelesai=" + jamSelesai +
                '}';
    }
}
