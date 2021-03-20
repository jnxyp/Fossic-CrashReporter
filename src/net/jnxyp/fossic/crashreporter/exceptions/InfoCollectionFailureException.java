package net.jnxyp.fossic.crashreporter.exceptions;

import net.jnxyp.fossic.crashreporter.collectors.BaseInfoCollector;

public class InfoCollectionFailureException extends Exception {
    public InfoCollectionFailureException(BaseInfoCollector collector, String message, Throwable cause) {
        super(String.format("[%s]%s", collector.getName(), message), cause);
    }
}
