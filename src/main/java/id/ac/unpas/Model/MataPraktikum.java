package id.ac.unpas.Model;

public class MataPraktikum {
    private Long id;
    private String kode;
    private String nama;

    public MataPraktikum() {
    }

    public MataPraktikum(Long id, String kode, String nama) {
        this.id = id;
        this.kode = kode;
        this.nama = nama;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    @Override
    public String toString() {
        String kodePart = (kode == null) ? "" : kode.trim();
        String namaPart = (nama == null) ? "" : nama.trim();
        if (!kodePart.isEmpty() && !namaPart.isEmpty()) {
            return kodePart + " - " + namaPart;
        }
        return !namaPart.isEmpty() ? namaPart : kodePart;
    }
}
