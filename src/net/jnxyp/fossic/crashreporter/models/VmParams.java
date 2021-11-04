package net.jnxyp.fossic.crashreporter.models;

import net.jnxyp.fossic.crashreporter.Config;
import net.jnxyp.fossic.crashreporter.Util;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

public class VmParams {
    protected String vmParams;
    protected File vmParamsFile;

    public VmParams(File vmParamsFile) throws IOException {
        this.vmParamsFile = vmParamsFile;
        vmParams = Util.readFile(vmParamsFile, Config.VMPARAMS_CHARSET);
    }

    public String getVmParams() {
        return vmParams;
    }

    public void setXms(int xms) {
        Matcher mXms = Config.VMPARAMS_XMS_VALUE_PATTERN.matcher(vmParams);
        vmParams = mXms.replaceAll(String.format("-Xms%dm", xms));
    }

    public void setXmx(int xmx) {
        Matcher mXmx = Config.VMPARAMS_XMX_VALUE_PATTERN.matcher(vmParams);
        vmParams = mXmx.replaceAll(String.format("-Xmx%dm", xmx));
    }

    public int getXms() {
        int xms = Config.VMPARAMS_XMS_DEFAULT_VALUE;
        Matcher mXms = Config.VMPARAMS_XMS_VALUE_PATTERN.matcher(vmParams);
        if (mXms.find()) {
            xms = Integer.parseInt(mXms.group(2));
        }
        return xms;
    }

    public int getXmx() {
        int xmx = Config.VMPARAMS_XMX_DEFAULT_VALUE;
        Matcher mXmx = Config.VMPARAMS_XMX_VALUE_PATTERN.matcher(vmParams);
        if (mXmx.find()) {
            xmx = Integer.parseInt(mXmx.group(2));
        }
        return xmx;
    }

    public void save() throws IOException {
        Util.writeTextFile(vmParamsFile, getVmParams(), Config.VMPARAMS_CHARSET);
    }
}
