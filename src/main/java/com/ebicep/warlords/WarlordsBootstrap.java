package com.ebicep.warlords;

import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.JavaUtils;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;


public class WarlordsBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        Logger rootLogger = (Logger) org.apache.logging.log4j.LogManager.getRootLogger();
        rootLogger.addFilter(new CustomFilter());
    }

    private static class CustomFilter extends AbstractFilter {

        @Override
        public Result filter(LogEvent event) {
            if (event.getThrown() != null) {
                ChatUtils.MessageType.sendErrorToAdmin(
                        JavaUtils.throwableToString(event.getThrown(), 10)
                );
            }
            return super.filter(event);
        }

        @Override
        public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
            if (t != null) {
                ChatUtils.MessageType.sendErrorToAdmin(
                        JavaUtils.throwableToString(t, 10)
                );
            }
            return super.filter(logger, level, marker, msg, t);
        }

        @Override
        public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
            if (t != null) {
                ChatUtils.MessageType.sendErrorToAdmin(
                        JavaUtils.throwableToString(t, 10)
                );
            }
            return super.filter(logger, level, marker, msg, t);
        }

        @Override
        public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
            if (params != null) {
                for (Object param : params) {
                    if (param instanceof Throwable) {
                        ChatUtils.MessageType.sendErrorToAdmin(
                                JavaUtils.throwableToString((Throwable) param, 10)
                        );
                    }
                }
            }
            return super.filter(logger, level, marker, msg, params);
        }

    }

}
