package com.blazeloader.api.direct.base.event;

import com.blazeloader.api.core.base.main.BLMain;
import com.blazeloader.api.core.base.mod.BLMod;
import com.mumfrey.liteloader.core.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

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
