package net.jnxyp.fossic.crashreporter.collectors;

import net.jnxyp.fossic.crashreporter.Util;
import net.jnxyp.fossic.crashreporter.exceptions.InfoCollectionFailureException;
import net.jnxyp.fossic.crashreporter.exceptions.InfoCollectionPartialFailureException;

public abstract class BaseInfoCollector {
    public abstract String getName();

    public abstract String getRawInfo() throws InfoCollectionPartialFailureException;

}
