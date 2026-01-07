package id.ac.unpas;

import id.ac.unpas.Controller.*;
import id.ac.unpas.Dao.*;
import id.ac.unpas.Utils.*;
import id.ac.unpas.View.*;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppView appView = new AppView();

            try {
                DbInit.init();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(appView,
                        "Gagal inisialisasi database: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            MataPraktikumDao mataPraktikumDao = new MataPraktikumDao();
            AsistenDao asistenDao = new AsistenDao();
            RuanganDao ruanganDao = new RuanganDao();
            JadwalDao jadwalDao = new JadwalDao();

            new MataPraktikumController(mataPraktikumDao, appView.getMataPraktikumView());
            new AsistenController(asistenDao, appView.getAsistenView());
            new RuanganController(ruanganDao, appView.getRuanganView());
            JadwalController jadwalController = new JadwalController(jadwalDao, mataPraktikumDao, asistenDao,
                    ruanganDao, appView.getJadwalView());

            appView.addTabsChangeListener(e -> {
                if (appView.isJadwalTabSelected()) {
                    jadwalController.refresh();
                }
            });

            appView.setVisible(true);
        });
    }
}
