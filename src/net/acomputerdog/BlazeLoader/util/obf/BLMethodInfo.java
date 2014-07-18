package net.acomputerdog.BlazeLoader.util.obf;

import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.transformers.event.MethodInfo;

public class BLMethodInfo extends MethodInfo {
    /**
     * Create a MethodInfo for the specified class and literal method name
     *
     * @param owner  Owner name descriptor
     * @param method Literal method name
     */
    public BLMethodInfo(Obf owner, String method) {
        super(owner, method);
    }

    //public BLMethodInfo(Obf owner, Obf method) {
    //    super(owner.name, owner.obf, method.name, method.obf, method.srg);
    //}
}
