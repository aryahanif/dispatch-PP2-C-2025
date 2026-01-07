package id.ac.unpas.Model;

public class Asisten {
    private Long id;
    private String nim;
    private String nama;

    public Asisten() {
    }

    public Asisten(Long id, String nim, String nama) {
        this.id = id;
        this.nim = nim;
        this.nama = nama;
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
