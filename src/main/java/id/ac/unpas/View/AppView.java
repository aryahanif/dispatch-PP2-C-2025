package id.ac.unpas.View;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;

public class AppView extends JFrame {

    private final MataPraktikumView mataPraktikumView;
    private final AsistenView asistenView;
    private final RuanganView ruanganView;
    private final JadwalView jadwalView;
    private final JTabbedPane tabs;

    public AppView() {
        super("Aplikasi Manajemen Jadwal Praktikum");

        this.mataPraktikumView = new MataPraktikumView();
        this.asistenView = new AsistenView();
        this.ruanganView = new RuanganView();
        this.jadwalView = new JadwalView();

        this.tabs = new JTabbedPane();
        tabs.addTab("Mata Praktikum", mataPraktikumView);
        tabs.addTab("Asisten", asistenView);
        tabs.addTab("Ruangan", ruanganView);
        tabs.addTab("Jadwal", jadwalView);

        setContentPane(tabs);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    public MataPraktikumView getMataPraktikumView() {
        return mataPraktikumView;
    }

    public AsistenView getAsistenView() {
        return asistenView;
    }

    public RuanganView getRuanganView() {
        return ruanganView;
    }

    public JadwalView getJadwalView() {
        return jadwalView;
    }

    public void addTabsChangeListener(ChangeListener listener) {
        tabs.addChangeListener(listener);
    }

    public boolean isJadwalTabSelected() {
        return tabs.getSelectedComponent() == jadwalView;
    }
}