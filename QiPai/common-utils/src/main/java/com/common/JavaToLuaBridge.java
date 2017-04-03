package com.common;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class JavaToLuaBridge {
	private static LuaValue javaToLua;

    private JavaToLuaBridge() {

    }

    public static void init(String packagePath, String javaToLuaFile,
            String javaToLuaClass) {
        LuaValue _G = JsePlatform.standardGlobals();
        _G.get("package").set("path", packagePath);

        _G.get("dofile").call(LuaValue.valueOf(javaToLuaFile));
        javaToLua = _G.get(javaToLuaClass);
    }

    public static LuaValue getData(String dbName, int dataId, int fieldIndex) {
        LuaValue result = javaToLua.get("getData").call(
                LuaValue.valueOf(dbName), LuaValue.valueOf(dataId),
                LuaValue.valueOf(fieldIndex));
        return result;
    }
}
