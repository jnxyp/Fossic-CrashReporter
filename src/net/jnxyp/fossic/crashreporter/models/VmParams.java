package net.jnxyp.fossic.crashreporter.models;

import net.jnxyp.fossic.crashreporter.Config;
import net.jnxyp.fossic.crashreporter.Util;
import net.jnxyp.fossic.crashreporter.exceptions.InfoCollectionPartialFailureException;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

public class VmParams {
    public String vmParams;
    public int xms;
    public int xmx;

    public VmParams(String vmParams, int xms, int xmx) {
        this.vmParams = vmParams;
        this.xms = xms;
        this.xmx = xmx;
    }

    public static VmParams fromFile(File vmparamFile) throws IOException {
        int xms = Config.VMPARAMS_XMS_DEFAULT_VALUE;
        int xmx = Config.VMPARAMS_XMX_DEFAULT_VALUE;

        String vmParams = Util.readFile(vmparamFile, Config.VMPARAMS_CHARSET);
        Matcher mXms = Config.VMPARAMS_XMS_VALUE_PATTERN.matcher(vmParams);
        if (mXms.find()) {
            xms = Integer.parseInt(mXms.group(2));
        }
        Matcher mXmx = Config.VMPARAMS_XMX_VALUE_PATTERN.matcher(vmParams);
        if (mXmx.find()) {
            xmx = Integer.parseInt(mXmx.group(2));
        }
        return new VmParams(vmParams, xms, xmx);
    }

    public void save() {
        Matcher mXms = Config.VMPARAMS_XMS_VALUE_PATTERN.matcher(vmParams);
        vmParams = mXms.replaceAll(String.format("-Xms%dm", xms));
        Matcher mXmx = Config.VMPARAMS_XMX_VALUE_PATTERN.matcher(vmParams);
        vmParams = mXmx.replaceAll(String.format("-Xmx%dm", xmx));
        // todo
    }
}
