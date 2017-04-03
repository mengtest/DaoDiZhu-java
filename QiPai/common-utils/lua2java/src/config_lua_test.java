import org.luaj.vm2.*;
import org.luaj.vm2.lib.*;
public class config_lua_test extends VarArgFunction {
   public Varargs onInvoke(Varargs $arg) {
      env.get(module).call(jsonsss);
      env.set(test,new ZeroArgFunction(env) {
         public LuaValue call() {
            env.get(print).call(hellow);
            return NONE;
         }
      });
      return NONE;
   }
   static final LuaValue module = valueOf("module");
   static final LuaValue jsonsss = valueOf("jsonsss");
   static final LuaValue print = valueOf("print");
   static final LuaValue hellow = valueOf("hellow");
   static final LuaValue test = valueOf("test");
}