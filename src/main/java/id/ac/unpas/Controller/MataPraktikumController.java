package id.ac.unpas.Controller;

import id.ac.unpas.Dao.MataPraktikumDao;
import id.ac.unpas.Model.MataPraktikum;
import id.ac.unpas.View.MataPraktikumView;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.List;

public class MataPraktikumController {
    private final MataPraktikumDao dao;
    private final MataPraktikumView view;

    public MataPraktikumController(MataPraktikumDao dao, MataPraktikumView view) {
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

        try {
            MataPraktikum mp = new MataPraktikum(null, view.getKode(), view.getNama());
            long id = dao.insert(mp);
            reloadTable();
            view.clearForm();
            view.showInfo("Berhasil menambah Mata Praktikum (ID=" + id + ")");
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

        try {
            MataPraktikum mp = new MataPraktikum(id, view.getKode(), view.getNama());
            dao.update(mp);
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
        if (!UiUtil.confirm(view, "Hapus Mata Praktikum ID=" + id + " ?")) {
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
            List<MataPraktikum> data = dao.findAll();
            DefaultTableModel model = (DefaultTableModel) view.getTable().getModel();
            model.setRowCount(0);
            for (MataPraktikum mp : data) {
                model.addRow(new Object[] { mp.getId(), mp.getKode(), mp.getNama() });
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
        view.setIdText(idObj == null ? "" : String.valueOf(idObj));
        Object kodeObj = view.getTable().getValueAt(row, 1);
        Object namaObj = view.getTable().getValueAt(row, 2);
        view.setKode(kodeObj == null ? "" : String.valueOf(kodeObj));
        view.setNama(namaObj == null ? "" : String.valueOf(namaObj));
    }
}