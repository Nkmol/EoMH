package Configuration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Configuration {
        private volatile ConcurrentMap<String, String> variables;
        private boolean allowReset;
          
        public Configuration(){
                this.variables = new ConcurrentHashMap<String, String>();
                this.allowReset = false;
        }
        public Configuration(boolean allow){
                this.variables = new ConcurrentHashMap<String, String>();
                this.allowReset = allow;
        }
        public String getVar(String var){
                return this.variables.get(var);
        }
        public void setVar(String var, String val){
                
                if (this.allowReset) this.variables.put(var, val);
                else this.variables.putIfAbsent(var, val);
        }
        public int getIntVar(String var) {
                return Integer.parseInt(this.variables.get(var));
        }

}