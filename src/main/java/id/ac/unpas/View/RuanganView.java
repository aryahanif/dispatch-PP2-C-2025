package id.ac.unpas.View;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class RuanganView extends JPanel {

    private final JTextField txtId = new JTextField(10);
    private final JTextField txtKode = new JTextField(20);
    private final JTextField txtNama = new JTextField(30);
    private final JTextField txtKapasitas = new JTextField(10);

    private final JButton btnTambah = new JButton("Tambah");
    private final JButton btnUbah = new JButton("Ubah");
    private final JButton btnHapus = new JButton("Hapus");
    private final JButton btnClear = new JButton("Clear");

    private final JTable table;

    public RuanganView() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtId.setEnabled(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Form Ruangan"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        form.add(new JLabel("Kode"), c);
        c.gridx = 1;
        c.weightx = 1;
        form.add(txtKode, c);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        form.add(new JLabel("Nama"), c);
        c.gridx = 1;
        c.weightx = 1;
        form.add(txtNama, c);

        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0;
        form.add(new JLabel("Kapasitas"), c);
        c.gridx = 1;
        c.weightx = 1;
        form.add(txtKapasitas, c);

        JPanel actions = new JPanel();
        actions.add(btnTambah);
        actions.add(btnUbah);
        actions.add(btnHapus);
        actions.add(btnClear);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.weightx = 0;
        form.add(actions, c);

        DefaultTableModel model = new DefaultTableModel(new Object[] { "ID", "Kode", "Nama", "Kapasitas" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public JTable getTable() {
        return table;
    }

    public JButton getBtnTambah() {
        return btnTambah;
    }

    public JButton getBtnUbah() {
        return btnUbah;
    }

    public JButton getBtnHapus() {
        return btnHapus;
    }

    public JButton getBtnClear() {
        return btnClear;
    }

    public String getIdText() {
        return txtId.getText().trim();
    }

    public void setIdText(String id) {
        txtId.setText(id == null ? "" : id);
    }

    public String getKode() {
        return txtKode.getText().trim();
    }

    public void setKode(String kode) {
        txtKode.setText(kode == null ? "" : kode);
    }

    public String getNama() {
        return txtNama.getText().trim();
    }

    public void setNama(String nama) {
        txtNama.setText(nama == null ? "" : nama);
    }

    public String getKapasitasText() {
        return txtKapasitas.getText().trim();
    }

    public void setKapasitasText(String kapasitas) {
        txtKapasitas.setText(kapasitas == null ? "" : kapasitas);
    }

    public void clearForm() {
        setIdText("");
        setKode("");
        setNama("");
        setKapasitasText("");
    }

    public String validateInput() {
        if (getKode().isEmpty()) {
            return "Kode wajib diisi.";
        }
        if (getNama().isEmpty()) {
            return "Nama wajib diisi.";
        }
        String raw = getKapasitasText();
        if (raw.isEmpty()) {
            return "Kapasitas wajib diisi.";
        }
        try {
            int k = Integer.parseInt(raw);
            if (k <= 0) {
                return "Kapasitas harus lebih dari 0.";
            }
        } catch (NumberFormatException ex) {
            return "Kapasitas harus angka.";
        }
        return null;
    }

    public void setTableModel(TableModel model) {
        table.setModel(model);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}
