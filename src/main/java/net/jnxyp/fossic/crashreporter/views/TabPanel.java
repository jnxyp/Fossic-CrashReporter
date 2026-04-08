package net.jnxyp.fossic.crashreporter.views;

import javax.swing.*;

abstract class TabPanel extends JPanel {
    abstract String getTabName();

    abstract void init();
}
