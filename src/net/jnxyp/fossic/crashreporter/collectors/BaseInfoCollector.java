package net.jnxyp.fossic.crashreporter.collectors;

import net.jnxyp.fossic.crashreporter.Util;
import net.jnxyp.fossic.crashreporter.exceptions.InfoCollectionFailureException;
import net.jnxyp.fossic.crashreporter.exceptions.InfoCollectionPartialFailureException;

public abstract class BaseInfoCollector {
    protected boolean infoCollected = false;
    public abstract String getName();

    public void collectInfo() throws InfoCollectionPartialFailureException {
        this.infoCollected = true;
    }

    public abstract String asMarkdown();


}
