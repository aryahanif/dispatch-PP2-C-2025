package id.ac.unpas.Controller;
import id.ac.unpas.Dao.AsistenDao;
import id.ac.unpas.Model.Asisten;
import id.ac.unpas.View.AsistenView;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.List;

public class AsistenController {
        private final AsistenDao dao;
        private final AsistenView view;

        public AsistenController(AsistenDao dao, AsistenView view) {
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
                Asisten a = new Asisten(null, view.getNim(), view.getNama());
                long id = dao.insert(a);
                reloadTable();
                view.clearForm();
                view.showInfo("Berhasil menambah Asisten (ID=" + id + ")");
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
                Asisten a = new Asisten(id, view.getNim(), view.getNama());
                dao.update(a);
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
            if (!UiUtil.confirm(view, "Hapus Asisten ID=" + id + " ?")) {
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
                List<Asisten> data = dao.findAll();
                DefaultTableModel model = (DefaultTableModel) view.getTable().getModel();
                model.setRowCount(0);
                for (Asisten a : data) {
                    model.addRow(new Object[] { a.getId(), a.getNim(), a.getNama() });
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
            Object nimObj = view.getTable().getValueAt(row, 1);
            Object namaObj = view.getTable().getValueAt(row, 2);

            view.setIdText(idObj == null ? "" : String.valueOf(idObj));
            view.setNim(nimObj == null ? "" : String.valueOf(nimObj));
            view.setNama(namaObj == null ? "" : String.valueOf(namaObj));
        }
}
