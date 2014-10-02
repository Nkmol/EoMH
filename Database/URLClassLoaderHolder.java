package Database;

import java.net.URL;
import java.net.URLClassLoader;

public class URLClassLoaderHolder {

	public static URLClassLoader ucl;
	
	public static URLClassLoader getUcl(URL u){
		
		if(ucl==null)
			ucl=new URLClassLoader(new URL[] { u });
		return ucl;
		
	}
	
}
