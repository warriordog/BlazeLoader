package com.blazeloader.api.direct.base.event;

import com.mumfrey.liteloader.core.event.HandlerList;

/**
 * Side-independent event handler
 */
public class EventHandlerBase {
    public static final HandlerList<ModEventBaseHandler> modEventHandlers = new HandlerList<ModEventBaseHandler>(ModEventBaseHandler.class);
    public static final HandlerList<TickEventBaseHandler> tickEventHandlers = new HandlerList<TickEventBaseHandler>(TickEventBaseHandler.class);

    public static void eventStart() {
        modEventHandlers.all().start();
    }

    public static void eventEnd() {
        modEventHandlers.all().stop();
    }
}
