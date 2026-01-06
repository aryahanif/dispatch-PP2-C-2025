package id.ac.unpas.Model;

public class Asisten {
    private Long id;
    private String nim;
    private String nama;
    private String kontak;

    public Asisten() {
    }

    public Asisten(Long id, String nim, String nama, String kontak) {
        this.id = id;
        this.nim = nim;
        this.nama = nama;
        this.kontak = kontak;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKontak() {
        return kontak;
    }

    public void setKontak(String kontak) {
        this.kontak = kontak;
    }

    @Override
    public String toString() {
        String nimPart = (nim == null) ? "" : nim.trim();
        String namaPart = (nama == null) ? "" : nama.trim();
        if (!nimPart.isEmpty() && !namaPart.isEmpty()) {
            return nimPart + " - " + namaPart;
        }
        return !namaPart.isEmpty() ? namaPart : nimPart;
    }
}
