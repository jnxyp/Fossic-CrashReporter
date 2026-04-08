package net.jnxyp.fossic.crashreporter.collectors;

import net.jnxyp.fossic.crashreporter.exceptions.InfoCollectionFatalFailureException;
import net.jnxyp.fossic.crashreporter.models.info.BaseInfo;

public abstract class BaseInfoCollector {
    protected boolean infoCollected = false;
    protected BaseInfo info;

    public String getName(){
        return getInfo().getName() + "信息收集器";
    }

    public boolean hasInfo() {return infoCollected;}

    public void collectInfo() {
        try {
            tryCollectInfo();
        } catch (Exception e) {
            getInfo().addError(new InfoCollectionFatalFailureException(this, "本部分信息收集失败", e));
        }
    }

    public abstract void tryCollectInfo();

    public abstract BaseInfo getInfo();


}
