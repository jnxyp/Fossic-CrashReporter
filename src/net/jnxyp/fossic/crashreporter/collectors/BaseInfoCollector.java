package net.jnxyp.fossic.crashreporter.collectors;

import net.jnxyp.fossic.crashreporter.models.BaseInfo;

public abstract class BaseInfoCollector {
    protected boolean infoCollected = false;
    protected BaseInfo info;

    public String getName(){
        return getInfo().getName() + "信息收集器";
    }

    public void collectInfo() {
        this.infoCollected = true;
    }

    public BaseInfo getInfo() {
        return info;
    }


}
