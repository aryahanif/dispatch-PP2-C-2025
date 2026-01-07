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

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

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

        view.getCbFilterAsisten().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                reloadTable();
            }
        });
        view.getCbFilterRuangan().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                reloadTable();
            }
        });
        view.getBtnResetFilter().addActionListener(e -> {
            view.resetFilter();
            reloadTable();
        });
        view.getBtnExportPdf().addActionListener(e -> onExportPdf());

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

            Long selectedFilterAsistenId = null;
            Asisten selectedFilterAsisten = (Asisten) view.getCbFilterAsisten().getSelectedItem();
            if (selectedFilterAsisten != null) {
                selectedFilterAsistenId = selectedFilterAsisten.getId();
            }

            Long selectedFilterRuanganId = null;
            Ruangan selectedFilterRuangan = (Ruangan) view.getCbFilterRuangan().getSelectedItem();
            if (selectedFilterRuangan != null) {
                selectedFilterRuanganId = selectedFilterRuangan.getId();
            }

            view.setMataPraktikumItems(cacheMp);
            view.setAsistenItems(cacheAsisten);
            view.setRuanganItems(cacheRuangan);

            view.setFilterAsistenItems(cacheAsisten);
            view.setFilterRuanganItems(cacheRuangan);

            if (selectedFilterAsistenId != null) {
                view.getCbFilterAsisten().setSelectedItem(findAsistenById(cacheAsisten, selectedFilterAsistenId));
            }
            if (selectedFilterRuanganId != null) {
                view.getCbFilterRuangan().setSelectedItem(findRuanganById(cacheRuangan, selectedFilterRuanganId));
            }
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
            Asisten filterAsisten = (Asisten) view.getCbFilterAsisten().getSelectedItem();
            Ruangan filterRuangan = (Ruangan) view.getCbFilterRuangan().getSelectedItem();
            Long asistenId = (filterAsisten == null) ? null : filterAsisten.getId();
            Long ruanganId = (filterRuangan == null) ? null : filterRuangan.getId();

            List<Object[]> rows = jadwalDao.findForTable(asistenId, ruanganId);
            DefaultTableModel model = (DefaultTableModel) view.getTable().getModel();
            model.setRowCount(0);
            for (Object[] r : rows) {
                model.addRow(r);
            }
        } catch (SQLException ex) {
            view.showError("Gagal memuat data jadwal: " + ex.getMessage());
        }
    }

    private void onExportPdf() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Simpan Laporan Jadwal (PDF)");
        chooser.setFileFilter(new FileNameExtensionFilter("PDF (*.pdf)", "pdf"));
        chooser.setSelectedFile(new File("laporan-jadwal.pdf"));

        int result = chooser.showSaveDialog(view);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        if (file == null) {
            return;
        }
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = new File(file.getParentFile(), file.getName() + ".pdf");
        }

        TableModel model = view.getTable().getModel();

        try (PDDocument doc = new PDDocument()) {
            PDRectangle pageSize = PDRectangle.A4;
            float margin = 48;
            float yStart = pageSize.getHeight() - margin;
            float leading = 14;
            float y = yStart;

            PDPage page = new PDPage(pageSize);
            doc.addPage(page);
            PDPageContentStream cs = new PDPageContentStream(doc, page);

            // Header
            cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
            y = writeLine(cs, margin, y, "Laporan Jadwal Praktikum");
            cs.setFont(PDType1Font.HELVETICA, 11);
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            y = writeLine(cs, margin, y, "Dicetak: " + ts);
            y -= leading;

            cs.setFont(PDType1Font.HELVETICA, 10);
            for (int row = 0; row < model.getRowCount(); row++) {
                String id = String.valueOf(model.getValueAt(row, 0));
                String mp = String.valueOf(model.getValueAt(row, 1));
                String asisten = String.valueOf(model.getValueAt(row, 2));
                String ruangan = String.valueOf(model.getValueAt(row, 3));
                String hari = String.valueOf(model.getValueAt(row, 4));
                String mulai = String.valueOf(model.getValueAt(row, 5));
                String selesai = String.valueOf(model.getValueAt(row, 6));

                // Page break
                if (y < margin + (leading * 6)) {
                    cs.close();
                    page = new PDPage(pageSize);
                    doc.addPage(page);
                    cs = new PDPageContentStream(doc, page);
                    cs.setFont(PDType1Font.HELVETICA, 10);
                    y = yStart;
                }

                y = writeLine(cs, margin, y, "[" + id + "] " + hari + " " + mulai + " - " + selesai);
                y = writeWrapped(cs, margin, y, leading, "Mata Praktikum: " + mp, pageSize.getWidth() - (2 * margin));
                y = writeWrapped(cs, margin, y, leading, "Asisten: " + asisten, pageSize.getWidth() - (2 * margin));
                y = writeWrapped(cs, margin, y, leading, "Ruangan: " + ruangan, pageSize.getWidth() - (2 * margin));
                y -= leading;
            }

            cs.close();
            doc.save(file);
            view.showInfo("Berhasil export PDF: " + file.getAbsolutePath());
        } catch (IOException ex) {
            view.showError("Gagal export PDF: " + ex.getMessage());
        }
    }

    private static float writeLine(PDPageContentStream cs, float x, float y, String text) throws IOException {
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(safePdfText(text));
        cs.endText();
        return y - 14;
    }

    private static float writeWrapped(PDPageContentStream cs, float x, float y, float leading, String text,
            float maxWidth) throws IOException {
        if (text == null) {
            return y - leading;
        }

        String sanitized = safePdfText(text);
        String[] words = sanitized.split("\\s+");
        StringBuilder line = new StringBuilder();
        for (String w : words) {
            String next = line.length() == 0 ? w : (line + " " + w);
            float width = (PDType1Font.HELVETICA.getStringWidth(next) / 1000f) * 10;
            if (width > maxWidth && line.length() > 0) {
                y = writeLineWithLeading(cs, x, y, line.toString(), leading);
                line.setLength(0);
                line.append(w);
            } else {
                line.setLength(0);
                line.append(next);
            }
        }

        if (line.length() > 0) {
            y = writeLineWithLeading(cs, x, y, line.toString(), leading);
        }
        return y;
    }

    private static float writeLineWithLeading(PDPageContentStream cs, float x, float y, String text, float leading)
            throws IOException {
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(safePdfText(text));
        cs.endText();
        return y - leading;
    }

    private static String safePdfText(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder out = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '\n' || ch == '\r' || ch == '\t') {
                out.append(' ');
                continue;
            }
            // Best-effort: PDFBox built-in Type1 fonts are limited (WinAnsi-ish)
            if ((ch >= 32 && ch <= 126) || (ch >= 160 && ch <= 255)) {
                out.append(ch);
            } else {
                out.append('?');
            }
        }
        return out.toString();
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