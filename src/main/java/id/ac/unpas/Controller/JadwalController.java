package id.ac.unpas.Controller;

import id.ac.unpas.Dao.AsistenDao;
import id.ac.unpas.Dao.JadwalDao;
import id.ac.unpas.Dao.MataPraktikumDao;
import id.ac.unpas.Dao.RuanganDao;
import id.ac.unpas.Model.Asisten;
import id.ac.unpas.Model.Hari;
import id.ac.unpas.Model.Jadwal;
import id.ac.unpas.Model.MataPraktikum;
import id.ac.unpas.Model.Ruangan;
import id.ac.unpas.View.JadwalView;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.List;

public class JadwalController {

    private final JadwalDao jadwalDao;
    private final MataPraktikumDao mataPraktikumDao;
    private final AsistenDao asistenDao;
    private final RuanganDao ruanganDao;
    private final JadwalView view;

    private List<MataPraktikum> cacheMp;
    private List<Asisten> cacheAsisten;
    private List<Ruangan> cacheRuangan;

    public JadwalController(JadwalDao jadwalDao,
            MataPraktikumDao mataPraktikumDao,
            AsistenDao asistenDao,
            RuanganDao ruanganDao,
            JadwalView view) {
        this.jadwalDao = jadwalDao;
        this.mataPraktikumDao = mataPraktikumDao;
        this.asistenDao = asistenDao;
        this.ruanganDao = ruanganDao;
        this.view = view;

        bind();
        reloadCombos();
        reloadTable();
    }

    public void refresh() {
        reloadCombos();
        reloadTable();
    }

    private void bind() {
        view.getBtnTambah().addActionListener(e -> onTambah());
        view.getBtnUbah().addActionListener(e -> onUbah());
        view.getBtnHapus().addActionListener(e -> onHapus());
        view.getBtnClear().addActionListener(e -> view.clearForm());

        view.getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                onTableSelected();
            }
        });
    }

    private void reloadCombos() {
        try {
            cacheMp = mataPraktikumDao.findAll();
            cacheAsisten = asistenDao.findAll();
            cacheRuangan = ruanganDao.findAll();

            view.setMataPraktikumItems(cacheMp);
            view.setAsistenItems(cacheAsisten);
            view.setRuanganItems(cacheRuangan);
        } catch (SQLException ex) {
            view.showError("Gagal memuat master data: " + ex.getMessage());
        }
    }

    private void onTambah() {
        String err = view.validateInput();
        if (err != null) {
            view.showError(err);
            return;
        }

        MataPraktikum mp = view.getSelectedMataPraktikum();
        Asisten a = view.getSelectedAsisten();
        Ruangan r = view.getSelectedRuangan();
        Hari hari = view.getSelectedHari();
        LocalTime mulai = view.getJamMulai();
        LocalTime selesai = view.getJamSelesai();

        try {
            if (jadwalDao.existsBentrokRuangan(r.getId(), hari, mulai, selesai, null)) {
                view.showError("Bentrok jadwal: Ruangan sudah terpakai di waktu tersebut.");
                return;
            }
            if (jadwalDao.existsBentrokAsisten(a.getId(), hari, mulai, selesai, null)) {
                view.showError("Bentrok jadwal: Asisten sudah terjadwal di waktu tersebut.");
                return;
            }

            Jadwal j = new Jadwal(null, mp.getId(), a.getId(), r.getId(), hari, mulai, selesai);
            long id = jadwalDao.insert(j);
            reloadTable();
            view.clearForm();
            view.showInfo("Berhasil menambah Jadwal (ID=" + id + ")");
        } catch (SQLException ex) {
            view.showError("Gagal menambah jadwal: " + ex.getMessage());
        }
    }

    private void onUbah() {
        Long id = UiUtil.tryParseLong(view.getIdText());
        if (id == null) {
            view.showError("Pilih data yang akan diubah.");
            return;
        }

        String err = view.validateInput();
        if (err != null) {
            view.showError(err);
            return;
        }

        MataPraktikum mp = view.getSelectedMataPraktikum();
        Asisten a = view.getSelectedAsisten();
        Ruangan r = view.getSelectedRuangan();
        Hari hari = view.getSelectedHari();
        LocalTime mulai = view.getJamMulai();
        LocalTime selesai = view.getJamSelesai();

        try {
            if (jadwalDao.existsBentrokRuangan(r.getId(), hari, mulai, selesai, id)) {
                view.showError("Bentrok jadwal: Ruangan sudah terpakai di waktu tersebut.");
                return;
            }
            if (jadwalDao.existsBentrokAsisten(a.getId(), hari, mulai, selesai, id)) {
                view.showError("Bentrok jadwal: Asisten sudah terjadwal di waktu tersebut.");
                return;
            }

            Jadwal j = new Jadwal(id, mp.getId(), a.getId(), r.getId(), hari, mulai, selesai);
            jadwalDao.update(j);
            reloadTable();
            view.clearForm();
            view.showInfo("Berhasil mengubah jadwal.");
        } catch (SQLException ex) {
            view.showError("Gagal mengubah jadwal: " + ex.getMessage());
        }
    }

    private void onHapus() {
        Long id = UiUtil.tryParseLong(view.getIdText());
        if (id == null) {
            view.showError("Pilih data yang akan dihapus.");
            return;
        }
        if (!UiUtil.confirm(view, "Hapus Jadwal ID=" + id + " ?")) {
            return;
        }
        try {
            jadwalDao.delete(id);
            reloadTable();
            view.clearForm();
            view.showInfo("Berhasil menghapus jadwal.");
        } catch (SQLException ex) {
            view.showError("Gagal menghapus jadwal: " + ex.getMessage());
        }
    }

    private void reloadTable() {
        try {
            List<Object[]> rows = jadwalDao.findAllForTable();
            DefaultTableModel model = (DefaultTableModel) view.getTable().getModel();
            model.setRowCount(0);
            for (Object[] r : rows) {
                model.addRow(r);
            }
        } catch (SQLException ex) {
            view.showError("Gagal memuat data jadwal: " + ex.getMessage());
        }
    }

    private void onTableSelected() {
        int row = view.getTable().getSelectedRow();
        if (row < 0) {
            return;
        }
        Object idObj = view.getTable().getValueAt(row, 0);
        Long id = (idObj instanceof Number)
                ? Long.valueOf(((Number) idObj).longValue())
                : UiUtil.tryParseLong(String.valueOf(idObj));
        if (id == null) {
            return;
        }

        try {
            Jadwal j = jadwalDao.findById(id);
            if (j == null) {
                return;
            }
            view.setIdText(String.valueOf(j.getId()));
            view.setSelectedHari(j.getHari());
            view.setJamMulai(j.getJamMulai());
            view.setJamSelesai(j.getJamSelesai());

            view.setSelectedMataPraktikum(findMataPraktikumById(cacheMp, j.getMataPraktikumId()));
            view.setSelectedAsisten(findAsistenById(cacheAsisten, j.getAsistenId()));
            view.setSelectedRuangan(findRuanganById(cacheRuangan, j.getRuanganId()));
        } catch (SQLException ex) {
            view.showError("Gagal mengambil detail jadwal: " + ex.getMessage());
        }
    }

    private static MataPraktikum findMataPraktikumById(List<MataPraktikum> list, Long id) {
        if (list == null || id == null) {
            return null;
        }
        for (MataPraktikum mp : list) {
            if (id.equals(mp.getId())) {
                return mp;
            }
        }
        return null;
    }

    private static Asisten findAsistenById(List<Asisten> list, Long id) {
        if (list == null || id == null) {
            return null;
        }
        for (Asisten a : list) {
            if (id.equals(a.getId())) {
                return a;
            }
        }
        return null;
    }

    private static Ruangan findRuanganById(List<Ruangan> list, Long id) {
        if (list == null || id == null) {
            return null;
        }
        for (Ruangan r : list) {
            if (id.equals(r.getId())) {
                return r;
            }
        }
        return null;
    }
}