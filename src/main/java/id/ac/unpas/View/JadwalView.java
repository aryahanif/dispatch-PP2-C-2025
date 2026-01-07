package id.ac.unpas.View;

import id.ac.unpas.Model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.*;
import java.util.*;

public class JadwalView extends JPanel {

    private final JTextField txtId = new JTextField(10);
    private final JComboBox<MataPraktikum> cbMataPraktikum = new JComboBox<>();
    private final JComboBox<Asisten> cbAsisten = new JComboBox<>();
    private final JComboBox<Ruangan> cbRuangan = new JComboBox<>();
    private final JComboBox<Hari> cbHari = new JComboBox<>(Hari.values());

    private final JSpinner spJamMulai = new JSpinner(new SpinnerDateModel());
    private final JSpinner spJamSelesai = new JSpinner(new SpinnerDateModel());

    private final JButton btnTambah = new JButton("Tambah");
    private final JButton btnUbah = new JButton("Ubah");
    private final JButton btnHapus = new JButton("Hapus");
    private final JButton btnClear = new JButton("Clear");

    private final JTable table;

    public JadwalView() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtId.setEnabled(false);

        // show time only
        spJamMulai.setEditor(new JSpinner.DateEditor(spJamMulai, "HH:mm"));
        spJamSelesai.setEditor(new JSpinner.DateEditor(spJamSelesai, "HH:mm"));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Form Jadwal Praktikum"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        form.add(new JLabel("Mata Praktikum"), c);
        c.gridx = 1;
        c.weightx = 1;
        form.add(cbMataPraktikum, c);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        form.add(new JLabel("Asisten"), c);
        c.gridx = 1;
        c.weightx = 1;
        form.add(cbAsisten, c);

        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0;
        form.add(new JLabel("Ruangan"), c);
        c.gridx = 1;
        c.weightx = 1;
        form.add(cbRuangan, c);

        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 0;
        form.add(new JLabel("Hari"), c);
        c.gridx = 1;
        c.weightx = 1;
        form.add(cbHari, c);

        c.gridx = 0;
        c.gridy = 4;
        c.weightx = 0;
        form.add(new JLabel("Jam Mulai"), c);
        c.gridx = 1;
        c.weightx = 1;
        form.add(spJamMulai, c);

        c.gridx = 0;
        c.gridy = 5;
        c.weightx = 0;
        form.add(new JLabel("Jam Selesai"), c);
        c.gridx = 1;
        c.weightx = 1;
        form.add(spJamSelesai, c);

        JPanel actions = new JPanel();
        actions.add(btnTambah);
        actions.add(btnUbah);
        actions.add(btnHapus);
        actions.add(btnClear);

        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 2;
        c.weightx = 0;
        form.add(actions, c);

        DefaultTableModel model = new DefaultTableModel(
                new Object[] { "ID", "Mata Praktikum", "Asisten", "Ruangan", "Hari", "Mulai", "Selesai" }, 0) {
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

    public void setMataPraktikumItems(List<MataPraktikum> items) {
        cbMataPraktikum.removeAllItems();
        if (items == null) {
            return;
        }
        for (MataPraktikum mp : items) {
            cbMataPraktikum.addItem(mp);
        }
    }

    public void setAsistenItems(List<Asisten> items) {
        cbAsisten.removeAllItems();
        if (items == null) {
            return;
        }
        for (Asisten a : items) {
            cbAsisten.addItem(a);
        }
    }

    public void setRuanganItems(List<Ruangan> items) {
        cbRuangan.removeAllItems();
        if (items == null) {
            return;
        }
        for (Ruangan r : items) {
            cbRuangan.addItem(r);
        }
    }

    public MataPraktikum getSelectedMataPraktikum() {
        return (MataPraktikum) cbMataPraktikum.getSelectedItem();
    }

    public void setSelectedMataPraktikum(MataPraktikum mp) {
        cbMataPraktikum.setSelectedItem(mp);
    }

    public Asisten getSelectedAsisten() {
        return (Asisten) cbAsisten.getSelectedItem();
    }

    public void setSelectedAsisten(Asisten asisten) {
        cbAsisten.setSelectedItem(asisten);
    }

    public Ruangan getSelectedRuangan() {
        return (Ruangan) cbRuangan.getSelectedItem();
    }

    public void setSelectedRuangan(Ruangan ruangan) {
        cbRuangan.setSelectedItem(ruangan);
    }

    public Hari getSelectedHari() {
        return (Hari) cbHari.getSelectedItem();
    }

    public void setSelectedHari(Hari hari) {
        cbHari.setSelectedItem(hari);
    }

    public LocalTime getJamMulai() {
        return toLocalTime((Date) spJamMulai.getValue());
    }

    public LocalTime getJamSelesai() {
        return toLocalTime((Date) spJamSelesai.getValue());
    }

    public void setJamMulai(LocalTime time) {
        if (time == null) {
            return;
        }
        spJamMulai.setValue(Date.from(time.atDate(java.time.LocalDate.now())
                .atZone(ZoneId.systemDefault()).toInstant()));
    }

    public void setJamSelesai(LocalTime time) {
        if (time == null) {
            return;
        }
        spJamSelesai.setValue(Date.from(time.atDate(java.time.LocalDate.now())
                .atZone(ZoneId.systemDefault()).toInstant()));
    }

    public void clearForm() {
        setIdText("");
        cbMataPraktikum.setSelectedItem(null);
        cbAsisten.setSelectedItem(null);
        cbRuangan.setSelectedItem(null);
        cbHari.setSelectedIndex(0);
    }

    public String validateInput() {
        if (getSelectedMataPraktikum() == null) {
            return "Mata Praktikum wajib dipilih.";
        }
        if (getSelectedAsisten() == null) {
            return "Asisten wajib dipilih.";
        }
        if (getSelectedRuangan() == null) {
            return "Ruangan wajib dipilih.";
        }
        LocalTime mulai = getJamMulai();
        LocalTime selesai = getJamSelesai();
        if (mulai == null || selesai == null) {
            return "Jam mulai/selesai wajib diisi.";
        }
        if (!mulai.isBefore(selesai)) {
            return "Jam mulai harus lebih kecil dari jam selesai.";
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

    private static LocalTime toLocalTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime().withSecond(0).withNano(0);
    }
}