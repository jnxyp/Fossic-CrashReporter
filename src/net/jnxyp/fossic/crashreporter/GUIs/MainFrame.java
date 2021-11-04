package net.jnxyp.fossic.crashreporter.GUIs;

import com.sun.org.apache.xpath.internal.operations.Bool;
import net.jnxyp.fossic.crashreporter.Config;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainFrame extends JFrame {

    protected JTabbedPane tabbedPane;
    protected HashMap<TabPanel, Boolean> tabInitialized;

    public MainFrame() {
        super(Config.PROGRAM_NAME + " " + Config.PROGRAM_VERSION);

        tabbedPane = new JTabbedPane();
        tabInitialized = new HashMap<>();

        this.getContentPane().add(tabbedPane);

        // Set window properties
        this.setBounds(400, 400, 800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set native look and feel
        //        try {
        //            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        //                if ("Nimbus".equals(info.getName())) {
        //                    UIManager.setLookAndFeel(info.getClassName());
        //                    break;
        //                }
        //            }
        //        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
        //            e.printStackTrace();
        //        }

        // Initialize the panel only when selected
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int tabIndex = tabbedPane.getSelectedIndex();
                TabPanel panel = (TabPanel) tabbedPane.getComponentAt(tabIndex);
                if (panel!=null && !tabInitialized.get(panel)) {
                    panel.init();
                    tabInitialized.put(panel, true);
                }
            }
        });

        // Add tab panels
        this.addTabPanel(MainFrame.generateProgramInfoPanel());
    }

    public static TabPanel generateProgramInfoPanel() {
        return new TabPanel() {
            @Override
            String getTabName() {
                return "关于";
            }

            @Override
            void init() {
                String aboutText = Config.PROGRAM_NAME +
                        "\n" +
                        Config.PROGRAM_VERSION;

                this.add(new JLabel(aboutText));
            }
        };
    }

    public void addTabPanel(TabPanel panel) {
        addTabPanel(panel, tabbedPane.getTabCount());
    }

    public void addTabPanel(TabPanel panel, int index) {
        tabInitialized.put(panel, false);
        tabbedPane.insertTab(panel.getTabName(), null, panel, "", index);
    }

    public void switchToTab(TabPanel panel) {
        tabbedPane.setSelectedComponent(panel);
    }
}
