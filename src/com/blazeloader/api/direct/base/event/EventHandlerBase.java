package com.blazeloader.api.direct.base.event;

import com.blazeloader.api.core.base.main.BLMain;
import com.blazeloader.api.core.base.mod.BLMod;

import java.util.ArrayList;
import java.util.List;

/**
 * Side-independent event handler
 */
public class EventHandlerBase {
    public static final List<ModEventBaseHandler> modEventHandlers = new ArrayList<ModEventBaseHandler>();
    public static final List<TickEventBaseHandler> tickEventHandlers = new ArrayList<TickEventBaseHandler>();

    protected static void setActiveMod(Object mod) {
        if (mod instanceof BLMod) {
            BLMain.currActiveMod = (BLMod) mod;
        } else {
            BLMain.currActiveMod = null;
        }
    }


    public static void eventStart() {
        BLMod prevMod = BLMain.currActiveMod;
        for (ModEventBaseHandler mod : modEventHandlers) {
            setActiveMod(mod);
            mod.start();
        }
        BLMain.currActiveMod = prevMod;
    }

    public static void eventEnd() {
        BLMod prevMod = BLMain.currActiveMod;
        for (ModEventBaseHandler mod : modEventHandlers) {
            setActiveMod(mod);
            mod.stop();
        }
        BLMain.currActiveMod = prevMod;
    }
}
