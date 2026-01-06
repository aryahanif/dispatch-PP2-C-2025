package id.ac.unpas.Controller;

import id.ac.unpas.Dao.RuanganDao;
import id.ac.unpas.Model.Ruangan;
import id.ac.unpas.View.RuanganView;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.List;

public class RuanganController {
    private final RuanganDao dao;
    private final RuanganView view;

    public RuanganController(RuanganDao dao, RuanganView view) {
        this.dao = dao;
        this.view = view;
        bind();
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

    private void onTambah() {
        String err = view.validateInput();
        if (err != null) {
            view.showError(err);
            return;
        }

        int kapasitas = Integer.parseInt(view.getKapasitasText());
        try {
            Ruangan r = new Ruangan(null, view.getKode(), view.getNama(), kapasitas);
            long id = dao.insert(r);
            reloadTable();
            view.clearForm();
            view.showInfo("Berhasil menambah Ruangan (ID=" + id + ")");
        } catch (SQLException ex) {
            view.showError("Gagal menambah data: " + ex.getMessage());
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

        int kapasitas = Integer.parseInt(view.getKapasitasText());
        try {
            Ruangan r = new Ruangan(id, view.getKode(), view.getNama(), kapasitas);
            dao.update(r);
            reloadTable();
            view.clearForm();
            view.showInfo("Berhasil mengubah data.");
        } catch (SQLException ex) {
            view.showError("Gagal mengubah data: " + ex.getMessage());
        }
    }

    private void onHapus() {
        Long id = UiUtil.tryParseLong(view.getIdText());
        if (id == null) {
            view.showError("Pilih data yang akan dihapus.");
            return;
        }
        if (!UiUtil.confirm(view, "Hapus Ruangan ID=" + id + " ?")) {
            return;
        }
        try {
            dao.delete(id);
            reloadTable();
            view.clearForm();
            view.showInfo("Berhasil menghapus data.");
        } catch (SQLException ex) {
            view.showError("Gagal menghapus data: " + ex.getMessage());
        }
    }

    private void reloadTable() {
        try {
            List<Ruangan> data = dao.findAll();
            DefaultTableModel model = (DefaultTableModel) view.getTable().getModel();
            model.setRowCount(0);
            for (Ruangan r : data) {
                model.addRow(new Object[] { r.getId(), r.getKode(), r.getNama(), r.getKapasitas() });
            }
        } catch (SQLException ex) {
            view.showError("Gagal memuat data: " + ex.getMessage());
        }
    }

    private void onTableSelected() {
        int row = view.getTable().getSelectedRow();
        if (row < 0) {
            return;
        }
        Object idObj = view.getTable().getValueAt(row, 0);
        Object kodeObj = view.getTable().getValueAt(row, 1);
        Object namaObj = view.getTable().getValueAt(row, 2);
        Object kapObj = view.getTable().getValueAt(row, 3);

        view.setIdText(idObj == null ? "" : String.valueOf(idObj));
        view.setKode(kodeObj == null ? "" : String.valueOf(kodeObj));
        view.setNama(namaObj == null ? "" : String.valueOf(namaObj));
        view.setKapasitasText(kapObj == null ? "" : String.valueOf(kapObj));
    }
}
