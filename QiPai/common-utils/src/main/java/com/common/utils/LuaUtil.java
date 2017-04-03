package com.common.utils;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaUtil {

	public static LuaValue loadFile(String luaPck, String luaFile) {
		LuaValue _G = JsePlatform.standardGlobals();
		_G.get("package").set("path", luaPck);
		_G.get("dofile").call(LuaValue.valueOf(luaFile));
		return _G;
	}
}
